package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.main.entity.saved.SavedCourseVO
import javax.inject.Inject

/** 저장한 코스 목록 조회. 서버가 size 를 1~50 으로 제한하므로 범위를 맞춰 넘긴다. */
class GetSavedCoursesUseCase
    @Inject
    constructor(
        private val repository: SavedCourseRepository,
    ) {
        suspend operator fun invoke(size: Int = DEFAULT_SIZE): List<SavedCourseVO> =
            repository.getSavedCourses(size.coerceIn(MIN_SIZE, MAX_SIZE))

        companion object {
            const val DEFAULT_SIZE = 20
            private const val MIN_SIZE = 1
            private const val MAX_SIZE = 50
        }
    }
