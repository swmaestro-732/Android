# 칠삼이 Android (Courmy)

멀티모듈 클린아키텍처 + MVI 기반의 Android 클라이언트.

기능을 `entity / domain / data / presentation` 4-레이어 모듈로 분해하는 구조입니다. 현재는 레퍼런스 feature 없이 앱 셸(`main`)·공유 베이스(`common`)·성능 계측 모듈(`tti`, `baselineprofile`)만 있는 기본 골격이며, 새 기능은 4-레이어 모듈로 추가합니다.

## 1. 스택

- Kotlin 2.3.21 / JVM target 17 (AGP 9.2.1, KSP 2.3.6)
- Jetpack Compose (BOM 2026.04.01) / Material3 / Navigation3 1.1.1
- Hilt 2.59.2 (DI) · Coroutines 1.10.2 + Flow (비동기)
- Retrofit 3.0.0 / OkHttp 5.3.2 / kotlinx.serialization 1.11.0 (네트워크)
- Coil 3.4.0 (compose·gif·video) · Lottie 6.7.1 (이미지 / 애니메이션)
- JankStats 1.0.0-beta02 · Macrobenchmark 1.5.0-alpha05 · Baseline Profile (성능 / 관측)
- kotlinx-collections-immutable 0.4.0 (UIState 컬렉션)
- JUnit4 · MockK 1.14.5 (도메인 UseCase 단위 테스트) · UiAutomator (`:baselineprofile` 전용)

`compileSdk` / `targetSdk` = 37, `minSdk` = 24 (Baseline Profile 수집은 API 28+).

## 2. 로컬 개발

```bash
# 빌드 / 실행
./gradlew :app:assembleDebug          # 디버그 빌드
./gradlew :app:installDebug           # 연결된 디바이스/에뮬레이터에 설치
./gradlew test                        # 도메인 UseCase 단위 테스트

# Baseline Profile 재생성 (실 디바이스 또는 API 28+ 에뮬 필요)
./gradlew :app:generateReleaseBaselineProfile

# 매크로벤치마크 (콜드 스타트 측정)
./gradlew :baselineprofile:connectedBenchmarkAndroidTest
```

- `local.properties` 의 `sdk.dir` 만 실행 환경에 맞게 설정하면 그대로 빌드됩니다.
- 시스템 JDK 가 없으면 Android Studio 내장 JBR 사용:
  `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`
- API 키 / BASE_URL 은 `local.properties` 의 `API_KEY` · `API_BASE_URL` 로 주입 → `BuildConfig`.
  값이 없으면 데모 기본값으로 동작합니다.

## 3. 아키텍처

### 3-1. 모듈 구조

```
app                                              실행 가능한 Android Application
├─ common/{entity,domain,data,presentation}      공유 베이스 (MviViewModel, 테마/토큰, NetworkModule)
├─ main/{entity,domain,data,presentation}        내비게이션 셸 (AppNavHost, AppRouteRegistry, 딥링크, HomePage)
├─ tti                                           TTI(Time To Initial Display) 계측 라이브러리
└─ baselineprofile                               Macrobenchmark / Baseline Profile 수집 모듈
```

기본 진입 화면은 `main/presentation` 의 `HomePage` 하나이며, 새 기능을 추가하면 해당 feature 모듈과 라우트를 여기에 연결합니다.

### 3-2. 레이어 규칙

의존 방향은 단방향입니다: `presentation → domain → entity`, `data → domain → entity`.
`data` 와 `presentation` 은 서로를 모르고, 둘 다 `domain` 인터페이스만 봅니다.

- **`entity`** — 순수 Kotlin/JVM. VO / enum.
- **`domain`** — 순수 Kotlin/JVM. `Repository` 인터페이스, `UseCase`, `Page`, `ErrorType`.
- **`data`** — `com.android.library`. Retrofit `ApiService`, `DTO → toVO()`, `RepositoryImpl`, Hilt `DataModule`.
- **`presentation`** — `com.android.library`. Compose UI, `@HiltViewModel` ViewModel (MVI: `UiState` + `Intent`).

### 3-3. 새 기능 추가

