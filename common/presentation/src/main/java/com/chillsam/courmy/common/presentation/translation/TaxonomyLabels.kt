package com.chillsam.courmy.common.presentation.translation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.entity.category.PlaceCategoryVO
import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import com.chillsam.courmy.common.presentation.R

/*
 * taxonomy(코스 태그·장소 카테고리·장소 상세 카테고리) 한글 라벨을 현재 locale 라벨로 바꾸는 표시 전용 리졸버.
 *
 * - data 레이어에서는 사용하지 않는다. 서버 요청/응답·DTO↔VO 변환은 enum 코드/이름을 그대로 쓰고,
 *   여기서는 오직 화면에 그릴 때만 한글 라벨 → locale 라벨로 치환한다.
 * - 역매핑 테이블(LABEL_TO_RES_ID)은 세 enum 의 한글 label 을 키로, 대응 R.string resId 를 값으로 갖는다.
 *   같은 한글 label 이 여러 enum 에 겹치면 먼저 등록된 하나(코스 태그 > 장소 카테고리 > 상세 카테고리)를 쓴다.
 * - 런타임 getIdentifier 는 쓰지 않는다(R8 리소스 수축·오타 안전). resId 는 컴파일 타임 상수로 직접 매핑한다.
 */

/** 코스 태그 한글 label → `R.string.taxonomy_tag_*` resId. */
private val COURSE_TAG_RES_ID: Map<String, Int> =
    mapOf(
        CourseTagVO.DATE.label to R.string.taxonomy_tag_date,
        CourseTagVO.HEALING.label to R.string.taxonomy_tag_healing,
        CourseTagVO.FOOD.label to R.string.taxonomy_tag_food,
        CourseTagVO.CAFETOUR.label to R.string.taxonomy_tag_cafetour,
        CourseTagVO.CULTURE.label to R.string.taxonomy_tag_culture,
        CourseTagVO.NATURE.label to R.string.taxonomy_tag_nature,
        CourseTagVO.NIGHTVIEW.label to R.string.taxonomy_tag_nightview,
        CourseTagVO.SHOPPING.label to R.string.taxonomy_tag_shopping,
        CourseTagVO.TRADITION.label to R.string.taxonomy_tag_tradition,
        CourseTagVO.ACTIVITY.label to R.string.taxonomy_tag_activity,
        CourseTagVO.FAMILY.label to R.string.taxonomy_tag_family,
        CourseTagVO.SOLO.label to R.string.taxonomy_tag_solo,
    )

/** 장소 카테고리 한글 label → `R.string.taxonomy_cat_*` resId. */
private val PLACE_CATEGORY_RES_ID: Map<String, Int> =
    mapOf(
        PlaceCategoryVO.CAFE.label to R.string.taxonomy_cat_cafe,
        PlaceCategoryVO.RESTAURANT.label to R.string.taxonomy_cat_restaurant,
        PlaceCategoryVO.BAR.label to R.string.taxonomy_cat_bar,
        PlaceCategoryVO.SHOPPING.label to R.string.taxonomy_cat_shopping,
        PlaceCategoryVO.CULTURE.label to R.string.taxonomy_cat_culture,
        PlaceCategoryVO.EXPERIENCE.label to R.string.taxonomy_cat_experience,
        PlaceCategoryVO.NATURE.label to R.string.taxonomy_cat_nature,
        PlaceCategoryVO.LANDMARK.label to R.string.taxonomy_cat_landmark,
        PlaceCategoryVO.ENTERTAINMENT.label to R.string.taxonomy_cat_entertainment,
        PlaceCategoryVO.WELLNESS.label to R.string.taxonomy_cat_wellness,
        PlaceCategoryVO.ETC.label to R.string.taxonomy_cat_etc,
    )

