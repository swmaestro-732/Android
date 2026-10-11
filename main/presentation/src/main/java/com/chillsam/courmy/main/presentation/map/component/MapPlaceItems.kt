package com.chillsam.courmy.main.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapPlaceVO

private val CardShape = RoundedCornerShape(12.dp)
private val ThumbShape = RoundedCornerShape(10.dp)

/** 캐러셀 카드 한 장의 너비(FS-10). 옆 카드가 살짝 보여 "옆으로 넘길 수 있다"는 게 드러나는 폭. */
private val CarouselCardWidth = 180.dp
private val CarouselImageHeight = 108.dp

/**
 * 시트 캐러셀의 장소 카드(FS-10). 사진 위에 평점 배지를 얹고 아래에 이름·요약을 둔다.
 */
@Composable
internal fun MapPlaceCarouselCard(
    place: MapPlaceVO,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .width(CarouselCardWidth)
                .clip(CardShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
    ) {
        Box {
            PlaceImage(
                url = place.thumbnailUrl,
                category = place.category,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(CarouselImageHeight),
            )
            if (place.rating > 0) {
                RatingBadge(
                    rating = place.rating,
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                )
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            DsText(
                text = place.categoryAndWalk(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** 선택한 장소 하나를 보여주는 시트 카드(FS-10-ResultMap). */
@Composable
internal fun MapPlaceDetailCard(
    place: MapPlaceVO,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PlaceImage(
            url = place.thumbnailUrl,
            category = place.category,
            modifier = Modifier.size(72.dp),
            shape = ThumbShape,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            PlaceSummaryLine(place = place)
            if (place.closingTime != null) {
                DsText(
                    text = place.openStatusText(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = if (place.openNow) color.contentSuccess else color.contentDefaultLevel3,
                )
            }
        }
        SaveToggleIcon(saved = place.saved, onClick = onSaveClick)
    }
}

/** 결과 리스트의 한 줄(FS-10-List). 왼쪽에 순위 숫자가 붙는다. */
@Composable
internal fun MapRankedPlaceRow(
    rank: Int,
    place: MapPlaceVO,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(if (isSelected) color.bgAccentSubtle else color.bgDefaultLevel1)
                .border(1.dp, if (isSelected) color.borderAccent else color.bgDefaultLevel1, CardShape)
                .semantics { selected = isSelected }
                .clickable(onClick = onClick)
                .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsText(
            text = rank.toString(),
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel2,
        )
        PlaceImage(
            url = place.thumbnailUrl,
            category = place.category,
            modifier = Modifier.size(54.dp),
            shape = ThumbShape,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            PlaceSummaryLine(place = place)
        }
        SaveToggleIcon(saved = place.saved, onClick = onSaveClick)
    }
}

/** `★ 4.8 · 카페 · 베이커리 · 도보 6분` 한 줄. 별만 강조색이라 평점 숫자와 나머지를 나눠 그린다. */
@Composable
private fun PlaceSummaryLine(
    place: MapPlaceVO,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val style = DesignSystemThemeImpl.typeScale.textRegularXS
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        if (place.rating > 0) {
            Icon(
                painter = painterResource(R.drawable.ic_star_24),
                contentDescription = null,
                tint = color.contentRating,
                modifier = Modifier.size(13.dp),
            )
            DsText(text = place.rating.toString(), style = style, color = color.contentDefaultLevel1)
        }
        DsText(
            text = place.categoryAndWalk(prefixSeparator = place.rating > 0),
            style = style,
            color = color.contentDefaultLevel2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 사진 위에 얹는 어두운 평점 배지(FS-10 캐러셀). */
@Composable
private fun RatingBadge(
    rating: Double,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(percent = 50))
                .background(color.contentDefaultLevel1)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_star_24),
            contentDescription = null,
            tint = color.contentOnAccent,
            modifier = Modifier.size(12.dp),
        )
        DsText(
            text = rating.toString(),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentOnAccent,
        )
    }
}

/** 저장(북마크) 토글. 저장되면 채운 아이콘 + 강조색으로 바뀐다. */
@Composable
private fun SaveToggleIcon(
    saved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Icon(
        painter =
            painterResource(
                if (saved) R.drawable.ic_bookmark_filled_24 else R.drawable.ic_tab_bookmark_24,
            ),
        contentDescription = if (saved) "저장 해제" else "저장",
        tint = if (saved) color.contentAccent else color.contentDefaultLevel3,
        modifier =
            modifier
                .size(22.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
    )
}

/** 사진 로딩 중·실패·미제공 시 카테고리 아이콘을 표시한다. */
@Composable
private fun PlaceImage(
    url: String?,
    category: String,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(0.dp),
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
            painter = painterResource(placeCategoryIcon(category)),
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

/** `카페 · 베이커리 · 도보 6분`. [prefixSeparator] 면 앞에 붙는 평점과 잇도록 구분점으로 시작한다. */
private fun MapPlaceVO.categoryAndWalk(prefixSeparator: Boolean = false): String {
    val parts = listOfNotNull(category, subCategory, walkingMinutes?.let { "도보 ${it}분" })
    val joined = parts.joinToString(" · ")
    return if (prefixSeparator && joined.isNotEmpty()) "· $joined" else joined
}

/** `영업 중 · 22:00 종료`. */
private fun MapPlaceVO.openStatusText(): String {
    val status = if (openNow) "영업 중" else "영업 종료"
    return if (closingTime != null) "$status · $closingTime 종료" else status
}

@Preview
@Composable
private fun PlaceImagePreview() {
    DesignSystemTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("카페", "음식점", "공원").forEach { category ->
                PlaceImage(url = null, category = category, modifier = Modifier.size(72.dp), shape = ThumbShape)
            }
        }
    }
}
