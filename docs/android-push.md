# Android 푸시 연동

## 작업 단위와 상태

- SCRUM-724: FCM SDK·기기 알림 권한·기기 토큰 등록/해제.
- SCRUM-725: 푸시 수신 표시·알림 탭 시 Navigation3 화면 이동.
- 담당: 허나영. 마이 상단 알림함을 포함한다. iOS/Web은 제외한다.
- 토큰 연동과 코스 알림 이동 구현 중. 서버 배포·실제 FCM 수신 검증은 별도 완료 조건이다.

## 기기와 사용자 식별

- 사용자 식별은 Authorization의 JWT 사용자 ID로 한다. device ID 자체는 인증 수단이 아니다.
- `X-Device-Id`는 앱 설치별 UUID다. 앱 재시작·로그아웃·계정 전환에도 유지하고, 앱 데이터 삭제/재설치 때 변경한다.
- UUID는 백업 제외 저장소에 보관하고 Courmy API의 HTTPS 요청에만 붙인다. 외부 호스트에는 보내지 않는다.
- 서버는 사용자와 기기의 연결을 관리한다. 같은 설치에서 다른 계정으로 로그인하면 토큰 소유자를 새 사용자로 이전해야 한다.
- 서버 PR #153에는 아직 device ID 저장 처리가 없다. 헤더 이름·저장 계약의 서버 반영 확인이 필요하다.

## 등록·해제

- 앱 프로세스 시작, 로그인/가입 완료, 세션 복원, FCM onNewToken에서 최신 토큰 등록을 시도한다.
- 백그라운드 토큰 갱신에서도 저장 세션을 복원한다. 비로그인 상태에서는 등록하지 않는다.
- 사용자 요청 기준 PUT `/api/v1/my/devices` body는 `token`, `platform: ANDROID`, `appBuild`(설치 APK versionCode 정수)다.
- 기존 `X-App-Platform`·`X-App-Build` 헤더도 유지한다. 서버 PR #153은 body token만 받고 플랫폼·빌드는 헤더에서 읽으므로 배포된 API에서 추가 body 필드 수용 여부를 확인해야 한다.
- 로그아웃은 인증 삭제 전에 DELETE `/api/v1/my/devices` body `{token}`을 보내고 로컬 FCM 토큰도 폐기한다. 서버 탈퇴 성공 시 서버가 토큰을 정리하므로 로컬 FCM 토큰만 폐기한다.
- 등록·해제는 직렬화한다. 토큰 조회 중 세션이 바뀌거나 로그아웃하면 이전 등록 작업을 폐기한다. 로그아웃 취소에도 로컬 인증은 정리한다.
- SDK/API 작업은 각각 10초 제한이며 실패는 토큰·응답 본문 없이 분류값만 기록한다. 실패한 등록은 다음 앱 실행·로그인·토큰 갱신에서 재시도한다.
- FCM 자동 초기화는 기본 비활성화하고, 로그인 세션의 토큰 조회 시 활성화, 토큰 삭제 시 비활성화한다.
- 토큰은 URL·로그·PR·스크린샷에 남기지 않는다.

## 수신·화면 이동

- `courmy_activity` 활동 알림 채널과 알림 권한 설정을 사용한다.
- 앱 수신 서비스는 data와 notification+data 메시지를 처리한다. 백그라운드 notification 메시지는 FCM SDK가 표시하고 탭 extras를 Activity가 처리한다.
- `type`, `targetType`, `targetId`, `notificationId`를 검증한다. ID는 양수 Long만 허용하며 임의 URL은 열지 않는다.
- COURSE_LIKED / COURSE_COMMENTED / COURSE_TRACE_COMPLETED / COURSE_REVIEWED / COURSE_BOOKMARKED + COURSE는 기존 코스 상세 route로 연결한다.
- 콜드 스타트는 Splash를 거친다. 초기화·로그인·가입 완료 후 이동하며 Activity 재생성에도 미소비 payload를 보관한다.
- USER_FOLLOWED + USER는 payload 검증까지 구현했다. 서버 targetId가 수신자 ID이고 Android 프로필은 handle을 요구하므로 프로필 이동은 계약 확인 대기다. 현재는 알림함으로 이동한다.
- 존재하지 않거나 삭제된 코스의 조회 오류는 기존 상세 화면 정책을 따른다.

