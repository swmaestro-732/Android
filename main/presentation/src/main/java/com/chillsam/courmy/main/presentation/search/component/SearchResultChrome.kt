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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

private val PillShape = RoundedCornerShape(percent = 50)

/**
 * 검색어를 담은 상단바(FS-18). 누르면 검색어를 고치러 입력 화면으로 돌아간다.
 *
 * 지도 화면의 같은 역할 컴포넌트와 따로 두는 건 그쪽은 지도 위에 떠 있어 그림자를 지고,
 * 이쪽은 배경 위에 평평하게 놓여 모양이 다르기 때문이다.
 */
@Composable
internal fun SearchResultTopBar(
    query: String,
    onBack: () -> Unit,
    onQueryClick: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left_24),
            contentDescription = "뒤로",
            tint = color.contentDefaultLevel0,
            modifier =
                Modifier
                    .size(24.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    ),
        )
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .clip(PillShape)
                    .background(color.bgDefaultLevel1)
                    .clickable(onClick = onQueryClick)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search_24),
                contentDescription = null,
                tint = color.contentDefaultLevel2,
                modifier = Modifier.size(18.dp),
            )
            DsText(
                text = query,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.close_small_24),
                contentDescription = "검색어 지우기",
                tint = color.contentDefaultLevel2,
                modifier =
                    Modifier
                        .size(18.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClear,
                        ),
            )
        }
    }
}

/**
 * 결과 구분 탭 칩(전체·장소·코스).
 *
 * 공용 [com.chillsam.courmy.common.presentation.component.DsChip] 을 안 쓰는 건 선택 색이 달라서다 —
 * DsChip 의 선택 상태는 강조색(초록)인데 시안의 이 탭은 검정으로 채운다. 필터가 아니라
 * "지금 보고 있는 목록"을 가리키는 자리라 색을 구분해 둔 것으로 보고 그대로 따른다.
 */
@Composable
internal fun SearchTabChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .clip(PillShape)
                .background(if (selected) color.contentDefaultLevel0 else color.bgDefaultLevel1)
                .then(
                    if (selected) {
                        Modifier
                    } else {
                        Modifier.border(1.dp, color.borderDefaultLevel0, PillShape)
                    },
                ).clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = if (selected) color.contentOnAccent else color.contentDefaultLevel1,
        )
    }
}

/** 결과 개수 + 정렬 선택이 놓이는 줄(`장소 18곳` / `거리순 ⌄`). */
@Composable
internal fun SearchResultSectionBar(
    title: String,
    sortLabel: String,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        Row(
            modifier =
                Modifier
                    .clip(PillShape)
                    .background(color.bgDefaultLevel1)
                    .border(1.dp, color.borderDefaultLevel1, PillShape)
                    .clickable(onClick = onSortClick)
                    .padding(start = 14.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            DsText(
                text = sortLabel,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down_24),
                contentDescription = null,
                tint = color.contentDefaultLevel2,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

/**
 * "지도에서 한눈에 보기" 안내 카드(FS-18 전체 탭).
 *
 * 검색 결과를 지도로 넘겨 보는 입구다. 목록만으로는 장소들이 서로 얼마나 가까운지 알 수 없어
 * 장소 구역 맨 위에 둔다.
 */
@Composable
internal fun SearchMapPreviewCard(
    placeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(color.contentDefaultLevel2)
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            DsText(
                text = "지도에서 한눈에 보기",
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentOnAccent,
            )
            DsText(
                text = "장소 ${placeCount}곳",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentOnAccent,
            )
        }
        Row(
            modifier =
                Modifier
                    .clip(PillShape)
                    .background(color.bgDefaultLevel1)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DsText(
                text = "지도 보기",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel0,
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right_24),
                contentDescription = null,
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(13.dp),
            )
        }
    }
}
