package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.domain.auth.IsLoggedInUseCase
import com.chillsam.courmy.common.domain.auth.SessionRepository
import com.chillsam.courmy.common.domain.telemetry.NoOpTelemetry
import com.chillsam.courmy.common.domain.telemetry.ProductEvent
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.course.domain.CourseLikeRepository
import com.chillsam.courmy.course.domain.CourseManageRepository
import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.domain.CourseSaveRepository
import com.chillsam.courmy.course.domain.DeleteCourseUseCase
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import com.chillsam.courmy.course.domain.SetCourseLikedUseCase
import com.chillsam.courmy.course.domain.SetCourseSavedUseCase
import com.chillsam.courmy.course.domain.SetFollowAuthorUseCase
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CourseEditVO
import com.chillsam.courmy.course.entity.CourseVisibility
import com.chillsam.courmy.course.entity.DraftSummaryVO
import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CourseDetailViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeCourseRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `비로그인 사용자의 저장은 로그인 안내를 띄우고 저장 요청을 보내지 않는다`() =
        runTest(dispatcher) {
            val viewModel = viewModel(loggedIn = false)
            viewModel.onIntent(CourseDetailIntent.Load(169L))
            advanceUntilIdle()
            assertNotNull(viewModel.uiState.value.detail)
            assertFalse(viewModel.uiState.value.isLoading)

            viewModel.onIntent(CourseDetailIntent.ToggleSave)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.needsLogin)
            assertTrue(repository.saveRequests.isEmpty())
            assertFalse(viewModel.uiState.value.isSaving)
            assertFalse(
                viewModel.uiState.value.detail!!
                    .isSaved,
            )
        }

    @Test
    fun `로그인 사용자는 코스 저장과 저장 취소를 요청하고 확정 상태를 표시한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel(loggedIn = true)
            viewModel.onIntent(CourseDetailIntent.Load(169L))
            advanceUntilIdle()

            viewModel.onIntent(CourseDetailIntent.ToggleSave)
            advanceUntilIdle()

            assertEquals(listOf(169L to true), repository.saveRequests)
            assertTrue(
                viewModel.uiState.value.detail!!
                    .isSaved,
            )
            assertFalse(viewModel.uiState.value.needsLogin)
            assertFalse(viewModel.uiState.value.isSaving)

            viewModel.onIntent(CourseDetailIntent.ToggleSave)
            advanceUntilIdle()

            assertEquals(listOf(169L to true, 169L to false), repository.saveRequests)
            assertFalse(
                viewModel.uiState.value.detail!!
                    .isSaved,
            )
        }

    private val events = mutableListOf<Pair<ProductEvent, Map<String, String>>>()
    private val telemetry =
        object : Telemetry by NoOpTelemetry {
            override fun logEvent(
                event: ProductEvent,
                properties: Map<String, String>,
            ) {
                events += event to properties
            }
        }

    @Test
    fun `successful detail load tracks the course id`() =
        runTest {
            val vm = viewModel(loggedIn = true)
            vm.onIntent(CourseDetailIntent.Load(169L))
            advanceUntilIdle()
            assertEquals(
                listOf(
                    ProductEvent.ContentView to
                        mapOf("screen" to "/course/detail", "content_type" to "course", "course_id" to "169"),
                ),
                events,
            )
        }

    private fun viewModel(loggedIn: Boolean) =
        CourseDetailViewModel(
            GetCourseDetailUseCase(repository, NoOpTelemetry),
            SetCourseSavedUseCase(repository, NoOpTelemetry),
            SetCourseLikedUseCase(repository, NoOpTelemetry),
            SetFollowAuthorUseCase(repository, NoOpTelemetry),
            DeleteCourseUseCase(repository),
            IsLoggedInUseCase(
                object : SessionRepository {
                    override val isLoggedIn = loggedIn
                },
            ),
            resources = testResources(),
            telemetry = telemetry,
        )

    private class FakeCourseRepository :
        CourseRepository,
        CourseSaveRepository,
        CourseLikeRepository,
        CourseManageRepository {
        val saveRequests = mutableListOf<Pair<Long, Boolean>>()
        override val savedCourses = MutableStateFlow<List<SavedCourseVO>>(emptyList())
        override val lastCompleted = MutableStateFlow<CourseCompleteVO?>(null)

        override suspend fun getCourseDetail(courseId: Long) =
            CourseDetailVO(
                title = "테스트 코스",
                authorName = "작성자",
                authorHandle = "author",
                placeCount = 0,
                walkingMinutes = null,
                tracingCount = 0,
                description = "",
                places = emptyList(),
                rating = "",
                reviewCount = 0,
                reviews = emptyList(),
            )

        override suspend fun setSaved(
            courseId: Long,
            saved: Boolean,
        ) {
            saveRequests += courseId to saved
        }

        override suspend fun getDrafts(): List<DraftSummaryVO> = error("unused")

        override suspend fun getCourseDraft(courseId: Long): CourseDraftVO = error("unused")

        override suspend fun saveDraft(
            draft: CourseDraftVO,
            courseId: Long?,
        ): Long = error("unused")

        override suspend fun deleteDraft(courseId: Long): Unit = error("unused")

        override fun completeCourse(course: CourseCompleteVO?): Unit = error("unused")

        override suspend fun getRecommendedTags(
            placeIds: List<Long>,
            limit: Int,
        ): List<String> = error("unused")

        override suspend fun setFollowAuthor(
            userId: Long,
            follow: Boolean,
        ): Boolean = error("unused")

        override suspend fun setLiked(
            courseId: Long,
            liked: Boolean,
        ): Nothing = error("unused")

        override suspend fun getVisibility(courseId: Long): CourseVisibility? = error("unused")

        override suspend fun updateCourse(
            courseId: Long,
            edit: CourseEditVO,
        ): Unit = error("unused")

        override suspend fun deleteCourse(courseId: Long): Unit = error("unused")

        override suspend fun createCourse(
            draft: CourseDraftVO,
            thumbnailUrl: String?,
            published: Boolean,
            draftCourseId: Long?,
        ): Long = error("unused")
    }
}
