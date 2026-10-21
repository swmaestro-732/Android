package com.chillsam.courmy.main.presentation.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.search.RankChange
import com.chillsam.courmy.main.entity.search.TrendingKeywordVO

private val RankColumnWidth = 22.dp

/**
 * 급상승 검색어 한 줄(FS-17): 순위 · 검색어 · 변동.
 *
 * 순위 칸에 고정 폭을 주는 건 두 자리 수(10위)가 되어도 검색어 시작점이 흔들리지 않게 하려는 것이다.
 */
@Composable
internal fun TrendingKeywordRow(
    item: TrendingKeywordVO,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier.clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsText(
            text = item.rank.toString(),
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            // 상위 3개만 강조색으로 눈에 띄게 한다(시안 기준).
            color = if (item.rank <= TOP_RANK_THRESHOLD) color.contentAccent else color.contentDefaultLevel3,
            modifier = Modifier.width(RankColumnWidth),
        )
        DsText(
            text = item.keyword,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel0,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        RankChangeBadge(change = item.change)
    }
}

/** 상위권으로 볼 순위. 여기까지는 순위 숫자를 강조색으로 그린다. */
private const val TOP_RANK_THRESHOLD = 3

@Composable
private fun RankChangeBadge(
    change: RankChange,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    when (change) {
        is RankChange.Up -> {
            RankArrow(places = change.places, tint = color.contentDanger, up = true, modifier = modifier)
        }

        is RankChange.Down -> {
            RankArrow(places = change.places, tint = color.contentDefaultLevel3, up = false, modifier = modifier)
        }

        RankChange.New -> {
            DsText(
                text = "NEW",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentOnAccent,
                modifier =
                    modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(color.contentDanger)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }

        RankChange.Same -> {
            DsText(
                text = "—",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun RankArrow(
    places: Int,
    tint: Color,
    up: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_down_24),
            contentDescription = if (up) "${places}계단 상승" else "${places}계단 하락",
            tint = tint,
            // 아래 화살표 하나만 두고 상승은 뒤집어 쓴다. 같은 모양이라 자산을 늘릴 이유가 없다.
            modifier = Modifier.size(12.dp).rotate(if (up) 180f else 0f),
        )
        DsText(
            text = places.toString(),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = tint,
        )
    }
}
