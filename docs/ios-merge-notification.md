# develop 머지 후 iOS Slack 공유

`iOS merge notification`은 PR이 `develop`에 실제 머지됐을 때 `#ios-update`에 알린다.
PR 생성·수정·미머지 종료 및 Dependabot PR은 제외한다. develop 머지는 스토어 배포가 아니다.

## 머지 전 작성할 내용

- PR 본문의 `## iOS 공유사항`: 후속 AI 구현용 상세 명세. 문구·값·조건·API·예외 처리·완료 확인 항목·코드 근거를 유지한다.
- `### Slack 알림 요약`: 핵심 변경 3~5개, iOS 확인사항, 중요한 미구현·미검증 사항. 없으면 기존 PR의 변경 요약을 사용한다.
- 스크린샷: 변경된 모든 화면·주요 상태의 원본 이미지와 이름·상태·촬영일·검증 한계. 이미지가 없으면 미촬영 화면과 이유를 명시한다.
- 캡처는 Markdown 이미지나 HTML `img`, 직접 이미지 링크로 첨부한다. 디렉터리 링크만 넣어 이미지 목록 확인을 생략하지 않는다.

자동화는 PR 내용을 전송하며 AI로 코드를 분석하거나 캡처를 생성하지 않는다.
부정확한 PR 내용을 서버 계약으로 확정하지 않는다. 머지 후 전송은 CI 통과를 대체하지 않는다.

## 최초 연결

1. Slack 앱 관리 화면에서 팀 워크스페이스에 앱을 만들거나 기존 알림용 앱을 사용한다.
2. OAuth & Permissions의 Bot Token Scopes에 `chat:write`, `files:write`를 추가하고 워크스페이스에 설치한다. 기존 앱의 권한을 바꾸면 재설치한다.
3. Slack `#ios-update` 채널에 그 앱을 초대한다. 개인 ChatGPT Slack 플러그인 연결과 별도다.
4. GitHub Android 저장소 Settings → Secrets and variables → Actions → Secrets에 Bot User OAuth Token을 `SLACK_BOT_TOKEN`으로 저장한다. 토큰을 코드·PR·채팅에 넣지 않는다.
5. Variables에 `SLACK_IOS_CHANNEL_ID`를 설정한다. 현재 대상 채널 ID는 `C0CD2JYCD47`이다.
6. 워크플로와 스크립트를 기본 브랜치인 develop에 머지한다.

Slack 권한: [메시지·파일 작업](https://docs.slack.dev/messaging/working-with-files/).
여러 이미지의 업로드를 준비한 뒤 [files.completeUploadExternal](https://docs.slack.dev/reference/methods/files.completeUploadExternal/)의 파일 배열로 묶어 공유한다.

## 전송 결과·재실행

- PR의 `courmy-ios-merge-notice` 상태 댓글에서 Slack 스레드 링크와 이미지별 상태를 확인한다.
- 요약은 채널에 한 번, 스크린샷은 같은 스레드에 최대 10장씩 묶어 보낸다. 원본 파일과 이미지 순서를 유지한다.
- 다운로드·형식 검증에 실패한 이미지는 건너뛰고 나머지 이미지를 보낸다. 실패한 화면 번호·원본 링크·`이미지 첨부 미완료` 사유는 묶음 설명과 PR 상태 댓글에 남긴다. 묶음 전체가 실패하면 같은 스레드의 텍스트 메시지로 링크와 사유를 보낸다.
- 이미지 누락이나 폴더 링크 경고가 있으면 상태는 `complete_with_warnings`다. 알림 전달은 끝났지만 이미지 첨부 완료는 아니며, 재실행해도 중복 전송하지 않는다. 원본 수정 후 누락 이미지 추가 공유는 별도 복구 작업으로 수행한다.
- Actions의 해당 실행이 실패하면 로그의 오류 원인을 먼저 해결한다. 토큰·채널 미설정이나 Slack 오류를 성공으로 처리하지 않는다.
- 실패한 실행은 **Re-run failed jobs**로 재실행한다. 또는 Actions → iOS merge notification → Run workflow에서 develop과 이미 머지된 PR 번호를 선택한다. 아직 열려 있거나 다른 브랜치에 머지된 PR은 전송하지 않는다.
- 저장된 전송 기록을 이어서 사용한다. Slack 전송 직후 상태 기록 전에 연결이 끊기는 등 결과가 불명확하면 중복을 피하려고 중단한다. Slack에 실제 등록됐는지 확인한 뒤 상태를 복구해야 하며 댓글을 지우고 무조건 재실행하지 않는다.
- 전송 상태 댓글은 자동화용이다. 요약 수정은 원본 PR 본문에 하되 이미 완료한 알림을 다시 보내는 기능으로 사용하지 않는다.

## 구현·검증

- 실행 코드: `scripts/notify_ios_merge.py` (Python 표준 라이브러리).
- 워크플로: `.github/workflows/ios-merge-notification.yml`.
- `pull_request_target`에서는 PR head 코드를 실행하지 않고 이미 머지된 develop의 스크립트만 실행한다. PR 본문은 명령으로 평가하지 않는다.
- Actions token은 repository content 읽기와 PR 상태 댓글 쓰기에 사용한다. Slack token은 Slack API에만 사용한다.
- 이미지 URL 출처는 첫 메시지 전에 검증한다. 실제 다운로드는 이미지 하나씩 진행해 메모리 사용량을 제한하며, 접근·형식 실패는 링크 안내로 처리한다. Slack 업로드·공유 API 실패는 정상 완료로 처리하지 않는다.
- 로컬 검증: `python3 -m unittest discover -s scripts -p 'test_notify_ios_merge.py' -v`.
- 실전송 검증: 이미 머지된 비 Dependabot PR 하나를 지정해 실행하고 요약·이미지 묶음·상태 댓글을 확인한다. 같은 PR 재실행 시 중복 메시지가 생기지 않아야 한다.
