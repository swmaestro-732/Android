package com.chillsam.courmy.common.entity.category

/** 확정된 장소 상세 아이콘 73종. 상위 장소 카테고리와 코스 테마를 대체하지 않는다. */
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

    // 신규 25종 (2026-11-07). code 는 제안값이며 서버 detailCategory 계약 확정 대상이다.
    STATIONERY("stationery", "문구·사무용품", PlaceCategoryVO.SHOPPING),
    TOY("toy", "장난감·완구", PlaceCategoryVO.SHOPPING),
    ART_CRAFT("art_craft", "미술·공예", PlaceCategoryVO.SHOPPING),
    RECORD_STORE("record_store", "음반·레코드샵", PlaceCategoryVO.SHOPPING),
    FLORIST("florist", "꽃집·꽃배달", PlaceCategoryVO.SHOPPING),
    FURNITURE("furniture", "가구판매", PlaceCategoryVO.SHOPPING),
    HOBBY_SUPPLIES("hobby_supplies", "취미용품점", PlaceCategoryVO.SHOPPING),
    DRUGSTORE("drugstore", "드럭스토어", PlaceCategoryVO.SHOPPING),
    ELECTRONICS("electronics", "전자제품판매", PlaceCategoryVO.SHOPPING),
    FOOD_STORE("food_store", "식품판매", PlaceCategoryVO.SHOPPING),
    SHOPPING_ARCADE("shopping_arcade", "상가·아케이드", PlaceCategoryVO.SHOPPING),
    LEARNING_CENTER("learning_center", "체험학습장", PlaceCategoryVO.EXPERIENCE),
    FARM("farm", "농장·목장", PlaceCategoryVO.EXPERIENCE),
    PARK_FACILITY("park_facility", "공원시설물", PlaceCategoryVO.NATURE),
    TOURIST_FACILITY("tourist_facility", "관광지부속시설", PlaceCategoryVO.LANDMARK),
    HARBOR("harbor", "항구·포구", PlaceCategoryVO.LANDMARK),
    PIER("pier", "선착장·항만시설", PlaceCategoryVO.LANDMARK),
    OVERPASS("overpass", "육교", PlaceCategoryVO.LANDMARK),
    PROTESTANT_CHURCH("protestant_church", "기독교", PlaceCategoryVO.LANDMARK),
    CATHOLIC_CHURCH("catholic_church", "천주교", PlaceCategoryVO.LANDMARK),
    PHOTO_STUDIO("photo_studio", "사진관·포토스튜디오", PlaceCategoryVO.ENTERTAINMENT),
    ACCOMMODATION("accommodation", "숙박", PlaceCategoryVO.WELLNESS),
    ACCOMMODATION_FACILITY("accommodation_facility", "숙박부속시설", PlaceCategoryVO.WELLNESS),
    BEAUTY_SALON("beauty_salon", "미용", PlaceCategoryVO.WELLNESS),
    SPACE_RENTAL("space_rental", "공간대여", PlaceCategoryVO.WELLNESS),
    ;

    companion object {
        /** 선택 응답 필드. 누락·빈 값·알 수 없는 값은 상위 카테고리 아이콘으로 폴백한다. */
        fun fromCode(code: String?): PlaceDetailCategoryVO? =
            entries.firstOrNull { it.code.equals(code?.trim(), ignoreCase = true) }

        /**
         * 서버 `categoryDetail` 값 → VO. 서버는 한글 라벨로 내려준다(예: "카페", "제과·베이커리").
         * 코드/enum 이름도 함께 허용하며, 한글 라벨은 구분자(`,`·`·`)와 공백 차이를 무시하고 비교한다.
         * 누락·빈 값·매칭 실패는 null(상위 카테고리 폴백).
         */
        fun fromServerValue(value: String?): PlaceDetailCategoryVO? {
            val raw = value?.trim().orEmpty()
            if (raw.isEmpty()) return null
            val normalized = raw.normalizeLabel()
            // 1) 코드/enum 이름 → 2) 전체 라벨("제과,베이커리"→"제과·베이커리")
            // → 3) 복합 라벨이 조각으로 쪼개져 올 때 대비한 구성 조각("베이커리"·"사우나") 일치.
            //    조각 매칭은 전체 일치 실패 시에만 동작하고, 조각 문자열은 복합 라벨 간 중복이 없다.
            return fromCode(raw)
                ?: entries.firstOrNull { it.label.normalizeLabel() == normalized }
                ?: entries.firstOrNull { entry -> entry.label.split("·").any { it.normalizeLabel() == normalized } }
        }

        private fun String.normalizeLabel(): String = trim().replace(",", "·").replace(" ", "")
    }
}
