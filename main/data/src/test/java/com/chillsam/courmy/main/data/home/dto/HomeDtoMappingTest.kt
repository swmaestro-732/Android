package com.chillsam.courmy.main.data.home.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `GET /service/v2/home` 응답 → [com.chillsam.courmy.main.entity.home.HomeVO] 변환 검증.
 *
 * 서버가 nullable 로 내려주는 필드와, 화면이 쓸 수 없는 항목(id 없음)·띄우면 안 되는 값
 * (평가 없는 장소의 "0.0")을 어떻게 처리하는지 골든 케이스로 고정한다.
 */
class HomeDtoMappingTest {
    @Test
    fun `모든 필드가 채워지면 그대로 옮긴다`() {
        val vo =
            HomeDTO(
                recommendedCourses =
                    listOf(
                        HomeRecommendedCourseDTO(
                            id = 134L,
                            title = "중구 테토남 루트",
                            coverImageUrl = "https://cdn.example.com/1.png",
                            theme = "CAFETOUR",
                            likesCnt = 1,
                            savesCnt = 2,
                        ),
                    ),
                nearbySavedPlaces =
                    listOf(
                        HomeNearbyPlaceDTO(
                            placeId = 101L,
                            name = "어니언 성수",
                            imageUrl = "https://cdn.example.com/2.jpg",
                            category = "CAFE",
                            rating = 4.8,
                            ratingCount = 212,
                            walkingMinutes = 6,
                            visited = true,
                        ),
                    ),
            ).toVO()

        val course = vo.recommendedCourses.single()
        assertEquals("134", course.id)
        assertEquals("중구 테토남 루트", course.title)
        assertEquals("카페 투어", course.categoryLabel)
        assertEquals("1", course.likeCountText)
        assertEquals("2", course.saveCountText)

        val place = vo.nearbySavedPlaces.single()
        assertEquals(101L, place.id)
        assertEquals("어니언 성수", place.name)
        assertEquals("https://cdn.example.com/2.jpg", place.thumbnailUrl)
        assertEquals("카페·디저트", place.categoryLabel)
        assertEquals("4.8", place.ratingText)
        assertEquals(6, place.walkingMinutes)
        assertTrue(place.visited)
    }

    /** 평가가 없는 장소는 서버가 rating=0.0/ratingCount=0 으로 준다 — "0.0"을 띄우지 않는다. */
    @Test
    fun `평가 없는 장소는 평점 텍스트를 비운다`() {
        val place =
            HomeNearbyPlaceDTO(
                placeId = 1600L,
                name = "카페마트리",
                rating = 0.0,
                ratingCount = 0,
            ).toVO()

        assertEquals("", place.ratingText)
    }

    /** 평점이 있어도 평가 수가 0 이면 집계 전 값으로 보고 숨긴다. */
    @Test
    fun `평가 수가 0 이면 평점 텍스트를 비운다`() {
        val place =
            HomeNearbyPlaceDTO(
                placeId = 1L,
                name = "장소",
                rating = 4.5,
                ratingCount = 0,
            ).toVO()

        assertEquals("", place.ratingText)
    }

    @Test
    fun `id 없는 코스와 placeId 없는 장소는 버린다`() {
        val vo =
            HomeDTO(
                recommendedCourses = listOf(HomeRecommendedCourseDTO(id = null, title = "제목")),
                nearbySavedPlaces = listOf(HomeNearbyPlaceDTO(placeId = null, name = "이름")),
            ).toVO()

        assertTrue(vo.recommendedCourses.isEmpty())
        assertTrue(vo.nearbySavedPlaces.isEmpty())
    }

    @Test
    fun `목록이 null 이면 빈 목록으로 본다`() {
        val vo = HomeDTO(recommendedCourses = null, nearbySavedPlaces = null).toVO()

        assertTrue(vo.recommendedCourses.isEmpty())
        assertTrue(vo.nearbySavedPlaces.isEmpty())
    }
}