## 검증과 남은 확인

- 요청 body/DELETE 경로·푸시 parser·기기 ID 유지/외부 전송 차단 테스트.
- 백그라운드 세션 복원, 등록 중 로그아웃 순서, 토큰 조회 중 계정 변경, 로그아웃·탈퇴 취소 회귀 테스트.
- 실제 dev API 등록/삭제/계정 이전, 서버 device ID 저장, FCM HTTP v1 발송과 종료/백그라운드/포그라운드 수신은 통합 검증이 필요하다.
- Play 데이터 보안/개인정보처리방침의 실제 등록값과 토큰·설치 ID 처리를 대조한다.

참고: https://firebase.google.com/docs/cloud-messaging/android/get-started
참고: https://firebase.google.com/docs/cloud-messaging/android/receive-messages

### 로컬 검증 결과 (2026-10-31)

- 전체 `test`, `:app:assembleDebug`, `:app:lintDebug`, `detekt`, 변경 파일 ktlint 통과.
- 독립 작성한 수명주기·취소 회귀 테스트 9개 통과. 로그아웃 정리는 NonCancellable 구간에서 SDK/API별 제한 시간을 적용하고, 복원 중 취소는 loaded 상태로 기억하지 않는다.
- 최신 APK 에뮬레이터 설치 성공. debug 전용 예시 데이터로 마이·알림함과 공통 렌더러의 시스템 알림을 검증한다. 실제 서버 데이터·FCM 발송 검증과 구분한다.
- 실제 FCM 발송·서버 device ID 저장·기기 API 배포 확인 및 USER 이동 계약이 남아 있어 전체 작업 완료가 아니다.

## 앱 안 알림함 (2026-10-31)

- 마이 상단 공유 버튼 옆 알림 버튼과 미읽음 배지. `/my/notifications`에서 목록·빈 상태·실패 재시도·더 보기·전체 읽음을 제공한다.
- GET `/api/v1/my/notifications`의 Long cursor 페이지, GET `unread-count`, PATCH `{id}/read`, POST `read-all` 사용. `read-all.count`는 처리 개수이므로 미읽음 개수를 별도 조회한다.
- 서버 `actorCount`는 행동 수이며 사람 수로 표현하지 않는다. 갱신 시간은 표시하지만 서버 ID 내림차순 순서를 유지한다.
- 코스 알림은 읽음 처리 후 상세로 이동하고, 팔로우 알림은 읽음 처리 후 알림함에 머문다. 푸시 탭도 읽음을 요청한다.
- FCM 수신·읽음 변경 이벤트로 열린 알림함과 마이 배지를 갱신한다. 이 구조는 기존 main entity/domain/data/presentation 모듈을 사용한다.
- 백엔드 알림함 PR #144 및 기기 API PR #153의 배포 여부는 미확인이다.
- debug 전용 `NotificationPreviewActivity`는 예시 데이터만 사용하며 release에는 포함되지 않는다. 푸시 미리보기는 실제 FCM 전송이 아닌 동일 렌더러의 로컬 시스템 알림이다.
- 사용자 제공 dark 브랜드 아이콘을 런처에 반영했다. adaptive/monochrome 및 상태 표시줄은 C·잎 심볼의 Android vector 표현을 사용한다.

## 설정 > 알림 > 전체 알림 (2026-10-31)

