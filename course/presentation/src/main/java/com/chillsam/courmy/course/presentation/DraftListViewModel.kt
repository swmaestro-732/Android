package com.chillsam.courmy.course.presentation

import androidx.lifecycle.ViewModel
import com.chillsam.courmy.course.domain.ObserveDraftsUseCase
import com.chillsam.courmy.course.entity.SavedCourseVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** 임시저장 코스 목록([drafts])을 노출하는 읽기 전용 ViewModel. */
@HiltViewModel
class DraftListViewModel
    @Inject
    constructor(
        observeDrafts: ObserveDraftsUseCase,
    ) : ViewModel() {
        val drafts: StateFlow<List<SavedCourseVO>> = observeDrafts()
    }
