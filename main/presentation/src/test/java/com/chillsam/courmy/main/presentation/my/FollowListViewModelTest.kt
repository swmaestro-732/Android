package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.domain.follow.FollowListRepository
import com.chillsam.courmy.main.domain.follow.GetFollowListUseCase
import com.chillsam.courmy.main.domain.profile.ProfileRepository
import com.chillsam.courmy.main.domain.user.ToggleFollowUseCase
import com.chillsam.courmy.main.entity.my.FollowUserVO
import io.mockk.mockk
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

@OptIn(ExperimentalCoroutinesApi::class)
class FollowListViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel() =
        FollowListViewModel(
            GetFollowListUseCase(repository),
            ToggleFollowUseCase(mockk<ProfileRepository>(relaxed = true)),
        )

    @Test
    fun `첫 진입은 전달받은 대상과 초기 탭의 첫 페이지를 조회한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()

            viewModel.onIntent(FollowListIntent.Refresh(userId = 42, initialTab = FollowTab.FOLLOWING))
            advanceUntilIdle()

            assertEquals(listOf(Request(FollowTab.FOLLOWING, 42, null)), repository.requests)
            assertEquals(FollowTab.FOLLOWING, viewModel.uiState.value.selectedTab)
            assertEquals(
                listOf(1L),
                viewModel.uiState.value.users
                    .map { it.id },
            )
        }

    @Test
    fun `같은 방문에서 이미 조회한 탭으로 돌아오면 캐시를 사용한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.onIntent(FollowListIntent.Refresh(null, FollowTab.FOLLOWER))
            advanceUntilIdle()
            viewModel.onIntent(FollowListIntent.SelectTab(FollowTab.FOLLOWING))
            advanceUntilIdle()

            viewModel.onIntent(FollowListIntent.SelectTab(FollowTab.FOLLOWER))
            advanceUntilIdle()

            assertEquals(2, repository.requests.size)
            assertEquals(FollowTab.FOLLOWER, viewModel.uiState.value.selectedTab)
            assertEquals(setOf(FollowTab.FOLLOWER, FollowTab.FOLLOWING), viewModel.uiState.value.loadedTabs)
        }

    @Test
    fun `복귀 시 선택한 탭을 유지하고 새 목록으로 교체하며 다른 탭도 다시 조회한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.onIntent(FollowListIntent.Refresh(null, FollowTab.FOLLOWER))
            advanceUntilIdle()
            viewModel.onIntent(FollowListIntent.SelectTab(FollowTab.FOLLOWING))
            advanceUntilIdle()
            repository.page = page(2)

            viewModel.onIntent(FollowListIntent.Refresh(null, FollowTab.FOLLOWER))
            advanceUntilIdle()

            assertEquals(Request(FollowTab.FOLLOWING, null, null), repository.requests.last())
            assertEquals(FollowTab.FOLLOWING, viewModel.uiState.value.selectedTab)
            assertEquals(
                listOf(2L),
                viewModel.uiState.value.users
                    .map { it.id },
            )
            assertEquals(setOf(FollowTab.FOLLOWING), viewModel.uiState.value.loadedTabs)

            viewModel.onIntent(FollowListIntent.SelectTab(FollowTab.FOLLOWER))
            advanceUntilIdle()
            assertEquals(4, repository.requests.size)
            assertEquals(
                listOf(2L),
                viewModel.uiState.value.users
                    .map { it.id },
            )
        }

    @Test
    fun `대상이 바뀌면 이전 목록과 커서를 비우고 초기 탭을 선택한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            repository.page = page(1, "old-next")
            viewModel.onIntent(FollowListIntent.Refresh(null, FollowTab.FOLLOWER))
            advanceUntilIdle()
            viewModel.onIntent(FollowListIntent.SelectTab(FollowTab.FOLLOWING))
            advanceUntilIdle()
            repository.page = page(2)

            viewModel.onIntent(FollowListIntent.Refresh(42, FollowTab.FOLLOWER))
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertEquals(Request(FollowTab.FOLLOWER, 42, null), repository.requests.last())
            assertEquals(FollowTab.FOLLOWER, state.selectedTab)
            assertEquals(listOf(2L), state.followers.map { it.id })
            assertTrue(state.followings.isEmpty())
            assertNull(state.followerCursor)
            assertNull(state.followingCursor)
            assertFalse(state.followingHasNext)
            assertEquals(setOf(FollowTab.FOLLOWER), state.loadedTabs)
        }

    @Test
    fun `복귀 시 진행 중인 다음 페이지를 취소해 새 목록에 옛 결과가 붙지 않는다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            repository.page = page(1, "old-next")
            viewModel.onIntent(FollowListIntent.Refresh(null, FollowTab.FOLLOWER))
            advanceUntilIdle()
            val pending = CompletableDeferred<CursorPageVO<FollowUserVO>>()
            repository.pendingNextPage = pending
            viewModel.onIntent(FollowListIntent.LoadMore)
            runCurrent()
            assertTrue(viewModel.uiState.value.isLoadingMore)
            repository.page = page(2)

            viewModel.onIntent(FollowListIntent.Refresh(null, FollowTab.FOLLOWER))
            runCurrent()
            pending.complete(page(99))
            advanceUntilIdle()

            assertTrue(repository.nextPageCancelled)
            assertEquals(
                listOf(2L),
                viewModel.uiState.value.users
                    .map { it.id },
            )
            assertFalse(viewModel.uiState.value.isLoadingMore)
            assertEquals(Request(FollowTab.FOLLOWER, null, null), repository.requests.last())
        }

    private data class Request(
        val tab: FollowTab,
        val userId: Long?,
        val cursor: String?,
    )

    private class FakeRepository : FollowListRepository {
        val requests = mutableListOf<Request>()
        var page = page(1)
        var pendingNextPage: CompletableDeferred<CursorPageVO<FollowUserVO>>? = null
        var nextPageCancelled = false

        override suspend fun getFollowers(
            userId: Long?,
            size: Int,
            cursor: String?,
        ) = respond(Request(FollowTab.FOLLOWER, userId, cursor))

        override suspend fun getFollowings(
            userId: Long?,
            size: Int,
            cursor: String?,
        ) = respond(Request(FollowTab.FOLLOWING, userId, cursor))

        private suspend fun respond(request: Request): CursorPageVO<FollowUserVO> {
            requests += request
            val pending = pendingNextPage
            if (request.cursor != null && pending != null) {
                try {
                    return pending.await()
                } catch (error: kotlinx.coroutines.CancellationException) {
                    nextPageCancelled = true
                    throw error
                }
            }
            return page
        }
    }

    private companion object {
        fun page(
            id: Long,
            cursor: String? = null,
        ) = CursorPageVO(
            items = listOf(FollowUserVO(id, "사용자 $id", "user$id")),
            nextCursor = cursor,
            hasNext = cursor != null,
        )
    }
}
