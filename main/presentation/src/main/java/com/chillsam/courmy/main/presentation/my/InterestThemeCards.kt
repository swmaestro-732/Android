package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.component.themeImageRes

private const val LARGE_FONT_SCALE = 1.3f

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InterestThemeCards(
    selected: Set<String>,
    enabled: Boolean,
    onToggle: (String) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        maxItemsInEachRow = if (LocalDensity.current.fontScale > LARGE_FONT_SCALE) 2 else 3,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CourseTagVO.entries.forEach { theme ->
            InterestThemeCard(
                theme = theme,
                selected = theme.code in selected,
                enabled = enabled,
                onToggle = { onToggle(theme.code) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun InterestThemeCard(
    theme: CourseTagVO,
    selected: Boolean,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier =
            modifier
                .alpha(if (enabled) 1f else 0.45f)
                .clip(shape)
                .background(if (selected) color.bgAccentSubtle else color.bgDefaultLevel1)
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) color.borderAccent else color.borderDefaultLevel0,
                    shape = shape,
                ).toggleable(value = selected, enabled = enabled, role = Role.Checkbox, onValueChange = { onToggle() }),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            DsText(
                text = theme.label,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = if (selected) color.contentAccent else color.contentDefaultLevel0,
                maxLines = Int.MAX_VALUE,
                modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp).padding(end = 18.dp),
            )
            Image(
                painter = painterResource(theme.themeImageRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alignment = Alignment.BottomEnd,
                modifier = Modifier.fillMaxWidth().height(72.dp),
            )
        }
        if (selected) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(18.dp)
                        .background(color.bgAccent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "✓",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.bgDefaultLevel1,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun InterestThemeCardsPreview() {
    DesignSystemTheme {
        InterestThemeCards(selected = setOf("date", "cafetour", "nature"), enabled = true, onToggle = {})
    }
}
