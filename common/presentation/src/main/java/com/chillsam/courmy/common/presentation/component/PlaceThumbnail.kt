package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 장소 썸네일 공용 컴포저블. 사진 로딩 중·실패·미제공 시 카테고리 아이콘을 표시한다.
 *
 * 지도 시트의 장소 줄에서 쓰던 표현을 홈 근처 저장 장소·저장함·검색 결과가 함께 쓰도록 승격했다
 * — 회색 자리 지킴 대신 어디서든 같은 아이콘 placeholder 가 보인다.
 */
@Composable
fun PlaceThumbnail(
    url: String?,
    category: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(0.dp),
    detailCategory: PlaceDetailCategoryVO? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .clip(shape)
                .background(
                    Brush.linearGradient(listOf(color.bgAccentSubtle, color.bgDefaultLevel1)),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(placeIcon(category, detailCategory)),
            contentDescription = null,
            tint = color.contentAccent,
            modifier =
                Modifier
                    .background(
                        color.bgDefaultLevel1.copy(alpha = 0.65f),
                        CircleShape,
                    ).padding(7.dp)
                    .size(26.dp),
        )
        if (!url.isNullOrBlank()) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