- 기존 `기기 알림` 행은 `알림` 상세(`/settings/notifications`) 진입으로 변경했다. 내부에는 `전체 알림` 스위치 하나만 둔다.
- 수신 선호는 이 설치의 로컬 설정이며 기본값은 ON이다. 재시작·로그아웃·계정 전환에도 유지한다. 서버의 계정별 알림 설정 API를 추가하거나 알림함 생성/목록을 끄는 변경은 아니다.
- 표시 상태는 로컬 선호 AND Android 앱 알림 허용 AND `courmy_activity` 채널 허용이다. OS 차단 시 OFF로 표시하고, 켜기 시 최초 권한 요청 또는 기기 설정 안내를 제공한다. 기기 설정에서 복귀하면 재조회한다.
- OFF 시 선호 저장 → 자동 초기화 중지 → 기존 기기 API 해제 시도 → FCM 토큰 삭제 → 표시 중 알림 정리. 로그인/복원/토큰 갱신은 저장된 OFF를 덮어쓰지 않는다. OFF인 앱 시작에서는 토큰 삭제를 재시도한다.
- ON 시 선호 저장 후 로그인 세션의 최신 토큰 등록을 다시 시도한다. 등록 실패는 기존 재시도 정책(앱 실행·로그인·토큰 갱신)을 따른다.
- 네트워크/FCM 토큰 삭제 실패·시간 초과는 완료로 숨기지 않고 기기 설정 안내를 표시한다. OFF 값은 유지한다. 백그라운드 notification+data는 SDK가 표시하므로 토큰 삭제 실패/이미 전송 중인 메시지까지 즉시 차단한다고 보장하지 않는다. data 메시지와 포그라운드는 로컬 OFF 검사로 표시를 막는다.
- 테스트 목적으로 토큰을 복사해 둔 경우 OFF→ON 후에는 새 토큰을 다시 확보해야 한다.

### 전체 알림 검증 결과

- common data 29개, main data 65개, main presentation 57개 단위 테스트 통과. APK assembleDebug, app lintDebug, detekt, 변경 Kotlin ktlint 통과.
- 최신 APK 실제 에뮬레이터에서 설정 → 알림 진입, OS 최초 권한 허용 후 ON, OFF 저장, 프로세스 재시작 후 OFF 유지 및 FCM 토큰 캐시 없음, 다시 ON 후 토큰 생성 확인.
- ON/OFF·설정 화면 캡처는 `docs/ui/2026-10-31-notification-settings/`와 Notion UI Flow에 반영. Notion 기존 설정 이미지의 제자리 교체는 연결 도구가 갱신된 원문도 매칭하지 못해 실패하여 기존 이미지/순서를 유지하고 새 전체 알림 섹션에 최신 설정 캡처를 추가했다.
- 변경 후 실제 서버 DELETE/PUT 응답 및 OFF 상태 원격 메시지 발송 검증은 별도다. 단위 테스트·로컬 토큰 확인과 서버 통합 검증을 구분한다.

## 앱 시작 시 알림 권한 (2026-10-31)

- Android 13 이상에서 스플래시 종료 및 선택 업데이트 조회/안내 종료 후, 화면이 RESUMED일 때 POST_NOTIFICATIONS를 자동 요청한다. 강제 업데이트·공통 안내창·미소비 푸시 이동 중에는 보류한다.
- 설치별 `notification_permission.requested`를 설정 화면과 공유한다. 이미 허용됐거나 한 번 요청한 경우, 로컬 전체 알림이 OFF인 경우 자동 요청하지 않는다. 거절해도 앱 진입을 막지 않으며 이후에는 설정 > 알림에서 직접 변경한다.
- Android 12 이하는 OS 알림 권한 런타임 팝업을 요청하지 않는다. 위치 권한은 기존 홈의 위치 허용 버튼 흐름을 유지한다.
- 선택 업데이트의 조회 시작과 완료를 구분해 조회 중 알림 권한 창과 늦은 업데이트 창이 겹치지 않게 한다. 조회 실패·5초 시간 초과·오늘 숨김도 완료로 처리해 권한 요청을 영구 대기시키지 않는다.

### 시작 권한 검증 결과

- main presentation 단위 테스트 59개, APK assembleDebug, app lintDebug, detekt 통과. 자동 요청 조건 8조합과 업데이트 조회 완료/실패/시간초과/오늘 숨김을 검증했다.
- 실제 에뮬레이터에서 요청 이력 초기화 후 콜드 스타트 팝업 표시를 확인·캡처했다. 요청 이력이 있는 상태에서 ADB로 OS 권한을 회수한 뒤 재실행하면 재요청 없이 홈에 진입함을 확인했다. 테스트 후 OS 권한은 허용 상태로 복원했다.
- `docs/ui/2026-10-31-startup-notification/permission.png` 및 Notion UI Flow에 실제 시스템 권한 창을 기록했다.
