package com.chillsam.courmy.main.presentation.search

import com.chillsam.courmy.main.domain.map.MapPlaceSearchRepository
import com.chillsam.courmy.main.domain.map.SuggestMapPlacesUseCase
import com.chillsam.courmy.main.domain.search.RecentSearchRepository
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapPlacesVO
import com.chillsam.courmy.main.presentation.testResources
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
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
class SearchViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeRepository()
    private val recentSearchRepository = FakeRecentSearchRepository()

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(placeSearch: Boolean = true) =
        SearchViewModel(SuggestMapPlacesUseCase(repository), recentSearchRepository, resources = testResources()).also {
            it.onIntent(SearchIntent.Load(placeSearch))
        }

    @Test
    fun `장소 입력 후 499ms에는 요청하지 않고 500ms에 제안을 조회한다`() =
        runTest(dispatcher) {
            val places = listOf(place(1))
            repository.respond = { places }
            val vm = viewModel()
            vm.onIntent(SearchIntent.ChangeQuery("  카페  "))

            advanceTimeBy(499)
            runCurrent()
            assertTrue(repository.queries.isEmpty())
            assertTrue(vm.uiState.value.suggestionsLoading)
            advanceTimeBy(1)
            runCurrent()

            assertEquals(listOf("카페"), repository.queries)
            assertEquals(places, vm.uiState.value.suggestions)
            assertFalse(vm.uiState.value.suggestionsLoading)
        }

    @Test
    fun `연속 입력은 마지막 키워드에서 500ms 후 한 번만 요청한다`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(SearchIntent.ChangeQuery("성"))
            advanceTimeBy(300)
            vm.onIntent(SearchIntent.ChangeQuery("성수"))
            advanceTimeBy(499)
            runCurrent()
            assertTrue(repository.queries.isEmpty())
            advanceTimeBy(1)
            runCurrent()
            assertEquals(listOf("성수"), repository.queries)
        }

    @Test
    fun `취소를 무시하고 늦게 도착한 이전 제안은 최신 결과를 덮지 않는다`() =
        runTest(dispatcher) {
            val oldResponse = CompletableDeferred<List<MapPlaceVO>>()
            val latest = listOf(place(2))
            repository.respond = { query ->
                if (query == "이전") withContext(NonCancellable) { oldResponse.await() } else latest
            }
            val vm = viewModel()
            vm.onIntent(SearchIntent.ChangeQuery("이전"))
            advanceTimeBy(500)
            runCurrent()
            vm.onIntent(SearchIntent.ChangeQuery("최신"))
            advanceTimeBy(500)
            runCurrent()
            assertEquals(latest, vm.uiState.value.suggestions)

            oldResponse.complete(listOf(place(1)))
            advanceUntilIdle()
            assertEquals("최신", vm.uiState.value.query)
            assertEquals(latest, vm.uiState.value.suggestions)
            assertNull(vm.uiState.value.suggestionsError)
        }

    @Test
    fun `빈 입력과 코스 검색은 장소 API를 호출하지 않는다`() =
        runTest(dispatcher) {
            val placeVm = viewModel()
            placeVm.onIntent(SearchIntent.ChangeQuery("카페"))
            advanceTimeBy(200)
            placeVm.onIntent(SearchIntent.ClearQuery)
            advanceUntilIdle()
            assertFalse(placeVm.uiState.value.suggestionsLoading)
            assertTrue(
                placeVm.uiState.value.suggestions
                    .isEmpty(),
            )
            placeVm.onIntent(SearchIntent.ChangeQuery(" \t "))
            advanceUntilIdle()

            val courseVm = viewModel(placeSearch = false)
            courseVm.onIntent(SearchIntent.ChangeQuery("성수 코스"))
            advanceUntilIdle()
            assertTrue(repository.queries.isEmpty())
            assertFalse(courseVm.uiState.value.suggestionsLoading)
        }

    @Test
    fun `조회 실패와 정상 빈 결과를 구분하고 재입력하면 오류를 지운다`() =
        runTest(dispatcher) {
            repository.respond = { throw IOException("offline") }
            val vm = viewModel()
            vm.onIntent(SearchIntent.ChangeQuery("카페"))
            advanceUntilIdle()
            assertNotNull(vm.uiState.value.suggestionsError)
            assertFalse(vm.uiState.value.suggestionsLoading)

            repository.respond = { emptyList() }
            vm.onIntent(SearchIntent.ChangeQuery("없는 장소"))
            assertNull(vm.uiState.value.suggestionsError)
            advanceUntilIdle()
            assertTrue(
                vm.uiState.value.suggestions
                    .isEmpty(),
            )
            assertNull(vm.uiState.value.suggestionsError)
            assertFalse(vm.uiState.value.suggestionsLoading)
        }

    @Test
    fun `최근 검색어는 재진입에 복원되고 중복 순서와 삭제가 저장된다`() {
        val vm = viewModel()
        repeat(12) { vm.onIntent(SearchIntent.Submit("검색어 $it")) }
        vm.onIntent(SearchIntent.Submit("  검색어 5  "))
        val restored = viewModel()
        assertEquals(10, restored.uiState.value.recentKeywords.size)
        assertEquals(
            "검색어 5",
            restored.uiState.value.recentKeywords
                .first(),
        )
        assertFalse(
            restored.uiState.value.recentKeywords
                .contains("검색어 0"),
        )
        restored.onIntent(SearchIntent.RemoveRecent("검색어 5"))
        assertFalse(
            viewModel()
                .uiState.value.recentKeywords
                .contains("검색어 5"),
        )
        restored.onIntent(SearchIntent.ClearAllRecent)
        assertTrue(
            viewModel()
                .uiState.value.recentKeywords
                .isEmpty(),
        )
    }

    private class FakeRecentSearchRepository : RecentSearchRepository {
        private var keywords: List<String> = emptyList()

        override fun getAll(): List<String> = keywords

        override fun replaceAll(keywords: List<String>) {
            this.keywords = keywords
        }
    }

    private fun place(id: Long) = MapPlaceVO(id, "장소 $id", "카페", latitude = 37.5, longitude = 127.0)

    private class FakeRepository : MapPlaceSearchRepository {
        val queries = mutableListOf<String>()
        var respond: suspend (String) -> List<MapPlaceVO> = { emptyList() }

        override suspend fun suggestions(query: String): List<MapPlaceVO> {
            queries += query
            return respond(query)
        }

        override suspend fun search(
            query: String,
            bounds: MapBounds,
            category: MapCategory,
            latitude: Double?,
            longitude: Double?,
        ): MapPlacesVO = error("제안 조회에서 지도 영역 API를 호출하면 안 됩니다.")
    }
}
