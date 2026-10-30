# Android 푸시 연동

## 작업 단위

- SCRUM-724: FCM SDK·기기 알림 권한·기기 토큰 등록/해제.
- SCRUM-725: 푸시 수신 표시·알림 탭 시 Navigation3 화면 이동.
- 담당: 허나영. iOS/Web 및 앱 안 알림함은 이 작업에 포함하지 않는다.

## 현재 구현

- Firebase BOM을 사용하는 Messaging SDK 의존성.
- Android 8 이상 `courmy_activity`(활동 알림) 채널.
- 마이 > 설정 > 기기 알림: 앱 전체 알림 허용 상태 표시, Android 13 이상 최초 권한 요청, 이후 시스템 설정 이동 및 복귀 시 재확인.
- Android 7은 앱 상세 설정으로 이동한다.
- 서버 연동 준비 전이므로 FCM 자동 초기화는 비활성화한다. 토큰을 직접 조회·출력·등록하지 않는다.
- 이 상태는 푸시 수신 기능 완료가 아니다. 수신 서비스·토큰 동기화·탭 이동은 아직 없다.

## 서버 계약 확인 대기

사용자 제공 기획: `PUT /api/v1/my/devices`의 body에 `token`, `platform=ANDROID`, `appBuild`; 로그아웃 전 `DELETE /api/v1/my/devices` body에 `token`.

2026-10-30 확인: BackEnd develop에 기기 API가 없고, PR #144는 알림함 1a만 포함한다. 다음을 확정한 뒤 구현한다.

1. appBuild의 타입·의미, 등록/삭제 응답과 실패 코드, 인증·기능 버전 조건.
2. 동일 토큰의 계정 이전과 갱신 처리. 로그아웃/탈퇴/세션 만료 시 토큰 해제 실패 처리.
3. notification/data 메시지 형식, type·targetType 허용값, targetId·notificationId 예시 및 채널 ID.
4. 알림을 탭했을 때 필요한 로그인·삭제된 대상 처리. 클라이언트가 임의의 외부 경로를 열지 않는다.

백엔드 작업 및 배포를 대신 수행하지 않는다. 토큰은 URL·로그·PR·스크린샷에 남기지 않는다. 실제 FCM 전송 검증과 서버 API 연동 검증은 별도 완료 조건이다.

## 등록 정보

FCM 도입에 따른 기기 ID·알림 목적 처리와 실제 토큰 전송을 Play 데이터 보안/개인정보처리방침과 대조한다. 권한 허용과 서버 수신 설정은 별개다.

참고: https://firebase.google.com/docs/cloud-messaging/android/get-started
