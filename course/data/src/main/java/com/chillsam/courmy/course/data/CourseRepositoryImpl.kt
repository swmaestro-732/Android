package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CourseVisibility

/**
 * 코스 작성 Repository 구현.
 *
 * TODO-API-SPEC: 현재는 UI 확인용 더미를 반환한다. "새 코스 만들기"는 빈 초안에서 시작하며,
 * 추천 태그만 채워 준다(정적 제안). 실제 임시저장/코스 조회 API 가 붙으면 DataSource·DTO 를 추가하고 교체한다.
 */
class CourseRepositoryImpl : CourseRepository {
    override suspend fun getCourseDraft(): CourseDraftVO = EMPTY_DRAFT

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
