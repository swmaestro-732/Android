package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseVisibility
import javax.inject.Inject

/**
 * 코스의 현재 공개 설정을 읽는다.
 *
 * 편집 저장은 공개 설정을 본문에 실어야 하는데 화면 조합 상세 API 에는 그 필드가 없다. 이 UseCase 로
 * 도메인 API 에서 한 번 더 읽어 초기값을 채운다. 실패하거나 응답에 없으면 null 이며, 화면은 그때만
 * "현재 설정을 불러오지 못했다"고 알린다.
 */
class GetCourseVisibilityUseCase
    @Inject
    constructor(
        private val repository: CourseManageRepository,
    ) {
        suspend operator fun invoke(courseId: Long): CourseVisibility? = repository.getVisibility(courseId)
    }
