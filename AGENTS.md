# Courmy Android 작업 지침

Courmy는 `26-mobile-eng-class/AndroidArchi`를 기반으로 만든 멀티모듈 Android 프로젝트다.
이 저장소에서 작업할 때는 프로젝트 코드만 보지 말고, 형제 저장소의 `.ai` 위키와 원본 아키텍처 문서를 함께 확인한다.

## 문서 위치

동일한 상위 디렉터리에 다음 저장소가 있다고 가정한다.

```text
GitHub/
├── .ai/
├── 26-mobile-eng-class/
│   └── AndroidArchi/
└── courmy/
```

경로가 없으면 추측으로 대체하지 말고 현재 checkout 위치를 확인한다.

## 작업 전 항상 읽을 문서

1. `../.ai/AGENTS.md`
2. `../.ai/conventions.md`
3. `../.ai/android/README.md`
4. `../.ai/android/architecture.md`
5. `../.ai/android/code-convention.md`

## 작업별 추가 문서

### 모듈·상태 관리

- `../.ai/android/architecture/module-structure.md`
- `../.ai/android/architecture/mvi.md`
- `../26-mobile-eng-class/AndroidArchi/docs/architecture/module-structure.md`
- `../26-mobile-eng-class/AndroidArchi/docs/architecture/mvi.md`

### 네비게이션·App Links

- `../.ai/android/architecture/navigation.md`
- `../26-mobile-eng-class/AndroidArchi/docs/architecture/navigation.md`

### 데이터·API·에러

- `../.ai/api-design.md`
- `../.ai/api-spec.md`
- `../.ai/android/api-integration.md`
- `../.ai/android/architecture/data-layer.md`
- `../.ai/android/architecture/error-handling.md`
- `../26-mobile-eng-class/AndroidArchi/docs/architecture/data-layer.md`
- `../26-mobile-eng-class/AndroidArchi/docs/architecture/error-handling.md`

### UI·디자인

- `../.ai/android/design.md`
- `../.ai/android/architecture/design-system.md`
- `../.ai/android/design/components.md`
- `../.ai/android/design/figma-workflow.md`
- `../26-mobile-eng-class/AndroidArchi/docs/architecture/design-system.md`
- `../26-mobile-eng-class/AndroidArchi/docs/DESIGN_TO_CODE_GUIDE.md`

### 테스트·성능·검증

- `../.ai/android/architecture/testing.md`
- `../.ai/android/architecture/performance.md`
- `../.ai/android/ai/verification.md`
- `../26-mobile-eng-class/AndroidArchi/docs/architecture/performance.md`

## 판단 우선순위

문서와 코드가 충돌하면 다음 순서로 판단한다.

1. 현재 사용자 요청
2. 이 저장소의 `AGENTS.md`
3. `../.ai` 위키의 팀 합의·계약
4. Courmy의 실제 코드와 Gradle 설정
5. `../26-mobile-eng-class/AndroidArchi/docs/architecture/`의 원본 패턴
6. 설치된 스킬의 예시와 절차

AndroidArchi는 구현 패턴의 참고 자료다. 다음 템플릿 값을 Courmy 코드에 그대로 복사하지 않는다.

- 패키지 `com.jongchan.androidarchi`
- `ArchiThemeImpl`, `ArchiText`
- intro·search·favorite·fullScreenMedia 전용 구조
- Kakao API base URL·인증 header
- `www.androidarchi.com`

Courmy에서는 현재 코드의 `com.chillsam.courmy`, `DesignSystemThemeImpl`, `DsText`,
`AppRouteRegistry`와 Courmy API·디자인 계약을 사용한다.

## 핵심 아키텍처 규칙

1. 의존 방향은 `presentation → domain → entity`, `data → domain → entity`다.
2. presentation과 data는 서로 직접 의존하지 않는다.
3. entity와 domain은 순수 Kotlin/JVM을 유지한다.
4. 독립 feature는 원칙적으로 `entity/domain/data/presentation` 4모듈로 구성한다.
5. 화면 입력은 `onIntent(Intent)`, 상태 변경은 `dispatch(ReducerEvent)`와 `reduce()`를 거친다.
6. 화면 이동은 Navigation3 공통 계약과 `AppRouteRegistry`를 사용한다.
7. 색상·타이포는 `DesignSystemThemeImpl`과 Courmy 공통 컴포넌트를 사용한다.
8. DTO와 VO를 분리하고 변환은 data 레이어에서 수행한다.

## 변경 후 검증

변경 범위에 맞는 좁은 task를 먼저 실행하고, PR 전에는 전체 영향을 확인한다.

```bash
./gradlew :app:compileDebugKotlin
./gradlew test
./gradlew :app:assembleDebug :app:lintDebug
```

- Gradle·Manifest·리소스·DI 변경은 APK 조립까지 확인한다.
- 테스트나 빌드를 실행하지 못했으면 통과로 보고하지 않고 미실행 사유를 남긴다.
- 테스트를 삭제하거나 lint·detekt baseline을 늘려 통과시키지 않는다.

## 위키 동기화

작업 종료 전 `../.ai/sync.md`를 기준으로 위키 갱신 필요 여부를 확인한다.

다음 변경은 관련 `.ai` 문서를 같은 작업에서 갱신한다.

- 앱 모듈 구조·아키텍처·주요 라이브러리
- API 계약과 오류 처리
- 화면·사용자 흐름·디자인 시스템
- 빌드·테스트·CI/CD 규칙
- AI 스킬·검증 방식

당장 위키를 갱신할 수 없으면 커밋 메시지나 PR 본문에 `[wiki-needed]`를 남긴다.

## AI 스킬

Android 스킬의 정책과 검증 기준은 다음 문서를 따른다.

- `../.ai/android/ai/README.md`
- `../.ai/android/ai/skill-policy.md`
- `../.ai/android/ai/verification.md`

스킬은 위키와 현재 코드를 실행하는 절차다. 스킬 내부의 하드코딩이 위키·현재 코드와 다르면 위키와 코드를 우선하고 차이를 보고한다.

## 보안·로컬 설정

- `local.properties`, API key, keystore, token을 커밋하지 않는다.
- `.claude/settings.local.json`은 개인 전용이며 공유하지 않는다.
- 개인 홈 절대 경로와 임시 세션 경로를 공용 문서·스킬에 넣지 않는다.
