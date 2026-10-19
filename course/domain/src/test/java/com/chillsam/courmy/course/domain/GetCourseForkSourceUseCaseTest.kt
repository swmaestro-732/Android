package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 포크 프리필이 **장소 구성만** 가져오는지 고정한다.
 * 원본 작성자의 사진·한마디를 옮기면 서버 계약(`ForkCourseRequest`)과 어긋난다.
 */
class GetCourseForkSourceUseCaseTest {
    private val repository: CourseRepository = mockk()
    private val useCase = GetCourseForkSourceUseCase(repository)

    @Test
    fun `원본의 사진과 한마디는 가져오지 않는다`() =
        runTest {
            coEvery { repository.getCourseDetail(7L) } returns
                detailWith(
                    place(order = 1, placeId = 11L, tip = "웨이팅 길어요", imageUrls = listOf("https://a.jpg")),
                )

            val source = useCase(7L)

            val place = source.places.single()
            assertEquals("11", place.id)
            assertTrue(place.note.isEmpty())
            assertTrue(place.photoUrls.isEmpty())
        }

    @Test
    fun `장소는 표시 순번대로 정렬하고 좌표와 도보 분은 유지한다`() =
        runTest {
            coEvery { repository.getCourseDetail(7L) } returns
                detailWith(
                    place(order = 2, placeId = 22L, latitude = 37.5, longitude = 127.0),
                    place(order = 1, placeId = 11L, walkingMinutesToNext = 6),
                )

            val source = useCase(7L)

            assertEquals(listOf("11", "22"), source.places.map { it.id })
            assertEquals(6, source.places.first().walkingMinutes)
            assertEquals(37.5, source.places.last().latitude!!, 0.0)
            assertEquals(127.0, source.places.last().longitude!!, 0.0)
        }

    @Test
    fun `원본 작성자 이름과 코스 id 를 함께 전달한다`() =
        runTest {
            coEvery { repository.getCourseDetail(7L) } returns
                detailWith(place(order = 1, placeId = 11L)).copy(authorName = "지호님")

            val source = useCase(7L)

            assertEquals(7L, source.courseId)
            assertEquals("지호님", source.authorName)
        }

    private fun detailWith(vararg places: CourseDetailPlaceVO): CourseDetailVO =
        CourseDetailVO(
            title = "성수 카페 코스",
            authorName = "작성자",
            authorHandle = "@author",
            placeCountText = "${places.size}곳",
            walkText = "도보 20분",
            followerText = "1.2k 따라감",
            description = "설명",
            places = places.toList(),
            rating = "4.8",
            reviewCountText = "128개",
            reviews = emptyList(),
        )

    private fun place(
        order: Int,
        placeId: Long,
        tip: String = "",
        imageUrls: List<String> = emptyList(),
        walkingMinutesToNext: Int? = null,
        latitude: Double? = null,
        longitude: Double? = null,
    ): CourseDetailPlaceVO =
        CourseDetailPlaceVO(
            placeId = placeId,
            order = order,
            name = "장소 $order",
            category = "카페 · 베이커리",
            photoCountText = "",
            tip = tip,
            imageUrls = imageUrls,
            walkingMinutesToNext = walkingMinutesToNext,
            latitude = latitude,
            longitude = longitude,
        )
}
