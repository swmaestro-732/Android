package com.chillsam.courmy.course.presentation

import androidx.lifecycle.ViewModel
import com.chillsam.courmy.course.domain.ObserveLastCompletedUseCase
import com.chillsam.courmy.course.entity.CourseCompleteVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** 방금 저장 완료한 코스([lastCompleted])를 노출하는 읽기 전용 ViewModel. */
@HiltViewModel
class CourseCompleteViewModel
    @Inject
    constructor(
        observeLastCompleted: ObserveLastCompletedUseCase,
    ) : ViewModel() {
        val lastCompleted: StateFlow<CourseCompleteVO?> = observeLastCompleted()
    }
