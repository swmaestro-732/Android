package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.course.domain.CourseCommentRepository
import com.chillsam.courmy.course.domain.DeleteCourseCommentUseCase
import com.chillsam.courmy.course.domain.EditCourseCommentUseCase
import com.chillsam.courmy.course.domain.GetCourseCommentsUseCase
import com.chillsam.courmy.course.domain.WriteCourseCommentUseCase
import com.chillsam.courmy.course.entity.CourseCommentPageVO
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
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CourseCommentsViewModelTest {
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun successfulWriteClearsDraftBeforeRefreshAndKeepsItClearedWhenRefreshFails() =
        runTest {
            val repository = FakeCommentRepository()
            val viewModel = createViewModel(repository)
            viewModel.onIntent(CourseCommentsIntent.Load(1L))
            advanceUntilIdle()
            viewModel.onIntent(CourseCommentsIntent.ChangeDraft("등록할 댓글"))
            val refresh = CompletableDeferred<CourseCommentPageVO>()
            repository.refresh = refresh

            viewModel.onIntent(CourseCommentsIntent.Write)
            runCurrent()

            assertEquals(listOf("등록할 댓글"), repository.writtenComments)
            assertEquals("", viewModel.uiState.value.draft)

            refresh.completeExceptionally(IOException("목록 조회 실패"))
            advanceUntilIdle()

            assertEquals("", viewModel.uiState.value.draft)
            assertFalse(viewModel.uiState.value.isSubmitting)
            assertNotNull(viewModel.uiState.value.message)

            viewModel.onIntent(CourseCommentsIntent.Write)
            advanceUntilIdle()
            assertEquals(1, repository.writtenComments.size)
        }

    @Test
    fun failedWriteKeepsDraftForRetryAndDoesNotRefresh() =
        runTest {
            val repository = FakeCommentRepository()
            val viewModel = createViewModel(repository)
            viewModel.onIntent(CourseCommentsIntent.Load(1L))
            advanceUntilIdle()
            viewModel.onIntent(CourseCommentsIntent.ChangeDraft("다시 보낼 댓글"))
            repository.writeFailure = IOException("작성 실패")

            viewModel.onIntent(CourseCommentsIntent.Write)
            advanceUntilIdle()

            assertEquals("다시 보낼 댓글", viewModel.uiState.value.draft)
            assertFalse(viewModel.uiState.value.isSubmitting)
            assertNotNull(viewModel.uiState.value.message)
            assertEquals(1, repository.readCount)
            assertEquals(emptyList<String>(), repository.writtenComments)
        }

    private fun createViewModel(repository: CourseCommentRepository) =
        CourseCommentsViewModel(
            getComments = GetCourseCommentsUseCase(repository),
            writeComment = WriteCourseCommentUseCase(repository),
            editComment = EditCourseCommentUseCase(repository),
            deleteComment = DeleteCourseCommentUseCase(repository),
        )

    private class FakeCommentRepository : CourseCommentRepository {
        var refresh: CompletableDeferred<CourseCommentPageVO>? = null
        var writeFailure: IOException? = null
        var readCount = 0
        val writtenComments = mutableListOf<String>()

        override suspend fun getComments(
            courseId: Long,
            cursor: String?,
            size: Int,
        ): CourseCommentPageVO {
            readCount += 1
            return refresh?.await() ?: CourseCommentPageVO()
        }

        override suspend fun writeComment(
            courseId: Long,
            content: String,
        ): Long {
            writeFailure?.let { throw it }
            writtenComments += content
            return 1L
        }

        override suspend fun editComment(
            courseId: Long,
            commentId: Long,
            content: String,
        ): Unit = error("이 테스트에서 수정은 호출하면 안 됩니다.")

        override suspend fun deleteComment(
            courseId: Long,
            commentId: Long,
        ): Unit = error("이 테스트에서 삭제는 호출하면 안 됩니다.")
    }
}
