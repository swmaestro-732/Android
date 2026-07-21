package com.chillsam.courmy.main.presentation.course

import com.chillsam.courmy.course.entity.CourseCompleteVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 테스트용 세션 인메모리 코스 저장소.
 *
 * 프로세스가 살아있는 동안(= 앱이 켜져 있는 동안)에만 데이터를 보관하며, 콜드 스타트 시 초기화된다.
 * 네비게이션 플로우 검증용 임시 구현으로, 실제로는 data 레이어의 Repository 로 대체한다.
 */
object CourseSessionStore {
    data class SavedCourse(
        val title: String,
        val createdAtMillis: Long,
    )

    private val _courses = MutableStateFlow<List<SavedCourse>>(emptyList())
    val courses: StateFlow<List<SavedCourse>> = _courses.asStateFlow()

    private val _drafts = MutableStateFlow<List<SavedCourse>>(emptyList())
    val drafts: StateFlow<List<SavedCourse>> = _drafts.asStateFlow()

    private val _lastCompleted = MutableStateFlow<CourseCompleteVO?>(null)

    /** 방금 저장 완료한 코스(FS-34-Done 완성 화면 표시용). null 이면 완성 화면이 예시로 폴백. */
    val lastCompleted: StateFlow<CourseCompleteVO?> = _lastCompleted.asStateFlow()

    /** 완성한 코스를 저장(마이 화면에 노출). */
    fun addCourse(title: String) {
        _courses.update { it + SavedCourse(title, System.currentTimeMillis()) }
    }

    /** 임시저장 코스를 저장(임시저장 목록에 노출). */
    fun addDraft(title: String) {
        _drafts.update { it + SavedCourse(title, System.currentTimeMillis()) }
    }

    /** 코스 저장 완료 처리: 완성 화면 데이터를 보관하고 마이 목록에도 추가한다. */
    fun completeCourse(course: CourseCompleteVO?) {
        _lastCompleted.value = course
        course?.let { addCourse(it.title) }
    }
}

/** 저장 시각을 "yyyy.MM.dd HH:mm" 로 포맷. minSdk 24 를 고려해 java.time 대신 SimpleDateFormat 사용. */
fun formatCreatedAt(millis: Long): String = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA).format(millis)
