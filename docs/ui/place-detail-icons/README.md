# 공통 장소 아이콘

상태: 디자인 확정 · Android 상세 필드 선연동 (2026-11-03, 신규 25종 추가 2026-11-07)

## 확정 디자인

사용자 승인 원본은 [approved.html](approved.html)이다. 빵은 v12 바게트, 수예·자수는 v13 가위로 확정했다.
여가시설 게임패드는 세로 도형 중심이 약 y=13.23이어서 전체를 1.25 위로 이동했다(2026-11-03). 형태·크기는 유지하고 공통 원본과 다운로드 확정본을 동기화했다.
상세값 73종 + 알 수 없는 카테고리의 기본 핀 1종을 24×24 / stroke 1.65 / round cap·join으로 변환했다.
2026-11-07 신규 상세값 25종(쇼핑 11·체험 2·자연 1·명소 6·여가 1·웰니스 4)을 같은 규격으로 추가했다(시안: `proposed-2026-11-07.html`). 폴백 핀 전용 상위 카테고리로 `PlaceCategoryVO.ETC`("기타")를 추가했으며, 지도 10종 필터(`MapCategory`)는 그대로 둔다. 신규 25종의 code 는 기존 48종과 같이 **앱 디자인 제안값(서버 `detailCategory` 계약 확정 대상)** 이다.

```bash
python3 scripts/generate_place_detail_icons.py
```

변환기는 SVG의 path·circle·ellipse·rect·rotate를 VectorDrawable로 옮긴다.
임의로 도형을 근사하거나 디자인을 다시 그리지 않는다. 색상은 지도에서 기존 semantic tint를 적용한다.

## 표시 조건

- 지도 및 검색 응답의 선택 필드 `detailCategory` → `PlaceDetailCategoryVO` → 지도 핀 리소스.
- 알려진 상세값은 해당 아이콘을 사용한다. 대문자 enum 이름과 소문자 코드를 읽고 앞뒤 공백을 제거한다.
- 필드 누락·null·빈 값·미지원 값이면 공통 `placeIcon(category)`의 상위 분류 폴백를 유지한다.
- 상위 카테고리도 식별할 수 없으면 확정한 `default_pin`을 사용한다.
- `subCategory`, 장소 이름, 주소로 상세 분류를 추론하지 않는다. 외부 검색의 `subCategory`에는 주소가 들어가기도 한다.
- 핀 크기(일반 40dp / 선택 52dp), 꼬리·색·말풍선·클러스터·탭 동작은 유지한다.
- 상세값 변경도 Compose remember 키에 포함하여 같은 장소에서 상세 분류가 갱신되면 핀을 다시 그린다.
- `common:presentation`의 `placeIcon(category, detailCategory)`가 모든 장소 아이콘을 결정한다. 지도 전용 매핑과 썸네일 전용 매핑은 없다.
- 지도 핀, 지도 목록·검색·선택 카드, 홈 근처 저장 장소, 저장함, 검색 결과, 코스 작성 장소 카드·장소 검색은 모두 같은 함수를 사용한다. 사진 로딩·실패·미제공 시 PlaceThumbnail도 동일한 상세 분류를 사용한다. 정상 사진은 유지한다.
- 홈·저장·코스·계획·장소 검색·상세 응답 및 임시저장/코스 가져오기도 상세값을 전달한다. 상세 시트로 변환할 때 상세값을 보존한다.
- 상위 카테고리는 코드·공식 라벨·기존 별칭과 정확히 일치할 때만 매칭한다. 주소 일부가 카테고리 단어를 포함해도 아이콘을 추론하지 않는다.
- 카테고리 필터 10종과 코스 태그 12종은 변경하지 않는다. 순서 번호를 표시하는 코스 경로 핀과 위치 권한·주소 등 동작용 아이콘은 분류 아이콘이 아니다.

## 서버 계약 (2026-11-08 실데이터 확인)

