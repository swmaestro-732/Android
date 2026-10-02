package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseFolderVO
import javax.inject.Inject

/**
 * 저장 폴더 생성 UseCase. 생성된 폴더 id 를 돌려준다.
 *
 * 이름 제약(1~10자)은 서버 계약이라 요청 전에 걸러 400 왕복을 줄인다.
 *
 * TODO: 폴더 생성 UI 가 생기면 거기서 호출한다. 지금은 데이터 계층만 연결돼 있다.
 */
class CreateCourseFolderUseCase
    @Inject
    constructor(
        private val repository: CourseFolderRepository,
    ) {
        suspend operator fun invoke(name: String): Long {
            val trimmed = name.trim()
            require(trimmed.isNotEmpty()) { "폴더 이름을 입력해 주세요." }
            require(trimmed.length <= CourseFolderVO.NAME_MAX_LENGTH) {
                "폴더 이름은 ${CourseFolderVO.NAME_MAX_LENGTH}자까지 쓸 수 있습니다."
            }
            return repository.createFolder(trimmed)
        }
    }
