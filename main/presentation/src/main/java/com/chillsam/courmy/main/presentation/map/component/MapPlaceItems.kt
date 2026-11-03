package com.chillsam.courmy.main.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.PlaceThumbnail
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapPlaceVO

private val CardShape = RoundedCornerShape(12.dp)
private val ThumbShape = RoundedCornerShape(10.dp)

/** 상단맞춤 행에서 순위·북마크를 첫 줄 텍스트 윗선에 얼추 맞추기 위한 top padding(textStrongS 높이 보정). */
private val RowTopAlignPadding = 1.dp

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
    selected: Boolean = false,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .width(CarouselCardWidth)
                .clip(CardShape)
                .background(color.bgDefaultLevel1)
                // 지도에서 고른 핀과 같은 장소임을 알리는 하이라이트. 배경은 그대로 두고 테두리만 쓴다.
                .then(
                    if (selected) Modifier.border(1.dp, color.contentAccent, CardShape) else Modifier,
                ).clickable(onClick = onClick),
    ) {
        Box {
            PlaceThumbnail(
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
                text = place.categoryMeta(),
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
        PlaceThumbnail(
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

/** 결과 리스트의 한 줄(FS-10-List). */
@Composable
internal fun MapRankedPlaceRow(
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
        // 3줄까지 늘어나므로 순위·썸네일·북마크를 세로 가운데가 아닌 상단에 맞춘다.
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PlaceThumbnail(
            url = place.thumbnailUrl,
            category = place.category,
            modifier = Modifier.size(54.dp),
            shape = ThumbShape,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            PlaceNameAndCategoryLine(place = place)
            RankedPlaceMetaLine(place = place)
            if (place.address.isNotBlank()) {
                DsText(
                    text = place.address,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        SaveToggleIcon(
            saved = place.saved,
            onClick = onSaveClick,
            // 북마크도 첫 줄 텍스트 윗선에 맞춘다.
            modifier = Modifier.padding(top = RowTopAlignPadding),
        )
    }
}

/** 첫 줄: 장소명 옆에 카테고리(+세부 카테고리)를 인라인으로 붙인다. 이름은 공간을 양보하며 줄지 않는다. */
@Composable
private fun PlaceNameAndCategoryLine(
    place: MapPlaceVO,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val category = place.categoryLabel()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DsText(
            text = place.name,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alignByBaseline().weight(1f, fill = false),
        )
        if (category.isNotEmpty()) {
            DsText(
                text = category,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.alignByBaseline(),
            )
        }
    }
}

/**
 * 결과 행 둘째 줄: `★ 4.8 · 영업 중 · 22:00 종료`.
 * 지도엔 위치가 이미 있어 영업 상태가 핵심이라 색을 달리 주려고 평점과 따로 그린다.
 * (카테고리는 첫 줄로 올라가 여기선 뺀다.)
 */
@Composable
private fun RankedPlaceMetaLine(
    place: MapPlaceVO,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val style = DesignSystemThemeImpl.typeScale.textRegularXS
    // 평점 → 영업 상태 순서로, 앞 항목이 있으면 가운뎃점을 붙여 잇는다.
    val hasRating = place.rating > 0
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        if (hasRating) {
            Icon(
                painter = painterResource(R.drawable.ic_star_24),
                contentDescription = null,
                tint = color.contentRating,
                modifier = Modifier.size(13.dp),
            )
            DsText(text = place.rating.toString(), style = style, color = color.contentDefaultLevel1)
        }
        val statusPrefix = hasRating
        val status = place.rankedOpenStatusText()
        DsText(
            text = if (statusPrefix) "· $status" else status,
            style = style,
            // 영업 중은 강조색, 영업 종료는 흐린 색으로 상태를 색으로 구분한다.
            color = if (place.openNow) color.contentAccent else color.contentDefaultLevel3,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** `★ 4.8 · 카페 · 베이커리` 한 줄. 별만 강조색이라 평점 숫자와 나머지를 나눠 그린다. */
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
            text = place.categoryMeta(prefixSeparator = place.rating > 0),
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

/** `카페 · 베이커리`. [prefixSeparator] 면 앞에 붙는 평점과 잇도록 구분점으로 시작한다. */
private fun MapPlaceVO.categoryMeta(prefixSeparator: Boolean = false): String {
    val parts = listOfNotNull(category, subCategory)
    val joined = parts.joinToString(" · ")
    return if (prefixSeparator && joined.isNotEmpty()) "· $joined" else joined
}

/** `영업 중 · 22:00 종료`. */
private fun MapPlaceVO.openStatusText(): String {
    val status = if (openNow) "영업 중" else "영업 종료"
    return if (closingTime != null) "$status · $closingTime 종료" else status
}

/** 첫 줄에 인라인으로 붙는 카테고리 라벨. 세부 카테고리가 있으면 `카페 · 베이커리`처럼 잇는다. */
private fun MapPlaceVO.categoryLabel(): String = listOfNotNull(category, subCategory).joinToString(" · ")

/**
 * 결과 행용 영업 상태 문구.
 * 영업 중이면 `영업 중`(마감 시간 있으면 `영업 중 · 22:00 종료`), 영업 종료면 `영업 종료`만 보여준다.
 */
private fun MapPlaceVO.rankedOpenStatusText(): String =
    when {
        openNow && closingTime != null -> "영업 중 · $closingTime 종료"
        openNow -> "영업 중"
        else -> "영업 종료"
    }

@Preview
@Composable
private fun PlaceImagePreview() {
    DesignSystemTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("카페·디저트", "음식점", "자연·아웃도어").forEach { category ->
                PlaceThumbnail(url = null, category = category, modifier = Modifier.size(72.dp), shape = ThumbShape)
            }
        }
    }
}
