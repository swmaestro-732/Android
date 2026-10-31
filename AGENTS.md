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

## 카테고리·테마 기준

카테고리·테마를 구현·수정할 때는 항상 `../.ai/taxonomy.md`를 참조한다. **두 체계가 별개다.**

- **장소 카테고리 10종**(`PlaceCategoryVO`, `cafe` …): 지도 필터, 장소 상세·검색·저장함 장소 탭. 지도 `category` 요청은 대문자 enum 이름. 라벨은 `toPlaceCategoryLabel()`. 디저트·갤러리·편집숍을 별도 카테고리로 만들지 않는다.
- **코스 태그 12종**(`CourseTagVO`, `date`·`cafetour` …): 홈 테마 타일, 추천 코스 필터, 관심 테마(가입·편집), 코스 카드 좌상단. 라벨은 `toCourseTagLabel()`. 서버 `likeThemes` 요청·`theme(s)` 응답은 enum 이름(대문자)이고, UI 선택·캐시는 소문자 코드다.

표시용 코드는 소문자이며 요청은 엔드포인트별 계약을 따른다. 작성자의 자유 입력 해시태그만 별도로 취급한다.

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

## develop PR의 iOS 변경사항 공유

- Dependabot이 생성한 의존성·GitHub Actions 업데이트 PR은 iOS Slack 공유 대상에서 제외한다.
- `develop` 대상 Android PR을 생성하면 Slack `#ios-update`에 PR 링크와 iOS 공유사항을 알린다. 머지 완료나 배포 완료로 표현하지 않는다.
- PR의 base 대비 전체 diff(추가·삭제·이름 변경 포함)를 확인하고, PR 본문의 `iOS 공유사항`을 화면·기능별 세부 변경 목록으로 작성한다. 제목이나 커밋 메시지만으로 요약하지 않는다.
- 각 항목에 변경 전 → 후, 적용 조건, iOS 확인·반영 항목, 근거 코드 링크를 적는다. 문구·수치·기본값·노출 조건·탭 동작·화면 이동·로딩/빈 화면/오류 처리·API 필드/enum/null 처리 등 작은 변경도 해당하면 빠뜨리지 않는다. API 사용 변경과 서버 계약 변경을 구분하고, 확인하지 못한 동작은 추정으로 표시한다.
- UI 변경은 확인한 스크린샷/화면 명세 링크를 첨부한다. Android 전용 구현·빌드 변경도 별도 항목에 영향과 근거를 남겨 검토 범위를 확인할 수 있게 한다. iOS 영향이 없으면 `iOS 영향 없음`과 근거를 적는다.
- PR 본문은 후속 AI가 iOS 구현에 사용할 수 있는 상세 명세로 남긴다. 화면·상태별 변경 전후, 정확한 문구·수치·기본값, 입력/이동 조건, 로딩·빈 상태·오류·재시도, API 필드·enum·null/생략 의미, 서버 선행 조건, 미구현·미검증 항목, 완료 확인 체크리스트와 커밋 고정 코드 링크를 포함한다. Android 구현 관용구와 플랫폼 공통 요구사항을 구분한다.
- Slack 첫 메시지는 PR 링크·핵심 변경 3~5개·iOS 확인사항·중요 제한을 짧게 담는다. 세부 명세는 PR 본문의 `iOS 공유사항`으로 연결하고, 같은 스레드에 스크린샷과 필요한 보충만 보낸다. 상세 내용을 Slack에 반복 복사해 길게 늘리지 않되 PR에서는 생략하지 않는다.
- PR 본문에 스크린샷이 있으면 Markdown 이미지·HTML `img`·접힌 영역의 이미지까지 확인하고, 같은 Slack 스레드에 원본 이미지를 함께 전송한다. 이미지마다 상세 항목 번호·화면명·Before/After 또는 화면 상태·PR에 명시된 촬영 날짜를 붙이고, 예시 데이터·로컬 알림 등 검증 한계를 유지한다. 날짜나 실제 동작 확인 여부를 추측하지 않는다.
- 스크린샷은 PR별 한 메시지에 여러 원본 파일을 묶어 첨부하고, 이미지 순서에 맞는 번호·화면 설명을 함께 적는다. 파일 수 제한에 걸리면 화면 흐름별로 최소한의 묶음으로 나눈다. 여러 화면을 한 장으로 합쳐 원본 해상도를 잃게 하지 않는다.
- 단일 파일 업로드 도구만 있으면 업로드 후 메시지의 첨부 파일 ID 목록을 묶어 갱신한다. 한 메시지에 모든 파일이 실제 등록됐는지 다시 읽어 확인한 뒤 중복된 개별 첨부 메시지만 정리한다. 원본 파일·상세 설명은 삭제하지 않고 묶음 메시지 링크를 PR에 기록한다.
- UI diff와 이미지 목록을 대조해 변경된 모든 화면·주요 상태를 스크린샷에 대응시킨다. 대표 화면 한 장으로 다른 화면의 변경을 대체하지 않는다. 누락된 화면은 해당 PR 빌드에서 추가 촬영하고 PR과 Slack에 함께 첨부한다. 촬영할 수 없으면 화면·상태·사유를 PR에 명시하고 전체 캡처 완료로 보고하지 않는다.
- 스크린샷은 접근 가능한 원본을 열어 확인한 뒤 파일 첨부를 우선한다. 이미지 첨부를 지원하지 않거나 원본 접근이 불가능하면 화면명과 원본 링크를 대신 보내고 `이미지 첨부 미완료` 및 사유를 기록한다. 링크 전송·미리보기를 파일 첨부 성공으로 취급하지 않으며 이미지를 임의로 공개하지 않는다.
- 전송 전 PR 본문과 기존 Slack 공유 기록을 확인해 같은 PR의 중복 알림을 피한다. 이후 iOS 영향이 달라지면 기존 메시지의 스레드로 보완한다.
- 텍스트와 스크린샷 전송 결과를 각각 확인한다. 일부 실패하면 성공한 메시지·이미지는 유지하고 실패한 항목만 같은 스레드에 재전송한다. PR 본문에 이미지별 전송 결과와 스레드 링크를 기록하고, 스크린샷이 없으면 `해당 없음`으로 표시한다.
- 전송 성공 시 메시지 링크를 PR 본문에 남긴다. 채널 미지정·연결 불가·전송 실패 시 완료로 보고하지 않고 미전송 사유를 기록한다. 채널·수신자를 임의로 정하지 않는다.

## UI 스크린샷 동기화

UI가 변경되면 최신 빌드에서 해당 화면을 캡처하고 [Courmy UI Flow](https://www.notion.so/3f6b448bdf9d81bea9f1f35597ec3d54)의 대응 이미지를 교체한다.
기존 화면 순서·설명은 유지하고 캡처 날짜·화면 상태를 표시한다. 실제 화면을 확인한 이미지만 올린다.
로그인·서버 데이터·진입 조건 때문에 촬영할 수 없으면 기존 이미지를 유지하고 미갱신 화면과 사유를 명시한다.

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
