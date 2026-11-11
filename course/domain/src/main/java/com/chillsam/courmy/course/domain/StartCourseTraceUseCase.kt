package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * 따라가기 시작 기록 UseCase. 발급된 traceId 를 돌려준다.
 *
 * 서버는 사용자당 따라가기 세션을 1개만 허용한다. **다른 코스**의 세션이 남아 있으면 시작이
 * 409(4100)로 거절되므로([ActiveTraceConflictException]), 기존 세션을 빈 기록으로 종료한 뒤
 * 한 번만 다시 시작한다 — 사용자가 새 코스의 따라가기를 눌렀다는 것이 묵은 세션을 끝낸다는
 * 의사 표시다. 같은 코스 재시작은 서버가 기존 세션을 그대로 돌려줘 여기로 오지 않는다.
 *
 * 기존 세션 종료가 "이미 종료(4099)"·"세션 없음(4049)" 등으로 실패해도 재시작은 시도한다 —
 * 그 실패는 충돌이 이미 풀렸다는 뜻일 수 있고, 아니면 재시작이 같은 충돌로 다시 실패해 알게 된다.
 */
class StartCourseTraceUseCase
    @Inject
    constructor(
        private val repository: TraceRepository,
    ) {
        suspend operator fun invoke(courseId: Long): Long {
            requireLocalized(courseId > 0) { LocalizedValidationException(StringResource.VALIDATION_COURSE_TARGET) }
            return try {
                repository.startTrace(courseId)
            } catch (e: ActiveTraceConflictException) {
                val staleTraceId = e.activeTraceId ?: throw e
                endStaleTrace(staleTraceId)
                repository.startTrace(courseId)
            }
        }

        /** 묵은 세션을 빈 기록(방문 0·0분·0m)으로 종료한다. 실패해도 재시작 시도를 막지 않는다. */
        private suspend fun endStaleTrace(traceId: Long) {
            runCatching {
                repository.endTrace(
                    traceId = traceId,
                    visits = emptyList(),
                    durationMinutes = 0,
                    distanceMeters = 0,
                )
            }.onFailure { if (it is CancellationException) throw it }
        }
    }
