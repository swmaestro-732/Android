package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.DraftSummaryVO
import javax.inject.Inject

/**
 * 내 임시저장 목록을 서버에서 조회한다. 목록 화면 진입과 재시도에서 부른다.
 *
 * 초안이 기기가 아니라 서버에 있으므로 관찰(StateFlow)이 아니라 그때그때 묻는 suspend 조회다.
 */
class GetDraftsUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
    ) {
        suspend operator fun invoke(): List<DraftSummaryVO> = repository.getDrafts()
    }
