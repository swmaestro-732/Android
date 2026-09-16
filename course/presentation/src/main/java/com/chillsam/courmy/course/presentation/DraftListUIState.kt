package com.chillsam.courmy.course.presentation

import com.chillsam.courmy.common.presentation.mvi.UiState
import com.chillsam.courmy.course.entity.DraftSummaryVO
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
 * 임시저장 목록 화면 상태(FS-20).
 *
 * 초안이 서버에 있으므로 "아직 못 불러온 상태"와 "정말 하나도 없는 상태"를 구분해야 한다.
 * [loadFailed] 가 true 면 빈 화면 대신 재시도를 띄운다.
 */
data class DraftListUIState(
    val isLoading: Boolean = true,
    val drafts: ImmutableList<DraftSummaryVO> = persistentListOf(),
    val loadFailed: Boolean = false,
    val errorMessage: String? = null,
) : UiState {
    /** 조회에 성공했는데 비어 있는 경우에만 "임시저장한 코스가 없어요"를 보여 준다. */
    val isEmpty: Boolean
        get() = !isLoading && !loadFailed && drafts.isEmpty()

    companion object {
        val empty: DraftListUIState = DraftListUIState()
    }
}
