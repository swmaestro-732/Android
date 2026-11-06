package com.chillsam.courmy.course.domain

/**
 * 다른 코스의 따라가기 세션이 이미 있어 시작이 거절됨(서버 HTTP 409, code 4100).
 *
 * 서버는 사용자당 따라가기 세션을 1개만 허용한다. 같은 코스를 다시 시작하면 기존 세션을
 * 200 으로 돌려주지만, **다른 코스**를 시작하면 이 충돌로 거절한다. 활성 세션 조회 API 가
 * 없어(`GET /api/v1/my/traces` 는 종료된 기록만 준다) 기존 세션 id 는 에러 메시지의
 * `traceId=N` 에서 추출한 [activeTraceId] 가 유일한 단서다.
 *
 * - [activeTraceId] 진행 중인 기존 세션 id. 메시지에서 추출하지 못하면 null — 그때는
 *   종료 후 재시작을 할 수 없으므로 호출부가 실패로 다룬다.
 */
class ActiveTraceConflictException(
    val activeTraceId: Long?,
    message: String?,
    cause: Throwable? = null,
) : Exception(message, cause)
