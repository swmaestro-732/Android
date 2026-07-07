package com.chillsam.courmy.intro.domain

import com.chillsam.courmy.common.domain.base.BaseUseCase
import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.domain.error.handlingErrorOnUseCase
import com.chillsam.courmy.common.domain.error.isCommonErrorHandling
import com.chillsam.courmy.common.domain.helper.MessageHelper
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.intro.entity.IntroVO
import com.chillsam.courmy.search.domain.SearchPage
import com.chillsam.courmy.tti.TTIHelper
import javax.inject.Inject

class GetIntroUseCase @Inject constructor(
    resourceHelper: ResourceHelper,
    messageHelper: MessageHelper,
    navigationHelper: NavigationHelper,
    ttiHelper: TTIHelper,
) : BaseUseCase(resourceHelper, messageHelper, navigationHelper, ttiHelper) {

    operator fun invoke(): Result<IntroVO> {
        return try {
//            val result = introRepository.getIntro()
            navigationHelper.navigateTo(SearchPage)
            Result.success(IntroVO.empty)
        } catch (e: HttpResponseException) {
            handleIntroError(e)
            Result.failure(e)
        }
    }

    private fun handleIntroError(e: HttpResponseException) {
        if (e.isCommonErrorHandling()) {
            executeCommonErrorHanding(e)
            return
        }
        val errorType = e.handlingErrorOnUseCase<IntroErrorType>() ?: return
        when (errorType) {
            IntroErrorType.REQUIRED_FORCE_UPDATE -> {
                messageHelper.showOneButtonDialog(
                    cantIgnore = true,
                    descText = "You should update this App",
                    buttonText = "Move to Play Store",
                    onClickButton = {
                        navigationHelper.navigateToBack()
                    },
                )
            }
        }
    }
}