/** 장소 상세 카테고리 한글 label → `R.string.taxonomy_detail_*` resId. */
private val PLACE_DETAIL_RES_ID: Map<String, Int> =
    mapOf(
        PlaceDetailCategoryVO.CAFE.label to R.string.taxonomy_detail_cafe,
        PlaceDetailCategoryVO.BAKERY.label to R.string.taxonomy_detail_bakery,
        PlaceDetailCategoryVO.RESTAURANT.label to R.string.taxonomy_detail_restaurant,
        PlaceDetailCategoryVO.PUB.label to R.string.taxonomy_detail_pub,
        PlaceDetailCategoryVO.DEPARTMENT_STORE.label to R.string.taxonomy_detail_department_store,
        PlaceDetailCategoryVO.SHOPPING_MALL.label to R.string.taxonomy_detail_shopping_mall,
        PlaceDetailCategoryVO.OUTLET_STORE.label to R.string.taxonomy_detail_outlet_store,
        PlaceDetailCategoryVO.FASHION.label to R.string.taxonomy_detail_fashion,
        PlaceDetailCategoryVO.MARKET.label to R.string.taxonomy_detail_market,
        PlaceDetailCategoryVO.HOUSEHOLD_GOODS.label to R.string.taxonomy_detail_household_goods,
        PlaceDetailCategoryVO.CONVENIENCE_STORE.label to R.string.taxonomy_detail_convenience_store,
        PlaceDetailCategoryVO.SUPERMARKET.label to R.string.taxonomy_detail_supermarket,
        PlaceDetailCategoryVO.SOUVENIR.label to R.string.taxonomy_detail_souvenir,
        PlaceDetailCategoryVO.POPUP_STORE.label to R.string.taxonomy_detail_popup_store,
        PlaceDetailCategoryVO.CULTURE_FACILITY.label to R.string.taxonomy_detail_culture_facility,
        PlaceDetailCategoryVO.BOOKSTORE.label to R.string.taxonomy_detail_bookstore,
        PlaceDetailCategoryVO.GALLERY.label to R.string.taxonomy_detail_gallery,
        PlaceDetailCategoryVO.PERFORMANCE.label to R.string.taxonomy_detail_performance,
        PlaceDetailCategoryVO.FESTIVAL.label to R.string.taxonomy_detail_festival,
        PlaceDetailCategoryVO.LIBRARY.label to R.string.taxonomy_detail_library,
        PlaceDetailCategoryVO.EXPERIENCE_TRIP.label to R.string.taxonomy_detail_experience_trip,
        PlaceDetailCategoryVO.AQUARIUM.label to R.string.taxonomy_detail_aquarium,
        PlaceDetailCategoryVO.POTTERY.label to R.string.taxonomy_detail_pottery,
        PlaceDetailCategoryVO.NEEDLEWORK.label to R.string.taxonomy_detail_needlework,
        PlaceDetailCategoryVO.THEME_PARK.label to R.string.taxonomy_detail_theme_park,
        PlaceDetailCategoryVO.ZOO.label to R.string.taxonomy_detail_zoo,
        PlaceDetailCategoryVO.PARK.label to R.string.taxonomy_detail_park,
        PlaceDetailCategoryVO.MOUNTAIN.label to R.string.taxonomy_detail_mountain,
        PlaceDetailCategoryVO.VALLEY.label to R.string.taxonomy_detail_valley,
        PlaceDetailCategoryVO.HIKING_TRAIL.label to R.string.taxonomy_detail_hiking_trail,
        PlaceDetailCategoryVO.FOREST.label to R.string.taxonomy_detail_forest,
        PlaceDetailCategoryVO.ISLAND.label to R.string.taxonomy_detail_island,
        PlaceDetailCategoryVO.LAKE.label to R.string.taxonomy_detail_lake,
        PlaceDetailCategoryVO.STREAM.label to R.string.taxonomy_detail_stream,
        PlaceDetailCategoryVO.WATERFALL.label to R.string.taxonomy_detail_waterfall,
        PlaceDetailCategoryVO.BOTANICAL_GARDEN.label to R.string.taxonomy_detail_botanical_garden,
        PlaceDetailCategoryVO.WALKING_TRAIL.label to R.string.taxonomy_detail_walking_trail,
        PlaceDetailCategoryVO.ECOLOGY_RESERVE.label to R.string.taxonomy_detail_ecology_reserve,
        PlaceDetailCategoryVO.CAMPING.label to R.string.taxonomy_detail_camping,
        PlaceDetailCategoryVO.TOURIST_ATTRACTION.label to R.string.taxonomy_detail_tourist_attraction,
        PlaceDetailCategoryVO.BUILDING.label to R.string.taxonomy_detail_building,
        PlaceDetailCategoryVO.BUDDHIST_TEMPLE.label to R.string.taxonomy_detail_buddhist_temple,
        PlaceDetailCategoryVO.BRIDGE.label to R.string.taxonomy_detail_bridge,
        PlaceDetailCategoryVO.LEISURE_FACILITY.label to R.string.taxonomy_detail_leisure_facility,
        PlaceDetailCategoryVO.SPORTS_LEISURE.label to R.string.taxonomy_detail_sports_leisure,
        PlaceDetailCategoryVO.BATHHOUSE_SAUNA.label to R.string.taxonomy_detail_bathhouse_sauna,
        PlaceDetailCategoryVO.YOGA_PILATES.label to R.string.taxonomy_detail_yoga_pilates,
        PlaceDetailCategoryVO.HOT_SPRING.label to R.string.taxonomy_detail_hot_spring,
        PlaceDetailCategoryVO.STATIONERY.label to R.string.taxonomy_detail_stationery,
        PlaceDetailCategoryVO.TOY.label to R.string.taxonomy_detail_toy,
        PlaceDetailCategoryVO.ART_CRAFT.label to R.string.taxonomy_detail_art_craft,
        PlaceDetailCategoryVO.RECORD_STORE.label to R.string.taxonomy_detail_record_store,
        PlaceDetailCategoryVO.FLORIST.label to R.string.taxonomy_detail_florist,
        PlaceDetailCategoryVO.FURNITURE.label to R.string.taxonomy_detail_furniture,
        PlaceDetailCategoryVO.HOBBY_SUPPLIES.label to R.string.taxonomy_detail_hobby_supplies,
        PlaceDetailCategoryVO.DRUGSTORE.label to R.string.taxonomy_detail_drugstore,
        PlaceDetailCategoryVO.ELECTRONICS.label to R.string.taxonomy_detail_electronics,
        PlaceDetailCategoryVO.FOOD_STORE.label to R.string.taxonomy_detail_food_store,
        PlaceDetailCategoryVO.SHOPPING_ARCADE.label to R.string.taxonomy_detail_shopping_arcade,
        PlaceDetailCategoryVO.LEARNING_CENTER.label to R.string.taxonomy_detail_learning_center,
        PlaceDetailCategoryVO.FARM.label to R.string.taxonomy_detail_farm,
        PlaceDetailCategoryVO.PARK_FACILITY.label to R.string.taxonomy_detail_park_facility,
        PlaceDetailCategoryVO.TOURIST_FACILITY.label to R.string.taxonomy_detail_tourist_facility,
        PlaceDetailCategoryVO.HARBOR.label to R.string.taxonomy_detail_harbor,
        PlaceDetailCategoryVO.PIER.label to R.string.taxonomy_detail_pier,
        PlaceDetailCategoryVO.OVERPASS.label to R.string.taxonomy_detail_overpass,
        PlaceDetailCategoryVO.PROTESTANT_CHURCH.label to R.string.taxonomy_detail_protestant_church,
        PlaceDetailCategoryVO.CATHOLIC_CHURCH.label to R.string.taxonomy_detail_catholic_church,
        PlaceDetailCategoryVO.PHOTO_STUDIO.label to R.string.taxonomy_detail_photo_studio,
        PlaceDetailCategoryVO.ACCOMMODATION.label to R.string.taxonomy_detail_accommodation,
        PlaceDetailCategoryVO.ACCOMMODATION_FACILITY.label to R.string.taxonomy_detail_accommodation_facility,
        PlaceDetailCategoryVO.BEAUTY_SALON.label to R.string.taxonomy_detail_beauty_salon,
        PlaceDetailCategoryVO.SPACE_RENTAL.label to R.string.taxonomy_detail_space_rental,
    )

