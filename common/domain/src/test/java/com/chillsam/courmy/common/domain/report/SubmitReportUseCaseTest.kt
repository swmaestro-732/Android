package com.chillsam.courmy.common.domain.report

import com.chillsam.courmy.common.entity.report.ReportReason
import com.chillsam.courmy.common.entity.report.ReportResultVO
import com.chillsam.courmy.common.entity.report.ReportTargetType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SubmitReportUseCaseTest {
    @Test
    fun `설명을 정규화하고 지원 대상과 사유를 그대로 전달한다`() =
        runTest {
            val repository = mockk<ReportRepository>()
            coEvery { repository.submit(any(), any(), any(), any()) } returns ReportResultVO(1, "접수")
            val useCase = SubmitReportUseCase(repository)

            useCase(ReportTargetType.USER, 42, ReportReason.PRIVACY, "  개인정보 노출 \n")
            useCase(ReportTargetType.COURSE_COMMENT, 43, ReportReason.OTHER, " \t\n")

            coVerify { repository.submit(ReportTargetType.USER, 42, ReportReason.PRIVACY, "개인정보 노출") }
            coVerify { repository.submit(ReportTargetType.COURSE_COMMENT, 43, ReportReason.OTHER, null) }
        }

    @Test
    fun `양수 ID와 최대 500자를 검사하고 잘못된 입력은 전송하지 않는다`() =
        runTest {
            val repository = mockk<ReportRepository>()
            val useCase = SubmitReportUseCase(repository)
            listOf(0L, -1L).forEach { id ->
                assertTrue(
                    runCatching {
                        useCase(ReportTargetType.COURSE, id, ReportReason.SPAM, "")
                    }.exceptionOrNull() is IllegalArgumentException,
                )
            }
            assertTrue(
                runCatching {
                    useCase(ReportTargetType.COURSE, 1, ReportReason.SPAM, "가".repeat(501))
                }.exceptionOrNull() is IllegalArgumentException,
            )
            coVerify(exactly = 0) { repository.submit(any(), any(), any(), any()) }

            coEvery { repository.submit(any(), any(), any(), any()) } returns ReportResultVO(1, "접수")
            useCase(ReportTargetType.COURSE, 1, ReportReason.SPAM, "가".repeat(500))
            coVerify(exactly = 1) { repository.submit(ReportTargetType.COURSE, 1, ReportReason.SPAM, "가".repeat(500)) }
        }
}
