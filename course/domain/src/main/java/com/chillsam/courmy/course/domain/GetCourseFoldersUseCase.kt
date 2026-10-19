package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseFolderVO
import javax.inject.Inject

/**
 * 저장 폴더 목록 조회 UseCase.
 *
 */
class GetCourseFoldersUseCase
    @Inject
    constructor(
        private val repository: CourseFolderRepository,
    ) {
        suspend operator fun invoke(): List<CourseFolderVO> = repository.getFolders()
    }
