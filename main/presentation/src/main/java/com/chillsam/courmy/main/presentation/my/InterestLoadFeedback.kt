package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.R

/** 선택 화면을 가리지 않는 조회 안내. 첫 조회 실패에도 복구 액션에 접근할 수 있게 유지한다. */
@Composable
internal fun InterestLoadFeedback(
    isLoading: Boolean,
    loadError: Boolean,
    onRetry: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val type = DesignSystemThemeImpl.typeScale
    if (isLoading) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = color.contentAccent, strokeWidth = 2.dp)
            DsText(
                stringResource(R.string.main_interest_load_feedback_loading),
                style = type.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
    } else if (loadError) {
        Surface(
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            color = color.bgDefaultLevel0,
            border = BorderStroke(1.dp, color.borderDefaultLevel0),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DsText(
                    stringResource(R.string.main_interest_load_feedback_error),
                    style = type.textRegularXS,
                    color = color.contentDefaultLevel2,
                    maxLines = 2,
                    modifier = Modifier.weight(1f).semantics { liveRegion = LiveRegionMode.Polite },
                )
                DsText(
                    stringResource(R.string.main_interest_load_feedback_retry),
                    style = type.textRegularXS,
                    color = color.contentAccent,
                    modifier =
                        Modifier
                            .clickable(
                                role = Role.Button,
                                onClick = onRetry,
                            ).minimumInteractiveComponentSize(),
                )
            }
        }
    }
}
