package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.saved.SavedPlaceVO

/**
 * 장소 카드: 썸네일 + 이름 + "카테고리 · 지역"(+ 방문함 표시) + 저장(북마크) 버튼.
 * 저장함 장소 탭에서 쓴다. 코스 카드([SavedCourseCard])와 달리 상세 화면이 아직 없어
 * 카드 자체는 눌리지 않는다.
 */
@Composable
fun SavedPlaceCard(
    place: SavedPlaceVO,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .cardShadow(RoundedCornerShape(16.dp))
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.imagePlaceholder),
        ) {
            if (place.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = place.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
        ) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            DsText(
                // "카테고리 · 지역 · 방문함". 빈 값은 빼고 이어 붙여 빈 칸·고아 구분점을 남기지 않는다.
                text =
                    listOfNotNull(
                        place.categoryLabel.takeIf { it.isNotBlank() },
                        place.area.takeIf { it.isNotBlank() },
                        "방문함".takeIf { place.visited },
                    ).joinToString(" · "),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.bgDefaultLevel1)
                    .clickable(onClick = onBookmarkClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bookmark_filled_24),
                contentDescription = "저장 취소",
                tint = color.contentAccent,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
