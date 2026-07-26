package com.chillsam.courmy.course.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.course.domain.GetCourseDetailUseCase
import com.chillsam.courmy.course.entity.CourseDetailVO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 코스 상세 화면 상태. */
data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val course: CourseDetailVO? = null,
    val isError: Boolean = false,
)

/** 코스 상세를 courseId 로 서버에서 조회해 노출한다. */
@HiltViewModel
class CourseDetailViewModel
    @Inject
    constructor(
        private val getCourseDetail: GetCourseDetailUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(CourseDetailUiState())
        val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

        private var loadedId: String? = null

        /** 같은 코스를 이미 불러왔으면 재요청하지 않는다. */
        fun load(courseId: String) {
            if (loadedId == courseId && !_uiState.value.isError) return
            loadedId = courseId
            viewModelScope.launch {
                _uiState.value = CourseDetailUiState(isLoading = true)
                runCatching { getCourseDetail(courseId) }
                    .onSuccess { _uiState.value = CourseDetailUiState(isLoading = false, course = it) }
                    .onFailure { _uiState.value = CourseDetailUiState(isLoading = false, isError = true) }
            }
        }
    }
