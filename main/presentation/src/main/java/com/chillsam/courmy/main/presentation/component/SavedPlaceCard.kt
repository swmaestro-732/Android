package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.PlaceThumbnail
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.saved.SavedPlaceVO

/**
 * 장소 카드: 썸네일 + 이름 + "카테고리 · 지역"(+ 방문함 표시) + 저장(북마크) 버튼.
 * 카드 본문은 장소 상세를 열고, 북마크는 저장 취소 확인을 연다.
 */
@Composable
fun SavedPlaceCard(
    place: SavedPlaceVO,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1)
                .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceThumbnail(
            url = place.imageUrl,
            category = place.categoryLabel,
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(12.dp),
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
        ) {
            // 1줄: 장소명 + 카테고리 라벨 인라인. 카테고리가 비면 이름만.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DsText(
                    text = place.name,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier =
                        Modifier
                            .alignByBaseline()
                            .weight(1f, fill = false),
                )
                if (place.categoryLabel.isNotBlank()) {
                    DsText(
                        text = place.categoryLabel,
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }
            // 2줄: 지역 + 방문함 칩. 둘 다 없으면 2줄 자체를 생략한다.
            if (place.area.isNotBlank() || place.visited) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    if (place.area.isNotBlank()) {
                        DsText(
                            text = place.area,
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = color.contentDefaultLevel2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false),
                        )
                    }
                    if (place.visited) {
                        DsText(
                            text = "방문함",
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = color.contentAccent,
                            maxLines = 1,
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(color.bgAccentSubtle)
                                    .padding(horizontal = 9.dp, vertical = 3.dp),
                        )
                    }
                }
            }
        }
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.bgDefaultLevel1)
                    .clickable(onClick = onBookmarkClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bookmark_filled_24),
                contentDescription = "저장 취소",
                tint = color.contentAccent,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
