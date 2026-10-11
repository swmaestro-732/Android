package com.chillsam.courmy.main.presentation.map.component

import androidx.annotation.DrawableRes
import com.chillsam.courmy.main.presentation.R

/** API enum과 검색 API의 한글 세부 카테고리를 같은 아이콘으로 표시한다. */
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
        listOf("ENTERTAINMENT", "놀거리", "오락", "놀이", "게임", "영화", "공연") to R.drawable.ic_place_entertainment_24,
        listOf("WELLNESS", "웰니스", "운동", "요가", "스파", "피트니스") to R.drawable.ic_place_wellness_24,
    )
