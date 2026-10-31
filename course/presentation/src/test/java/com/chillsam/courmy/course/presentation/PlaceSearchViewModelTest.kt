package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.course.domain.PlaceRepository
import com.chillsam.courmy.course.domain.SearchExternalPlacesUseCase
import com.chillsam.courmy.course.domain.SearchPlacesUseCase
import com.chillsam.courmy.course.entity.CoursePlaceVO
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PlaceSearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakePlaceRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `등록된 검색 결과가 있으면 외부 검색 없이 커서를 유지한다`() =
        runTest(dispatcher) {
            val places = listOf(place("101"))
            repository.registered = { _, _ -> CursorPageVO(places, "next", true) }
            val viewModel = viewModel()

            viewModel.onIntent(PlaceSearchIntent.QueryChanged("카페"))
            advanceUntilIdle()

            assertEquals(places, viewModel.uiState.value.results)
            assertFalse(viewModel.uiState.value.fromMapSearch)
            assertEquals("next", viewModel.uiState.value.nextCursor)
            assertTrue(viewModel.uiState.value.hasNext)
            assertTrue(repository.externalQueries.isEmpty())
        }

    @Test
    fun `등록 결과가 없으면 같은 검색어로 외부 결과를 자동 표시한다`() =
        runTest(dispatcher) {
            val places = listOf(place("205"))
            val externalResponse = CompletableDeferred<List<CoursePlaceVO>>()
            repository.external = { externalResponse.await() }
            val viewModel = viewModel()

            viewModel.onIntent(PlaceSearchIntent.QueryChanged("  새로운 카페  "))
            advanceTimeBy(300)
            runCurrent()
            assertTrue(viewModel.uiState.value.isSearching)
            assertEquals(listOf("새로운 카페"), repository.externalQueries)

            externalResponse.complete(places)
            advanceUntilIdle()

            assertEquals(places, viewModel.uiState.value.results)
            assertTrue(viewModel.uiState.value.fromMapSearch)
            assertFalse(viewModel.uiState.value.isSearching)
            assertFalse(viewModel.uiState.value.hasNext)
            assertNull(viewModel.uiState.value.nextCursor)
            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `두 검색 모두 비어야 최종 빈 결과가 된다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()

            viewModel.onIntent(PlaceSearchIntent.QueryChanged("없는 장소"))
            advanceUntilIdle()

            assertEquals(listOf("없는 장소"), repository.externalQueries)
            assertTrue(
                viewModel.uiState.value.results
                    .isEmpty(),
            )
            assertTrue(viewModel.uiState.value.fromMapSearch)
            assertFalse(viewModel.uiState.value.isSearching)
            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `등록 검색 실패는 빈 결과로 취급하여 외부 검색하지 않는다`() =
        runTest(dispatcher) {
            repository.registered = { _, _ -> throw IOException("offline") }
            val viewModel = viewModel()

            viewModel.onIntent(PlaceSearchIntent.QueryChanged("카페"))
            advanceUntilIdle()

            assertTrue(repository.externalQueries.isEmpty())
            assertNotNull(viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.isSearching)
            assertFalse(viewModel.uiState.value.fromMapSearch)
        }

    @Test
    fun `외부 검색 실패는 정상 빈 결과와 구분한다`() =
        runTest(dispatcher) {
            repository.external = { throw IOException("external unavailable") }
            val viewModel = viewModel()

            viewModel.onIntent(PlaceSearchIntent.QueryChanged("새 장소"))
            advanceUntilIdle()

            assertEquals(listOf("새 장소"), repository.externalQueries)
            assertNotNull(viewModel.uiState.value.errorMessage)
            assertFalse(viewModel.uiState.value.isSearching)
        }

    @Test
    fun `새 검색어는 진행 중 외부 검색을 취소하고 늦은 결과로 덮이지 않는다`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<List<CoursePlaceVO>>()
            val latest = listOf(place("new"))
            var externalCancelled = false
            repository.registered = { query, _ ->
                if (query == "새 검색") CursorPageVO(items = latest) else CursorPageVO()
            }
            repository.external = {
                try {
                    pending.await()
                } finally {
                    externalCancelled = true
                }
            }
            val viewModel = viewModel()
            viewModel.onIntent(PlaceSearchIntent.QueryChanged("이전 검색"))
            advanceTimeBy(300)
            runCurrent()
            assertEquals(listOf("이전 검색"), repository.externalQueries)

            viewModel.onIntent(PlaceSearchIntent.QueryChanged("새 검색"))
            runCurrent()
            assertTrue(externalCancelled)
            pending.complete(listOf(place("old")))
            advanceUntilIdle()

            assertEquals("새 검색", viewModel.uiState.value.query)
            assertEquals(latest, viewModel.uiState.value.results)
            assertFalse(viewModel.uiState.value.fromMapSearch)
            assertFalse(viewModel.uiState.value.isSearching)
        }

    @Test
    fun `외부 조회 중 검색어를 지우면 로딩과 결과를 즉시 비운다`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<List<CoursePlaceVO>>()
            repository.external = { pending.await() }
            val viewModel = viewModel()
            viewModel.onIntent(PlaceSearchIntent.QueryChanged("검색"))
            advanceTimeBy(300)
            runCurrent()
            assertTrue(viewModel.uiState.value.isSearching)

            viewModel.onIntent(PlaceSearchIntent.QueryChanged(""))
            runCurrent()
            assertFalse(viewModel.uiState.value.isSearching)
            assertTrue(
                viewModel.uiState.value.results
                    .isEmpty(),
            )
            pending.complete(listOf(place("stale")))
            advanceUntilIdle()

            assertEquals("", viewModel.uiState.value.query)
            assertTrue(
                viewModel.uiState.value.results
                    .isEmpty(),
            )
            assertFalse(viewModel.uiState.value.fromMapSearch)
            assertNull(viewModel.uiState.value.errorMessage)
        }

    @Test
    fun `빠른 입력은 마지막 검색어만 조회하고 외부 결과는 추가 페이지를 호출하지 않는다`() =
        runTest(dispatcher) {
            repository.external = { listOf(place("external")) }
            val viewModel = viewModel()
            viewModel.onIntent(PlaceSearchIntent.QueryChanged("성"))
            advanceTimeBy(100)
            viewModel.onIntent(PlaceSearchIntent.QueryChanged("성수"))
            advanceUntilIdle()
            viewModel.onIntent(PlaceSearchIntent.LoadMore)
            advanceUntilIdle()

            assertEquals(listOf("성수" to null), repository.registeredQueries)
            assertEquals(listOf("성수"), repository.externalQueries)
            assertFalse(viewModel.uiState.value.isLoadingMore)
        }

    @Test
    fun `수동 지도 검색은 진행 중 페이지 요청을 취소하고 검색어와 커서를 교체한다`() =
        runTest(dispatcher) {
            val pendingPage = CompletableDeferred<CursorPageVO<CoursePlaceVO>>()
            var pageCancelled = false
            repository.registered = { _, cursor ->
                if (cursor == null) {
                    CursorPageVO(listOf(place("first")), "next", true)
                } else {
                    try {
                        pendingPage.await()
                    } finally {
                        pageCancelled = true
                    }
                }
            }
            val external = listOf(place("manual"))
            repository.external = { external }
            val viewModel = viewModel()
            viewModel.onIntent(PlaceSearchIntent.QueryChanged("등록 검색"))
            advanceUntilIdle()
            viewModel.onIntent(PlaceSearchIntent.LoadMore)
            runCurrent()
            assertTrue(viewModel.uiState.value.isLoadingMore)

            viewModel.onIntent(PlaceSearchIntent.SearchOnMap("  지도 검색  "))
            runCurrent()
            assertTrue(pageCancelled)
            pendingPage.complete(CursorPageVO(items = listOf(place("stale page"))))
            advanceUntilIdle()

            assertEquals("지도 검색", viewModel.uiState.value.query)
            assertEquals(external, viewModel.uiState.value.results)
            assertEquals(listOf("지도 검색"), repository.externalQueries)
            assertTrue(viewModel.uiState.value.fromMapSearch)
            assertFalse(viewModel.uiState.value.hasNext)
            assertFalse(viewModel.uiState.value.isLoadingMore)
            assertNull(viewModel.uiState.value.nextCursor)
        }

    private fun viewModel() =
        PlaceSearchViewModel(SearchPlacesUseCase(repository), SearchExternalPlacesUseCase(repository))

    private fun place(id: String) = CoursePlaceVO(id = id, name = "장소 $id")

    private class FakePlaceRepository : PlaceRepository {
        val registeredQueries = mutableListOf<Pair<String, String?>>()
        val externalQueries = mutableListOf<String>()
        var registered: suspend (String, String?) -> CursorPageVO<CoursePlaceVO> = { _, _ -> CursorPageVO() }
        var external: suspend (String) -> List<CoursePlaceVO> = { emptyList() }

        override suspend fun searchPlaces(
            query: String,
            cursor: String?,
        ): CursorPageVO<CoursePlaceVO> {
            registeredQueries += query to cursor
            return registered(query, cursor)
        }

        override suspend fun searchExternalPlaces(query: String): List<CoursePlaceVO> {
            externalQueries += query
            return external(query)
        }
    }
}
