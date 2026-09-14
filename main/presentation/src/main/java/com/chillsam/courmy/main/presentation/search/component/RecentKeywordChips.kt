package com.chillsam.courmy.main.presentation.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.collections.immutable.ImmutableList

private val ChipShape = RoundedCornerShape(percent = 50)

/**
 * 최근 검색어 칩 묶음(FS-17).
 *
 * 공용 [com.chillsam.courmy.common.presentation.component.DsChip] 을 쓰지 않는 건 이 칩이
 * 삭제(×) 버튼을 품고 있어서다 — DsChip 은 선택 상태만 다루는 필터용이다.
 *
 * 칩 개수가 정해져 있지 않아 [FlowRow] 로 줄을 넘긴다. 가로 스크롤로 두면 뒤쪽 검색어가 화면 밖에
 * 숨어 지우기 어렵다.
 */
@Composable
internal fun RecentKeywordChips(
    keywords: ImmutableList<String>,
    onKeywordClick: (String) -> Unit,
    onRemoveClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        keywords.forEach { keyword ->
            RecentKeywordChip(
                keyword = keyword,
                onClick = { onKeywordClick(keyword) },
                onRemove = { onRemoveClick(keyword) },
            )
        }
    }
}

@Composable
private fun RecentKeywordChip(
    keyword: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .clip(ChipShape)
                .background(color.bgDefaultLevel1)
                .border(width = 1.dp, color = color.borderDefaultLevel0, shape = ChipShape)
                .clickable(onClick = onClick)
                .padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DsText(
            text = keyword,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel1,
        )
        Icon(
            painter = painterResource(R.drawable.close_small_24),
            contentDescription = "$keyword 검색 기록 삭제",
            tint = color.contentDefaultLevel3,
            modifier =
                Modifier
                    .size(14.dp)
                    // 지우기는 칩 전체 클릭(=재검색)과 다른 동작이라 자기 클릭을 따로 갖는다.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onRemove,
                    ),
        )
    }
}
