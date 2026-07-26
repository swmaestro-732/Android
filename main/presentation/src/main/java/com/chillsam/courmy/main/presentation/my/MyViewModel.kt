package com.chillsam.courmy.main.presentation.my

import androidx.lifecycle.ViewModel
import com.chillsam.courmy.course.domain.ObserveSavedCoursesUseCase
import com.chillsam.courmy.course.entity.SavedCourseVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * 마이 화면 ViewModel. 저장 완료한 코스 목록([savedCourses])을 course 모듈에서 관찰해 노출한다.
 * (마이 화면은 코스를 "표시만" 하므로 데이터 소유 모듈인 course:domain 의 UseCase 에 의존한다.)
 */
@HiltViewModel
class MyViewModel
    @Inject
    constructor(
        observeSavedCourses: ObserveSavedCoursesUseCase,
    ) : ViewModel() {
        val savedCourses: StateFlow<List<SavedCourseVO>> = observeSavedCourses()
    }
