# 홈 지역 코스 API 연동 (2026-11-11)

GET /service/v2/home/recommended-courses의 미지원 sigungu 쿼리를 region으로 변경. 초기 진입·세부 지역 선택·재시도·다음 페이지 모두 동일 필터를 사용한다. 전체 지역은 전국이 아니라 진입 시도의 코드를 사용한다. 일반 추천 전체보기는 region 생략.

서버 코드: https://github.com/swmaestro-732/BackEnd/blob/20281e9525e89f3f21ea4c5dc5a803974b3a83d3/src/main/kotlin/com/example/backend/mobile/home/adapter/inbound/web/v2/HomeController.kt

region은 시도 2자리·구 있는 시 4자리·시군구 5자리 prefix. 광주 29와 전남 46은 통합 시도 12의 지역을 분리하는 서버 가상 코드다. 광주 하위는 12210/12240/12270/12300/12330, 전남 하위는 12 prefix에서 이 5개 제외. 타일 전남 12→46, 광주29 유지, 울릉47U→47940. 하위 칩은 기존 번들 area_map을 사용하며 API 지역 검색으로 대체하지 않는다. 서버 데이터/계약 변경 없음.

실서버 공개 조회 확인: 서울11→양천구·성동구, 종로11110→종로구·신문로2가, 부산26→우동·광안동, 광주29→광산구, 전남46→목포시·순천시, 강원51→평창군·영월군, 전북52→진안군·남원시. 응답 code=2000. API 응답 예시이며 모든 코스의 실제 위치 전수 검증은 아니다.

검증: domain71개/presentation131개 테스트 모두 통과, assembleDebug/lintDebug 통과. 최신 앱 설치 후 서울→종로 선택 및 목록 변경 확인. expanded-en.png와 selected-en.png 최신 API 응답으로 교체. Notion 기존 이미지 제자리 교체는 도구 No matches found 오류로 실패하여 최신 섹션·원본 이미지2개 추가.
