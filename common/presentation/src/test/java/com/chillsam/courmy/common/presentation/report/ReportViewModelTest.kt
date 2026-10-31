package com.chillsam.courmy.common.presentation.report

import com.chillsam.courmy.common.domain.report.ReportRepository
import com.chillsam.courmy.common.domain.report.ReportSubmissionException
import com.chillsam.courmy.common.domain.report.SubmitReportUseCase
import com.chillsam.courmy.common.entity.report.ReportReason
import com.chillsam.courmy.common.entity.report.ReportResultVO
import com.chillsam.courmy.common.entity.report.ReportTargetType
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeRepository()

    @Before fun setUp() = Dispatchers.setMain(dispatcher)

    @After fun tearDown() = Dispatchers.resetMain()

    @Test
    fun `사유 없이 전송하지 않고 제출 중 중복과 폼 변경을 막는다`() =
        runTest(dispatcher) {
            val viewModel = ReportViewModel(SubmitReportUseCase(repository))
            val submit = ReportIntent.Submit(ReportTargetType.COURSE, 42)
            viewModel.onIntent(submit)
            runCurrent()
            assertEquals(0, repository.calls)
            viewModel.onIntent(ReportIntent.SelectReason(ReportReason.SPAM))
            viewModel.onIntent(ReportIntent.Describe("원래 설명"))
            viewModel.onIntent(submit)
            viewModel.onIntent(submit)
            viewModel.onIntent(ReportIntent.Describe("바뀐 설명"))
            viewModel.onIntent(ReportIntent.Reset)
            runCurrent()
            assertEquals(1, repository.calls)
            assertEquals("원래 설명", viewModel.uiState.value.description)

            repository.result.complete(ReportResultVO(1, "접수 완료"))
            advanceUntilIdle()
            viewModel.onIntent(submit)
            runCurrent()
            assertEquals(1, repository.calls)
            assertEquals("접수 완료", viewModel.uiState.value.success)
        }

    @Test
    fun `실패 시 사유와 설명을 보존하고 같은 폼으로 재시도할 수 있다`() =
        runTest(dispatcher) {
            val viewModel = ReportViewModel(SubmitReportUseCase(repository))
            viewModel.onIntent(ReportIntent.SelectReason(ReportReason.PRIVACY))
            viewModel.onIntent(ReportIntent.Describe("개인정보 노출"))
            viewModel.onIntent(ReportIntent.Submit(ReportTargetType.USER, 42))
            runCurrent()
            repository.result.completeExceptionally(IOException("네트워크 실패"))
            advanceUntilIdle()

            assertEquals(ReportReason.PRIVACY, viewModel.uiState.value.reason)
            assertEquals("개인정보 노출", viewModel.uiState.value.description)
            assertEquals("신고하지 못했어요. 다시 시도해 주세요.", viewModel.uiState.value.error)
            assertFalse(viewModel.uiState.value.submitting)
            assertNull(viewModel.uiState.value.success)

            repository.result = CompletableDeferred(ReportResultVO(2, "접수"))
            viewModel.onIntent(ReportIntent.Submit(ReportTargetType.USER, 42))
            advanceUntilIdle()
            assertEquals(2, repository.calls)
            assertNull(viewModel.uiState.value.error)
            assertEquals("접수", viewModel.uiState.value.success)
        }

    @Test
    fun `서버 안내만 표시하고 원시 JSON을 담은 다른 예외는 숨긴다`() =
        runTest(dispatcher) {
            val viewModel = ReportViewModel(SubmitReportUseCase(repository))
            viewModel.onIntent(ReportIntent.SelectReason(ReportReason.SPAM))
            repository.result.completeExceptionally(ReportSubmissionException("이미 신고한 대상입니다."))
            viewModel.onIntent(ReportIntent.Submit(ReportTargetType.USER, 42))
            advanceUntilIdle()
            assertEquals("이미 신고한 대상입니다.", viewModel.uiState.value.error)

            repository.result =
                CompletableDeferred<ReportResultVO>().apply {
                    completeExceptionally(IllegalStateException("""HTTP 500 {"debug":"internal failure"}"""))
                }
            viewModel.onIntent(ReportIntent.Submit(ReportTargetType.USER, 42))
            advanceUntilIdle()
            assertEquals("신고하지 못했어요. 다시 시도해 주세요.", viewModel.uiState.value.error)
            assertNull(viewModel.uiState.value.success)
        }

    private class FakeRepository : ReportRepository {
        var calls = 0
        var result = CompletableDeferred<ReportResultVO>()

        override suspend fun submit(
            targetType: ReportTargetType,
            targetId: Long,
            reason: ReportReason,
            description: String?,
        ): ReportResultVO {
            calls++
            return result.await()
        }
    }
}
