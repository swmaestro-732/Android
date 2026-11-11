package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.error.LocalizedValidationException
import com.chillsam.courmy.common.domain.error.requireLocalized
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.course.entity.CourseFolderVO
import javax.inject.Inject

/**
 * 저장 폴더 생성 UseCase. 생성된 폴더 id 를 돌려준다.
 *
 * 이름 제약(1~10자)은 서버 계약이라 요청 전에 걸러 400 왕복을 줄인다.
 *
 */
class CreateCourseFolderUseCase
    @Inject
    constructor(
        private val repository: CourseFolderRepository,
    ) {
        suspend operator fun invoke(name: String): Long {
            val trimmed = name.trim()
            requireLocalized(
                trimmed.isNotEmpty(),
            ) { LocalizedValidationException(StringResource.VALIDATION_FOLDER_REQUIRED) }
            requireLocalized(trimmed.length <= CourseFolderVO.NAME_MAX_LENGTH) {
                LocalizedValidationException(StringResource.VALIDATION_FOLDER_LENGTH, CourseFolderVO.NAME_MAX_LENGTH)
            }
            return repository.createFolder(trimmed)
        }
    }
