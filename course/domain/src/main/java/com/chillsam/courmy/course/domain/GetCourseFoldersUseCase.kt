package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseFolderVO
import javax.inject.Inject

/**
 * 저장 폴더 목록 조회 UseCase.
 *
 * TODO: 저장함에 폴더 칩/필터 UI 가 생기면 거기서 호출한다. 지금은 데이터 계층만 연결돼 있다.
 */
class GetCourseFoldersUseCase
    @Inject
    constructor(
        private val repository: CourseFolderRepository,
    ) {
        suspend operator fun invoke(): List<CourseFolderVO> = repository.getFolders()
    }
