package com.chillsam.courmy.common.entity.category

/** 확정된 장소 상세 아이콘 48종. 상위 장소 카테고리와 코스 테마를 대체하지 않는다. */
enum class PlaceDetailCategoryVO(
    val code: String,
    val label: String,
    val parent: PlaceCategoryVO,
) {
    CAFE("cafe", "카페", PlaceCategoryVO.CAFE),
    BAKERY("bakery", "제과·베이커리", PlaceCategoryVO.CAFE),
    RESTAURANT("restaurant", "음식점", PlaceCategoryVO.RESTAURANT),
    PUB("pub", "술집", PlaceCategoryVO.BAR),
    DEPARTMENT_STORE("department_store", "백화점", PlaceCategoryVO.SHOPPING),
    SHOPPING_MALL("shopping_mall", "복합쇼핑몰", PlaceCategoryVO.SHOPPING),
    OUTLET_STORE("outlet_store", "상설할인매장", PlaceCategoryVO.SHOPPING),
    FASHION("fashion", "패션", PlaceCategoryVO.SHOPPING),
    MARKET("market", "시장", PlaceCategoryVO.SHOPPING),
    HOUSEHOLD_GOODS("household_goods", "생활용품점", PlaceCategoryVO.SHOPPING),
    CONVENIENCE_STORE("convenience_store", "편의점", PlaceCategoryVO.SHOPPING),
    SUPERMARKET("supermarket", "슈퍼마켓", PlaceCategoryVO.SHOPPING),
    SOUVENIR("souvenir", "기념품판매", PlaceCategoryVO.SHOPPING),
    POPUP_STORE("popup_store", "팝업스토어", PlaceCategoryVO.SHOPPING),
    CULTURE_FACILITY("culture_facility", "문화시설", PlaceCategoryVO.CULTURE),
    BOOKSTORE("bookstore", "도서", PlaceCategoryVO.CULTURE),
    GALLERY("gallery", "화랑", PlaceCategoryVO.CULTURE),
    PERFORMANCE("performance", "공연", PlaceCategoryVO.CULTURE),
    FESTIVAL("festival", "페스티벌", PlaceCategoryVO.CULTURE),
    LIBRARY("library", "도서관", PlaceCategoryVO.CULTURE),
    EXPERIENCE_TRIP("experience_trip", "체험여행", PlaceCategoryVO.EXPERIENCE),
    AQUARIUM("aquarium", "아쿠아리움", PlaceCategoryVO.EXPERIENCE),
    POTTERY("pottery", "도자기", PlaceCategoryVO.EXPERIENCE),
    NEEDLEWORK("needlework", "수예·자수", PlaceCategoryVO.EXPERIENCE),
    THEME_PARK("theme_park", "테마파크", PlaceCategoryVO.EXPERIENCE),
    ZOO("zoo", "동물원", PlaceCategoryVO.EXPERIENCE),
    PARK("park", "공원", PlaceCategoryVO.NATURE),
    MOUNTAIN("mountain", "산", PlaceCategoryVO.NATURE),
    VALLEY("valley", "계곡", PlaceCategoryVO.NATURE),
    HIKING_TRAIL("hiking_trail", "등산로", PlaceCategoryVO.NATURE),
    FOREST("forest", "숲", PlaceCategoryVO.NATURE),
    ISLAND("island", "섬", PlaceCategoryVO.NATURE),
    LAKE("lake", "호수", PlaceCategoryVO.NATURE),
    STREAM("stream", "하천", PlaceCategoryVO.NATURE),
    WATERFALL("waterfall", "폭포", PlaceCategoryVO.NATURE),
    BOTANICAL_GARDEN("botanical_garden", "수목원·식물원", PlaceCategoryVO.NATURE),
    WALKING_TRAIL("walking_trail", "도보여행", PlaceCategoryVO.NATURE),
    ECOLOGY_RESERVE("ecology_reserve", "생태보존·서식지", PlaceCategoryVO.NATURE),
    CAMPING("camping", "야영·캠핑장", PlaceCategoryVO.NATURE),
    TOURIST_ATTRACTION("tourist_attraction", "관광·명소", PlaceCategoryVO.LANDMARK),
    BUILDING("building", "빌딩", PlaceCategoryVO.LANDMARK),
    BUDDHIST_TEMPLE("buddhist_temple", "불교", PlaceCategoryVO.LANDMARK),
    BRIDGE("bridge", "교량·다리", PlaceCategoryVO.LANDMARK),
    LEISURE_FACILITY("leisure_facility", "여가시설", PlaceCategoryVO.ENTERTAINMENT),
    SPORTS_LEISURE("sports_leisure", "스포츠·레저", PlaceCategoryVO.ENTERTAINMENT),
    BATHHOUSE_SAUNA("bathhouse_sauna", "목욕탕·사우나", PlaceCategoryVO.WELLNESS),
    YOGA_PILATES("yoga_pilates", "요가·필라테스", PlaceCategoryVO.WELLNESS),
    HOT_SPRING("hot_spring", "온천", PlaceCategoryVO.WELLNESS),
    ;

    companion object {
        /** 선택 응답 필드. 누락·빈 값·알 수 없는 값은 상위 카테고리 아이콘으로 폴백한다. */
        fun fromCode(code: String?): PlaceDetailCategoryVO? =
            entries.firstOrNull { it.code.equals(code?.trim(), ignoreCase = true) }
    }
}
