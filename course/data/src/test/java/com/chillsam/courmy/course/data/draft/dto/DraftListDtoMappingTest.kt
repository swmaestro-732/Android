package com.chillsam.courmy.course.data.draft.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `GET /api/v1/courses/drafts` 응답 파싱·매핑 검증.
 *
 * 이 응답은 두 가지가 다른 목록 API 와 다르다 — `data` 가 객체가 아니라 **배열**이고, 코스 id 가
 * 문자열이 아니라 **숫자**다. 어느 쪽이 어긋나도 임시저장 화면이 통째로 예외로 죽으므로 계약을 고정한다.
 */
class DraftListDtoMappingTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `data 가 배열인 실제 응답을 파싱한다`() {
        val envelope = json.decodeFromString<DraftListEnvelope>(RESPONSE)

        assertEquals(listOf(41L, 40L), envelope.data?.map { it.id })
    }

    @Test
    fun `제목이 비면 목록 표시용 기본 제목으로 채운다`() {
        val vo = json.decodeFromString<DraftListEnvelope>(RESPONSE).toVOList()

        // 임시저장은 제목 없이도 만들 수 있어 서버가 빈 문자열을 준다.
        assertEquals("제목 없는 코스", vo[0].title)
        assertEquals("임시저장 테스트", vo[1].title)
    }

    /** 마이크로초까지 오는 UTC 문자열을 밀리초로 바꾼다. 소수 이하를 통째로 밀리초로 읽으면 15분 넘게 밀린다. */
    @Test
    fun `createdAt 을 epoch millis 로 바꾼다`() {
        val vo = json.decodeFromString<DraftListEnvelope>(RESPONSE).toVOList()

        // 2026-08-08T12:14:07Z
        assertEquals(1786191247000L, vo[1].savedAtMillis)
    }

    @Test
    fun `createdAt 을 못 읽으면 0 으로 떨어뜨린다`() {
        val vo = DraftListEnvelope(data = listOf(DraftSummaryDTO(id = 1L, createdAt = "어제"))).toVOList()

        assertEquals(0L, vo.single().savedAtMillis)
    }

    /** id 가 없으면 이어서 작성도 삭제도 걸 수 없다. 아무 동작도 안 하는 항목을 목록에 남기지 않는다. */
    @Test
    fun `id 없는 항목은 버린다`() {
        val vo = DraftListEnvelope(data = listOf(DraftSummaryDTO(id = null, title = "이름만"))).toVOList()

        assertTrue(vo.isEmpty())
    }

    private companion object {
        /** 운영 서버 실제 응답(발췌). */
        val RESPONSE =
            """
            {"code":2000,"message":null,"data":[
              {"id":41,"authorId":1,"title":"","coverImageUrl":null,"theme":null,
               "likesCnt":0,"savesCnt":0,"createdAt":"2026-08-08T12:14:07.923816Z"},
              {"id":40,"authorId":1,"title":"임시저장 테스트","coverImageUrl":null,"theme":null,
               "likesCnt":0,"savesCnt":0,"createdAt":"2026-08-08T12:14:07.452341Z"}
            ]}
            """.trimIndent()
    }
}
