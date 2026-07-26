package com.chillsam.courmy.course.presentation

import androidx.lifecycle.ViewModel
import com.chillsam.courmy.course.domain.BeginEditDraftUseCase
import com.chillsam.courmy.course.domain.ObserveDraftsUseCase
import com.chillsam.courmy.course.entity.DraftSummaryVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** 임시저장 코스 목록([drafts])을 노출하고, "이어서 편집" 진입을 처리하는 ViewModel. */
@HiltViewModel
class DraftListViewModel
    @Inject
    constructor(
        observeDrafts: ObserveDraftsUseCase,
        private val beginEditDraftUseCase: BeginEditDraftUseCase,
    ) : ViewModel() {
        val drafts: StateFlow<List<DraftSummaryVO>> = observeDrafts()

        /** [draftId] 초안을 다음 코스 만들기 진입에서 이어서 편집하도록 지정한다. */
        fun beginEdit(draftId: String) = beginEditDraftUseCase(draftId)
    }
