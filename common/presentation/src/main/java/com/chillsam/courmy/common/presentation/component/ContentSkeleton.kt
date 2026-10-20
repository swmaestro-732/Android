package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 콘텐츠의 첫 로딩 자리. 저장·전송 버튼의 진행 표시와 구분한다. */
@Composable
fun ContentSkeleton(
    modifier: Modifier = Modifier,
    showHero: Boolean = true,
) {
    val color = DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0
    Column(
        modifier = modifier.fillMaxWidth().padding(20.dp).semantics { contentDescription = "콘텐츠를 불러오는 중" },
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (showHero) Box(Modifier.fillMaxWidth().height(160.dp).background(color, RoundedCornerShape(16.dp)))
        Box(Modifier.fillMaxWidth(0.6f).height(20.dp).background(color, RoundedCornerShape(6.dp)))
        Box(Modifier.fillMaxWidth().height(12.dp).background(color, RoundedCornerShape(6.dp)))
        repeat(3) {
            Box(Modifier.fillMaxWidth().height(64.dp).background(color, RoundedCornerShape(12.dp)))
        }
    }
}

@Preview
@Composable
private fun ContentSkeletonPreview() {
    DesignSystemTheme { ContentSkeleton() }
}
