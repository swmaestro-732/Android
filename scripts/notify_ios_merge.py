#!/usr/bin/env python3
"""Share a merged develop PR and its screenshots; only Python stdlib is required."""

import hashlib
import html
from html.parser import HTMLParser
import json
import os
import re
import sys
from urllib.error import HTTPError, URLError
from urllib.parse import quote, unquote, urlencode, urlsplit
from urllib.request import HTTPRedirectHandler, Request, build_opener

MARKER = "<!-- courmy-ios-merge-notice -->"
MAX_IMAGE_BYTES = 20 * 1024 * 1024
IMAGE_EXTENSION = re.compile(r"\.(?:png|jpe?g|gif|webp)(?:[?#].*)?$", re.I)


class SlackRejected(ValueError):
    """A parsed ok:false response: unlike a timeout, the write was rejected."""


def eligible_pr(pr):
    return bool(
        pr.get("merged")
        and pr.get("base", {}).get("ref") == "develop"
        and pr.get("user", {}).get("login") not in {"dependabot", "dependabot[bot]"}
        and not pr.get("head", {}).get("ref", "").startswith("dependabot/")
    )


def section(body, title, level=2):
    body = re.sub(r"<!--.*?-->", "", body or "", flags=re.S)
    lines = body.splitlines()
    start = None
    for index, line in enumerate(lines):
        heading = re.match(r"^(#{1,6})\s+(.+?)\s*#*\s*$", line)
        if not heading:
            continue
        depth, name = len(heading[1]), heading[2]
        if start is not None and depth <= level:
            return "\n".join(lines[start:index]).strip()
        if depth == level and (name == title or name.endswith(" " + title)):
            start = index + 1
    return "\n".join(lines[start:]).strip() if start is not None else ""


def slack_text(value):
    return html.escape(value, quote=False)


def message_text(pr):
    body = pr.get("body") or ""
    details = section(body, "iOS 공유사항")
    if not details:
        raise ValueError("PR 본문의 '## iOS 공유사항'을 작성한 뒤 다시 실행하세요.")
    summary = section(details, "Slack 알림 요약", 3) or section(body, "변경 요약")
    if not summary:
        raise ValueError("PR에 '### Slack 알림 요약' 또는 '## 변경 요약'을 작성하세요.")
    summary = summary[:2200]
    return (
        f"*Android → iOS | develop 머지 완료 · PR #{pr['number']}*\n"
        f"{slack_text(pr['title'])}\n{slack_text(summary)}\n\n"
        f"<{pr['html_url']}|iOS 구현 상세 명세·완료 체크리스트>\n"
        f"머지 커밋: `{pr['merge_commit_sha']}` · 앱 배포 완료 알림은 아닙니다.\n"
        "스크린샷과 전송 결과는 이 스레드 및 PR의 자동화 댓글에서 확인하세요."
    )


class ImageParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.urls = []

    def handle_starttag(self, tag, attrs):
        if tag.lower() == "img":
            self.urls.extend(value for name, value in attrs if name == "src" and value)


def image_urls(body):
    body = re.sub(r"<!--.*?-->", "", body or "", flags=re.S)
    parser = ImageParser()
    parser.feed(body)
    markdown = re.findall(r"!\[[^\]]*\]\(\s*<?(https://[^\s)>]+)>?(?:\s+[^)]*)?\)", body)
    direct = [url for url in re.findall(r"https://[^\s<>\"')]+", body) if IMAGE_EXTENSION.search(url)]
    # Source order makes captions and re-runs deterministic across Markdown and HTML.
    urls = set(html.unescape(url) for url in parser.urls + markdown + direct)
    return sorted(urls, key=lambda url: body.find(url))


def image_source(url, repository, refs=()):
    parsed = urlsplit(url)
    if parsed.scheme != "https" or parsed.username or parsed.password or parsed.port:
        raise ValueError("HTTPS GitHub 원본 이미지 주소만 지원합니다.")
    host, path = parsed.hostname, unquote(parsed.path)
    prefix = "/" + repository + "/"
    if host == "raw.githubusercontent.com" and path.startswith(prefix):
        rest = path[len(prefix):]
    elif host == "github.com" and path.startswith(prefix + "blob/"):
        rest = path[len(prefix + "blob/"):]
    elif (
        host == "github.com" and path.startswith("/user-attachments/assets/")
    ) or host in {"user-images.githubusercontent.com", "private-user-images.githubusercontent.com"}:
        return "public", "", url
    else:
        raise ValueError("이 저장소의 GitHub 이미지 또는 GitHub 첨부 이미지만 지원합니다.")
    if parsed.query or parsed.fragment:
        # GitHub blob ?raw=true is a presentation parameter, never an API credential.
        if parsed.query not in {"", "raw=true", "raw=1"} or parsed.fragment:
            raise ValueError("저장소 이미지 URL의 쿼리/fragment를 제거하세요.")
    if any(part in {".", ".."} for part in rest.split("/")):
        raise ValueError("잘못된 저장소 이미지 경로입니다.")
    for ref in sorted(filter(None, refs), key=len, reverse=True):
        if rest.startswith(ref + "/"):
            return "repository", ref, rest[len(ref) + 1:]
    ref, separator, filename = rest.partition("/")
    if not separator or not ref or not filename or any(part in {".", ".."} for part in rest.split("/")):
        raise ValueError("잘못된 저장소 이미지 경로입니다.")
    return "repository", ref, filename


