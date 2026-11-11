# SCRUM-629 프로필 편집 재조회 실패 복원력 · 입력 잠금

촬영일: 2026-11-11 / 빌드: fix/SCRUM-629-profile-edit-resilience (da4d7c18) / 에뮬레이터 Pixel(API 16k)
세션: 로그인 사용자(닉네임·아이디 courmy / 관심 테마 데이트·맛집·힐링 / 관심 지역 부산·대전)

| # | 파일 | 화면·상태 | 설명 |
| --- | --- | --- | --- |
| 01 | 01-loaded.png | 프로필 편집 정상 로드 | 온라인 진입 시 입력값·관심사 정상 표시(회귀 없음) |
| 02 | 02-before-refetch-fail-wiped.png | 수정 전 — 재조회 실패 | 복귀 재조회 실패 시 입력값·관심사가 **빈 값으로 소실**(develop 동작) |
| 03 | 03-after-refetch-fail-retained-locked.png | 수정 후 — 재조회 실패 | 값 **유지** + "편집"/저장 버튼 **잠금** + 인라인 "잠시 후 다시 시도해 주세요. 새로고침" |

재현: 온라인으로 프로필 편집 진입 → 비행기 모드 ON → 화면 off/on(순수 resume)으로 `RefreshOnResume` 재조회 유발.
02는 수정 전 동작(재조회 실패가 VM에서 profile 을 null 로 비워 편집 기준이 빈 값이 됨)을 같은 절차로 촬영.
