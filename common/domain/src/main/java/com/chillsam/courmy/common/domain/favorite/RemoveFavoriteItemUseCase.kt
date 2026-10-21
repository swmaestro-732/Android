package com.chillsam.courmy.common.domain.favorite

import com.chillsam.courmy.common.domain.base.BaseUseCase
import com.chillsam.courmy.common.domain.helper.MessageHelper
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.common.domain.message.IconType
import com.chillsam.courmy.tti.TTIHelper
import javax.inject.Inject

class RemoveFavoriteItemUseCase
    @Inject
    constructor(
        private val favoriteRepository: FavoriteRepository,
        resourceHelper: ResourceHelper,
        messageHelper: MessageHelper,
        navigationHelper: NavigationHelper,
        ttiHelper: TTIHelper,
    ) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {
        suspend operator fun invoke(url: String): Result<Unit> =
            runCatching { favoriteRepository.deleteFavoriteItem(url) }
                .onFailure {
                    messageHelper.showSnackBar(
                        iconType = IconType.ERROR,
                        messageText = resourceHelper.getString(StringResource.FAVORITE_REMOVE_FAILED),
                    )
                }
    }
