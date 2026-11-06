# Amplitude 상호작용·화면 이동 이벤트

2026-11-07, SCRUM-800. 기존 핵심 flow 이벤트는 유지하고 아래 이벤트를 추가한다. Debug 빌드와 API key 미설정 빌드는 Amplitude 전송을 하지 않는다. Logcat은 이벤트명과 속성 key만 출력한다. 실제 대시보드 수신은 별도 확인 대상이다.

## 이벤트 계약

| 이벤트 | 발생 시점 | 속성 |
|---|---|---|
| `screen_view` | Navigation3 화면 표시 | `screen`, `previous_screen` (앱 실행 첫 화면은 생략) |
| `navigation` | 실제 NavKey가 바뀐 뒤 | `from_screen`, `to_screen`, `action`; 클릭 출처를 명시한 곳은 `source_screen`, `trigger=button`, `button`. 자동 완료는 `trigger=result`, `reason` |
| `button_click` | 계측 대상 버튼 클릭 | `screen`, `button`, 해당하면 `course_id`, `provider`, `destination`, 선택한 `themes`/`regions` |
| `search_submit` | 비어 있지 않은 검색어 제출 | `screen=/search`, `query` (trim 후 최대 100자), `target=course/place`, `source=keyboard/recent/trending/place_suggestion` |
| `login_provider_result` | 제공자 인증 결과 | `provider=kakao/naver/google`, `result=success/failure/cancelled/not_configured/timeout`, 성공 시 `new_user=true/false`, SDK/서버 실패 시 `stage=sdk/server` |
| `selection_change` | 필터·선택값 변경 | `screen`, `control` 또는 검색결과 `filter`, `value`; 선택 토글은 `selected=true/false` |

`navigation.action`은 `push`, `replace`, `deep_link`, `app_back`, `system_back`, `authentication_completed`. 시스템 뒤로가기가 자체 검색 편집 콜백을 사용하는 지도 검색은 `trigger=system`, `reason=system_back`으로 구분한다. 동일 path라도 코스ID/인자가 달라지면 이동이고, 동일 NavKey로의 no-op은 이벤트를 만들지 않는다.

검색에서 Back 후 결과를 push하는 기존 동작은 `from_screen`이 중간 화면일 수 있다. 실제 클릭 화면은 `source_screen=/search`로 보존하므로 분석에서는 `source_screen + button + to_screen`을 사용한다. `source_screen` 없는 이동에 직전 버튼을 추측해 붙이지 않는다. 버튼→비동기 API→후속 이동은 클릭과 result/action 이벤트를 구분하고 SDK 세션의 순서로 분석한다.

route args 전체를 전송하지 않는다. 이동의 허용 부가정보는 `courseId`, `placeId`, `planId`, `theme`, `category`, `tab`, `target`, `section`, `sido`이며 `from_`/`to_` 접두사로 구분한다. route query·handle·위경도는 제외한다.

## 화면별 계측 범위

| 화면 | 버튼·선택·이동 출처 |
|---|---|
| 홈 | 프로필, 검색, 진행 중 코스, 추천 전체/코스, 근처 지도·장소·빈 상태, 테마/지역, 코스·계획 만들기, 임시저장, 하단탭 |
| 검색 입력 | 키보드 제출·최근/인기 검색어·장소 제안의 각 출처와 제출값, 입력 지우기, 최근 항목/전체 삭제, 테마 |
| 검색 결과 | 검색어 수정/초기화, 코스 카드, 북마크, 지역/테마/정렬 선택 |
| 추천 | 코스 카드, 북마크, 테마·세부 지역 선택 |
| 코스 상세 | 북마크·좋아요·팔로우·공유·복제·따라가기·수정·후기·댓글; 작성자/내 프로필·로그인 확인·복제 종류의 목적지 |
| 코스 작성/완료 | 게시/계획 저장 성공 목적지, 완료 후 홈·내 코스 이동 |
| 게스트 마이 | 환영 로그인·저장/계획/후기/팔로우 혜택 카드 각각, 하단탭 |
| 마이/타유저 | 마이 내부 탭 변경, 팔로워·팔로잉, 알림함, 설정, 코스·계획, 빈 상태 생성, 하단탭 |
| 저장함 | 코스/장소 탭 변경, 코스 카드, 게스트 로그인, 하단탭 |
| 지도/지도검색 | 지도 핀 선택·해제, 장소 카테고리·정렬·결과 시트 펼침 변경, 장소 상세 시트·북마크, 검색 진입, 검색어 수정/초기화, 앱·시스템 뒤로가기, 하단탭 |
| 가입/관심사 | 제공자별 로그인 버튼·결과, 약관 문서 보기/계속, 프로필 다음, 관심 테마·지역 선택값/다음/저장, 인증 완료 복귀 |
| 설정/프로필 | 프로필·계정·알림·버전 이동, 관심 테마·지역 이동, 문의·개인정보 문서의 외부 브라우저 열기 |

위 목록은 실제 계측한 범위다. 모든 Compose 클릭을 자동 수집하거나 폼 입력 중의 키 입력을 전송하지 않는다. 같은 버튼의 이동은 `navigation`, 이동 없이 실행되는 액션은 `button_click`을 함께 사용한다. 같은 액션을 이벤트명 하나만으로 전체 집계하지 않는다.

## 개인정보·전송 범위

- 사용자 요청에 따라 검색 **제출값**을 수집한다. 검색어에는 사용자가 개인정보를 입력할 수 있으므로 기존 메타데이터 전용 수집보다 범위가 넓다. 검색값은 Amplitude 제품 분석에만 전달하며 Firebase/Meta/Crashlytics/logcat에 복제하지 않는다.
- 닉네임·프로필 handle·댓글/후기/메모 원문·인증 토큰·이메일·정밀 위치는 새 속성으로 전송하지 않는다. 관심 지역은 사용자가 고른 행정구역 코드다.
- 기존 `login`, `signup`, `course_save` 등 flow 이벤트와 Meta 성공 전환은 유지한다. `login_provider_result`는 제공자별 분석용이며 기존 login 성공 수에 더해 이중 집계하지 않는다.
- 외부 대시보드 수신/보관·삭제 설정과 개인정보처리방침/Play 데이터 보안 신고의 실제 등록 갱신은 코드 변경으로 완료되지 않는다. release 전 검토 대상이다.

## 검증

- 검색 trim/빈 값/길이 제한, 버튼 공통 속성 보호, 로그인 결과에서 토큰·서버 원문 제외 테스트.
- 동일 key no-op, 동일 path 다른 args, Back+push 출처 유지, route 인자 allowlist, 자동 완료 reason, app_start 테스트.
- SDK 전송 예외는 분석을 건너뛰고 앱의 클릭·화면 이동은 계속한다.


## 1.2.0 배포 준비

2026-11-07 읽기 전용 [Play 조회](https://github.com/swmaestro-732/Android/actions/runs/42286188778): production 11, beta 없음, alpha 3, internal 3. Gradle versionBase=1.2, Fastlane patch_base_code=12로 변경해 다음 versionCode12를1.2.0으로 산정한다. 이후13은1.2.1이다. APK와AAB에 동일 속성을주입한다. 개발용고정versionCode2000000000은Play에업로드하지않는다.
