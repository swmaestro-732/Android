# Courmy Android

장소를 탐색하고 코스를 만들고 저장하며 여행 계획과 방문 기록을 관리하는 Kotlin·Jetpack Compose 앱입니다. `com.chillsam.courmy` 패키지와 멀티모듈 Clean Architecture·MVI 구조를 사용합니다.

## 문서와 작업 추적

- [Android 작업 현황·담당자 확인 사항](https://github.com/swmaestro-732/.ai/blob/main/android/work-status.md): 작업별 PR/커밋, 미머지·로컬 상태, 백엔드·iOS 확인 항목, 실제 화면 캡처.
- [열린 PR](https://github.com/swmaestro-732/Android/pulls) · [머지된 PR](https://github.com/swmaestro-732/Android/pulls?q=is%3Apr+is%3Amerged): 최신 리뷰·통합 상태. PR 등록이나 develop 머지는 배포 완료를 뜻하지 않습니다.
- [Android 위키](https://github.com/swmaestro-732/.ai/blob/main/android/README.md) · [API 명세](https://github.com/swmaestro-732/.ai/blob/main/api-spec.md) · [카테고리/테마](https://github.com/swmaestro-732/.ai/blob/main/taxonomy.md).
- [Courmy UI Flow](https://www.notion.so/3f6b448bdf9d81bea9f1f35597ec3d54): 확인한 화면과 상태. PR 첨부 캡처의 촬영 날짜·예시 데이터 여부도 확인합니다.
- [작업 지침](AGENTS.md): 위키 확인·검증·동기화 기준.

## 구현 범위

2026-10-31 develop `b87bc5f` 기준으로 로그인·온보딩, 홈 추천, 지도·장소 검색, 장소/코스 상세, 코스 작성·임시저장·복제, 저장함, 계획, 따라가기·후기, 프로필·팔로우, 설정·앱 업데이트 흐름을 포함합니다. 서버 배포와 실제 동작 검증은 기능별 PR에서 확인합니다.

알림함·FCM·알림 설정, 분석 이벤트 보완, 추천 필터/UI 정리, 동행 선택 등 진행 중인 변경은 [작업 현황](https://github.com/swmaestro-732/.ai/blob/main/android/work-status.md)에 별도로 기록합니다. 해당 PR의 코드를 develop에 포함된 기능으로 간주하지 않습니다.

## 기술 스택과 구조

Kotlin·JDK 17, Compose·Material3·Navigation3, Hilt/KSP, Coroutines, Retrofit/OkHttp/kotlinx.serialization, Coil, Firebase 및 Amplitude를 사용합니다. 정확한 버전과 SDK 수준은 [버전 카탈로그](gradle/libs.versions.toml)와 [앱 Gradle 설정](app/build.gradle.kts)이 기준입니다.

```text
presentation → domain → entity
data         → domain → entity
```

presentation과 data는 직접 의존하지 않으며 app에서 구현을 조립합니다. entity/domain은 순수 Kotlin/JVM으로 유지하고, DTO→VO 변환은 data에서 수행합니다. 화면 입력은 Intent, 상태 변경은 ReducerEvent와 reduce(), 이동은 Navigation3 공통 계약과 AppRouteRegistry를 사용합니다.

| 모듈 | 역할 |
|---|---|
| `app` | 앱 진입점, SDK 초기화, DI 조립, 빌드·서명 설정 |
| `common/{entity,domain,data,presentation}` | 공통 모델·네트워크·인증 기반·네비게이션 계약·디자인 시스템·MVI·계측 |
| `main/{entity,domain,data,presentation}` | 로그인·가입, 홈·지도·검색·저장함·프로필·설정 및 네비게이션 호스트 |
| `course/{entity,domain,data,presentation}` | 코스/장소 상세, 코스 작성·초안·계획·따라가기·후기 |
| `tti` | 초기 상호작용 시간 계측 |
| `baselineprofile` | Baseline Profile 생성과 시작 성능 벤치마크 |

색상·타이포는 DesignSystemThemeImpl과 공통 컴포넌트를 사용합니다. 장소 카테고리 10종과 코스 태그 12종은 별도 체계입니다. 아키텍처 상세는 [Android 위키](https://github.com/swmaestro-732/.ai/blob/main/android/architecture.md)를 참고합니다.

## 로컬 설정과 실행

JDK 17과 [버전 카탈로그](gradle/libs.versions.toml)에 지정된 Android SDK가 필요합니다. `local.properties`에 SDK 경로와 필요한 앱 설정을 준비합니다. API 주소 설정은 [common/data Gradle](common/data/build.gradle.kts), SDK 설정은 [app Gradle](app/build.gradle.kts)을 확인합니다.

| 설정 | 용도 |
|---|---|
| `API_BASE_URL` | Courmy API 주소 |
| `NAVER_MAP_CLIENT_ID` | 지도 인증 |
| `KAKAO_NATIVE_APP_KEY` | 카카오 로그인 |
| `NAVER_LOGIN_CLIENT_ID`, `NAVER_LOGIN_CLIENT_SECRET` | 네이버 로그인 |
| `GOOGLE_SERVER_CLIENT_ID` | Google 로그인용 웹 OAuth 클라이언트 ID |
| `AMPLITUDE_API_KEY` | 제품 분석 |
| `FACEBOOK_APP_ID`, `FACEBOOK_CLIENT_TOKEN` | 광고 전환 SDK |

Firebase 설정 파일 `app/google-services.json`은 프로젝트 담당자에게 받아 로컬에 둡니다. 파일이 없어도 debug 빌드는 가능하지만 Firebase 기능 검증 환경으로 사용할 수는 없습니다. debug Firebase Analytics/Crashlytics 수집은 [debug Manifest](app/src/debug/AndroidManifest.xml)에서 비활성화합니다. Amplitude의 debug 차단 변경은 PR #120 상태를 확인합니다.

`local.properties`, `google-services.json`, keystore·서명 비밀번호·토큰은 커밋하지 않습니다. release APK/AAB 패키징에는 Firebase 설정, 카카오·네이버 지도 키와 완전한 release 서명 설정이 필요하며 누락 시 빌드를 실패시킵니다.

```bash
./gradlew :app:compileDebugKotlin
./gradlew test
./gradlew :app:assembleDebug :app:lintDebug
./gradlew detekt
```

변경 범위의 좁은 task를 먼저 실행하고 PR 전에 전체 영향을 확인합니다. Gradle·Manifest·리소스·DI 변경은 APK 조립까지 검증합니다. 검사 통과를 위해 테스트를 삭제하거나 lint/detekt baseline을 늘리지 않습니다. 포맷·시크릿 검사는 [pre-commit 설정](.pre-commit-config.yaml)을 따릅니다.

## CI/CD

[CI](.github/workflows/ci.yml)는 PR의 빌드·테스트·정적 분석·보안 검사를 수행합니다. 정확한 실행 조건은 워크플로 파일이 기준입니다.

[CD](.github/workflows/cd.yml)는 **release push 또는 수동 실행에서 Fastlane으로 Google Play 프로덕션 심사를 제출**하고 APK/AAB를 GitHub Pre-release에도 게시합니다. 내부 테스트 파일만 만드는 흐름이 아닙니다. Play 심사 제출과 실제 게시 완료는 구분합니다.

[Fastlane](fastlane/Fastfile)은 Play 최신 versionCode를 조회해 다음 번호로 빌드하며 versionName 패치와 출시 노트를 처리합니다. 현재 게시 버전은 [Play Console 상태 위키](https://github.com/swmaestro-732/.ai/blob/main/android/play-console-state.md)와 실제 Console에서 확인합니다. Firebase Remote Config의 최신 버전 값과 Play 게시 버전은 자동 동기화된다고 가정하지 않습니다.

## 협업

기능 변경은 develop 대상 PR로 검토합니다. PR에는 구현·검증 범위와 서버/iOS 영향, 실제 화면 근거를 남기고 [공통 컨벤션](https://github.com/swmaestro-732/.ai/blob/main/conventions.md)의 iOS 공유 절차를 따릅니다. 위키는 별도 저장소이므로 같은 작업에서 갱신하고 PR에 위키 커밋을 연결합니다. 배포는 release 흐름과 필요한 Console 검토를 따릅니다.
