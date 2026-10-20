package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.telemetry.NoOpTelemetry
import com.chillsam.courmy.course.entity.CourseDraftVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateCourseUseCaseTest {
    @Test
    fun `기존 초안 발행은 초안 ID와 공개 여부를 저장소로 전달한다`() =
        runTest {
            val repository = mockk<CourseRepository>(relaxed = true)
            val draft = CourseDraftVO(name = "이어 쓴 코스")
            coEvery { repository.createCourse(any(), any(), any(), 42) } returns 42
            val useCase =
                CreateCourseUseCase(
                    repository,
                    mockk(),
                    NoOpTelemetry,
                    CompleteCourseUseCase(repository, NoOpTelemetry),
                )

            val result = useCase(draft, draftCourseId = 42)

            assertEquals(42L, result.courseId)
            coVerify(exactly = 1) { repository.createCourse(draft, null, true, 42) }
        }

    @Test
    fun `새 코스 작성은 기본값으로 초안 ID 없이 생성한다`() =
        runTest {
            val repository = mockk<CourseRepository>(relaxed = true)
            val draft = CourseDraftVO(name = "새 코스")
            coEvery { repository.createCourse(any(), any(), any(), null) } returns 99
            val useCase =
                CreateCourseUseCase(
                    repository,
                    mockk(),
                    NoOpTelemetry,
                    CompleteCourseUseCase(repository, NoOpTelemetry),
                )

            assertEquals(99L, useCase(draft).courseId)
            coVerify(exactly = 1) { repository.createCourse(draft, null, true, null) }
        }
}
