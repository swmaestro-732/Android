package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 별점의 최대 별 개수. 서버가 주는 별점은 1~[MAX_RATING] 이다. */
private const val MAX_RATING = 5

/**
 * 별점 표시. [rating] 개를 채운 별로, 나머지를 빈 별로 그린다.
 *
 * 표시 전용이라 탭을 받지 않는다 — 리뷰 작성 화면의 별점 **입력**은 별도 컴포넌트로 만든다.
 * 별 하나하나는 스크린 리더에 의미가 없어 Row 하나로 묶어 "별점 4점" 처럼 한 번만 읽힌다.
 *
 * @param rating 채울 별 개수. 범위를 벗어난 값은 0~[MAX_RATING] 으로 자른다.
 * @param starSize 별 하나의 크기. 본문에 섞여 들어가는 자리라 호출부가 글자 크기에 맞춰 정한다.
 */
@Composable
fun DsRatingStars(
    rating: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 12.dp,
    filledColor: Color = DesignSystemThemeImpl.designSystemColor.contentRating,
    emptyColor: Color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
) {
    val filled = rating.coerceIn(0, MAX_RATING)
    Row(
        modifier = modifier.clearAndSetSemantics { contentDescription = "별점 ${filled}점" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        repeat(MAX_RATING) { index ->
            DsRatingStar(
                starSize = starSize,
                tint = if (index < filled) filledColor else emptyColor,
            )
        }
    }
}

/**
 * 별점 입력. 별 하나를 누르면 그 개수가 점수가 된다(1~[MAX_RATING]).
 *
 * [DsRatingStars] 와 달리 별마다 탭을 받으므로 별 하나가 각각 스크린 리더 대상이다
 * — 묶어서 읽히면 어느 별을 누르는지 알 수 없다.
 *
 * 같은 별을 다시 눌러 0점으로 되돌리는 동작은 넣지 않았다. 별점은 후기 작성의 필수값이라
 * 실수로 비우면 제출 버튼이 잠겨 버리고, 사용자는 왜 잠겼는지 알기 어렵다.
 */
@Composable
fun DsRatingInput(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 32.dp,
    enabled: Boolean = true,
) {
    val filled = rating.coerceIn(0, MAX_RATING)
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(MAX_RATING) { index ->
            val value = index + 1
            Box(
                modifier =
                    Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .clip(CircleShape)
                        .clickable(enabled = enabled) { onRatingChange(value) }
                        .semantics {
                            contentDescription = "별점 ${value}점 주기"
                            selected = value == filled
                        },
                contentAlignment = Alignment.Center,
            ) {
                DsRatingStar(
                    starSize = starSize,
                    tint = if (index < filled) color.contentRating else color.contentDefaultLevel3,
                )
            }
        }
    }
}

/**
 * 별 하나. 평균 별점처럼 **숫자가 본체**이고 별은 기호로만 붙는 자리에 쓴다
 * (예: "리뷰 ★ 4.8"). 채운 별 개수로 점수를 나타내는 자리는 [DsRatingStars] 다.
 *
 * 아이콘·크기 관례를 한곳에 두려고 별도 컴포넌트로 뺐다 — 호출부가 `ic_star_24` 를 직접 집으면
 * 아이콘을 바꿀 때 갈라진다.
 */
@Composable
fun DsRatingStar(
    modifier: Modifier = Modifier,
    starSize: Dp = 12.dp,
    tint: Color = DesignSystemThemeImpl.designSystemColor.contentRating,
) {
    Icon(
        painter = painterResource(R.drawable.ic_star_24),
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(starSize),
    )
}

@Preview
@Composable
private fun DsRatingStarsPreview() {
    DesignSystemTheme {
        DsRatingStars(rating = 4)
    }
}
