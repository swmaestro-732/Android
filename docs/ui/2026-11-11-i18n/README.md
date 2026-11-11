# SCRUM-813 다국어 표시 후속 검증

2026-11-11, `feat/SCRUM-813-i18n-string-resources`에서 중단된 작업을 이어 수정했다.
캡처는 실제 에뮬레이터와 로그인 계정 데이터이며, 로컬 검증용 versionCode=2000000000 / versionName=1.3.0이다. 출시/서버 변경은 없다.

## 변경 내용

- 지도 검색 진입, 현재 위치, 장소 수, 로딩/빈 상태, 검색 결과 제목, 카테고리 필터, 정렬, 영업 상태, 목록 펼침/접힘 및 저장 접근성 문구를 기존 ko/en/ja 리소스로 연결했다.
- 마이 계획/코스 탭을 locale 리소스로 표시한다. 계획은 Plans / プラン이며 코스는 기존 Courses / コース를 재사용한다.
- taxonomy는 긴 등록 라벨부터 매칭해 `문화·전시`, `관광·명소` 내부의 가운뎃점을 보존한다. 단일/복합, 공백/무공백 구분자를 테스트한다. 등록된 토큰은 번역하고 미등록 토큰은 보존한다(`문화·전시관`처럼 모호한 입력은 `문화`와 미등록 `전시관`으로 해석된다).
- 공통 DsBilingualText는 원문을 항상 유지하고 영어/일본어 선택 시 다른 번역 결과를 보조 줄로 표시한다. 대기·실패·빈 결과·동일 결과·한국어에는 원문만 표시한다.
- 장소명, 주소/지역을 홈·저장·검색·지도·코스 장소 UI에 적용하고 장소 상세의 영업시간에도 적용했다. 입력/저장/길찾기 데이터는 원문을 유지한다.
- TranslationCache를 같은 이름의 파일로 분리해 기존 detekt 파일명 위반을 해결했다.

## 실제 확인한 화면

| 캡처 | 상태 및 확인 |
|---|---|
| [map-en.png](map-en.png) | 지도 기본 화면, Search places, 지도 SDK 지명은 SDK 자체 표시 |
| [saved-en.png](saved-en.png) | 장소 1개, Culture & Exhibitions, 원문+번역 장소명 |
| [my-en.png](my-en.png) | Plans 0 / Courses 501, 코스 카드 제목은 한국어 잔여 |
| [map-search-place-en.png](map-search-place-en.png) | 입력한 검색어 `필터` 유지, All/Cafes & Desserts/Restaurants/Bars & Pubs, Results 1 |
| [place-detail-en.png](place-detail-en.png) | 크림필터 행궁점, 원본+번역 장소명·주소·영업시간, 리뷰 빈 상태 |

마지막 두 캡처는 주소/영업시간 보완까지 포함한 최신 APK다. 앞의 세 화면은 해당 화면의 최종 변경을 포함한 앞선 검증 APK에서 촬영했다.

## 검증 결과

- `./gradlew test :app:compileDebugKotlin :app:assembleDebug :app:lintDebug :common:presentation:detekt :course:presentation:detekt -PappVersionCode=2000000000` 통과.
- taxonomy 테스트의 가짜 번역표를 실제 토큰 범위에 맞춰 보완한 뒤 `:common:presentation:testDebugUnitTest` 재실행 통과(회귀 테스트 3건).
- 변경 Kotlin 파일 ktlint 및 `git diff --check` 통과.
- 독립 읽기 전용 리뷰: 레이어·locale 재계산·높이 가변 구조 검토. 테스트의 등록 토큰 누락 지적을 반영했다.
- `:main:presentation:detekt`는 변경하지 않은 `navigation/RootComposable.kt`의 기존 CyclomaticComplexMethod(15, 임계값 15)로 실패했다. baseline을 변경하지 않았다.

## 미완료와 한계

- 온디바이스 번역은 공식 영문명이 아니다. 실제 `크림필터 행궁점`이 `Cream filter makers`, 주소가 `Gyeonggi-do Suwon, Gyeonggi-do 28`로 번역되어 고유명사 오역·주소 일부 누락이 확인됐다. 영업시간 `수 24시간 영업`도 `24 hours sales`로 번역돼 요일이 누락됐다. 번역 품질 해결 완료로 취급하지 않는다. 현재 DTO/VO에는 공식 외국어 이름/주소 필드가 없다.
- 일본어 실제 화면, 한국어 전환, 모델 미설치/오프라인 실패, 큰 글꼴, 모든 코스 편집/따라가기/장소 검색 상태는 이번에 촬영·실행하지 않았다. 해당 Notion 기존 이미지를 유지한다.
- 앱 전체 다국어 전환은 완료가 아니다. 마이 코스 제목 등 잔여 UI·동적 콘텐츠는 별도 점검 대상이다.
- Notion UI Flow의 기존 이미지 전체 태그를 이용한 교체가 `No matches found`로 실패했다. 기존 순서·설명·이미지를 유지하고 확인한 캡처를 후속 섹션에 추가했다. 제자리 교체 완료로 보고하지 않는다.
- 위키 `.ai/android/design.md`에 표시 정책을 갱신했다. 커밋·푸시·PR·머지는 수행하지 않았다.

## 후속: 앱 고정 문자열 외부화

앞 절의 제한된 작업 이후, 앱 전반의 고정 표시 문자열을 ko/en/ja 리소스로 확대했다. 서버 메시지·사용자 작성 콘텐츠·장소 고유명사·SDK 지도 지명·미리보기/비활성 채팅 예시·파싱 키·로그는 별개이며 원문을 유지한다.

