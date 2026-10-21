package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.course.domain.DeletePlaceReviewUseCase
import com.chillsam.courmy.course.domain.GetPlaceDetailUseCase
import com.chillsam.courmy.course.domain.GetPlaceReviewsUseCase
import com.chillsam.courmy.course.domain.PlaceDetailRepository
import com.chillsam.courmy.course.domain.PlaceReviewRepository
import com.chillsam.courmy.course.domain.PlaceSaveRepository
import com.chillsam.courmy.course.domain.SetPlaceSavedUseCase
import com.chillsam.courmy.course.entity.CourseReviewDraftVO
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort
import com.chillsam.courmy.course.entity.PlaceDetailVO
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakePlaceRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `저장 목록에서 열면 조회 중에도 저장됨을 유지하고 토글 요청을 보내지 않는다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.open(42L, initiallySaved = true)

            assertTrue(viewModel.uiState.value.isSaved)
            assertTrue(viewModel.uiState.value.isLoading)
            viewModel.toggleSave()
            runCurrent()

            assertTrue(repository.saveRequests.isEmpty())
            assertTrue(viewModel.uiState.value.isSaved)
            repository.detail.complete(PlaceDetailVO(name = "저장 장소", isSaved = true))
            advanceUntilIdle()

            viewModel.toggleSave()
            advanceUntilIdle()

            assertEquals(listOf(42L to false), repository.saveRequests)
            assertFalse(viewModel.uiState.value.isSaved)
        }

    @Test
    fun `상세 응답의 저장 상태가 목록보다 최신이면 응답을 기준으로 토글한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.open(42L, initiallySaved = true)
            repository.detail.complete(PlaceDetailVO(name = "저장 취소된 장소", isSaved = false))
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isSaved)
            assertFalse(viewModel.uiState.value.isLoading)
            viewModel.toggleSave()
            advanceUntilIdle()

            assertEquals(listOf(42L to true), repository.saveRequests)
        }

    @Test
    fun `상세 조회 실패 시에도 목록에서 확인한 저장 상태를 보존한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.open(42L, initiallySaved = true)
            repository.detail.completeExceptionally(IOException("offline"))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isSaved)
            assertFalse(viewModel.uiState.value.isLoading)
            viewModel.toggleSave()
            advanceUntilIdle()

            assertEquals(listOf(42L to false), repository.saveRequests)
        }

    private fun viewModel() =
        PlaceDetailViewModel(
            GetPlaceDetailUseCase(repository),
            GetPlaceReviewsUseCase(repository),
            SetPlaceSavedUseCase(repository),
            DeletePlaceReviewUseCase(repository),
        )

    private class FakePlaceRepository :
        PlaceDetailRepository,
        PlaceReviewRepository,
        PlaceSaveRepository {
        val detail = CompletableDeferred<PlaceDetailVO>()
        val saveRequests = mutableListOf<Pair<Long, Boolean>>()

        override suspend fun getPlaceDetail(placeId: Long): PlaceDetailVO = detail.await()

        override suspend fun getPlaceReviews(
            placeId: Long,
            sort: CourseReviewSort,
            cursor: String?,
            size: Int,
        ): CourseReviewListVO = CourseReviewListVO.empty

        override suspend fun createPlaceReview(
            placeId: Long,
            draft: CourseReviewDraftVO,
        ): Long = error("작성 요청은 이 테스트에서 사용하지 않는다")

        override suspend fun deletePlaceReview(
            placeId: Long,
            reviewId: Long,
        ) = Unit

        override suspend fun setSaved(
            placeId: Long,
            saved: Boolean,
        ) {
            saveRequests += placeId to saved
        }

        override suspend fun markVisited(placeId: Long) = Unit
    }
}
