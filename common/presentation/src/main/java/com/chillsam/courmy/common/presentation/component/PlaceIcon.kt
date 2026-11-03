package com.chillsam.courmy.common.presentation.component

import androidx.annotation.DrawableRes
import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import com.chillsam.courmy.common.presentation.R

/** 모든 장소 UI의 단일 아이콘 선택 경로. 상세값 → 상위 카테고리 → 기본 아이콘 순서로 선택한다. */
@DrawableRes
fun placeIcon(
    category: String,
    detailCategory: PlaceDetailCategoryVO? = null,
): Int = detailCategoryIcons[detailCategory] ?: parentCategoryIcon(category)

private val detailCategoryIcons =
    mapOf(
        PlaceDetailCategoryVO.CAFE to R.drawable.ic_place_detail_cafe_24,
        PlaceDetailCategoryVO.BAKERY to R.drawable.ic_place_detail_bakery_24,
        PlaceDetailCategoryVO.RESTAURANT to R.drawable.ic_place_detail_restaurant_24,
        PlaceDetailCategoryVO.PUB to R.drawable.ic_place_detail_pub_24,
        PlaceDetailCategoryVO.DEPARTMENT_STORE to R.drawable.ic_place_detail_department_store_24,
        PlaceDetailCategoryVO.SHOPPING_MALL to R.drawable.ic_place_detail_shopping_mall_24,
        PlaceDetailCategoryVO.OUTLET_STORE to R.drawable.ic_place_detail_outlet_store_24,
        PlaceDetailCategoryVO.FASHION to R.drawable.ic_place_detail_fashion_24,
        PlaceDetailCategoryVO.MARKET to R.drawable.ic_place_detail_market_24,
        PlaceDetailCategoryVO.HOUSEHOLD_GOODS to R.drawable.ic_place_detail_household_goods_24,
        PlaceDetailCategoryVO.CONVENIENCE_STORE to R.drawable.ic_place_detail_convenience_store_24,
        PlaceDetailCategoryVO.SUPERMARKET to R.drawable.ic_place_detail_supermarket_24,
        PlaceDetailCategoryVO.SOUVENIR to R.drawable.ic_place_detail_souvenir_24,
        PlaceDetailCategoryVO.POPUP_STORE to R.drawable.ic_place_detail_popup_store_24,
        PlaceDetailCategoryVO.CULTURE_FACILITY to R.drawable.ic_place_detail_culture_facility_24,
        PlaceDetailCategoryVO.BOOKSTORE to R.drawable.ic_place_detail_bookstore_24,
        PlaceDetailCategoryVO.GALLERY to R.drawable.ic_place_detail_gallery_24,
        PlaceDetailCategoryVO.PERFORMANCE to R.drawable.ic_place_detail_performance_24,
        PlaceDetailCategoryVO.FESTIVAL to R.drawable.ic_place_detail_festival_24,
        PlaceDetailCategoryVO.LIBRARY to R.drawable.ic_place_detail_library_24,
        PlaceDetailCategoryVO.EXPERIENCE_TRIP to R.drawable.ic_place_detail_experience_trip_24,
        PlaceDetailCategoryVO.AQUARIUM to R.drawable.ic_place_detail_aquarium_24,
        PlaceDetailCategoryVO.POTTERY to R.drawable.ic_place_detail_pottery_24,
        PlaceDetailCategoryVO.NEEDLEWORK to R.drawable.ic_place_detail_needlework_24,
        PlaceDetailCategoryVO.THEME_PARK to R.drawable.ic_place_detail_theme_park_24,
        PlaceDetailCategoryVO.ZOO to R.drawable.ic_place_detail_zoo_24,
        PlaceDetailCategoryVO.PARK to R.drawable.ic_place_detail_park_24,
        PlaceDetailCategoryVO.MOUNTAIN to R.drawable.ic_place_detail_mountain_24,
        PlaceDetailCategoryVO.VALLEY to R.drawable.ic_place_detail_valley_24,
        PlaceDetailCategoryVO.HIKING_TRAIL to R.drawable.ic_place_detail_hiking_trail_24,
        PlaceDetailCategoryVO.FOREST to R.drawable.ic_place_detail_forest_24,
        PlaceDetailCategoryVO.ISLAND to R.drawable.ic_place_detail_island_24,
        PlaceDetailCategoryVO.LAKE to R.drawable.ic_place_detail_lake_24,
        PlaceDetailCategoryVO.STREAM to R.drawable.ic_place_detail_stream_24,
        PlaceDetailCategoryVO.WATERFALL to R.drawable.ic_place_detail_waterfall_24,
        PlaceDetailCategoryVO.BOTANICAL_GARDEN to R.drawable.ic_place_detail_botanical_garden_24,
        PlaceDetailCategoryVO.WALKING_TRAIL to R.drawable.ic_place_detail_walking_trail_24,
        PlaceDetailCategoryVO.ECOLOGY_RESERVE to R.drawable.ic_place_detail_ecology_reserve_24,
        PlaceDetailCategoryVO.CAMPING to R.drawable.ic_place_detail_camping_24,
        PlaceDetailCategoryVO.TOURIST_ATTRACTION to R.drawable.ic_place_detail_tourist_attraction_24,
        PlaceDetailCategoryVO.BUILDING to R.drawable.ic_place_detail_building_24,
        PlaceDetailCategoryVO.BUDDHIST_TEMPLE to R.drawable.ic_place_detail_buddhist_temple_24,
        PlaceDetailCategoryVO.BRIDGE to R.drawable.ic_place_detail_bridge_24,
        PlaceDetailCategoryVO.LEISURE_FACILITY to R.drawable.ic_place_detail_leisure_facility_24,
        PlaceDetailCategoryVO.SPORTS_LEISURE to R.drawable.ic_place_detail_sports_leisure_24,
        PlaceDetailCategoryVO.BATHHOUSE_SAUNA to R.drawable.ic_place_detail_bathhouse_sauna_24,
        PlaceDetailCategoryVO.YOGA_PILATES to R.drawable.ic_place_detail_yoga_pilates_24,
        PlaceDetailCategoryVO.HOT_SPRING to R.drawable.ic_place_detail_hot_spring_24,
    )

