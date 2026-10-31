"""Offline checks for merge eligibility, PR content, and Slack delivery safety."""

import copy
import json
import os
import unittest
from unittest.mock import patch
from urllib.parse import parse_qs, urlsplit
from urllib.request import Request

import notify_ios_merge as notice


def pull_request():
    return {
        "number": 122,
        "merged": True,
        "base": {"ref": "develop"},
        "head": {"ref": "feat/SCRUM-734-ios-share", "sha": "a" * 40},
        "user": {"login": "developer"},
        "title": "SCRUM-734 feat: 알림 설정",
        "html_url": "https://github.com/swmaestro-732/Android/pull/122",
        "merge_commit_sha": "b" * 40,
        "body": (
            "## 📝 변경 요약\n전체 알림 스위치를 추가합니다.\n\n"
            "## iOS 공유사항\n기기별 설정을 유지합니다.\n\n"
            "### Slack 알림 요약\n- 기본 ON, OFF는 재실행 후에도 유지\n"
            "- iOS: 권한 거절 후에도 앱 이용 가능\n\n"
            "### 상세 명세\n구현을 위한 긴 내부 설명\n\n"
            "## 📸 스크린샷\n없음\n"
        ),
    }


class MergeNoticeContentTest(unittest.TestCase):
    def test_slack_read_methods_encode_query_and_keep_token_in_header(self):
        cases = [
            ("chat.getPermalink", {"channel": "C123", "message_ts": "123.456"}),
        ]
        for method, payload in cases:
            with self.subTest(method=method), patch.dict(os.environ, {"SLACK_BOT_TOKEN": "test-token"}), \
                    patch.object(notice, "request_bytes", return_value=b'{"ok":true}') as request:
                notice.slack(method, payload)
                args, kwargs = request.call_args
                self.assertEqual(kwargs, {"method": "GET"})
                self.assertEqual(parse_qs(urlsplit(args[0]).query),
                                 {key: [str(value)] for key, value in payload.items()})
                self.assertEqual(args[1]["Authorization"], "Bearer test-token")
                self.assertNotIn("test-token", args[0])

    def test_slack_write_methods_keep_json_post(self):
        with patch.dict(os.environ, {"SLACK_BOT_TOKEN": "test-token"}), \
                patch.object(notice, "request_bytes", return_value=b'{"ok":true}') as request:
            payload = {"channel": "C123", "text": "알림"}
            notice.slack("chat.postMessage", payload)
            args = request.call_args.args
            self.assertEqual(args[3], "POST")
            self.assertEqual(json.loads(args[2]), payload)

    def test_only_merged_develop_non_dependabot_pr_is_eligible(self):
        pr = pull_request()
        self.assertTrue(notice.eligible_pr(pr))
        cases = [
            {"merged": False},
            {"base": {"ref": "release"}},
            {"base": {"ref": "main"}},
            {"user": {"login": "dependabot[bot]"}},
            {"head": {"ref": "dependabot/gradle/compose"}},
        ]
        for changes in cases:
            with self.subTest(changes=changes):
                candidate = copy.deepcopy(pr)
                candidate.update(changes)
                self.assertFalse(notice.eligible_pr(candidate))

    def test_summary_prefers_author_summary_and_excludes_later_sections(self):
        pr = pull_request()
        text = notice.message_text(pr)
        self.assertIn("기본 ON", text)
        self.assertIn("권한 거절", text)
        self.assertIn(pr["html_url"], text)
        self.assertNotIn("긴 내부 설명", text)
        self.assertNotIn("전체 알림 스위치를 추가합니다.", text)
        self.assertLessEqual(len(text), 3500)

    def test_emoji_heading_fallback_and_comment_only_spec(self):
        pr = pull_request()
        pr["body"] = (
            "## 📝 변경 요약\n변경 전후의 짧은 설명\n"
            "## iOS 공유사항\n서버 배포 확인 필요\n"
        )
        self.assertIn("변경 전후의 짧은 설명", notice.message_text(pr))
        for body in ["## 변경 요약\n요약만 있음", "## iOS 공유사항\n<!-- 작성하세요 -->"]:
            with self.subTest(body=body):
                pr["body"] = body
                with self.assertRaises(ValueError):
                    notice.message_text(pr)

    def test_untrusted_text_cannot_emit_slack_mentions(self):
        pr = pull_request()
        pr["title"] = "<!channel> <@U123> & 변경"
        pr["body"] = "## iOS 공유사항\n실제 명세\n### Slack 알림 요약\n<!here> <@U456>"
        text = notice.message_text(pr)
        for mention in ["<!channel>", "<!here>", "<@U123>", "<@U456>"]:
            self.assertNotIn(mention, text)

    def test_screenshots_include_html_details_and_direct_links_once(self):
        a = "https://raw.githubusercontent.com/swmaestro-732/Android/abc/docs/a.png"
        b = "https://github.com/swmaestro-732/Android/blob/abc/docs/b.jpg"
        c = "https://github.com/user-attachments/assets/123"
        body = f'![기본]({a})\n<details><img src="{b}" /></details>\n[원본]({a})\n<img src="{c}" />'
        urls = notice.image_urls(body)
        self.assertEqual(set(urls), {a, b, c})
        self.assertEqual(len(urls), 3)
        self.assertEqual(notice.image_urls(f"[최신 화면]({b})"), [b])

    def test_only_approved_sources_can_receive_image_requests(self):
        repo = "swmaestro-732/Android"
        approved = [
            ("https://raw.githubusercontent.com/swmaestro-732/Android/main/docs/a.png", "repository"),
            ("https://github.com/swmaestro-732/Android/blob/main/docs/a.png", "repository"),
            ("https://github.com/user-attachments/assets/123", "public"),
        ]
        for url, kind in approved:
            with self.subTest(url=url):
                self.assertEqual(notice.image_source(url, repo)[0], kind)
        rejected = [
            "http://raw.githubusercontent.com/swmaestro-732/Android/main/a.png",
            "https://raw.githubusercontent.com/another/private/main/a.png",
            "https://github.com.evil.example/swmaestro-732/Android/blob/main/a.png",
            "https://github.com@evil.example/a.png",
            "https://secret@github.com/swmaestro-732/Android/blob/main/a.png",
            "https://127.0.0.1/a.png",
            "file:///tmp/a.png",
        ]
        for url in rejected:
            with self.subTest(url=url):
                with self.assertRaises(ValueError):
                    notice.image_source(url, repo)

    def test_image_content_must_match_a_supported_signature(self):
        self.assertEqual(notice.image_type(b"\x89PNG\r\n\x1a\n" + b"0" * 24), "image/png")
        self.assertEqual(notice.image_type(b"\xff\xd8\xff" + b"0" * 24), "image/jpeg")
        for data in [b"", b"<html>login required</html>", b"<svg>not a raster screenshot</svg>"]:
            with self.subTest(data=data):
                with self.assertRaises(ValueError):
                    notice.image_type(data)

    def test_branch_refs_do_not_allow_traversal(self):
        url = "https://raw.githubusercontent.com/swmaestro-732/Android/feat/change/../../other.png"
        with self.assertRaises(ValueError):
            notice.image_source(url, "swmaestro-732/Android", refs=("feat/change",))

    def test_redirect_never_forwards_authorization_or_uses_unknown_host(self):
        handler = notice.SafeRedirect()
        authenticated = Request("https://api.github.com/repos/swmaestro-732/Android/contents/a.png",
                                headers={"Authorization": "Bearer test-only"})
        public = Request("https://github.com/user-attachments/assets/123")
        cases = [
            (authenticated, "https://raw.githubusercontent.com/swmaestro-732/Android/main/a.png"),
            (public, "https://evil.example/a.png"),
            (public, "http://github.com/user-attachments/assets/123"),
        ]
        for request, target in cases:
            with self.subTest(target=target):
                with self.assertRaises(ValueError):
                    handler.redirect_request(request, None, 302, "Found", {}, target)

    def test_download_pins_deleted_head_branch_to_immutable_commit(self):
        pr = pull_request()
        url = f"https://raw.githubusercontent.com/swmaestro-732/Android/{pr['head']['ref']}/docs/a.png"
        png = b"\x89PNG\r\n\x1a\n" + b"0" * 24
        with patch.dict(os.environ, {"GITHUB_TOKEN": "test-only"}), \
                patch.object(notice, "request_bytes", return_value=png) as download:
            name, mime, data = notice.download_image(url, "swmaestro-732/Android", pr)
        parsed = urlsplit(download.call_args.args[0])
        self.assertEqual(parsed.hostname, "api.github.com")
        self.assertEqual(parsed.path, "/repos/swmaestro-732/Android/contents/docs/a.png")
        self.assertEqual(parse_qs(parsed.query), {"ref": [pr["head"]["sha"]]})
        self.assertEqual((name, mime, data), ("a.png", "image/png", png))

    def test_public_attachment_download_has_no_bearer_token(self):
        url = "https://github.com/user-attachments/assets/123"
        with patch.object(notice, "request_bytes", return_value=b"\x89PNG\r\n\x1a\n") as download:
            notice.download_image(url, "swmaestro-732/Android", pull_request())
        download.assert_called_once_with(url)


