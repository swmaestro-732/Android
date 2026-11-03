package com.chillsam.courmy.main.domain.area

import javax.inject.Inject

class GetAreaMapUseCase
    @Inject
    constructor(
        private val repository: AreaMapRepository,
    ) {
        suspend operator fun invoke() = repository.getRegions()
    }
