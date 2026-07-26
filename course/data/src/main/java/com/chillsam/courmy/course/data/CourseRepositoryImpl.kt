package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.data.courseDetail.CourseDetailDataSource
import com.chillsam.courmy.course.data.courseDetail.dto.toVO
import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CourseVisibility
import com.chillsam.courmy.course.entity.SavedCourseVO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 코스 작성·보관 Repository 구현.
 *
 * 세션 목록·완성 코스는 프로세스가 살아있는 동안(= 앱이 켜져 있는 동안)만 인메모리로 보관하며,
 * 콜드 스타트 시 초기화된다. [CourseDataModule] 이 `@Singleton` 으로 제공하므로 앱 전역에서 한 인스턴스를 공유한다.
 *
 * TODO-API-SPEC: 현재는 UI 확인용 더미다. "새 코스 만들기"는 빈 초안에서 시작하며 추천 태그만 채워 준다(정적 제안).
 * 실제 임시저장/코스 조회·저장 API 가 붙으면 DataSource·DTO 를 추가하고 인메모리 상태를 교체한다.
 */
class CourseRepositoryImpl(
    private val courseDetailDataSource: CourseDetailDataSource,
) : CourseRepository {
    private val _savedCourses = MutableStateFlow<List<SavedCourseVO>>(emptyList())
    override val savedCourses: StateFlow<List<SavedCourseVO>> = _savedCourses.asStateFlow()

    private val _drafts = MutableStateFlow<List<SavedCourseVO>>(emptyList())
    override val drafts: StateFlow<List<SavedCourseVO>> = _drafts.asStateFlow()

    private val _lastCompleted = MutableStateFlow<CourseCompleteVO?>(null)
    override val lastCompleted: StateFlow<CourseCompleteVO?> = _lastCompleted.asStateFlow()

    override suspend fun getCourseDraft(): CourseDraftVO = EMPTY_DRAFT

    override fun saveDraft(title: String) {
        _drafts.update { it + SavedCourseVO(title, System.currentTimeMillis()) }
    }

    override fun completeCourse(course: CourseCompleteVO?) {
        _lastCompleted.value = course
        course?.let { completed ->
            _savedCourses.update { it + SavedCourseVO(completed.title, System.currentTimeMillis()) }
        }
    }

    override suspend fun getCourseDetail(courseId: Long): CourseDetailVO {
        val envelope = courseDetailDataSource.getCourseDetail(courseId)
        val data = requireNotNull(envelope.data) { "코스 상세 응답에 data 가 없습니다: courseId=$courseId" }
        return data.toVO()
    }

    private companion object {
        val EMPTY_DRAFT =
            CourseDraftVO(
                name = "",
                description = "",
                tags = emptyList(),
                suggestedTags = listOf("감성카페", "통창뷰", "조용한", "데이트"),
                places = emptyList(),
                visibility = CourseVisibility.PUBLIC,
            )
    }
}