운영 서버가 상세 분류를 **`categoryDetail`** 필드로 내려준다(초기 가정 `detailCategory`가 아니다 — DTO는 `@SerialName("categoryDetail")`로 매핑). 값은 **한글 compound 문자열**이고 구분자는 쉼표다.
`GET /api/v1/places?q=빵&size=10` 실응답 예:
```json
{"id":591,"name":"빵미숙","categories":["CAFE"],"categoryDetail":"제과,베이커리"}
{"id":5083,"name":"만찐두빵","categories":["CAFE"],"categoryDetail":"카페"}
```
- `categoryDetail`: 한글 단일 값(쪼개지지 않음). 쉼표 구분자는 라벨 그대로다(예: `제과,베이커리`).
- `categories`: 상위 카테고리는 **대문자 코드** 배열(`["CAFE"]`).
- 매핑: `PlaceDetailCategoryVO.fromServerValue()`가 코드/enum 이름/한글 라벨(구분자 `,`·`·`·공백 무시, 조각 폴백)을 모두 받는다. 미지원·빈 값은 상위 카테고리 폴백.
2026-11-03 시점엔 지도 응답에 상세 분류가 없었으나 이후 서버가 제공하기 시작했다. 지도 핀마다 상세 API를 추가로 호출하지는 않는다.

## 검증 화면

`PlacePinPreviewActivity`는 debug 빌드에만 포함되며 운영 데이터가 아닌 예시 상세 분류를 사용한다.
일반/선택 핀을 **실제 placePinBitmap 렌더러**로, 사진 대체 아이콘을 **실제 PlaceThumbnail**로 나란히 표시한다. page 0~7로 전체 74종(상세 73 + 폴백 핀 1)을 확인한다.
로그인·서버·지도 SDK의 네트워크 응답 없이 벡터 변환과 핀 합성만 검증하는 화면이다.

```bash
adb shell am start -n com.chillsam.courmy/com.chillsam.courmy.main.presentation.map.component.PlacePinPreviewActivity --ei page 0
```

서버의 상세 분류가 없는 상태에서 이 캡처를 실제 지도 연동 성공으로 해석하지 않는다.

## PR 분리 검증 (2026-11-03)

최신 develop `d621485` 기반의 독립 작업트리에서 아이콘 변경만 적용했다. 최근 주소·영업시간 필드를 보존했다.
`:app:compileDebugKotlin`, `test`, `:app:assembleDebug`, `:app:lintDebug` 및 변경 모듈 detekt가 통과했다. 전체 단위 테스트 439개가 실패·오류 없이 통과했다(임시 제외 없음). 이전 작업트리의 InterestRegionRequestTest 오류는 아래 과거 검증 기록이며 이 PR 브랜치에는 해당하지 않는다.
`common-1.png`~`common-5.png`는 이 PR 작업트리 APK에서 다시 촬영한 debug 예시 화면이다. 실제 서버 장소 UI를 검증한 캡처가 아니다.

## 공통 적용 검증 (2026-11-03)

- 공통 함수와 모든 PlaceThumbnail 호출부를 독립 리뷰했다. 복합 분류 라벨(`카페·디저트 · 문화·전시`)은 구분자 단위로 정확히 비교하도록 보완했다.
- 최신 APK의 일반 핀 / 선택 핀 / 썸네일 49종을 직접 확인했다: [1](screenshots/common-1.png) · [2](screenshots/common-2.png) · [3](screenshots/common-3.png) · [4](screenshots/common-4.png) · [5](screenshots/common-5.png). 운영 데이터가 아닌 debug 예시 화면이다.
- app compileDebugKotlin / assembleDebug / lintDebug, 변경 모듈 detekt 통과.
- PlaceIconTest 3개, main/course PlaceIconCategoryMappingTest 각 1개, GetCourseForkSourceUseCaseTest 3개: 총 8개 통과. 8개 데이터 변환 경로의 누락·null·미지원·정상 분류, 상태 직렬화, 코스 가져오기 보존을 검증했다.
- main:data 대상 테스트에는 아래 기존 InterestRegionRequestTest 컴파일 오류 때문에 같은 임시 제외를 사용했다. 전체 테스트는 이 오류가 미해결인 상태다.
- Notion UI Flow에 공통 적용 범위 및 홈·저장·코스·지도·검색의 실데이터 상세 아이콘 캡처 미갱신 사유를 기록했다. 새 debug 캡처는 실데이터 화면을 대신하지 않는다.

