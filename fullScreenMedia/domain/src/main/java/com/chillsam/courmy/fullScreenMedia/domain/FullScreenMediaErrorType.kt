package com.chillsam.courmy.fullScreenMedia.domain

import com.chillsam.courmy.common.domain.error.HttpErrorType

enum class FullScreenMediaErrorType(
    override val type: String,
    override val errorMsg: String,
    override val isHandledOnDomain: Boolean = true,
) : HttpErrorType {
    UNKNOWN(
        type = "api.fullScreenMedia.unknown",
        errorMsg = "알수없는 에러가 발생했습니다.",
        isHandledOnDomain = true,
    ),
}
