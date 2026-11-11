package com.chillsam.courmy.common.presentation.translation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chillsam.courmy.common.domain.helper.StringResource
import com.chillsam.courmy.common.presentation.helper.toResId

/** 상태에는 문구의 의미와 값만 저장하고, 표시 시점의 locale로 해석한다. */
data class UiMessage(
    val key: StringResource,
    val args: List<Any> = emptyList(),
) {
    @Composable
    fun asString(): String = stringResource(key.toResId(), formatArgs = args.toTypedArray())
}