def image_type(data):
    if data.startswith(b"\x89PNG\r\n\x1a\n"):
        return "image/png"
    if data.startswith(b"\xff\xd8\xff"):
        return "image/jpeg"
    if data.startswith((b"GIF87a", b"GIF89a")):
        return "image/gif"
    if data.startswith(b"RIFF") and data[8:12] == b"WEBP":
        return "image/webp"
    raise ValueError("PNG/JPEG/GIF/WebP 원본이 아닌 응답입니다.")


class SafeRedirect(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        target = urlsplit(newurl)
        allowed = {"github.com", "raw.githubusercontent.com", "user-images.githubusercontent.com",
                   "private-user-images.githubusercontent.com", "objects.githubusercontent.com"}
        if (req.has_header("Authorization") or target.scheme != "https" or target.hostname not in allowed
                or target.username or target.password or target.port):
            raise ValueError("이미지 다운로드의 허용되지 않은 리디렉션입니다.")
        return super().redirect_request(req, fp, code, msg, headers, newurl)


def request_bytes(url, headers=None, payload=None, method=None, limit=MAX_IMAGE_BYTES):
    request = Request(url, data=payload, headers=headers or {}, method=method)
    try:
        with build_opener(SafeRedirect()).open(request, timeout=30) as response:
            data = response.read(limit + 1)
            if len(data) > limit:
                raise ValueError("응답/이미지가 허용 크기를 초과했습니다.")
            return data
    except HTTPError as error:
        raise RuntimeError(f"HTTP 요청 실패: {error.code}") from None
    except (URLError, TimeoutError, OSError):
        # Never print bearer tokens, signed upload/download URLs or remote response bodies.
        raise RuntimeError("네트워크 요청 실패/시간 초과. 전송 기록을 확인하세요.") from None


def github(method, path, payload=None):
    headers = {"Authorization": "Bearer " + os.environ["GITHUB_TOKEN"],
               "Accept": "application/vnd.github+json", "Content-Type": "application/json",
               "X-GitHub-Api-Version": "2022-11-28"}
    data = None if payload is None else json.dumps(payload, ensure_ascii=False).encode()
    return json.loads(request_bytes("https://api.github.com" + path, headers, data, method))


def slack(method, payload):
    headers = {"Authorization": "Bearer " + os.environ["SLACK_BOT_TOKEN"],
               "Content-Type": "application/json; charset=utf-8"}
    url = "https://slack.com/api/" + method
    if method == "chat.getPermalink":
        result = json.loads(request_bytes(url + "?" + urlencode(payload), headers, method="GET"))
    else:
        result = json.loads(request_bytes(url, headers,
                                         json.dumps(payload, ensure_ascii=False).encode(), "POST"))
    if not result.get("ok"):
        # Slack error identifiers are safe; don't echo the response payload.
        error = re.sub(r"[^a-zA-Z0-9_]", "", str(result.get("error", "unknown_error")))
        definite_rejections = {
            "invalid_auth", "not_authed", "token_revoked", "account_inactive",
            "missing_scope", "not_allowed_token_type", "channel_not_found",
            "not_in_channel", "is_archived", "restricted_action", "invalid_arguments",
            "invalid_arg_name", "invalid_array_arg", "no_text", "msg_too_long",
            "file_not_found", "files_not_found", "too_many_files", "ratelimited",
        }
        failure = SlackRejected if error in definite_rejections else RuntimeError
        raise failure(f"Slack {method} 실패: {error}")
    return result


def download_image(url, repository, pr):
    kind, ref, path = image_source(url, repository, (pr["head"]["ref"], pr["base"]["ref"]))
    if kind == "repository":
        if ref == pr["head"]["ref"]:
            ref = pr["head"].get("sha", ref)
        elif ref == pr["base"]["ref"]:
            ref = pr["merge_commit_sha"]
        api = f"https://api.github.com/repos/{repository}/contents/{quote(path, safe='/')}?{urlencode({'ref': ref})}"
        data = request_bytes(api, {"Authorization": "Bearer " + os.environ["GITHUB_TOKEN"],
                                   "Accept": "application/vnd.github.raw+json"})
        name = path.rsplit("/", 1)[-1]
    else:
        data = request_bytes(path)
        name = urlsplit(path).path.rsplit("/", 1)[-1]
    mime = image_type(data)
    suffix = {"image/png": ".png", "image/jpeg": ".jpg", "image/gif": ".gif", "image/webp": ".webp"}[mime]
    name = re.sub(r"[^a-zA-Z0-9가-힣._-]", "_", name)[:100]
    if not IMAGE_EXTENSION.search(name):
        name += suffix
    return name, mime, data


def screenshot_context(body):
    """Keep author-provided capture dates/states and table headings in the thread."""
    context = section(body, "스크린샷")
    context = re.sub(r"!\[([^\]]*)\]\([^)]*\)", r"[이미지: \1]", context)
    context = re.sub(r"<img\b[^>]*>", "[이미지]", context, flags=re.I)
    context = re.sub(r"https://[^\s<>\"')]+", "[PR 원본 링크]", context)
    return slack_text(context[:1500])


def comment_body(state):
    lines = [MARKER, "## iOS 머지 알림 전송 기록", "",
             f"- 상태: {state['phase']}", f"- 대상: `{state['channel']}`"]
    if state.get("message_url"):
        lines.append(f"- [Slack 스레드]({state['message_url']})")
    for index, group in enumerate(state.get("groups", []), 1):
        lines.append(f"- 스크린샷 묶음 {index}: {group['status']} ({len(group['files'])}장)")
        for failure in group.get("failed_images", []):
            lines.append(f"- 이미지 첨부 미완료 · 화면 {failure['number']}: {failure['reason']} — {failure['url']}")
    if state.get("error"):
        lines.append("- 오류: " + state["error"])
    if state.get("warnings"):
        lines.extend("- 확인 필요: " + warning for warning in state["warnings"])
    lines += ["", "상세 구현 명세와 캡처 상태는 PR 본문이 기준입니다.", "",
              "```json", json.dumps(state, ensure_ascii=False, sort_keys=True), "```"]
    return "\n".join(lines)


def notify(pr, github, slack, channel, repository=None):
    if not eligible_pr(pr):
        print("미머지/develop 외/Dependabot PR이므로 공유하지 않습니다.")
        return None
    repository = repository or os.environ["GITHUB_REPOSITORY"]
    text = message_text(pr)
    urls = image_urls(pr.get("body") or "")
    fingerprint = hashlib.sha256((text + json.dumps(urls)).encode()).hexdigest()
    comments_path = f"/repos/{repository}/issues/{pr['number']}/comments"
    comment_id, state = None, None
    page = 1
    while True:
        comments = github("GET", f"{comments_path}?per_page=100&page={page}")
        for comment in comments:
            if comment.get("user", {}).get("login") == "github-actions[bot]" and (comment.get("body") or "").startswith(MARKER):
                match = re.search(r"```json\n(.*?)\n```", comment["body"], re.S)
                if match:
                    candidate = json.loads(match[1])
                    if candidate.get("repository") == repository and candidate.get("pr") == pr["number"] and candidate.get("channel") == channel:
                        comment_id, state = comment["id"], candidate
        if len(comments) < 100:
            break
        page += 1
    if state and state["phase"] in {"complete", "complete_with_warnings"}:
        print("이미 전송 완료된 PR입니다: " + state["message_url"])
        return state
    if state and state["fingerprint"] != fingerprint:
        raise ValueError("부분 전송 이후 PR 요약/이미지가 바뀌었습니다. 자동화 댓글과 Slack을 확인하고 복구하세요.")
    state = state or {"repository": repository, "pr": pr["number"], "channel": channel,
                      "fingerprint": fingerprint, "phase": "ready", "groups": [], "warnings": []}

    def save():
        nonlocal comment_id
        path = f"/repos/{repository}/issues/comments/{comment_id}" if comment_id else comments_path
        result = github("PATCH" if comment_id else "POST", path, {"body": comment_body(state)})
        comment_id = result["id"]

    if state["phase"] == "posting" or any(g["status"] == "sharing" for g in state["groups"]):
        raise ValueError("이전 Slack 전송 결과가 불확실합니다. 자동 재전송하지 않습니다. Slack과 자동화 댓글을 확인하세요.")
    state.pop("error", None)
    # Validate sources before posting; download sequentially to bound memory to one image.
    source_errors = {}
    for number, url in enumerate(urls, 1):
        try:
            image_source(url, repository, (pr["head"]["ref"], pr["base"]["ref"]))
        except ValueError as error:
            source_errors[number] = str(error)
    try:
        if not state.get("thread_ts"):
            state["phase"] = "posting"
            save()
            try:
                response = slack("chat.postMessage", {"channel": channel, "text": text,
                                                      "unfurl_links": False, "unfurl_media": False})
            except SlackRejected:
                state["phase"] = "ready"  # A definite Slack rejection did not create a message.
                raise
            state["thread_ts"] = response["ts"]
            state["phase"] = "images"
            # Slack permalink retrieval is read-only; it is safe to retry if it fails.
            save()
        if not state.get("message_url"):
            state["message_url"] = slack("chat.getPermalink", {"channel": channel,
                                                               "message_ts": state["thread_ts"]})["permalink"]
            save()
        trees = re.findall(r"https://github\.com/[^\s)<>]+/tree/[^\s)<>]+", pr.get("body") or "")
        if trees:
            state["warnings"] = ["폴더 링크의 이미지는 자동 탐색하지 않습니다. 필요한 각 원본 이미지 링크를 PR에 첨부하세요."]
        for offset in range(0, len(urls), 10):
            index = offset // 10
            if index < len(state["groups"]) and state["groups"][index]["status"] == "complete":
                continue
            if index == len(state["groups"]):
                state["groups"].append({"status": "uploading", "files": []})
            group = state["groups"][index]
            if group["status"] != "prepared":
                group["files"] = []
                group["failed_images"] = []
                for number, url in enumerate(urls[offset:offset + 10], offset + 1):
                    try:
                        if number in source_errors:
                            raise ValueError(source_errors[number])
                        name, mime, data = download_image(url, repository, pr)
                    except (ValueError, RuntimeError) as error:
                        group["failed_images"].append({"number": number, "url": url,
                                                       "reason": str(error)[:500]})
                        continue
                    upload = slack("files.getUploadURLExternal", {"filename": name, "length": len(data),
                                                                  "alt_txt": f"PR #{pr['number']} 화면 {number}: {name}"})
                    parsed = urlsplit(upload["upload_url"])
                    if parsed.scheme != "https" or parsed.hostname != "files.slack.com" or parsed.username:
                        raise ValueError("Slack 업로드 URL 검증 실패")
                    request_bytes(upload["upload_url"], {"Content-Type": mime}, data, "POST")
                    group["files"].append({"id": upload["file_id"], "title": f"{number}. {name}"})
                group["status"] = "prepared"
                save()
            group["status"] = "sharing"
            save()
            caption = (
                f"PR #{pr['number']} 스크린샷 {offset + 1}–{min(offset + 10, len(urls))}\n"
                + "\n".join(item["title"] for item in group["files"])
                + "\n" + "\n".join(
                    f"이미지 첨부 미완료 · 화면 {item['number']}: {slack_text(item['reason'])}\n"
                    + slack_text(item["url"]) for item in group.get("failed_images", [])
                )
                + "\n\n" + screenshot_context(pr.get("body") or "")
                + "\n화면별 원본·전체 촬영 조건·검증 범위: " + pr["html_url"]
            )
            try:
                if group["files"]:
                    slack("files.completeUploadExternal", {"files": group["files"], "channel_id": channel,
                                                            "thread_ts": state["thread_ts"],
                                                            "initial_comment": caption})
                else:
                    slack("chat.postMessage", {"channel": channel, "thread_ts": state["thread_ts"],
                                               "text": caption, "unfurl_links": False, "unfurl_media": False})
            except SlackRejected:
                group["status"] = "prepared"
                raise
            group["status"] = "complete"
            save()
        incomplete_images = any(group.get("failed_images") for group in state["groups"])
        state["phase"] = "complete_with_warnings" if incomplete_images or state["warnings"] else "complete"
        save()
        print("iOS 머지 알림 " + state["phase"] + ": " + state["message_url"])
        return state
    except Exception as error:
        state["error"] = str(error)[:500]
        save()
        raise


def main():
    for name in ("GITHUB_TOKEN", "GITHUB_REPOSITORY", "PR_NUMBER", "SLACK_BOT_TOKEN", "SLACK_CHANNEL_ID"):
        if not os.environ.get(name):
            raise ValueError(f"{name} 설정이 없습니다. 저장소 Actions 시크릿/변수를 등록하세요.")
    repository = os.environ["GITHUB_REPOSITORY"]
    number = os.environ["PR_NUMBER"]
    if not re.fullmatch(r"[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+", repository) or not number.isdigit():
        raise ValueError("잘못된 저장소 또는 PR 번호입니다.")
    channel = os.environ["SLACK_CHANNEL_ID"]
    if not re.fullmatch(r"C[A-Z0-9]+", channel):
        raise ValueError("SLACK_CHANNEL_ID에는 #ios-update의 채널 ID를 지정하세요.")
    pr = github("GET", f"/repos/{repository}/pulls/{number}")
    notify(pr, github, slack, channel, repository)


if __name__ == "__main__":
    try:
        main()
    except (ValueError, RuntimeError) as error:
        print(f"iOS 알림 실패: {error}", file=sys.stderr)
        sys.exit(1)
