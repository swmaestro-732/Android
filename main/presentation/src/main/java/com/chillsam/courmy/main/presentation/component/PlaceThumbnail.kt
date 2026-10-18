package com.chillsam.courmy.main.presentation.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.presentation.R

/**
 * 장소 썸네일 공용 컴포저블. 사진 로딩 중·실패·미제공 시 카테고리 아이콘을 표시한다.
 *
 * 지도 시트의 장소 줄에서 쓰던 표현을 홈 근처 저장 장소·저장함·검색 결과가 함께 쓰도록 승격했다
 * — 회색 자리 지킴 대신 어디서든 같은 아이콘 placeholder 가 보인다.
 */
@Composable
internal fun PlaceThumbnail(
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

/** API enum과 검색 API·화면 라벨의 한글 카테고리를 같은 아이콘으로 표시한다. */
@DrawableRes
internal fun placeCategoryIcon(category: String): Int =
    categoryIcons
        .firstOrNull { (keywords, _) ->
            keywords.any { category.contains(it, ignoreCase = true) }
        }?.second ?: com.chillsam.courmy.common.presentation.R.drawable.ic_location_24

private val categoryIcons =
    listOf(
        listOf("CAFE", "카페", "커피", "디저트", "베이커리", "제과") to R.drawable.ic_place_cafe_24,
        listOf("BAR", "술집", "주점", "맥주", "와인", "펍") to R.drawable.ic_place_bar_24,
        listOf("RESTAURANT", "음식", "맛집", "식당", "한식", "일식", "중식", "양식", "분식") to R.drawable.ic_place_restaurant_24,
        listOf("SHOPPING", "쇼핑", "편집", "백화점", "상점") to R.drawable.ic_place_shopping_24,
        listOf("CULTURE", "문화", "전시", "갤러리", "미술", "박물관") to R.drawable.ic_place_culture_24,
        listOf("EXPERIENCE", "체험", "공방", "클래스") to R.drawable.ic_place_experience_24,
        listOf("NATURE", "자연", "공원", "산책", "정원") to R.drawable.ic_place_nature_24,
        listOf("LANDMARK", "랜드마크", "명소", "관광") to R.drawable.ic_place_landmark_24,
        listOf("ENTERTAINMENT", "여가", "엔터테인먼트", "놀거리", "오락", "놀이", "게임", "영화", "공연") to
            R.drawable.ic_place_entertainment_24,
        listOf("WELLNESS", "웰니스", "운동", "요가", "스파", "피트니스") to R.drawable.ic_place_wellness_24,
    )
