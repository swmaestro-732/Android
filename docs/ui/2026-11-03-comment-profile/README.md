# 댓글 작성자 프로필 연동

- 브랜치: `fix/SCRUM-709-comment-author-profile`
- 작성자 ID/`나` 대신 서버 `authorNickname`을 표시하고 `authorProfileImageUrl`을 36dp 원형 이미지에 연결한다. 누락/null은 `알 수 없는 사용자`와 기본 사람 아이콘이다. 긴 닉네임은 한 줄 말줄임으로 날짜를 보호한다.
- 이미지 버튼은 48dp다. 내 댓글은 시트를 닫고 마이페이지로 이동한다. 타인 댓글은 `authorHandle`이 제공된 경우에만 시트를 닫고 기존 타인 프로필 라우트로 이동한다. handle이 없는 타인 이미지는 비활성이다.
- 작성·수정 요청은 기존 content 계약을 유지하며 프로필 정보는 조회 응답에서만 읽는다.

## 서버 선행 조건

2026-11-03 확인한 BackEnd develop `5db688bc4ee7a38c75af6a3e2e9d120ac3282a76`의 댓글 응답은 `authorNickname`·`authorProfileImageUrl`을 제공하지만 **authorHandle은 제공하지 않는다**. 공개 프로필은 `/service/v1/mypage/{handle}`이며 `GET /api/v1/users/{userId}`는 제거됐다. 따라서 타인 프로필 이동은 Android 수신·화면 배선 준비 상태이고 운영 완료가 아니다.

필요한 서버 변경은 기존 작성자 배치 조회에서 handle을 댓글 응답까지 전달하는 것이다. 삭제·미존재 작성자는 null로 유지한다. 서버 변경 범위에 대한 사용자 확인은 대기 중이며 이 커밋에는 백엔드 변경·배포가 없다.

- [댓글 응답 근거](https://github.com/swmaestro-732/BackEnd/blob/5db688bc4ee7a38c75af6a3e2e9d120ac3282a76/src/main/kotlin/com/example/backend/course/adapter/inbound/web/response/CourseCommentResponse.kt)
- [ID 조회 제거 근거](https://github.com/swmaestro-732/BackEnd/blob/5db688bc4ee7a38c75af6a3e2e9d120ac3282a76/src/main/kotlin/com/example/backend/user/adapter/inbound/web/UserController.kt)

## 검증

- `:course:data:testDebugUnitTest`: 85건 통과(댓글 작성자 정보·handle 보존 및 누락/null 매핑 포함).
- `:course:presentation:testDebugUnitTest`: 21건 통과.
- `:app:assembleDebug`, `:app:lintDebug`, `:course:presentation:detekt`, `:course:data:detekt`, `:course:entity:detekt`: 통과.
- 독립 리뷰에서 발견한 긴 닉네임 overflow를 Ellipsis로 보완했다.
- 댓글 실데이터 이미지·본인/타인 클릭 왕복은 미검증이다. 검증 기기의 마이 프로필 조회 실패 및 서버 handle 미제공 때문에 Courmy UI Flow의 해당 정상 이미지들을 유지한다. 이번 작업에서 댓글 정상 화면 캡처를 갱신하지 않았다.