새 기능은 `<feature>/{entity,domain,data,presentation}` 4-레이어 모듈로 만들고, `settings.gradle.kts`·`app` 의존성에 등록한 뒤 `main/presentation` 의 `AppRouteRegistry` 에 라우트를 추가합니다.
`common` 이 제공하는 `MviViewModel`, 디자인 토큰/테마, `NetworkModule`, 내비게이션(`NavRoute`/`Page`) 을 베이스로 사용합니다.

## 4. 성능 측정

- **Baseline Profile** — `:baselineprofile` 에서 프로파일 수집 → 빌드 시 자동 머지. 콜드 스타트 첫 프레임 단축.
- **TTI (`:tti`)** — 페이지 오픈 → API Request Ready → API Response Completed 구간의 페이지 단위 TTI 로깅.
- **JankStats** — 페이지 이탈/스크롤 종료 등에서 프레임 통계(jank·frozen 수, 비율, 평균/최대) 로깅. Logcat 필터: `tag:"JankStats"`.

## 5. App Link 로 화면 테스트

App Link 가 설정되어 있어 `adb shell am start` 로 임의 화면 직접 진입 / 백스택 합성 / `onNewIntent` 처리를 검증할 수 있습니다.

스킴 / 호스트: **`https://www.courmy.com`** (autoVerify, `MainActivity` `singleTop`)

> 에뮬레이터에서는 autoVerify 미검증으로 브라우저로 빠질 수 있습니다. 이 경우 컴포넌트를 명시: `-n com.chillsam.courmy/.main.presentation.MainActivity`.

```bash
# 홈 (기본 진입 화면)
adb shell 'am start -W -a android.intent.action.VIEW -d "https://www.courmy.com/home" com.chillsam.courmy'
# 미등록 path → 홈 폴백
adb shell 'am start -W -a android.intent.action.VIEW -d "https://www.courmy.com/unknown" com.chillsam.courmy'
```

## 6. 컨벤션

### 6-1. 브랜치 전략 (gitflow)

```
feat/* ─▶ develop ─▶ main
```

- `main`: 항상 배포 가능한 릴리스 브랜치. 보호됨, 직접 푸시 금지.
- `develop`: 통합 브랜치. 기능은 여기로 먼저 병합.
- 작업 브랜치: `<type>/<SCRUM-키>-<요약>` — 예) `feat/SCRUM-90-안드로이드-레포-세팅`
- `main` 직접 머지 금지 — 반드시 `develop` 을 거친다.

### 6-2. 커밋 컨벤션 (Conventional Commits)

`<type>: 내용` — `type ∈ feat · docs · fix · chore · hotfix · release`

### 6-3. PR 규칙

- 제목: `SCRUM-<번호> <type>: 내용` — 앞에 Jira 키를 붙여 자동 연결.
- 템플릿(변경 요약 / 변경 유형 / 체크리스트)을 채운다.
- **CodeRabbit 리뷰를 확인·반영한 뒤 머지한다.**

### 6-4. 릴리스 (SemVer)

- `develop → main` PR, 제목 `release: vX.Y.Z`.
- 머지 후 `main` 에 `vX.Y.Z` 태그를 부여한다 (Semantic Versioning).

### 6-5. 품질 게이트 (pre-commit · CI)

- **pre-commit 훅** (`.pre-commit-config.yaml`): trailing-whitespace · end-of-file-fixer · check-yaml · check-merge-conflict · check-added-large-files · detect-private-key · **ktlint** · **conventional-pre-commit**(커밋 메시지 검사).
  - 설치: `pre-commit install`
- **CI** (`.github/workflows/ci.yml`, PR 시 실행):
  - `ktlint` — 코드 스타일 검사
  - `build-and-test` — `assembleDebug testDebugUnitTest lintDebug koverXmlReport`
  - `code-security` — Trivy 시크릿 스캔
  - `ci-report` — 결과 집계

## 참고자료

- JankStats: https://developer.android.com/topic/performance/jankstats?hl=ko
- Baseline Profile: https://developer.android.com/topic/performance/baselineprofiles/overview?hl=ko
- Navigation3 Recipes: https://github.com/android/nav3-recipes
