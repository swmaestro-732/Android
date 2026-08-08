# Courmy

Kotlin · Jetpack Compose 기반 Android 애플리케이션.
**멀티모듈 클린아키텍처 + DDD + MVI** 구조를 가짐.

> 현재는 아키텍처·CI·공용 인프라(미디어 검색, 즐겨찾기, 디자인 시스템, 성능 계측)를 갖춘 **초기 단계**입니다.
> 홈 화면은 최소 골격이며, 여기에 feature 를 붙여 나갑니다.

---

## 기술 스택

| 영역 | 사용 기술 |
|---|---|
| 언어 | Kotlin 2.3 (JDK 17) |
| UI | Jetpack Compose (BOM 2026.04) · Navigation 3 · Material 3 |
| DI | Hilt · KSP |
| 네트워크 | Retrofit · OkHttp · kotlinx.serialization |
| 이미지/애니메이션 | Coil 3 · Lottie |
| 비동기 | Coroutines |
| 테스트 | JUnit · MockK · Kover(커버리지) |
| 정적분석/포맷 | Detekt · ktlint |
| 성능 | Baseline Profile · Macrobenchmark · JankStats · TTI 계측 |

- `compileSdk` / `targetSdk` 37, `minSdk` 24

---

## 아키텍처

계층은 아래 한 방향으로만 의존합니다. `data` 는 `domain` 이 정의한 인터페이스(Repository/UseCase)를 구현합니다.

```
presentation ─▶ domain ─▶ entity
     │            ▲
     └─ data ─────┘   (domain 의 인터페이스를 구현)
```

- **entity** — 순수 데이터 모델(VO). 어떤 프레임워크에도 의존하지 않음
- **domain** — 비즈니스 규칙. UseCase / Repository 인터페이스 정의
- **data** — Repository 구현. Retrofit ApiService · DTO · DataSource
- **presentation** — Compose UI + MVI ViewModel(`MviViewModel`)

각 기능 도메인은 `common`(여러 feature 공용)과 `main`(앱 진입·네비게이션) 두 축으로 나뉘며, 축마다 위 4계층을 갖습니다.

---

## 모듈 구조

| 모듈 | 역할 |
|---|---|
| `common:entity` | 공용 VO — 미디어(`MediaItemVO`), 즐겨찾기(`FavoriteItemVO`) |
| `common:domain` | 미디어 검색·즐겨찾기 UseCase/Repository, 에러·네비게이션·메시지 헬퍼 규약 |
| `common:data` | 미디어 검색 API(카카오 다음 검색) 구현, 네트워크 모듈 |
| `common:presentation` | 디자인 시스템(토큰·타이포·컬러), 공용 컴포넌트, MVI 베이스, JankStats |
| `main:entity` | 앱 전용 엔티티 |
| `main:domain` | 홈, 딥링크 라우팅(`RouteMatcher`·`RoutePattern`) |
| `main:data` | 앱 전용 데이터 |
| `main:presentation` | `MainActivity`, 네비게이션 호스트, 홈 화면 |
| `tti` | Time-To-Interaction(초기 상호작용 시점) 계측 |
| `baselineprofile` | Baseline Profile 생성 + 시작 성능 벤치마크 |
| `app` | 모듈 조립 진입점 |

---

## 프로젝트 구조

```
courmy/
├── app/                        # 앱 진입점 · 모듈 조립
├── common/
│   ├── entity/                 # 공용 VO
│   ├── domain/                 # 공용 UseCase · Repository 규약
│   ├── data/                   # 미디어 검색 API 구현
│   └── presentation/           # 디자인 시스템 · 공용 컴포넌트 · MVI 베이스
├── main/
│   ├── entity/
│   ├── domain/                 # 홈 · 딥링크 라우팅
│   ├── data/
│   └── presentation/           # MainActivity · 네비게이션 · 홈 화면
├── tti/                        # TTI 성능 계측
├── baselineprofile/            # Baseline Profile · Macrobenchmark
├── config/detekt/              # Detekt 룰셋
├── gradle/libs.versions.toml   # 버전 카탈로그
└── .github/workflows/ci.yml    # CI 파이프라인
```

