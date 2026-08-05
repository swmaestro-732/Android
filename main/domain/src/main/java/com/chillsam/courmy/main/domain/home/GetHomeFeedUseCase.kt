package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.main.entity.home.HomeCourseVO
import javax.inject.Inject

/**
 * 홈 공개 코스 피드 조회 UseCase.
 *
 * [size] 기본값은 홈 화면이 한 번에 보여줄 분량이다. 서버가 1~50 을 벗어난 값을 400 으로 거부하므로
 * 호출부가 임의의 값을 넣더라도 여기서 범위 안으로 맞춘다.
 */
class GetHomeFeedUseCase
    @Inject
    constructor(
        private val repository: HomeFeedRepository,
    ) {
        suspend operator fun invoke(size: Int = DEFAULT_SIZE): List<HomeCourseVO> =
            repository.getCourseFeed(size.coerceIn(MIN_SIZE, MAX_SIZE))

        companion object {
            const val DEFAULT_SIZE = 20
            private const val MIN_SIZE = 1
            private const val MAX_SIZE = 50
        }
    }
