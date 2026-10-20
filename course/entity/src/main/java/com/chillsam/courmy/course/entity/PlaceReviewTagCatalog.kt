package com.chillsam.courmy.course.entity

import com.chillsam.courmy.common.entity.category.PlaceCategoryVO

/** 장소 후기 API의 소문자 코드와 사용자 확정 문구. 공통 항목은 모든 업종에 표시한다. */
enum class PlaceReviewTagCatalog(
    val code: String,
    val label: String,
    val icon: String,
    val category: PlaceCategoryVO? = null,
) {
    FRIENDLY("friendly", "사장님이 친절해요", "😊"),
    BROWSING("browsing", "편하게 둘러볼 수 있어요", "👁️"),
    HELPFUL("helpful", "설명을 잘해줘요", "💬"),
    QUICK("quick", "응대가 빨라요", "⚡"),
    CLEAN("clean", "매장이 깨끗해요", "✨"),
    INTERIOR("interior", "인테리어가 예뻐요", "🛋️"),
    PHOTO("photo", "사진 찍기 좋아요", "📸"),
    COZY("cozy", "조용하고 아늑해요", "🔮"),
    SPACIOUS("spacious", "공간이 넓어요", "↔️"),
    FINDABLE("findable", "찾기 쉬워요", "🛣️"),
    PARKING("parking", "주차하기 편해요", "🚗"),
    NOWAIT("nowait", "웨이팅이 적어요", "⏳"),
    SOLO("solo", "혼자 가기 좋아요", "👤"),
    REVISIT("revisit", "또 오고 싶어요", "🔁"),
    COFFEE("coffee", "커피가 맛있어요", "☕", PlaceCategoryVO.CAFE),
    DESSERT("dessert", "디저트가 맛있어요", "🍰", PlaceCategoryVO.CAFE),
    SIGNATURE("signature", "시그니처 메뉴가 좋아요", "🌟", PlaceCategoryVO.CAFE),
    LINGERING("lingering", "오래 있기 좋아요", "🕰️", PlaceCategoryVO.CAFE),
    OUTLETS("outlets", "콘센트가 많아요", "🔌", PlaceCategoryVO.CAFE),
    VIEW("view", "뷰가 좋아요", "🏔️", PlaceCategoryVO.CAFE),
    TASTY("tasty", "음식이 맛있어요", "😋", PlaceCategoryVO.RESTAURANT),
    FRESH("fresh", "재료가 신선해요", "🥬", PlaceCategoryVO.RESTAURANT),
    HEARTY("hearty", "양이 푸짐해요", "🍜", PlaceCategoryVO.RESTAURANT),
    VALUE("value", "가성비가 좋아요", "👛", PlaceCategoryVO.RESTAURANT),
    VARIETY("variety", "메뉴가 다양해요", "📋", PlaceCategoryVO.RESTAURANT),
    GROUP("group", "회식하기 좋아요", "👥", PlaceCategoryVO.RESTAURANT),
    DRINKS("drinks", "술 종류가 다양해요", "🥂", PlaceCategoryVO.BAR),
    SNACKS("snacks", "안주가 맛있어요", "🍖", PlaceCategoryVO.BAR),
    MOOD("mood", "분위기가 좋아요", "🎶", PlaceCategoryVO.BAR),
    LATE("late", "늦게까지 해요", "🌙", PlaceCategoryVO.BAR),
    DATE("date", "데이트하기 좋아요", "❤️", PlaceCategoryVO.BAR),
    STYLISH("stylish", "물건이 감각적이에요", "🪄", PlaceCategoryVO.SHOPPING),
    EXCLUSIVE("exclusive", "여기서만 파는 게 있어요", "💎", PlaceCategoryVO.SHOPPING),
    DISCOVERY("discovery", "구경하는 재미가 있어요", "🔍", PlaceCategoryVO.SHOPPING),
    GIFTS("gifts", "선물 고르기 좋아요", "🎁", PlaceCategoryVO.SHOPPING),
    ARTWORK("artwork", "작품이 인상적이에요", "🖼️", PlaceCategoryVO.CULTURE),
    CURATION("curation", "큐레이션이 좋아요", "🎧", PlaceCategoryVO.CULTURE),
    VIEWING("viewing", "여유롭게 감상해요", "👁️", PlaceCategoryVO.CULTURE),
    HEALING("healing", "힐링하기 좋아요", "😌", PlaceCategoryVO.CULTURE),
    GUIDANCE("guidance", "설명이 자세해요", "💬", PlaceCategoryVO.EXPERIENCE),
    BEGINNER("beginner", "초보도 하기 좋아요", "🌱", PlaceCategoryVO.EXPERIENCE),
    RESULT("result", "결과물이 만족스러워요", "🏆", PlaceCategoryVO.EXPERIENCE),
    MATERIALS("materials", "재료가 좋아요", "📦", PlaceCategoryVO.EXPERIENCE),
    FOCUS("focus", "집중하기 좋아요", "🎯", PlaceCategoryVO.EXPERIENCE),
    SUNSET("sunset", "노을이 예뻐요", "🌇", PlaceCategoryVO.NATURE),
    AIR("air", "공기가 맑아요", "💨", PlaceCategoryVO.NATURE),
    REST("rest", "조용히 쉬기 좋아요", "🤫", PlaceCategoryVO.NATURE),
    PET("pet", "반려동물과 오기 좋아요", "🐾", PlaceCategoryVO.NATURE),
    FAMILY("family", "아이와 나들이하기 좋아요", "👶🏻", PlaceCategoryVO.NATURE),
    SERENE("serene", "고즈넉해요", "☁️", PlaceCategoryVO.LANDMARK),
    ARCHITECTURE("architecture", "건축이 멋져요", "🏢", PlaceCategoryVO.LANDMARK),
    PEACEFUL("peaceful", "한적해서 여유로워요", "🪶", PlaceCategoryVO.LANDMARK),
    EXCITING("exciting", "신나고 재밌어요", "😆", PlaceCategoryVO.ENTERTAINMENT),
    TOGETHER("together", "여럿이 즐기기 좋아요", "👥", PlaceCategoryVO.ENTERTAINMENT),
    IMMERSIVE("immersive", "시간 가는 줄 몰라요", "⏱️", PlaceCategoryVO.ENTERTAINMENT),
    RAINYDAY("rainyday", "날씨 상관없이 즐겨요", "🌧️", PlaceCategoryVO.ENTERTAINMENT),
    DIVERSE("diverse", "즐길 거리가 다양해요", "🎮", PlaceCategoryVO.ENTERTAINMENT),
    DATEFUN("datefun", "데이트하기 좋아요", "❤️", PlaceCategoryVO.ENTERTAINMENT),
    REFRESHED("refreshed", "몸이 개운해져요", "💆", PlaceCategoryVO.WELLNESS),
    CALM("calm", "마음이 편안해져요", "🧘", PlaceCategoryVO.WELLNESS),
    EXPERT("expert", "관리가 전문적이에요", "✨", PlaceCategoryVO.WELLNESS),
    HYGIENE("hygiene", "시설이 청결해요", "🛁", PlaceCategoryVO.WELLNESS),
    TRANQUIL("tranquil", "조용히 쉬기 좋아요", "🌙", PlaceCategoryVO.WELLNESS),
    METIME("metime", "나를 위한 시간이에요", "🌿", PlaceCategoryVO.WELLNESS),
    ;

    fun toTagVO(): CourseReviewTagVO = CourseReviewTagVO(code, label, icon)

    companion object {
        fun forCategories(categories: List<PlaceCategoryVO>): List<CourseReviewTagVO> =
            entries.filter { it.category == null || it.category in categories }.map { it.toTagVO() }
    }
}
