# 2026-10-21 사용자 Flow 개선 확인

실제 에뮬레이터 APK 화면. 공개 코스 ID1과 courmy 프로필, 성수동 검색을 읽어 확인했다. 서버 후기/신고/저장 POST는 수행하지 않았다.

- 통합 브랜치 fix/SCRUM-624-flow-integration. 최초 캡처 d5836a5, 최종 신고 폼·지도 전체보기는9a6be80 빌드에서 다시 캡처.
- course-detail: 좋아요·저장 상단 아이콘, 신고 더보기.
- report: 신고 폼, 미제출 상태.
- place-detail/place-review: 실제 장소 상세·카페 키워드 후기 입력, 미제출 상태.
- fullmap/map-place-panel: 전체 지도와 선택 장소 상세, 선택 마커 가시성 확인.
- region-candidates: 성수동1가/2가 후보, 자동 선택 없음.
- profile: 타인 프로필 공통 팔로우 버튼. coursesCnt=0 서버 문제는 별도 잔여.

인증된 임시저장·발행·복제 저장, 저장함, 따라가기 진행/완주, 관계변경, 순간 로딩 상태는 촬영하지 못했다. 기존 UI Flow 이미지를 유지하고 미확인으로 기록한다.
