package com.chillsam.courmy.main.data

import com.chillsam.courmy.main.data.home.dto.CourseFeedDTO
import com.chillsam.courmy.main.data.profile.dto.MyPageScreenDTO
import com.chillsam.courmy.main.data.profile.dto.toMyProfileVO
import com.chillsam.courmy.main.data.profile.dto.toUserProfileVO
import com.chillsam.courmy.main.data.search.dto.CourseSearchDTO
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test
import com.chillsam.courmy.main.data.home.dto.toVOList as feedItems
import com.chillsam.courmy.main.data.search.dto.toVOList as searchItems

class CourseLikeCountMappingTest {
    @Test
    fun `피드 검색 내 프로필 타인 프로필은 서버 좋아요 수를 같은 규칙으로 표시한다`() {
        for ((value, expected) in listOf("0" to "0", "42" to "42", "1400" to "1.4k", "-1" to "0", "null" to "")) {
            val payload = """{"courses":[{"id":1,"likesCnt":$value}]}"""
            val feed = Json.decodeFromString<CourseFeedDTO>(payload).feedItems().single()
            val search = Json.decodeFromString<CourseSearchDTO>(payload).searchItems().single()
            val profile = Json.decodeFromString<MyPageScreenDTO>(payload.replace("\"id\":1", "\"id\":\"1\""))

            assertEquals(expected, feed.likeCountText)
            assertEquals(expected, search.likeCountText)
            assertEquals(
                expected,
                profile
                    .toMyProfileVO()
                    .myCourses.items
                    .single()
                    .likeCountText,
            )
            assertEquals(
                expected,
                profile
                    .toUserProfileVO(null)
                    .courses.items
                    .single()
                    .likeCountText,
            )
        }
    }

    @Test
    fun `좋아요 필드 누락은 0으로 만들지 않고 숨긴다`() {
        val payload = """{"courses":[{"id":1}]}"""
        assertEquals(
            "",
            Json
                .decodeFromString<CourseFeedDTO>(payload)
                .feedItems()
                .single()
                .likeCountText,
        )
        assertEquals(
            "",
            Json
                .decodeFromString<CourseSearchDTO>(payload)
                .searchItems()
                .single()
                .likeCountText,
        )
        val profile = Json.decodeFromString<MyPageScreenDTO>("""{"courses":[{"id":"1"}]}""")
        assertEquals(
            "",
            profile
                .toMyProfileVO()
                .myCourses.items
                .single()
                .likeCountText,
        )
    }

    @Test
    fun `검색 저장 수는 따라감으로 표시하지 않는다`() {
        val payload = """{"courses":[{"id":1,"savesCnt":42,"likesCnt":3}]}"""
        val course = Json.decodeFromString<CourseSearchDTO>(payload).searchItems().single()
        assertEquals("42", course.saveCountText)
        assertEquals("3", course.likeCountText)
        assertEquals("", course.followCountText)
    }
}
