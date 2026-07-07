package com.chillsam.courmy.intro.data.dto

import com.chillsam.courmy.common.entity.UNKNOWN
import com.chillsam.courmy.intro.entity.IntroVO
import kotlinx.serialization.Serializable

@Serializable
data class IntroDTO(
    val devTestMsg: String? = null,
    val minAppVersion: String? = null,
    val recommendAppVersion: String? = null,
) {
    fun toVO(): IntroVO =
        IntroVO(
            devTestMsg = devTestMsg ?: UNKNOWN,
            minAppVersion = minAppVersion ?: UNKNOWN,
            recommendAppVersion = recommendAppVersion ?: UNKNOWN,
        )
}