- 화면·접근성·알림·평점·신고·동행자·정렬·리뷰 태그·지역 라벨, ViewModel 오류, domain 로컬 입력 검증을 외부화했다.
- 로컬 검증은 `LocalizedValidationException`의 키·인자로 전달하고 UI에서 해석한다. 내부 진단용 예외는 노출하지 않으며 서버 HTTP 메시지는 유지한다.
- CourseDetailVO의 장소/리뷰/따라간 수와 도보 시간, SavedCourseVO·CourseCompleteVO의 장소 수는 숫자로 전달한다. 리뷰·댓글 시각은 epoch millis로 전달하며 상대시간 단위/복수형은 presentation에서 해석한다. 서버 API 계약과 저장된 사용자 데이터 형식은 변경하지 않았다.
- 선택 언어의 동기 snapshot과 관찰 Flow를 함께 유지해 문구 조회마다 DataStore를 기다리지 않는다. 앱 시작·Activity 재생성 시 시스템 알림 채널 이름을 갱신한다.
- CourseCreate 상태의 오류와 진행 불가 사유는 UiMessage로 보관해 화면 locale로 해석한다.

### 최종 검증

- `test :app:assembleDebug :app:lintDebug :common:domain:detekt :common:presentation:detekt :course:presentation:detekt -PappVersionCode=2000000000` 통과. APK 조립에 앱 Kotlin 컴파일 포함. 현재 테스트 결과 XML 합계 501건, 실패/오류/skip 0건.
- 변경 Kotlin 전체 ktlint, `git diff --check`, `python3 -m unittest discover -s scripts -p test_string_resources.py` 통과.
- 전체 detekt 실행에서 새 SpreadOperator·CourseCreate 생성자 인자 수 경고를 수정했다. `main:presentation`의 기존 RootComposable CyclomaticComplexMethod(15/15)는 남아 있으며 baseline을 늘리지 않았다.
- 독립 리뷰에서 마지막 장소 도보 연결선, 숫자 축약 버림 방식, 익명/빈 계획 제목 fallback, 영어 리뷰 수 중복, 완료 카테고리 번역 누락을 발견해 수정했다.

### 최신 APK 확인 화면

| 캡처 | 상태 |
|---|---|
| [course-detail-en.png](course-detail-en.png) | 로그인/타인 코스, 3 places · 104 min walk · 0 followed, 원문 장소명과 보조 번역 |
| [report-en.png](report-en.png) | 영어 신고 사유 5개·미제출. 추가 설명 placeholder는 화면 폭에서 말줄임 |
| [language-ja.png](language-ja.png) | 일본어 선택 후 Activity 재생성, 일본어 UI·고정 언어 자체 이름 |
| [language-ko.png](language-ko.png) | 한국어 복귀, AI 배지 숨김 |

촬영: 2026-11-11, Pixel 9, 로컬 versionCode 2000000000. 설치는 앱 데이터 유지 방식이다. 영어→일본어→한국어→영어 전환을 확인했으며 알림 채널 이름도 일본어로 갱신됨을 확인했다. 신고 제출/코스 저장 등 서버 변경 액션은 실행하지 않았다.

Notion UI Flow의 기존 **코스 상세와 신고 이미지 제자리 교체에 성공**했다. 언어 설정 두 캡처와 검증 범위는 후속 섹션에 추가했다. 이전 작업의 이미지 교체 실패 기록과 다른 화면 순서/설명은 보존했다.

### 수동 검증·캡처 범위 제한

- 코스/계획 작성·편집·완성, 댓글/후기 작성·삭제·오류, 가입/프로필/탈퇴: 실제 입력·저장·계정 상태별 조건을 전부 실행하지 않아 기존 이미지를 유지한다.
- 알림의 여러 이벤트·시간 경계, 각 서버 실패/업데이트 강제 상태: 해당 서버 응답/데이터 조건을 모두 재현하지 않았다. 문자열 키/형식과 상대시간 경계는 자동 검사·단위 테스트로 확인했다.
- 비활성 채팅은 기능 플래그가 꺼져 있다. 작성된 샘플 대화 내용은 예시 데이터로 유지하고 고정 UI·인원·시간 표시는 현지화했다.
- 온디바이스 번역의 고유명사 오역과 주소·요일 누락은 여전히 가능하다. 고정 문자열 외부화와 자동번역 품질/전체 화면 수동 검증 완료를 혼동하지 않는다.
- 위키 `android/design.md`를 갱신했다. 커밋·푸시·PR·머지는 하지 않았다.

## 후속: 홈 지역별 둘러보기 활성화

사용자 요청으로 `REGION_BROWSE_ENABLED=true`로 변경했다. [home-regions-en.png](home-regions-en.png)는 활성화 및 긴 영문 지명 폭 보완 후 최신 APK의 실제 홈 화면이다. Browse by region·시도 이름·Seoul courses 제목의 번역 연결을 확인했다. 지역명은 해당 라벨 중심이 포함된 타일의 가로 폭 안에 들어가도록 필요할 때만 축소한다. APK·lint·리소스 검사 통과, Notion UI Flow에 캡처/제한 기록 추가.

구·군 이름은 서버가 아니라 번들 `area_map.json` 원문이며 한국어로 표시된다. 서울을 선택해도 다른 지역 제목의 코스가 보여 서버 `sigungu` 필터 반영 및 지역 메타데이터는 추가 확인이 필요하다. 필터 동작 완료로 보고하지 않는다. 에뮬레이터는 영어 홈 지역 지도에 두었다.