/**
 * 한글 label → resId 통합 역매핑. 겹치는 label 은 코스 태그 > 장소 카테고리 > 상세 카테고리 순으로
 * 먼저 등록된 하나만 남긴다(뒤 항목은 무시).
 */
private val LABEL_TO_RES_ID: Map<String, Int> =
    buildMap {
        putAll(PLACE_DETAIL_RES_ID)
        putAll(PLACE_CATEGORY_RES_ID)
        putAll(COURSE_TAG_RES_ID)
    }

// 긴 라벨을 먼저 매칭해 문화·전시 같은 단일 카테고리를 쪼개지 않는다.
private val TAXONOMY_LABEL_PATTERN =
    Regex(
        "(?<=^|·)\\s*(" +
            LABEL_TO_RES_ID.keys.sortedByDescending { it.length }.joinToString("|") { Regex.escape(it) } +
            ")(?=\\s*(?:·|$))",
    )

/** 알려진 라벨과 원문 구분자를 분리한다. 미등록 라벨·공백은 그대로 보존한다. */
internal fun taxonomyLabelParts(label: String): List<String> =
    buildList {
        var start = 0
        for (match in TAXONOMY_LABEL_PATTERN.findAll(label)) {
            val group = match.groups[1] ?: continue
            add(label.substring(start, group.range.first))
            add(group.value)
            start = group.range.last + 1
        }
        add(label.substring(start))
    }

/** 표시 전용. 카테고리 자체의 가운뎃점과 여러 라벨 사이의 구분자를 구별한다. */
@Composable
fun rememberTaxonomyLabel(label: String): String {
    val builder = StringBuilder()
    for (part in taxonomyLabelParts(label)) {
        builder.append(localizeToken(part))
    }
    return builder.toString()
}

/** 코스 태그 enum 을 직접 로컬라이즈한다(테마 타일처럼 [CourseTagVO] 를 순회하는 자리). 표시 전용. */
@Composable
fun courseTagLabel(tag: CourseTagVO): String {
    val resId = COURSE_TAG_RES_ID[tag.label]
    return if (resId != null) stringResource(resId) else tag.label
}

/** 단일 토막을 역매핑. resId 가 있으면 locale 문자열, 없으면 원문 그대로. */
@Composable
private fun localizeToken(token: String): String {
    val resId = LABEL_TO_RES_ID[token]
    return if (resId != null) stringResource(resId) else token
}
