package com.chillsam.courmy.common.domain.report

import com.chillsam.courmy.common.entity.report.ReportReason
import com.chillsam.courmy.common.entity.report.ReportResultVO
import com.chillsam.courmy.common.entity.report.ReportTargetType
import javax.inject.Inject

interface ReportRepository {
    suspend fun submit(
        targetType: ReportTargetType,
        targetId: Long,
        reason: ReportReason,
        description: String?,
    ): ReportResultVO
}

class SubmitReportUseCase
    @Inject
    constructor(
        private val repository: ReportRepository,
    ) {
        suspend operator fun invoke(
            targetType: ReportTargetType,
            targetId: Long,
            reason: ReportReason,
            description: String,
        ): ReportResultVO {
            require(targetId > 0) { "신고할 대상을 확인할 수 없어요." }
            require(description.length <= MAX_DESCRIPTION_LENGTH) { "설명은 500자까지 입력해 주세요." }
            return repository.submit(targetType, targetId, reason, description.trim().takeIf { it.isNotEmpty() })
        }

        companion object {
            const val MAX_DESCRIPTION_LENGTH = 500
        }
    }

/** 사용자에게 전달할 서버 신고 안내. 원시 HTTP/파싱 오류와 구분한다. */
class ReportSubmissionException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
