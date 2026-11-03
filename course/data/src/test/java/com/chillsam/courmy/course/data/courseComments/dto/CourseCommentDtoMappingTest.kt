package com.chillsam.courmy.course.data.courseComments.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseCommentDtoMappingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `댓글 응답의 작성자 닉네임과 사진을 본인 여부와 관계없이 보존한다`() {
        val response =
            json.decodeFromString<CourseCommentListEnvelope>(
                """
                {
                  "code": 0,
                  "message": "성공",
                  "data": {
                    "items": [
                      {
                        "id": 12,
                        "authorId": 7,
                        "authorNickname": "내 닉네임",
                        "authorProfileImageUrl": "https://example.com/me.png",
                        "content": "내 댓글",
                        "createdAt": "2026-11-02T04:01:52.940Z",
                        "updatedAt": "2026-11-02T04:01:52.940Z",
                        "isMine": true
                      },
                      {
                        "id": 11,
                        "authorId": 8,
                        "authorNickname": "다른 작성자",
                        "authorProfileImageUrl": "https://example.com/other.png",
                        "content": "다른 댓글",
                        "isMine": false
                      }
                    ],
                    "hasNext": true
                  },
                  "fieldErrors": []
                }
                """.trimIndent(),
            )
        val page = requireNotNull(response.data).toVO()

        assertEquals(listOf("내 닉네임", "다른 작성자"), page.items.map { it.authorNickname })
        assertEquals(
            listOf("https://example.com/me.png", "https://example.com/other.png"),
            page.items.map { it.authorProfileImageUrl },
        )
        assertEquals(listOf(7L, 8L), page.items.map { it.authorId })
        assertTrue(page.items[0].isMine)
        assertFalse(page.items[1].isMine)
        assertTrue(page.hasNext)
        assertEquals("11", page.nextCursor)
    }

    @Test
    fun `작성자 프로필 필드가 누락되거나 null이어도 댓글을 유지한다`() {
        val response =
            json.decodeFromString<CourseCommentListDTO>(
                """
                {
                  "items": [
                    {"id": 2, "authorId": 7, "content": "이전 응답", "isMine": true},
                    {"id": 1, "authorNickname": null, "authorProfileImageUrl": null, "content": "빈 프로필"}
                  ],
                  "hasNext": false
                }
                """.trimIndent(),
            )
        val page = response.toVO()

        assertEquals(2, page.items.size)
        assertEquals(listOf(7L, 0L), page.items.map { it.authorId })
        assertEquals(listOf("", ""), page.items.map { it.authorNickname })
        assertEquals(listOf("", ""), page.items.map { it.authorProfileImageUrl })
        assertEquals(listOf("이전 응답", "빈 프로필"), page.items.map { it.content })
        assertTrue(page.items.first().isMine)
    }
}
