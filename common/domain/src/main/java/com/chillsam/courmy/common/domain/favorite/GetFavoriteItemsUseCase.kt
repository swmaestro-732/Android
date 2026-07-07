package com.chillsam.courmy.common.domain.favorite

import com.chillsam.courmy.common.domain.base.BaseUseCase
import com.chillsam.courmy.common.domain.helper.MessageHelper
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.common.entity.favorite.FavoriteItemVO
import com.chillsam.courmy.tti.TTIHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteItemsUseCase
    @Inject
    constructor(
        private val favoriteRepository: FavoriteRepository,
        resourceHelper: ResourceHelper,
        messageHelper: MessageHelper,
        navigationHelper: NavigationHelper,
        ttiHelper: TTIHelper,
    ) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {
        operator fun invoke(): Flow<List<FavoriteItemVO>> = favoriteRepository.getFavoriteItemsFlow()
    }
