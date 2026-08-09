package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseDraftVO
import javax.inject.Inject

/**
 * 임시저장한 초안을 불러와 작성 화면을 채운다("이어서 작성").
 *
 * 새 코스는 서버에 물어볼 것이 없으므로 이 UseCase 를 거치지 않고 빈 초안으로 시작한다.
 */
class GetCourseDraftUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke(courseId: Long): CourseDraftVO = repository.getCourseDraft(courseId)
    }
