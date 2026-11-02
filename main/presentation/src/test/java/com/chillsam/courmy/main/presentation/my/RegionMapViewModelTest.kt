package com.chillsam.courmy.main.presentation.my

import com.chillsam.courmy.main.domain.area.GetAreaMapUseCase
import com.chillsam.courmy.main.domain.my.GetMyProfileUseCase
import com.chillsam.courmy.main.domain.profile.SaveInterestsUseCase
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaMapVO
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.entity.my.MyProfileVO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RegionMapViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val getAreaMap = mockk<GetAreaMapUseCase>()
    private val getMyProfile = mockk<GetMyProfileUseCase>()
    private val saveInterests = mockk<SaveInterestsUseCase>()
    private val seoul = area("11", "서울특별시", AreaLevel.SIDO)
    private val seongdong = area("11200", "성동구", AreaLevel.SIGUNGU)
    private val mapo = area("11440", "마포구", AreaLevel.SIGUNGU)
    private val gyeonggi = area("41", "경기도", AreaLevel.SIDO)
    private val suwon = area("41111", "수원시 장안구", AreaLevel.SIGUNGU)
    private val sejong = area("36", "세종특별자치시", AreaLevel.SIDO)
    private val catalogue = listOf(seoul, seongdong, mapo, gyeonggi, suwon, sejong).map { AreaMapVO(it, emptyList()) }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        coEvery { getAreaMap() } returns catalogue
        coEvery { getMyProfile(any(), any()) } returns profile(emptyList())
        coEvery { saveInterests.saveRegions(any()) } returns Unit
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `가입은 프로필을 조회하지 않고 이전 선택을 복원하며 한 개부터 진행 가능하다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(editing = false, regions = listOf(mapo)))
            assertFalse(viewModel.uiState.value.canContinue)
            advanceUntilIdle()

            assertEquals(listOf(mapo), viewModel.uiState.value.selected)
            assertTrue(viewModel.uiState.value.canContinue)
            coVerify(exactly = 0) { getMyProfile(any(), any()) }

            viewModel.onIntent(RegionMapIntent.Remove(mapo.code))
            assertFalse(viewModel.uiState.value.canContinue)
            viewModel.onIntent(RegionMapIntent.Toggle(seongdong.code))
            assertTrue(viewModel.uiState.value.canContinue)
            viewModel.onIntent(RegionMapIntent.Save)
            advanceUntilIdle()
            coVerify(exactly = 0) { saveInterests.saveRegions(any()) }
        }

    @Test
    fun `시도 전체에서 하위 지역을 누르면 그 지역만 제외하고 다시 누르면 전체로 복원한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(false, listOf(seongdong, mapo, suwon)))
            advanceUntilIdle()

            viewModel.onIntent(RegionMapIntent.Toggle(seoul.code))
            assertEquals(listOf(suwon, seoul), viewModel.uiState.value.selected)

            viewModel.onIntent(RegionMapIntent.Toggle(seongdong.code))
            assertEquals(listOf(suwon, mapo), viewModel.uiState.value.selected)
            assertEquals(seongdong.code, viewModel.uiState.value.selectionFocusCode)
            viewModel.onIntent(RegionMapIntent.Toggle(seongdong.code))
            assertEquals(listOf(suwon, seoul), viewModel.uiState.value.selected)
            assertEquals(seongdong.code, viewModel.uiState.value.selectionFocusCode)
            viewModel.onIntent(RegionMapIntent.Browse(gyeonggi.code))
            assertNull(viewModel.uiState.value.selectionFocusCode)
        }

    @Test
    fun `전체 선택에서 하위 지역을 모두 제외하면 다음 진행이 비활성화된다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(false, listOf(seoul)))
            advanceUntilIdle()

            viewModel.onIntent(RegionMapIntent.Toggle(seongdong.code))
            assertEquals(listOf(mapo), viewModel.uiState.value.selected)
            assertTrue(viewModel.uiState.value.canContinue)

            viewModel.onIntent(RegionMapIntent.Toggle(mapo.code))
            assertTrue(
                viewModel.uiState.value.selected
                    .isEmpty(),
            )
            assertFalse(viewModel.uiState.value.canContinue)

            viewModel.onIntent(RegionMapIntent.Toggle(mapo.code))
            assertEquals(listOf(mapo), viewModel.uiState.value.selected)
            assertTrue(viewModel.uiState.value.canContinue)
        }

    @Test
    fun `일부 제외 후 전체 선택을 누르면 해당 시도만 복원하며 다시 누르면 전체 해제한다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(false, listOf(seoul, suwon)))
            advanceUntilIdle()
            viewModel.onIntent(RegionMapIntent.Toggle(seongdong.code))
            assertEquals(
                setOf(mapo, suwon),
                viewModel.uiState.value.selected
                    .toSet(),
            )

            viewModel.onIntent(RegionMapIntent.Toggle(seoul.code))
            assertEquals(
                setOf(seoul, suwon),
                viewModel.uiState.value.selected
                    .toSet(),
            )
            viewModel.onIntent(RegionMapIntent.Toggle(seoul.code))
            assertEquals(listOf(suwon), viewModel.uiState.value.selected)
        }

    @Test
    fun `일부 제외 상태로 재진입하거나 저장해도 제외한 지역이 다시 포함되지 않는다`() =
        runTest(dispatcher) {
            val original = viewModel()
            original.onIntent(RegionMapIntent.Initialize(false, listOf(seoul, suwon)))
            advanceUntilIdle()
            original.onIntent(RegionMapIntent.Toggle(seongdong.code))
            val remaining = original.uiState.value.selected

            val revisited = viewModel()
            revisited.onIntent(RegionMapIntent.Initialize(false, remaining))
            advanceUntilIdle()
            assertEquals(
                setOf(mapo, suwon),
                revisited.uiState.value.selected
                    .toSet(),
            )

            coEvery { getMyProfile(any(), any()) } returns profile(remaining)
            val editing = viewModel()
            editing.onIntent(RegionMapIntent.Initialize(true, emptyList()))
            advanceUntilIdle()
            assertEquals(
                setOf(mapo, suwon),
                editing.uiState.value.selected
                    .toSet(),
            )
            editing.onIntent(RegionMapIntent.Save)
            advanceUntilIdle()
            coVerify(exactly = 1) {
                saveInterests.saveRegions(match { it.toSet() == setOf(mapo, suwon) })
            }
            assertTrue(editing.uiState.value.saved)
        }

    @Test
    fun `확대는 선택을 변경하지 않으며 해당 시도의 시군구만 보여준다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(false, emptyList()))
            advanceUntilIdle()
            assertEquals(
                listOf(seoul, gyeonggi, sejong),
                viewModel.uiState.value.visibleRegions
                    .map { it.area },
            )

            viewModel.onIntent(RegionMapIntent.Browse(seoul.code))
            assertEquals(
                listOf(seongdong, mapo),
                viewModel.uiState.value.visibleRegions
                    .map { it.area },
            )
            assertTrue(
                viewModel.uiState.value.selected
                    .isEmpty(),
            )
            viewModel.onIntent(RegionMapIntent.Browse(seongdong.code))
            assertEquals(seoul.code, viewModel.uiState.value.provinceCode)
            viewModel.onIntent(RegionMapIntent.Browse(null))
            assertNull(viewModel.uiState.value.provinceCode)
        }

    @Test
    fun `시군구가 없는 세종은 전체 단위 한 개로 선택할 수 있다`() =
        runTest(dispatcher) {
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(false, emptyList()))
            advanceUntilIdle()
            viewModel.onIntent(RegionMapIntent.Browse(sejong.code))
            assertTrue(
                viewModel.uiState.value.visibleRegions
                    .isEmpty(),
            )
            viewModel.onIntent(RegionMapIntent.Toggle(sejong.code))
            assertEquals(listOf(sejong), viewModel.uiState.value.selected)
            assertTrue(viewModel.uiState.value.canContinue)
        }

    @Test
    fun `저장된 패딩 코드를 정규화하고 겹치는 하위 선택은 제거하되 다른 동 선택은 보존한다`() {
        val legacyDong = area("4111112900", "정자동", AreaLevel.DONG)
        val normalized =
            normalizeRegions(
                listOf(
                    mapo.copy(code = "1144000000", level = AreaLevel.UNKNOWN),
                    seoul.copy(code = "1100000000", level = AreaLevel.UNKNOWN),
                    seoul,
                    legacyDong,
                    area("", "빈 코드", AreaLevel.UNKNOWN),
                    area("11abc", "잘못된 코드", AreaLevel.UNKNOWN),
                ),
            )

        assertEquals(listOf(seoul, legacyDong), normalized)
        assertEquals(listOf(mapo), normalizeRegions(listOf(mapo.copy(code = "1144000000", level = AreaLevel.UNKNOWN))))
    }

    @Test
    fun `프로필 조회 실패는 편집과 저장을 막고 재시도에서 기존 지역을 복원한다`() =
        runTest(dispatcher) {
            coEvery { getMyProfile(any(), any()) } throws IOException("offline")
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(true, emptyList()))
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.loadError)
            assertFalse(viewModel.uiState.value.canContinue)
            viewModel.onIntent(RegionMapIntent.Toggle(seoul.code))
            viewModel.onIntent(RegionMapIntent.Save)
            advanceUntilIdle()
            assertTrue(
                viewModel.uiState.value.selected
                    .isEmpty(),
            )
            coVerify(exactly = 0) { saveInterests.saveRegions(any()) }

            coEvery { getMyProfile(any(), any()) } returns profile(listOf(mapo))
            viewModel.onIntent(RegionMapIntent.Retry)
            advanceUntilIdle()
            assertFalse(viewModel.uiState.value.loadError)
            assertEquals(listOf(mapo), viewModel.uiState.value.selected)
        }

    @Test
    fun `경계 자료 실패는 재시도 가능하고 재초기화로 사용자의 선택을 덮어쓰지 않는다`() =
        runTest(dispatcher) {
            coEvery { getAreaMap() } throws IOException("asset unavailable")
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(false, listOf(seongdong)))
            advanceUntilIdle()
            assertTrue(viewModel.uiState.value.loadError)
            assertFalse(viewModel.uiState.value.isLoading)

            coEvery { getAreaMap() } returns catalogue
            viewModel.onIntent(RegionMapIntent.Retry)
            advanceUntilIdle()
            assertEquals(listOf(seongdong), viewModel.uiState.value.selected)
            viewModel.onIntent(RegionMapIntent.Toggle(mapo.code))
            viewModel.onIntent(RegionMapIntent.Initialize(false, emptyList()))
            advanceUntilIdle()
            assertEquals(listOf(seoul), viewModel.uiState.value.selected)
            coVerify(exactly = 2) { getAreaMap() }
        }

    @Test
    fun `저장 실패는 선택을 보존하고 성공 재시도 전에는 완료되지 않는다`() =
        runTest(dispatcher) {
            coEvery { getMyProfile(any(), any()) } returns profile(listOf(seoul))
            coEvery { saveInterests.saveRegions(any()) } throws IOException("offline")
            val viewModel = viewModel()
            viewModel.onIntent(RegionMapIntent.Initialize(true, emptyList()))
            advanceUntilIdle()
            viewModel.onIntent(RegionMapIntent.Save)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.saveError)
            assertFalse(viewModel.uiState.value.saved)
            assertFalse(viewModel.uiState.value.isSaving)
            assertEquals(listOf(seoul), viewModel.uiState.value.selected)

            val pending = CompletableDeferred<Unit>()
            coEvery { saveInterests.saveRegions(any()) } coAnswers { pending.await() }
            viewModel.onIntent(RegionMapIntent.Save)
            viewModel.onIntent(RegionMapIntent.Save)
            runCurrent()
            assertFalse(viewModel.uiState.value.saveError)
            assertTrue(viewModel.uiState.value.isSaving)
            assertFalse(viewModel.uiState.value.canContinue)
            viewModel.onIntent(RegionMapIntent.Toggle(mapo.code))
            viewModel.onIntent(RegionMapIntent.Remove(seoul.code))
            assertEquals(listOf(seoul), viewModel.uiState.value.selected)
            pending.complete(Unit)
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.saved)
            assertFalse(viewModel.uiState.value.isSaving)
            coVerify(exactly = 2) { saveInterests.saveRegions(listOf(seoul)) }
        }

    private fun viewModel() = RegionMapViewModel(getAreaMap, getMyProfile, saveInterests)

    private fun profile(regions: List<AreaVO>): MyProfileVO =
        mockk {
            every { interestRegions } returns regions
        }

    private fun area(
        code: String,
        name: String,
        level: AreaLevel,
    ) = AreaVO(code, name, name, level)
}
