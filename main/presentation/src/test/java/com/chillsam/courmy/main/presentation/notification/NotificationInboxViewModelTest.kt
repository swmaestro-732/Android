package com.chillsam.courmy.main.presentation.notification

import com.chillsam.courmy.common.domain.helper.MessageHelper
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.push.NotificationEvents
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.notification.NotificationRepository
import com.chillsam.courmy.main.entity.notification.NotificationItemVO
import com.chillsam.courmy.main.entity.notification.NotificationPageVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class NotificationInboxViewModelTest {
    private val events = NotificationEvents()
    private val repository = mockk<NotificationRepository>()
    private val navigation = mockk<NavigationHelper>(relaxed = true)
    private val messages = mockk<MessageHelper>(relaxed = true)
    private val notification =
        NotificationItemVO(
            id = 30,
            type = "COURSE_LIKED",
            targetType = "COURSE",
            targetId = 4,
            actorCount = 1,
            lastActorId = 2,
            actorNickname = "유저",
            actorProfileImageUrl = null,
            courseTitle = "코스",
            isRead = false,
            createdAt = "2026-10-31T09:00:00Z",
            updatedAt = "2026-10-31T09:00:00Z",
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        coEvery { repository.unreadCount() } returns 1
        coEvery { repository.markRead(any()) } returns Unit
        coEvery { repository.markAllRead() } returns 1
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() = NotificationInboxViewModel(repository, navigation, messages, events)

    @Test
    fun `read all does not treat affected rows as remaining unread count`() =
        runTest {
            coEvery { repository.getNotifications(null) } returns NotificationPageVO(listOf(notification), false, null)
            val model = viewModel()
            model.onIntent(NotificationInboxIntent.Refresh)
            advanceUntilIdle()
            coEvery { repository.markAllRead() } returns 25
            coEvery { repository.unreadCount() } returns 2

            model.onIntent(NotificationInboxIntent.MarkAllRead)
            model.onIntent(NotificationInboxIntent.MarkAllRead)
            advanceUntilIdle()

            assertTrue(
                model.uiState.value.items
                    .single()
                    .isRead,
            )
            assertEquals(2L, model.uiState.value.unreadCount)
            assertFalse(model.uiState.value.isMarkingAll)
            coVerify(exactly = 1) { repository.markAllRead() }
        }

    @Test
    fun `course opens immediately while list request blocks read mutation`() =
        runTest {
            val response = CompletableDeferred<NotificationPageVO>()
            coEvery { repository.getNotifications(null) } coAnswers { response.await() }
            val model = viewModel()
            model.onIntent(NotificationInboxIntent.Refresh)
            runCurrent()
            model.onIntent(NotificationInboxIntent.Open(notification))
            verify(exactly = 1) { navigation.navigateDeepLink(CourseDetailPage.route("4")) }
            runCurrent()
            coVerify(exactly = 0) { repository.markRead(any()) }
            response.complete(NotificationPageVO(listOf(notification), false, null))
            advanceUntilIdle()

            assertTrue(
                model.uiState.value.items
                    .single()
                    .isRead,
            )
            verify(exactly = 1) { navigation.navigateDeepLink(CourseDetailPage.route("4")) }
        }

    @Test
    fun `failed read keeps unread item but still opens course`() =
        runTest {
            coEvery { repository.getNotifications(null) } returns NotificationPageVO(listOf(notification), false, null)
            coEvery { repository.markRead(30) } throws IOException("offline")
            val model = viewModel()
            model.onIntent(NotificationInboxIntent.Refresh)
            advanceUntilIdle()
            model.onIntent(NotificationInboxIntent.Open(notification))
            verify(exactly = 1) { navigation.navigateDeepLink(CourseDetailPage.route("4")) }
            coVerify(exactly = 0) { repository.markRead(any()) }
            advanceUntilIdle()

            assertFalse(
                model.uiState.value.items
                    .single()
                    .isRead,
            )
            verify { messages.showToast("읽음 상태를 저장하지 못했어요.") }
            verify { navigation.navigateDeepLink(CourseDetailPage.route("4")) }
        }

    @Test
    fun `next page deduplicates entries and suppresses duplicate loading`() =
        runTest {
            coEvery { repository.getNotifications(null) } returns NotificationPageVO(listOf(notification), true, 30)
            coEvery { repository.getNotifications(30) } returns
                NotificationPageVO(
                    listOf(notification, notification.copy(id = 29)),
                    false,
                    null,
                )
            val model = viewModel()
            model.onIntent(NotificationInboxIntent.Refresh)
            advanceUntilIdle()
            model.onIntent(NotificationInboxIntent.LoadMore)
            model.onIntent(NotificationInboxIntent.LoadMore)
            advanceUntilIdle()

            assertEquals(
                listOf(30L, 29L),
                model.uiState.value.items
                    .map { it.id },
            )
            assertFalse(model.uiState.value.hasNext)
            coVerify(exactly = 1) { repository.getNotifications(30) }
        }

    @Test
    fun `unread count failure remains unknown and list failure is not empty success`() =
        runTest {
            coEvery { repository.getNotifications(null) } throws IOException("offline")
            coEvery { repository.unreadCount() } throws IOException("offline")
            val model = viewModel()
            model.onIntent(NotificationInboxIntent.Refresh)
            advanceUntilIdle()

            assertNull(model.uiState.value.unreadCount)
            assertFalse(model.uiState.value.isLoading)
            assertEquals("알림을 불러오지 못했어요. 다시 시도해 주세요.", model.uiState.value.error)
        }

    @Test
    fun `foreground event refreshes badge without loading a list that was never opened`() =
        runTest {
            val model = viewModel()
            model.onIntent(NotificationInboxIntent.RefreshUnread)
            advanceUntilIdle()
            coEvery { repository.unreadCount() } returns 2

            events.notifyChanged()
            advanceUntilIdle()

            assertEquals(2L, model.uiState.value.unreadCount)
            coVerify(exactly = 0) { repository.getNotifications(any()) }
        }

    @Test
    fun `foreground event during an old list request reloads after that response completes`() =
        runTest {
            val response = CompletableDeferred<NotificationPageVO>()
            coEvery { repository.getNotifications(null) } coAnswers { response.await() }
            val model = viewModel()
            model.onIntent(NotificationInboxIntent.Refresh)
            runCurrent()
            events.notifyChanged()
            runCurrent()
            coEvery { repository.getNotifications(null) } returns
                NotificationPageVO(
                    listOf(notification.copy(id = 31), notification),
                    false,
                    null,
                )
            response.complete(NotificationPageVO(listOf(notification), false, null))
            advanceUntilIdle()

            assertEquals(
                listOf(31L, 30L),
                model.uiState.value.items
                    .map { it.id },
            )
            coVerify(exactly = 2) { repository.getNotifications(null) }
        }
}
