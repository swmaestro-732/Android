package com.chillsam.courmy.main.domain.profile

import com.chillsam.courmy.main.entity.area.AreaVO
import javax.inject.Inject

/**
 * 관심 테마·지역 저장.
 *
 * TODO-API-SPEC: 지금은 기기 저장만 한다([ProfileRepository.saveInterestThemes] 주석 참고).
 * 서버가 관심사 수정 API 를 주면 여기서 함께 호출한다. [wiki-needed]
 */
class SaveInterestsUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend fun saveThemes(themes: List<String>) = repository.saveInterestThemes(themes)

        suspend fun saveRegions(regions: List<AreaVO>) = repository.saveInterestRegions(regions)
    }
