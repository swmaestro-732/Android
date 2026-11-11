package com.chillsam.courmy.common.domain.base

import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.domain.helper.MessageHelper
import com.chillsam.courmy.common.domain.helper.NavigationHelper
import com.chillsam.courmy.common.domain.helper.ResourceHelper
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.tti.TTIHelper

open class BaseUseCase(
    protected open val resourceHelper: ResourceHelper,
    protected open val messageHelper: MessageHelper,
    protected open val navigationHelper: NavigationHelper,
    protected open val ttiHelper: TTIHelper,
) {
    fun executeCommonErrorHanding(e: HttpResponseException) {
        when (e.rawCode) {
            401 -> {
                messageHelper.showOneButtonDialog(
                    cantIgnore = true,
                    descText = resourceHelper.getString(StringResource.BASE_SESSION_EXPIRED),
                    buttonText = resourceHelper.getString(StringResource.BASE_MOVE_LOGIN),
                    onClickButton = {
//                        navigationHelper.navigateTo(AppPage.Intro)
                    },
                )
            }

            404 -> {
                messageHelper.showOneButtonDialog(
                    cantIgnore = true,
                    descText = resourceHelper.getString(StringResource.BASE_FEATURE_UNAVAILABLE),
                    buttonText = resourceHelper.getString(StringResource.BASE_GO_BACK),
                    onClickButton = {
                        navigationHelper.navigateToBack()
                    },
                )
            }

            else -> {
                messageHelper.showOneButtonDialog(
                    titleText = resourceHelper.getString(StringResource.BASE_TEMPORARY_ERROR),
                    descText = resourceHelper.getString(StringResource.BASE_ERROR_STATUS, e.rawCode),
                )
            }
        }
    }
}
