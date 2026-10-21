package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.course.entity.CoursePlaceVO
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseCreateForkStateTest {
    @Test
    fun `큰 원본에서도 서로 다른 2곳만 남기면 다음 단계와 임시저장이 가능하다`() {
        val state = forkState("1", "2")

        assertEquals(2, state.requiredKeptOriginPlaceCount)
        assertEquals(2, state.keptOriginPlaceCount)
        assertTrue(state.canSaveDraft)
        assertNull(state.stepBlockedReason)
        assertTrue(state.canGoNext)
        assertTrue(state.canSave)
    }

    @Test
    fun `원본 1곳과 새 장소로 총 2곳을 채워도 원본 유지 조건은 충족하지 않는다`() {
        val state = forkState("1", "new")

        assertEquals(1, state.keptOriginPlaceCount)
        assertFalse(state.canSaveDraft)
        assertNotNull(state.stepBlockedReason)
        assertFalse(state.canGoNext)
        assertFalse(state.canSave)
        assertNotNull(state.copy(step = CourseCreateUIState.STEP_SETTINGS).stepBlockedReason)
    }

    @Test
    fun `같은 원본 장소를 중복으로 담아도 서로 다른 2곳으로 계산하지 않는다`() {
        val state = forkState("1", "1", "new")

        assertEquals(1, state.keptOriginPlaceCount)
        assertFalse(state.canSaveDraft)
        assertNotNull(state.stepBlockedReason)
        assertFalse(state.canSave)
    }

    @Test
    fun `원본 2곳 경계에서는 원본 삭제를 막고 추가한 장소만 삭제할 수 있다`() {
        val state = forkState("1", "2", "new")

        assertFalse(state.canRemovePlace("1"))
        assertFalse(state.canRemovePlace("2"))
        assertTrue(state.canRemovePlace("new"))
        assertTrue(forkState("1", "2", "3").canRemovePlace("1"))
    }

    @Test
    fun `일반 코스는 원본 유지 조건 없이 장소 2곳부터 임시저장할 수 있다`() {
        val state = forkState("new1", "new2").copy(forkFromCourseId = null, forkOriginPlaceIds = persistentSetOf())

        assertEquals(0, state.requiredKeptOriginPlaceCount)
        assertTrue(state.canSaveDraft)
        assertNull(state.stepBlockedReason)
        assertFalse(state.copy(places = persistentListOf(place("new1"))).canSaveDraft)
        assertNotNull(state.copy(places = persistentListOf(place("new1"))).stepBlockedReason)
    }

    @Test
    fun `원본 2곳을 남긴 초안은 제목과 사진이 없어도 임시저장만 가능하다`() {
        val state =
            forkState("1", "2").copy(
                name = "",
                thumbnailPhotos = persistentListOf(),
                places = persistentListOf(CoursePlaceVO("1", "장소1"), CoursePlaceVO("2", "장소2")),
            )

        assertTrue(state.canSaveDraft)
        assertNull(state.stepBlockedReason)
        assertFalse(state.canSave)
        assertNotNull(state.copy(step = CourseCreateUIState.STEP_SETTINGS).stepBlockedReason)
    }

    private fun forkState(vararg ids: String) =
        CourseCreateUIState(
            isLoading = false,
            name = "복제 코스",
            thumbnailPhotos = persistentListOf("cover"),
            forkFromCourseId = 42L,
            forkOriginPlaceIds = (1..10).map(Int::toString).let { persistentSetOf(*it.toTypedArray()) },
            places = ids.map(::place).toPersistentList(),
        )

    private fun place(id: String) = CoursePlaceVO(id, "장소$id", photoUrls = listOf("photo$id"))
}
