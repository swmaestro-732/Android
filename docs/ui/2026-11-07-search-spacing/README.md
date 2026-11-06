# 검색 화면 제목·간격

2026-11-07 최신 debug APK 실제 에뮬레이터 캡처. 테스트용 versionCode는 2000000000이며 출시 버전 변경이 아니다.

- `empty.png`: 최근 검색어 없음. 검색란 아래 테마 섹션 앞 20dp 여백 추가.
- `recent.png`: 테스트 검색어 Seoul 제출 후 재진입. 최근 검색과 테마로 찾기 제목을 같은 textStrongS로 통일. 칩은 기존 textRegularXS 유지.
- APK 조립, main:presentation 단위 테스트, detekt 통과. 앞선 저장 수정은 app lintDebug도 통과.
- 검색 결과 데이터 정확성 및 서버 검색 성공을 증명하는 캡처는 아니다.

[wiki-needed] 검색 진입 명세의 제목 크기·빈 상태 간격 및 최근 검색어 저장 규칙 동기화.
