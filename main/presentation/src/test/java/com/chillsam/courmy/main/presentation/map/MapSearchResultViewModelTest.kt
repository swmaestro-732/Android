package com.chillsam.courmy.main.presentation.map

import com.chillsam.courmy.main.domain.map.MapPlaceSearchRepository
import com.chillsam.courmy.main.domain.map.SearchMapPlacesUseCase
import com.chillsam.courmy.main.domain.map.SuggestMapPlacesUseCase
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.map.MapPlacesVO
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MapSearchResultViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val bounds = MapBounds(35.1, 129.0, 35.2, 129.1)

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(
        suggestions: suspend (String) -> List<MapPlaceVO> = { emptyList() },
        search: suspend (String, MapBounds) -> MapPlacesVO,
    ): MapSearchResultViewModel {
        val repository =
            object : MapPlaceSearchRepository {
                override suspend fun suggestions(query: String): List<MapPlaceVO> = suggestions(query)

                override suspend fun search(
                    query: String,
                    bounds: MapBounds,
                    latitude: Double?,
                    longitude: Double?,
                ): MapPlacesVO = search(query, bounds)
            }
        return MapSearchResultViewModel(
            SearchMapPlacesUseCase(repository),
            mockk(),
            SuggestMapPlacesUseCase(repository),
        )
    }

    @Test
    fun `실제 영역이 준비된 뒤 검색하고 같은 영역은 재조회하지 않으며 클러스터를 보존한다`() =
        runTest(dispatcher) {
            val calls = mutableListOf<Pair<String, MapBounds>>()
            val clusters = listOf(MapClusterVO(35.15, 129.05, 5))
            val vm =
                viewModel { query, area ->
                    calls += query to area
                    MapPlacesVO(totalCount = 5, clusters = clusters)
                }
            vm.onIntent(MapSearchResultIntent.Load("카페", 35.15, 129.05))
            advanceUntilIdle()
            assertTrue(calls.isEmpty())
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            advanceUntilIdle()
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            advanceUntilIdle()
            assertEquals(listOf("카페" to bounds), calls)
            assertEquals(clusters, vm.uiState.value.clusters)
            assertFalse(vm.uiState.value.isLoading)
            assertFalse(vm.uiState.value.canLoadMore)
        }

    @Test
    fun `지도 이동 중 기존 선택 핀을 유지하고 취소된 이전 영역 응답을 버린다`() =
        runTest(dispatcher) {
            val oldResponse = CompletableDeferred<MapPlacesVO>()
            val places = listOf(MapPlaceVO(id = 1, name = "카페", category = "카페", latitude = 35.15, longitude = 129.05))
            val second = bounds.copy(neLat = 35.3)
            val third = bounds.copy(neLat = 35.4)
            val vm =
                viewModel { _, area ->
                    if (area == second) {
                        withContext(NonCancellable) { oldResponse.await() }
                    } else {
                        MapPlacesVO(places = places)
                    }
                }
            vm.onIntent(MapSearchResultIntent.Load("카페"))
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            advanceUntilIdle()
            vm.onIntent(MapSearchResultIntent.SelectPlace(1))
            vm.onIntent(MapSearchResultIntent.BoundsChanged(second))
            runCurrent()
            assertEquals(places, vm.uiState.value.places)
            assertEquals(1L, vm.uiState.value.selectedPlaceId)
            vm.onIntent(MapSearchResultIntent.BoundsChanged(third))
            runCurrent()
            oldResponse.complete(MapPlacesVO(places = listOf(MapPlaceVO(id = 99, name = "이전 결과", category = "카페"))))
            advanceUntilIdle()
            assertEquals(third, vm.uiState.value.bounds)
            assertEquals(places, vm.uiState.value.places)
            assertEquals(1L, vm.uiState.value.selectedPlaceId)
        }

    @Test
    fun `영역이 검색어보다 먼저 도착해도 최신 검색어로 다시 조회한다`() =
        runTest(dispatcher) {
            val calls = mutableListOf<String>()
            val vm =
                viewModel { query, _ ->
                    calls += query
                    MapPlacesVO()
                }
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            advanceUntilIdle()
            vm.onIntent(MapSearchResultIntent.Load("부산 카페", 35.15, 129.05))
            advanceUntilIdle()
            assertEquals("부산 카페", calls.last())
            assertEquals("부산 카페", vm.uiState.value.query)
            assertEquals(bounds, vm.uiState.value.bounds)
        }

    @Test
    fun `현재 영역에 장소가 있으면 제안을 조회하거나 자동 이동하지 않는다`() =
        runTest(dispatcher) {
            val places = listOf(place(1))
            val vm =
                viewModel(
                    suggestions = { error("현재 영역에 결과가 있으면 제안을 호출하면 안 됩니다.") },
                    search = { _, _ -> MapPlacesVO(places = places) },
                )
            vm.onIntent(MapSearchResultIntent.Load("카페"))
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            advanceUntilIdle()

            assertEquals(places, vm.uiState.value.places)
            assertNull(vm.uiState.value.cameraMove)
            assertNull(vm.uiState.value.errorMessage)
            assertFalse(vm.uiState.value.isCameraMoving)
        }

    @Test
    fun `빈 영역은 서버 순서의 첫 유효 제안으로 이동하고 도착 후 새 영역을 조회한다`() =
        runTest(dispatcher) {
            val calls = mutableListOf<MapBounds>()
            val destination = place(20, 37.5, 127.0)
            val targetBounds = MapBounds(37.4, 126.9, 37.6, 127.1)
            val vm =
                viewModel(
                    suggestions = { listOf(place(0), place(10, Double.NaN, 127.0), destination, place(30)) },
                    search = { _, area ->
                        calls += area
                        if (area == bounds) MapPlacesVO() else MapPlacesVO(places = listOf(destination))
                    },
                )
            vm.onIntent(MapSearchResultIntent.Load("카페"))
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            advanceUntilIdle()
            val move = requireNotNull(vm.uiState.value.cameraMove)
            assertEquals(37.5, move.latitude, 0.0)
            assertEquals(127.0, move.longitude, 0.0)
            assertTrue(vm.uiState.value.isCameraMoving)
            assertTrue(vm.uiState.value.isLoading)

            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds.copy(neLat = 36.0)))
            advanceUntilIdle()
            assertEquals(listOf(bounds), calls)
            vm.onIntent(MapSearchResultIntent.DestinationReached(move.id, targetBounds))
            advanceUntilIdle()
            assertEquals(listOf(bounds, targetBounds), calls)
            assertEquals(listOf(destination), vm.uiState.value.places)
            assertNull(vm.uiState.value.cameraMove)
            assertFalse(vm.uiState.value.isCameraMoving)
            assertFalse(vm.uiState.value.isLoading)
        }

    @Test
    fun `선택한 제안은 초기 지도 조회 없이 이동하고 올바른 도착 이벤트만 수락한다`() =
        runTest(dispatcher) {
            val calls = mutableListOf<MapBounds>()
            val targetBounds = MapBounds(37.4, 126.9, 37.6, 127.1)
            val vm =
                viewModel(
                    suggestions = { error("선택한 장소가 있으면 제안을 다시 조회하면 안 됩니다.") },
                    search = { _, area ->
                        calls += area
                        MapPlacesVO(places = listOf(place(20, 37.5, 127.0)))
                    },
                )
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            vm.onIntent(MapSearchResultIntent.Load("카페", targetLatitude = 37.5, targetLongitude = 127.0))
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            advanceUntilIdle()
            assertTrue(calls.isEmpty())
            val move = requireNotNull(vm.uiState.value.cameraMove)
            vm.onIntent(MapSearchResultIntent.DestinationReached(move.id + 1, targetBounds))
            advanceUntilIdle()
            assertTrue(calls.isEmpty())
            assertEquals(move, vm.uiState.value.cameraMove)

            vm.onIntent(MapSearchResultIntent.DestinationReached(move.id, targetBounds))
            advanceUntilIdle()
            assertEquals(listOf(targetBounds), calls)
            assertFalse(vm.uiState.value.isCameraMoving)
        }

    @Test
    fun `이동 시작은 진행 요청을 취소하고 이동 중 늦은 응답을 무시한 뒤 도착한 영역만 표시한다`() =
        runTest(dispatcher) {
            val oldResponse = CompletableDeferred<MapPlacesVO>()
            val destination = place(2)
            val nextBounds = bounds.copy(neLat = 35.4)
            var oldRequestCancelled = false
            val vm =
                viewModel { _, area ->
                    if (area == bounds) {
                        try {
                            withContext(NonCancellable) { oldResponse.await() }
                        } finally {
                            oldRequestCancelled = true
                        }
                    } else {
                        MapPlacesVO(places = listOf(destination))
                    }
                }
            vm.onIntent(MapSearchResultIntent.Load("카페"))
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            runCurrent()
            vm.onIntent(MapSearchResultIntent.CameraMoving)
            assertTrue(vm.uiState.value.isCameraMoving)
            assertTrue(vm.uiState.value.isLoading)
            oldResponse.complete(MapPlacesVO(places = listOf(place(99))))
            advanceUntilIdle()
            assertTrue(oldRequestCancelled)
            assertTrue(
                vm.uiState.value.places
                    .isEmpty(),
            )
            assertTrue(vm.uiState.value.isCameraMoving)

            vm.onIntent(MapSearchResultIntent.BoundsChanged(nextBounds))
            advanceUntilIdle()
            assertEquals(listOf(destination), vm.uiState.value.places)
            assertEquals(nextBounds, vm.uiState.value.bounds)
            assertFalse(vm.uiState.value.isCameraMoving)
            assertFalse(vm.uiState.value.isLoading)
        }

    @Test
    fun `일치하는 장소가 없으면 빈 결과를 표시하고 지도 이동마다 제안을 반복하지 않는다`() =
        runTest(dispatcher) {
            val suggestions = mutableListOf<String>()
            val vm =
                viewModel(
                    suggestions = {
                        suggestions += it
                        emptyList()
                    },
                    search = { _, _ -> MapPlacesVO() },
                )
            vm.onIntent(MapSearchResultIntent.Load("없는 장소"))
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds))
            advanceUntilIdle()
            assertEquals(listOf("없는 장소"), suggestions)
            assertTrue(
                vm.uiState.value.places
                    .isEmpty(),
            )
            assertNull(vm.uiState.value.cameraMove)
            assertFalse(vm.uiState.value.isLoading)

            vm.onIntent(MapSearchResultIntent.CameraMoving)
            vm.onIntent(MapSearchResultIntent.BoundsChanged(bounds.copy(neLat = 35.4)))
            advanceUntilIdle()
            vm.onIntent(MapSearchResultIntent.Retry)
            advanceUntilIdle()
            assertEquals(listOf("없는 장소"), suggestions)
            assertNull(vm.uiState.value.cameraMove)
        }

    private fun place(
        id: Long,
        latitude: Double = 35.15,
        longitude: Double = 129.05,
    ) = MapPlaceVO(id, "장소 $id", "카페", latitude = latitude, longitude = longitude)
}
