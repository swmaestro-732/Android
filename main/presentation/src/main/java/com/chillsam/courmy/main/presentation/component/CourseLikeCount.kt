package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 목록용 좋아요 집계. 개인의 좋아요 여부나 토글 동작을 나타내지 않는다. */
@Composable
internal fun CourseLikeCount(
    count: String,
    modifier: Modifier = Modifier,
) {
    if (count.isBlank()) return
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = "좋아요 $count" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_like_off_24),
            contentDescription = null,
            tint = color.contentDefaultLevel3,
            modifier = Modifier.size(16.dp),
        )
        DsText(
            text = count,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
    }
}
