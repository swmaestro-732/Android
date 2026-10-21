package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

@Composable
internal fun PlaceReviewEmptyState(
    enabled: Boolean,
    onWriteReview: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(64.dp).background(color.bgAccentSubtle, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_review_bubble_24),
                contentDescription = null,
                tint = color.contentAccent,
                modifier = Modifier.size(34.dp),
            )
        }
        DsText(
            text = "아직 등록된 후기가 없어요",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
            textAlign = TextAlign.Center,
            maxLines = Int.MAX_VALUE,
        )
        DsText(
            text = "다녀오셨다면 첫 후기를 남겨주세요.",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel3,
            textAlign = TextAlign.Center,
            maxLines = Int.MAX_VALUE,
        )
        Spacer(Modifier.height(4.dp))
        PlaceReviewWriteButton(enabled = enabled, onClick = onWriteReview, firstReview = true)
    }
}

@Composable
internal fun PlaceReviewWriteButton(
    enabled: Boolean,
    onClick: () -> Unit,
    firstReview: Boolean = false,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.heightIn(min = 48.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, if (enabled) color.contentAccent else color.borderDefaultLevel0),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color.contentAccent),
    ) {
        Icon(painterResource(R.drawable.ic_review_write_24), contentDescription = null, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        DsText(
            text = if (firstReview) "첫 후기 남기기" else "후기 남기기",
            color = if (enabled) color.contentAccent else color.contentDefaultLevel3,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            maxLines = Int.MAX_VALUE,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceReviewEmptyStatePreview() {
    DesignSystemTheme {
        PlaceReviewEmptyState(enabled = true, onWriteReview = {})
    }
}