class MergeNoticeDeliveryTest(unittest.TestCase):
    def setUp(self):
        self.comments = []
        self.calls = []
        self.upload_count = 0
        self.fail_once = None
        self.failure = RuntimeError("network result unknown")

    def github(self, method, path, payload=None):
        if method == "GET":
            return copy.deepcopy(self.comments)
        self.comments = [{"id": 1, "body": payload["body"],
                          "user": {"login": "github-actions[bot]"}}]
        return {"id": 1}

    def slack(self, method, payload):
        self.calls.append((method, copy.deepcopy(payload)))
        if method == self.fail_once:
            self.fail_once = None
            raise self.failure
        if method == "chat.postMessage":
            return {"ts": "123.456"}
        if method == "chat.getPermalink":
            self.assertEqual(payload, {"channel": "C123", "message_ts": "123.456"})
            return {"permalink": "https://example.slack.com/archives/C123/p123456"}
        if method == "files.getUploadURLExternal":
            self.upload_count += 1
            return {"file_id": f"F{self.upload_count}",
                    "upload_url": f"https://files.slack.com/upload/{self.upload_count}"}
        if method == "files.completeUploadExternal":
            return {"ok": True}
        self.fail(f"Unexpected Slack method: {method}")

    def notify(self, pr):
        return notice.notify(pr, self.github, self.slack, "C123", "swmaestro-732/Android")

    def test_images_are_grouped_and_completed_rerun_sends_nothing(self):
        pr = pull_request()
        pr["body"] += "\n".join(
            f"![화면 {i}](https://raw.githubusercontent.com/swmaestro-732/Android/main/docs/{i}.png)"
            for i in range(11)
        )
        with patch.object(notice, "download_image", return_value=("screen.png", "image/png", b"image")), \
                patch.object(notice, "request_bytes", return_value=b"OK"):
            state = self.notify(pr)
            self.assertEqual(state["phase"], "complete")
            completions = [payload for method, payload in self.calls if method == "files.completeUploadExternal"]
            self.assertEqual([len(payload["files"]) for payload in completions], [10, 1])
            self.assertTrue(all(payload["thread_ts"] == "123.456" for payload in completions))
            self.assertEqual(len([method for method, _ in self.calls if method == "chat.postMessage"]), 1)
            calls_before = len(self.calls)
            self.notify(pr)
            self.assertEqual(len(self.calls), calls_before)

    def test_failed_images_keep_valid_upload_and_report_original_links(self):
        pr = pull_request()
        urls = ["https://example.com/blocked.png",
                "https://raw.githubusercontent.com/swmaestro-732/Android/main/docs/missing.png",
                "https://raw.githubusercontent.com/swmaestro-732/Android/main/docs/valid.png"]
        pr["body"] += "\n".join(f"![화면]({url})" for url in urls)
        with patch.object(notice, "download_image", side_effect=[RuntimeError("HTTP 요청 실패: 404"),
                          ("valid.png", "image/png", b"image")]) as download, \
                patch.object(notice, "request_bytes", return_value=b"OK"):
            state = self.notify(pr)
            self.assertEqual(download.call_count, 2)  # Invalid host is rejected before download.
            self.assertEqual(state["phase"], "complete_with_warnings")
            payload = next(payload for method, payload in self.calls if method == "files.completeUploadExternal")
            self.assertEqual(len(payload["files"]), 1)
            self.assertEqual(payload["files"][0]["title"], "3. valid.png")
            for url in urls[:2]:
                self.assertIn(url, payload["initial_comment"])
                self.assertIn(url, self.comments[0]["body"])
            self.assertIn("이미지 첨부 미완료", payload["initial_comment"])
            self.assertIn("404", payload["initial_comment"])
            before = len(self.calls)
            self.notify(pr)
            self.assertEqual(len(self.calls), before)

    def test_all_invalid_images_send_one_thread_fallback_without_reposting(self):
        pr = pull_request()
        url = "https://raw.githubusercontent.com/swmaestro-732/Android/main/docs/invalid.png"
        pr["body"] += f"![잘못된 이미지]({url})"
        with patch.object(notice, "download_image", side_effect=ValueError("원본 이미지 형식이 아닙니다.")):
            state = self.notify(pr)
            self.assertEqual(state["phase"], "complete_with_warnings")
            messages = [payload for method, payload in self.calls if method == "chat.postMessage"]
            self.assertEqual(len(messages), 2)
            self.assertEqual(messages[1]["thread_ts"], "123.456")
            self.assertIn(url, messages[1]["text"])
            self.assertIn("이미지 첨부 미완료", messages[1]["text"])
            self.assertNotIn("files.completeUploadExternal", [method for method, _ in self.calls])
            before = len(self.calls)
            self.notify(pr)
            self.assertEqual(len(self.calls), before)

    def test_uncertain_fallback_thread_send_is_not_retried(self):
        pr = pull_request()
        pr["body"] += "![화면](https://example.com/unsupported.png)"

        def uncertain_thread(method, payload):
            if method == "chat.postMessage" and "thread_ts" in payload:
                self.calls.append((method, copy.deepcopy(payload)))
                raise RuntimeError("network result unknown")
            return self.slack(method, payload)

        with self.assertRaises(RuntimeError):
            notice.notify(pr, self.github, uncertain_thread, "C123", "swmaestro-732/Android")
        before = len(self.calls)
        with self.assertRaises(ValueError):
            self.notify(pr)
        self.assertEqual(len(self.calls), before)
        self.assertIn('"status": "sharing"', self.comments[0]["body"])

    def test_uncertain_post_is_not_blindly_retried(self):
        self.fail_once = "chat.postMessage"
        with self.assertRaises(RuntimeError):
            self.notify(pull_request())
        with self.assertRaises(ValueError):
            self.notify(pull_request())
        self.assertEqual([method for method, _ in self.calls], ["chat.postMessage"])
        self.assertIn('"phase": "posting"', self.comments[0]["body"])

    def test_uncertain_group_completion_keeps_ids_and_stops_rerun(self):
        pr = pull_request()
        pr["body"] += "![화면](https://raw.githubusercontent.com/swmaestro-732/Android/main/docs/a.png)"
        self.fail_once = "files.completeUploadExternal"
        with patch.object(notice, "download_image", return_value=("screen.png", "image/png", b"image")), \
                patch.object(notice, "request_bytes", return_value=b"OK"):
            with self.assertRaises(RuntimeError):
                self.notify(pr)
            calls_before = len(self.calls)
            with self.assertRaises(ValueError):
                self.notify(pr)
            self.assertEqual(len(self.calls), calls_before)
        self.assertIn('"status": "sharing"', self.comments[0]["body"])
        self.assertIn('"id": "F1"', self.comments[0]["body"])

    def test_invalid_json_response_does_not_reset_post_to_ready(self):
        self.fail_once = "chat.postMessage"
        self.failure = json.JSONDecodeError("malformed response", "", 0)
        with self.assertRaises(json.JSONDecodeError):
            self.notify(pull_request())
        self.assertIn('"phase": "posting"', self.comments[0]["body"])
        with self.assertRaises(ValueError):
            self.notify(pull_request())
        self.assertEqual([method for method, _ in self.calls], ["chat.postMessage"])

    def test_unmerged_pr_does_not_call_external_services(self):
        pr = pull_request()
        pr["merged"] = False
        self.assertIsNone(self.notify(pr))
        self.assertEqual(self.calls, [])
        self.assertEqual(self.comments, [])


if __name__ == "__main__":
    unittest.main()