---

## 빌드 & 실행

요구사항: **JDK 17**, Android SDK(compileSdk 37)

### 로컬 설정 — `app/google-services.json`

Firebase Crashlytics 설정 파일이다. API key 를 담고 있어 저장소에 커밋하지 않으므로
[Firebase 콘솔](https://console.firebase.google.com/project/courmy-qa732/settings/general)에서
각자 받아 `app/` 아래에 둔다.

- **없어도 debug 빌드는 된다** — 크래시 리포팅만 빠진다.
- **release 빌드는 실패한다.** 크래시 리포팅 없이 출시하는 걸 막기 위한 의도적인 동작이다.
  CI 는 `GOOGLE_SERVICES_JSON_BASE64` secret 으로 주입한다(`.github/workflows/cd.yml`).
- debug 빌드는 수집이 꺼져 있다. 개발 중 낸 크래시가 운영 대시보드에 섞이지 않게 하기 위함이며,
  로컬에서 리포팅을 확인해야 하면 `app/src/debug/AndroidManifest.xml` 의 값을 잠깐 `true` 로 바꾼다.

```bash
# 디버그 APK 빌드
./gradlew assembleDebug

# 유닛 테스트
./gradlew testDebugUnitTest

# 정적분석 · 포맷
./gradlew detekt
ktlint --relative        # 포맷 검사 (.editorconfig 기준)

# 커버리지 리포트
./gradlew koverXmlReport
```

---

## 품질 · 성능 전략

- **테스트/커버리지** — MockK 기반 UseCase 유닛테스트, Kover 로 커버리지 리포트
- **정적분석** — Detekt(코드 스멜·복잡도, 기존 위반은 모듈별 baseline 으로 동결) + ktlint(포맷)
- **런타임 성능**
  - `baselineprofile` 모듈이 Baseline Profile 을 생성해 앱 시작·핵심 경로를 사전 컴파일
  - `JankStats` 로 프레임 드랍(jank) 모니터링
  - `tti` 모듈로 초기 상호작용 도달 시점(Time-To-Interaction) 계측
- **Compose 안정성** — `compose_stability.conf` 로 안정성 힌트 제공, `-Pcomposecompiler.reports=true` 로 리포트 생성

---

## CI/CD · 컨벤션

**GitHub Actions** — PR(→ `develop`/`release`/`main`) 마다 실행하고 결과를 단일 PR 코멘트로 통합합니다.

| 잡 | 내용 |
|---|---|
| ktlint | 포맷 검사(로컬 pre-commit 과 동일 룰) |
| detekt | 정적분석(신규 위반 차단) |
| build & test | 빌드 · 유닛테스트 · Android Lint · Kover |
| code security | Gitleaks 시크릿 스캔(히스토리 포함, 발견 시 차단) |

**CD** (GitHub Actions + Fastlane) — 서명 release 아티팩트 빌드부터 단계적으로 배포를 자동화합니다.

| 단계 | 내용 | 트리거 | 상태 |
|---|---|---|---|
| 1. 내부 테스트 배포 | 서명 release AAB/APK를 GitHub Pre-release Assets로 업로드 | `release` push 또는 수동 | ✅ 도입 |
| 2. QA 배포 | Firebase App Distribution | develop 자동 | MVP 이후 |
| 3. 스토어 배포 | Google Play | release/tag | 출시 이후 |
| 4. 릴리스 운영 | 버전 자동화 · Sentry | tag/main | 이후 |

> 서명은 `KEYSTORE_*` secret 등록 시 실서명, 없으면 debug 서명 폴백.

- **pre-commit** — 커밋 전 로컬 검사: 포맷(ktlint, CI와 버전 일치) · 시크릿(gitleaks, staged diff) · 기본 파일/커밋 규칙
- **Dependabot** — 의존성 버전 업데이트 자동 PR (gradle · github-actions)
- **브랜치 전략** — 기능은 `develop`에 통합하고, 내부 테스트 대상은 `develop → release`, 검증을 마친 버전은 `release → main` PR로 승격합니다.
- **커밋** — Conventional Commits, PR 제목에 SCRUM 이슈 키 표기