## 최초 핀 적용 검증 결과 (2026-11-03)

- 최신 debug APK에서 49종의 일반/선택 렌더링을 직접 확인했다. 예시 데이터 캡처: [1](screenshots/pins-1.png) · [2](screenshots/pins-2.png) · [3](screenshots/pins-3.png) · [4](screenshots/pins-4.png) · [5](screenshots/pins-5.png).
- `:app:compileDebugKotlin`, `:app:assembleDebug`, `:app:lintDebug` 및 변경한 5개 모듈의 detekt 통과. debug 화면 여백 수정 후 assembleDebug와 main:presentation detekt 재통과.
- `PlaceDetailCategoryMappingTest` 4개, `당시 PlaceMarkerIconTest` 2개 통과.
- 전체 `test`는 기존 작업 중인 `InterestRegionRequestTest.kt:50`의 생성자 인자 타입 오류(`LegalAreaCodeMapper` / `DeviceRegistration`)로 컴파일 실패했다. 해당 파일은 수정하지 않았다. 위 6개 대상 테스트 실행에만 임시 Gradle init script로 해당 파일을 컴파일 대상에서 제외했다. 전체 테스트 통과로 간주하지 않는다.
- `.ai` 디자인·API 연동 문서 갱신, wiki-lint 오류·경고 0.
- Notion [Courmy UI Flow](https://www.notion.so/3f6b448bdf9d81bea9f1f35597ec3d54)에 서버 필드 부재로 지도·검색의 실데이터 상세 핀 캡처를 갱신하지 못한 사유를 기록했다. 기존 화면 이미지는 유지했다. 위 debug 캡처는 저장소 검증 자료이며 Notion의 운영 화면 이미지를 대체하지 않는다.

## 상세값 매핑

`DEFAULT_PIN`은 API 상세값 enum이 아니라 최종 폴백 리소스다. 아래 상세 코드와 라벨은 앱 디자인 기준이며 서버 합의 대상이다.

| 상세 코드 | 라벨 | 상위 카테고리 |
|---|---|---|
| `CAFE` | 카페 | `CAFE` |
| `BAKERY` | 제과·베이커리 | `CAFE` |
| `RESTAURANT` | 음식점 | `RESTAURANT` |
| `PUB` | 술집 | `BAR` |
| `DEPARTMENT_STORE` | 백화점 | `SHOPPING` |
| `SHOPPING_MALL` | 복합쇼핑몰 | `SHOPPING` |
| `OUTLET_STORE` | 상설할인매장 | `SHOPPING` |
| `FASHION` | 패션 | `SHOPPING` |
| `MARKET` | 시장 | `SHOPPING` |
| `HOUSEHOLD_GOODS` | 생활용품점 | `SHOPPING` |
| `CONVENIENCE_STORE` | 편의점 | `SHOPPING` |
| `SUPERMARKET` | 슈퍼마켓 | `SHOPPING` |
| `SOUVENIR` | 기념품판매 | `SHOPPING` |
| `POPUP_STORE` | 팝업스토어 | `SHOPPING` |
| `CULTURE_FACILITY` | 문화시설 | `CULTURE` |
| `BOOKSTORE` | 도서 | `CULTURE` |
| `GALLERY` | 화랑 | `CULTURE` |
| `PERFORMANCE` | 공연 | `CULTURE` |
| `FESTIVAL` | 페스티벌 | `CULTURE` |
| `LIBRARY` | 도서관 | `CULTURE` |
| `EXPERIENCE_TRIP` | 체험여행 | `EXPERIENCE` |
| `AQUARIUM` | 아쿠아리움 | `EXPERIENCE` |
| `POTTERY` | 도자기 | `EXPERIENCE` |
| `NEEDLEWORK` | 수예·자수 | `EXPERIENCE` |
| `THEME_PARK` | 테마파크 | `EXPERIENCE` |
| `ZOO` | 동물원 | `EXPERIENCE` |
| `PARK` | 공원 | `NATURE` |
| `MOUNTAIN` | 산 | `NATURE` |
| `VALLEY` | 계곡 | `NATURE` |
| `HIKING_TRAIL` | 등산로 | `NATURE` |
| `FOREST` | 숲 | `NATURE` |
| `ISLAND` | 섬 | `NATURE` |
| `LAKE` | 호수 | `NATURE` |
| `STREAM` | 하천 | `NATURE` |
| `WATERFALL` | 폭포 | `NATURE` |
| `BOTANICAL_GARDEN` | 수목원·식물원 | `NATURE` |
| `WALKING_TRAIL` | 도보여행 | `NATURE` |
| `ECOLOGY_RESERVE` | 생태보존·서식지 | `NATURE` |
| `CAMPING` | 야영·캠핑장 | `NATURE` |
| `TOURIST_ATTRACTION` | 관광·명소 | `LANDMARK` |
| `BUILDING` | 빌딩 | `LANDMARK` |
| `BUDDHIST_TEMPLE` | 불교 | `LANDMARK` |
| `BRIDGE` | 교량·다리 | `LANDMARK` |
| `LEISURE_FACILITY` | 여가시설 | `ENTERTAINMENT` |
| `SPORTS_LEISURE` | 스포츠·레저 | `ENTERTAINMENT` |
| `BATHHOUSE_SAUNA` | 목욕탕·사우나 | `WELLNESS` |
| `YOGA_PILATES` | 요가·필라테스 | `WELLNESS` |
| `HOT_SPRING` | 온천 | `WELLNESS` |
| `STATIONERY` | 문구·사무용품 | `SHOPPING` |
| `TOY` | 장난감·완구 | `SHOPPING` |
| `ART_CRAFT` | 미술·공예 | `SHOPPING` |
| `RECORD_STORE` | 음반·레코드샵 | `SHOPPING` |
| `FLORIST` | 꽃집·꽃배달 | `SHOPPING` |
| `FURNITURE` | 가구판매 | `SHOPPING` |
| `HOBBY_SUPPLIES` | 취미용품점 | `SHOPPING` |
| `DRUGSTORE` | 드럭스토어 | `SHOPPING` |
| `ELECTRONICS` | 전자제품판매 | `SHOPPING` |
| `FOOD_STORE` | 식품판매 | `SHOPPING` |
| `SHOPPING_ARCADE` | 상가·아케이드 | `SHOPPING` |
| `LEARNING_CENTER` | 체험학습장 | `EXPERIENCE` |
| `FARM` | 농장·목장 | `EXPERIENCE` |
| `PARK_FACILITY` | 공원시설물 | `NATURE` |
| `TOURIST_FACILITY` | 관광지부속시설 | `LANDMARK` |
| `HARBOR` | 항구·포구 | `LANDMARK` |
| `PIER` | 선착장·항만시설 | `LANDMARK` |
| `OVERPASS` | 육교 | `LANDMARK` |
| `PROTESTANT_CHURCH` | 기독교 | `LANDMARK` |
| `CATHOLIC_CHURCH` | 천주교 | `LANDMARK` |
| `PHOTO_STUDIO` | 사진관·포토스튜디오 | `ENTERTAINMENT` |
| `ACCOMMODATION` | 숙박 | `WELLNESS` |
| `ACCOMMODATION_FACILITY` | 숙박부속시설 | `WELLNESS` |
| `BEAUTY_SALON` | 미용 | `WELLNESS` |
| `SPACE_RENTAL` | 공간대여 | `WELLNESS` |
| `DEFAULT_PIN` | 상세값 없음 | `ETC` |

> 2026-11-07 신규 25종(`STATIONERY`~`SPACE_RENTAL`). `ETC`는 `PlaceCategoryVO`의 11번째 상위 카테고리("기타")로 승격했고, 상세값이 아니라 폴백 핀 전용이다.
