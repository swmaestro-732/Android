# Remote Config 업데이트 안내 (latest_version_code / latest_version_name)

소프트(선택) 업데이트 안내를 제어하는 Firebase Remote Config 값과, 이를 자동 게시하는 방법.

## 동작 원리

- Firebase 프로젝트: **`courmy-qa732`**
- Remote Config 키 2개
  - `latest_version_code` (Number) — 최신 출시 버전코드
  - `latest_version_name` (String) — 최신 버전명 (예: `1.2.1`)
- 앱(`RemoteConfigAppVersionRepository`)이 이 값을 받아
  **`latest_version_code > 설치본 versionCode`** 이면 업데이트 안내를 띄운다.
- fetch 주기 1시간(`FETCH_INTERVAL_SECONDS = 3600`). 게시 후 기존 사용자에게 최대 ~1시간 내 반영.
- 강제 업데이트(차단)는 이 값이 아니라 서버 HTTP 426(`UpdateRequiredInterceptor`)로 별개 처리된다.

## ⚠️ 타이밍

`latest_version_code` 에 넣을 값은 **이번 릴리스의 새 versionCode**이고,
이 값은 CD(`fastlane deploy`)가 "Play 최신 +1"로 배포 시점에 산정한다(`versionName = 1.2.(code-12)`).

**새 버전이 Play 에서 실제로 라이브된 뒤에 Remote Config 를 올려야 한다.**
아직 심사 중인 버전으로 올리면 사용자가 받을 수 없는 버전으로 안내가 떠 버린다.
그래서 이 작업은 `release` 배포(`cd.yml`)에 묶지 않고 **별도 수동 트리거**로 분리한다.

### 권장 순서

1. PR → `develop` 머지
2. `develop` → `release` 머지 → `cd.yml`이 Play 프로덕션에 업로드 + 심사 제출
3. Play 심사 통과 → **버전 라이브** 확인
4. 아래 방법으로 Remote Config 게시 → 기존 사용자에게 업데이트 안내 노출

## 게시 방법

### A. GitHub Actions (자동화, 권장)

Actions → **Remote Config bump** → Run workflow.

- `version_code`: 라이브된 versionCode 를 **직접 입력 권장**(비우면 Play 프로덕션 최고값 사용 — 심사 중 릴리스가 섞일 수 있음).
- `version_name`: 비우면 versionCode 로 산정(`1.2.(code-12)`).

내부적으로 `fastlane android bump_remote_config` 가 Remote Config REST
(GET 템플릿+ETag → 두 파라미터만 교체 → `If-Match` PUT)로 게시한다. 다른 파라미터·조건부 값은 보존.

#### 필요한 Secret (최초 1회)

| Secret | 용도 | 필수 |
| --- | --- | --- |
| `FIREBASE_SERVICE_ACCOUNT_JSON` | Remote Config 관리자 권한 Firebase 서비스계정 JSON 원문 | ✅ |
| `PLAY_SERVICE_ACCOUNT_JSON` | `version_code` 미입력 시 Play 라이브값 조회 | 선택 |

`FIREBASE_SERVICE_ACCOUNT_JSON`: Firebase 콘솔 → 프로젝트 설정 → 서비스 계정 →
새 비공개 키 생성(해당 SA 에 Remote Config 관리자/`firebase.remoteconfig.update` 권한) → JSON 원문을
저장소 Settings → Secrets 에 등록.

### B. 로컬 Fastlane

```bash
export FIREBASE_JSON_KEY_FILE=/path/to/firebase-sa.json
export FIREBASE_PROJECT_ID=courmy-qa732
bundle exec fastlane android bump_remote_config version_code:13 version_name:1.2.1
```

### C. Firebase 콘솔 (수동)

console.firebase.google.com → `courmy-qa732` → Remote Config →
`latest_version_code`·`latest_version_name` 수정 → **변경사항 게시**.

## 관련 파일

- `fastlane/Fastfile` — `bump_remote_config` lane + `publish_remote_config` 헬퍼
- `.github/workflows/remote-config.yml` — 수동 trigger 워크플로
- `main/data/.../update/RemoteConfigAppVersionRepository.kt` — 앱 소비 로직
