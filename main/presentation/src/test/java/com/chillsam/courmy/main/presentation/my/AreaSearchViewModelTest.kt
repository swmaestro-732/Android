package com.chillsam.courmy.main.presentation.my

import android.util.Log
import com.chillsam.courmy.main.domain.area.AreaRepository
import com.chillsam.courmy.main.domain.area.SearchAreasUseCase
import com.chillsam.courmy.main.entity.area.AreaVO
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AreaSearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Log::class)
        every { Log.w(any(), any<String>(), any<Throwable>()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun viewModel(search: suspend (String) -> List<AreaVO>) =
        AreaSearchViewModel(
            SearchAreasUseCase(
                object : AreaRepository {
                    override suspend fun searchAreas(keyword: String) = search(keyword)
                },
            ),
        )

    @Test
    fun `별칭 검색은 실제 지역 후보를 모두 유지하고 코드 없는 후보는 제외한다`() =
        runTest(dispatcher) {
            val areas = listOf(area("1120011400", "성수동1가"), area("1120011500", "성수동2가"))
            val viewModel = viewModel { areas + area("", "성수동") + area(" ", "잘못된 지역") }

            viewModel.onIntent(AreaSearchIntent.QueryChanged("성수동"))
            advanceUntilIdle()

            assertEquals(areas, viewModel.uiState.value.results)
            assertFalse(viewModel.uiState.value.searchFailed)
            assertFalse(viewModel.uiState.value.isSearching)
        }

    @Test
    fun `빠르게 바꾼 검색어는 마지막 값만 조회한다`() =
        runTest(dispatcher) {
            val requests = mutableListOf<String>()
            val viewModel =
                viewModel {
                    requests += it
                    emptyList()
                }

            viewModel.onIntent(AreaSearchIntent.QueryChanged("성수"))
            advanceTimeBy(100)
            viewModel.onIntent(AreaSearchIntent.QueryChanged(" 연남동 "))
            advanceUntilIdle()

            assertEquals(listOf("연남동"), requests)
        }

    @Test
    fun `새 검색은 이전 결과를 즉시 지우고 늦은 이전 응답으로 덮이지 않는다`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<List<AreaVO>>()
            val latest = listOf(area("1144012400", "연남동"))
            val viewModel = viewModel { if (it == "성수동") pending.await() else latest }
            viewModel.onIntent(AreaSearchIntent.QueryChanged("성수동"))
            advanceTimeBy(250)
            runCurrent()

            viewModel.onIntent(AreaSearchIntent.QueryChanged("연남동"))
            assertTrue(
                viewModel.uiState.value.results
                    .isEmpty(),
            )
            advanceUntilIdle()
            pending.complete(listOf(area("1120011400", "성수동1가")))
            advanceUntilIdle()

            assertEquals("연남동", viewModel.uiState.value.keyword)
            assertEquals(latest, viewModel.uiState.value.results)
        }

    @Test
    fun `검색 실패는 가짜 지역 대신 빈 결과와 실패 상태로 알리고 재검색하면 해제한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel { if (it == "성수동") throw IOException("offline") else emptyList() }

            viewModel.onIntent(AreaSearchIntent.QueryChanged("성수동"))
            advanceUntilIdle()
            assertTrue(
                viewModel.uiState.value.results
                    .isEmpty(),
            )
            assertTrue(viewModel.uiState.value.searchFailed)
            assertFalse(viewModel.uiState.value.isSearching)

            viewModel.onIntent(AreaSearchIntent.QueryChanged("없는 지역"))
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.searchFailed)
        }

    @Test
    fun `검색어를 지우면 진행 중 요청을 취소하고 검색 상태를 비운다`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<List<AreaVO>>()
            val viewModel = viewModel { pending.await() }
            viewModel.onIntent(AreaSearchIntent.QueryChanged("성수동"))
            advanceTimeBy(250)
            runCurrent()

            viewModel.onIntent(AreaSearchIntent.QueryChanged(""))
            pending.complete(listOf(area("1120011400", "성수동1가")))
            advanceUntilIdle()

            assertEquals("", viewModel.uiState.value.keyword)
            assertTrue(
                viewModel.uiState.value.results
                    .isEmpty(),
            )
            assertFalse(viewModel.uiState.value.isSearching)
            assertFalse(viewModel.uiState.value.searchFailed)
        }

    private fun area(
        code: String,
        name: String,
    ) = AreaVO(code, name, name)
}