/** API enum과 검색 API·화면 라벨의 한글 카테고리를 같은 아이콘으로 표시한다. */
@DrawableRes
private fun parentCategoryIcon(category: String): Int =
    categoryIcons
        .firstOrNull { (keywords, _) ->
            category.split(" · ").any { part -> keywords.any { part.trim().equals(it, ignoreCase = true) } }
        }?.second ?: R.drawable.ic_place_detail_default_pin_24

private val categoryIcons =
    listOf(
        listOf("CAFE", "카페·디저트", "카페", "커피", "디저트", "베이커리", "제과") to R.drawable.ic_place_cafe_24,
        listOf("BAR", "술집·바", "술집", "주점", "맥주", "와인", "펍") to R.drawable.ic_place_bar_24,
        listOf(
            "RESTAURANT",
            "음식점",
            "음식",
            "맛집",
            "식당",
            "한식",
            "일식",
            "중식",
            "양식",
            "분식",
        ) to R.drawable.ic_place_restaurant_24,
        listOf("SHOPPING", "쇼핑·상점", "쇼핑", "편집", "백화점", "상점") to R.drawable.ic_place_shopping_24,
        listOf("CULTURE", "문화·전시", "문화", "전시", "갤러리", "미술", "박물관") to R.drawable.ic_place_culture_24,
        listOf("EXPERIENCE", "체험·클래스", "체험", "공방", "클래스") to R.drawable.ic_place_experience_24,
        listOf("NATURE", "자연·아웃도어", "자연", "공원", "산책", "정원") to R.drawable.ic_place_nature_24,
        listOf("LANDMARK", "역사·명소", "랜드마크", "명소", "관광") to R.drawable.ic_place_landmark_24,
        listOf("ENTERTAINMENT", "여가·놀거리", "여가", "엔터테인먼트", "놀거리", "오락", "놀이", "게임", "영화", "공연") to
            R.drawable.ic_place_entertainment_24,
        listOf("WELLNESS", "웰니스·힐링", "웰니스", "운동", "요가", "스파", "피트니스") to R.drawable.ic_place_wellness_24,
    )
