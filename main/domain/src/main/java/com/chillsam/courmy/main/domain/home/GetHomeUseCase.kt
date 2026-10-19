package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.main.entity.home.HomeVO
import javax.inject.Inject

/**
 * 홈 화면 조합(`GET /service/v2/home`) 조회 UseCase — 추천 코스 미리보기 + 근처 저장 장소.
 *
 * 세로 피드 전체(전체보기)는 페이지네이션이 있는 [GetHomeFeedUseCase] 가 따로 맡는다.
 */
class GetHomeUseCase
    @Inject
    constructor(
        private val repository: HomeFeedRepository,
    ) {
        suspend operator fun invoke(
            userLat: Double? = null,
            userLng: Double? = null,
        ): HomeVO = repository.getHome(userLat, userLng)
    }
