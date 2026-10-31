package com.chillsam.courmy.main.data.notification

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class NotificationRepositoryTest {
    private val api = mockk<NotificationApiService>()
    private val repository = NotificationRepositoryImpl(NotificationDataSource(api))
    private val item =
        NotificationItemDTO(
            id = 7,
            type = "COURSE_LIKED",
            targetType = "COURSE",
            targetId = 42,
            lastActorId = 9,
            actorCount = 3,
            actorNickname = "나영",
            isRead = true,
            courseTitle = "동네 산책",
            createdAt = "2026-10-31T01:00:00Z",
            updatedAt = "2026-10-31T01:03:00Z",
        )

    @Test fun `묶음 횟수와 스냅샷 및 서버 순서를 그대로 보존한다`() =
        runBlocking {
            coEvery { api.getNotifications(null, 20) } returns
                Response.success(
                    NotificationEnvelope(2000, data = NotificationPageDTO(listOf(item, item.copy(id = 6)), true, 6)),
                )
            val page = repository.getNotifications()
            assertEquals(listOf(7L, 6L), page.items.map { it.id })
            assertEquals(3, page.items.first().actorCount)
            assertEquals("나영", page.items.first().actorNickname)
            assertEquals("동네 산책", page.items.first().courseTitle)
            assertEquals("2026-10-31T01:03:00Z", page.items.first().updatedAt)
            assertTrue(page.items.first().isRead)
            assertNull(page.items.first().actorProfileImageUrl)
            assertEquals(6L, page.nextCursor)
        }

    @Test fun `필수 식별자 누락이나 잘못된 값은 빈 알림으로 대체하지 않는다`() {
        listOf(item.copy(id = null), item.copy(id = 0), item.copy(targetId = -1), item.copy(lastActorId = null))
            .forEach { broken -> assertThrows(IllegalArgumentException::class.java) { broken.toVO() } }
    }

    @Test fun `다음 페이지가 있다면 유효한 커서와 항목이 필요하다`() {
        assertThrows(IllegalArgumentException::class.java) {
            NotificationPageDTO(listOf(item), true, null).toVO()
        }
        assertThrows(IllegalArgumentException::class.java) {
            NotificationPageDTO(emptyList(), true, 5).toVO()
        }
        assertFalse(NotificationPageDTO(emptyList(), false, null).toVO().hasNext)
    }

    @Test fun `같은 커서를 되돌려주는 응답은 무한 페이지 조회로 이어지지 않는다`() {
        coEvery { api.getNotifications(7, 20) } returns
            Response.success(
                NotificationEnvelope(2000, data = NotificationPageDTO(listOf(item), true, 7)),
            )
        assertThrows(IllegalArgumentException::class.java) { runBlocking { repository.getNotifications(7) } }
    }

    @Test fun `HTTP 성공이어도 실패 code를 읽기 성공이나 빈 목록으로 바꾸지 않는다`() {
        coEvery { api.getNotifications(null, 20) } returns
            Response.success(
                NotificationEnvelope(code = 4002, message = "알림을 조회할 수 없습니다."),
            )
        val failure = assertThrows(IllegalStateException::class.java) { runBlocking { repository.getNotifications() } }
        assertEquals("알림을 조회할 수 없습니다.", failure.message)
    }

    @Test fun `읽음 응답은 data 없이 성공하고 전체 읽음은 변경된 행 수를 반환한다`() =
        runBlocking {
            coEvery { api.markRead(7) } returns Response.success(NotificationEnvelope(2000))
            coEvery { api.markAllRead() } returns
                Response.success(NotificationEnvelope(2000, data = NotificationCountDTO(4)))
            repository.markRead(7)
            assertEquals(4L, repository.markAllRead())
            coVerify(exactly = 1) { api.markRead(7) }
        }

    @Test fun `미확인 개수 누락은 0개로 표시하지 않는다`() {
        coEvery { api.unreadCount() } returns
            Response.success(NotificationEnvelope(2000, data = NotificationCountDTO()))
        assertThrows(IllegalArgumentException::class.java) { runBlocking { repository.unreadCount() } }
    }

    @Test fun `호출 취소는 그대로 전달한다`() {
        coEvery { api.getNotifications(null, 20) } throws CancellationException("test cancellation")
        assertThrows(CancellationException::class.java) { runBlocking { repository.getNotifications() } }
    }

    @Test fun `서버 응답의 isRead와 추가 필드를 역직렬화한다`() {
        val dto =
            Json { ignoreUnknownKeys = true }.decodeFromString<NotificationItemDTO>(
                """{"id":7,"type":"COURSE_COMMENTED","targetType":"COURSE","targetId":42,
                "lastActorId":9,"isRead":true,"futureField":"ignored"}""",
            )
        assertTrue(dto.toVO().isRead)
    }
}
