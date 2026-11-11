package com.chillsam.courmy.main.presentation.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.entity.category.orCategoryLabel
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.PlaceThumbnail
import com.chillsam.courmy.common.presentation.translation.rememberTaxonomyLabel
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.presentation.R as MainR

private val CardShape = RoundedCornerShape(14.dp)
private val ThumbShape = RoundedCornerShape(10.dp)

/**
 * 검색 결과의 장소 한 줄(FS-18). 썸네일 · (이름+카테고리 / 평점 / 상세 주소) · 저장 토글.
 *
 * 1줄: 이름 옆에 카테고리를 인라인으로, 2줄: ★평점, 3줄: 상세 주소.
 * 주소가 비었거나 1줄 카테고리와 같으면(외부 검색 폴백 중복) 3줄은 생략해 2줄로 줄어든다.
 */
@Composable
internal fun SearchPlaceRow(
    place: MapPlaceVO,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val categoryLine = place.categoryLine()
    val showAddress =
        place.address.isNotBlank() && place.address != categoryLine
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick)
                .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PlaceThumbnail(
            url = place.thumbnailUrl,
            category = place.category,
            detailCategory = place.detailCategory,
            modifier = Modifier.size(58.dp),
            shape = ThumbShape,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
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
                    modifier = Modifier.alignByBaseline().weight(1f, fill = false),
                )
                if (categoryLine.isNotEmpty()) {
                    DsText(
                        text = rememberTaxonomyLabel(categoryLine),
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }
            // 평점만 보여 준다(평가 없는 장소는 별 아이콘째 숨긴다). 도보 시간은 표시하지 않는다.
            if (place.rating > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star_24),
                        contentDescription = null,
                        tint = color.contentRating,
                        modifier = Modifier.size(13.dp),
                    )
                    DsText(
                        text = place.rating.toString(),
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (showAddress) {
                DsText(
                    text = place.address,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        SaveToggleIcon(saved = place.saved, onClick = onSaveClick)
    }
}

/** `카페 · 베이커리` — 1줄에서 이름 옆에 붙는 카테고리. 둘 다 비면 빈 문자열. */
private fun MapPlaceVO.categoryLine(): String =
    listOfNotNull(
        detailCategory.orCategoryLabel(category).takeIf { it.isNotBlank() },
        subCategory,
    ).joinToString(" · ")

/** `장소 모두 보기 →` 처럼 구역 아래에 놓이는 더 보기 버튼. */
@Composable
internal fun SearchShowAllButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(color.bgDefaultLevel1)
                .border(width = 1.dp, color = color.borderDefaultLevel1, shape = CardShape)
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel1,
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            tint = color.contentDefaultLevel2,
            modifier = Modifier.padding(start = 4.dp).size(14.dp),
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
        contentDescription =
            if (saved) {
                stringResource(MainR.string.main_search_result_save_toggle_remove)
            } else {
                stringResource(MainR.string.main_search_result_save_toggle_add)
            },
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
