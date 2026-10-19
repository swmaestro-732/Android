package com.chillsam.courmy.main.presentation.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

private val SearchBarShape = RoundedCornerShape(percent = 50)
private val SearchBarElevation = 6.dp
private val SearchBarHorizontalPadding = 16.dp
private val SearchBarVerticalPadding = 13.dp
private val SearchIconSize = 18.dp
private val ActionIconSize = 16.dp

/**
 * 지도 탭 상단 검색바(FS-10). 실제 입력은 검색 화면에서 하고 여기서는 눌러서 넘어가기만 한다.
 *
 * 오른쪽 `필터` 는 검색과 다른 동작이라 바깥쪽 clickable 이 삼키지 않게 자기 clickable 을 따로 갖는다.
 */
@Composable
internal fun MapSearchEntryBar(
    placeholder: String,
    onClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .shadow(SearchBarElevation, SearchBarShape)
                .clip(SearchBarShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick)
                .padding(horizontal = SearchBarHorizontalPadding, vertical = SearchBarVerticalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search_24),
            contentDescription = null,
            tint = color.contentDefaultLevel2,
            modifier = Modifier.size(SearchIconSize),
        )
        DsText(
            text = placeholder,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel3,
            modifier = Modifier.weight(1f),
        )
        Row(
            modifier =
                Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onFilterClick,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_filter_list_24),
                contentDescription = null,
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(ActionIconSize),
            )
            DsText(
                text = "필터",
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
        }
    }
}

/**
 * 검색 결과 상단바(FS-10-ResultMap). 뒤로가기 버튼과 검색어가 담긴 바가 나란히 놓인다.
 *
 * 검색어 지우기(×)는 검색을 취소하는 뜻이라 상위가 뒤로/재검색 중 무엇으로 다룰지 정한다.
 */
@Composable
internal fun MapSearchQueryBar(
    query: String,
    onBackClick: () -> Unit,
    onQueryClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MapCircleIconButton(
            iconRes = R.drawable.ic_chevron_left_24,
            contentDescription = "뒤로",
            onClick = onBackClick,
        )
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .shadow(SearchBarElevation, SearchBarShape)
                    .clip(SearchBarShape)
                    .background(color.bgDefaultLevel1)
                    .clickable(onClick = onQueryClick)
                    .padding(
                        horizontal = SearchBarHorizontalPadding,
                        vertical = SearchBarVerticalPadding,
                    ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search_24),
                contentDescription = null,
                tint = color.contentDefaultLevel2,
                modifier = Modifier.size(SearchIconSize),
            )
            DsText(
                text = query,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel0,
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.close_small_24),
                contentDescription = "검색어 지우기",
                tint = color.contentDefaultLevel2,
                modifier =
                    Modifier
                        .size(SearchIconSize)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClearClick,
                        ),
            )
        }
    }
}
