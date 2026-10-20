package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.data.courseCreate.CourseCreateDataSource
import com.chillsam.courmy.course.data.courseCreate.dto.CourseIdDTO
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseEnvelope
import com.chillsam.courmy.course.data.courseManage.CourseManageDataSource
import com.chillsam.courmy.course.data.courseManage.dto.UpdateCourseRequest
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CourseVisibility
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class CourseDraftPublishTest {
    private val create = mockk<CourseCreateDataSource>()
    private val manage = mockk<CourseManageDataSource>(relaxed = true)
    private val repository =
        CourseRepositoryImpl(mockk(), create, mockk(), mockk(), manage, mockk(), { null })

    @Test
    fun `일반 초안과 복제 초안의 발행은 기존 ID를 갱신하고 POST 하지 않는다`() =
        runBlocking {
            val request = slot<UpdateCourseRequest>()
            coEvery { manage.updateCourse(42, capture(request)) } returns Unit

            listOf(null, 7L).forEach { origin ->
                val draft =
                    CourseDraftVO(
                        name = "이어 쓴 코스",
                        description = "작성한 설명",
                        tags = listOf("산책"),
                        visibility = CourseVisibility.PRIVATE,
                        forkedFromId = origin,
                        places =
                            listOf(
                                CoursePlaceVO(
                                    id = "11",
                                    name = "장소",
                                    note = "기록",
                                    photoUrls = listOf("https://example.com/place.jpg"),
                                ),
                            ),
                    )

                val result = repository.createCourse(draft, "https://example.com/cover.jpg", true, 42)

                assertEquals(42L, result)
                assertTrue(request.captured.isPublished)
                assertEquals("이어 쓴 코스", request.captured.title)
                assertEquals("작성한 설명", request.captured.description)
                assertEquals(listOf("산책"), request.captured.tags)
                assertEquals("PRIVATE", request.captured.visibility)
                assertEquals("https://example.com/cover.jpg", request.captured.thumbnailUrl)
                assertEquals(
                    11L,
                    request.captured.places
                        .single()
                        .placeId,
                )
                assertEquals(
                    "기록",
                    request.captured.places
                        .single()
                        .caption,
                )
                assertEquals(
                    listOf("https://example.com/place.jpg"),
                    request.captured.places
                        .single()
                        .imageUrls,
                )
            }
            coVerify(exactly = 2) { manage.updateCourse(42, any()) }
            coVerify(exactly = 0) { create.createCourse(any()) }
            coVerify(exactly = 0) { create.forkCourse(any(), any()) }
        }

    @Test
    fun `초안 ID 없는 일반 작성과 복제는 각각 생성과 포크 API를 유지한다`() =
        runBlocking {
            coEvery { create.createCourse(any()) } returns CreateCourseEnvelope(data = CourseIdDTO(10))
            coEvery { create.forkCourse(7, any()) } returns CreateCourseEnvelope(data = CourseIdDTO(20))

            assertEquals(10L, repository.createCourse(CourseDraftVO(name = "새 코스"), null, true, null))
            assertEquals(20L, repository.createCourse(CourseDraftVO(name = "복제", forkedFromId = 7), null, true, null))

            coVerify(exactly = 1) { create.createCourse(match { it.isPublished && it.title == "새 코스" }) }
            coVerify(exactly = 1) { create.forkCourse(7, match { it.isPublished && it.title == "복제" }) }
            coVerify(exactly = 0) { manage.updateCourse(any(), any()) }
        }

    @Test
    fun `기존 초안 발행 실패를 새 POST로 대체하지 않는다`() =
        runBlocking {
            val failure = IOException("일시적 실패")
            coEvery { manage.updateCourse(any(), any()) } throws failure

            val result = runCatching { repository.createCourse(CourseDraftVO(), null, true, 42) }

            assertEquals(failure, result.exceptionOrNull())
            coVerify(exactly = 0) { create.createCourse(any()) }
            coVerify(exactly = 0) { create.forkCourse(any(), any()) }
        }
}
