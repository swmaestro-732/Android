package com.chillsam.courmy.main.presentation.map.mock

import com.chillsam.courmy.main.entity.map.MapCategory
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.presentation.location.DefaultLocation

/**
 * 지도 화면(FS-10 계열)이 API 연동 전까지 쓰는 임시 데이터.
 *
 * Figma 시안의 성수동 장소를 그대로 옮겼다. 좌표는 실제 위치를 대략 맞춘 값이라 지도에 찍었을 때
 * 핀이 서로 흩어져 보인다 — 마커 겹침·클러스터 배지 같은 걸 눈으로 확인하려면 이게 필요하다.
 *
 * TODO(SCRUM-지도API): 장소 검색·주변 장소 API 가 붙으면 이 파일을 지우고 UseCase 로 교체한다.
 */
internal object MapMockData {
    /** 지도 첫 진입 시 카메라가 놓이는 지점(성수동 일대). 정본은 [DefaultLocation] 이다. */
    const val DEFAULT_LATITUDE = DefaultLocation.LATITUDE
    const val DEFAULT_LONGITUDE = DefaultLocation.LONGITUDE

    /**
     * 지도 탭에서 검색으로 넘어갈 때 쓰는 임시 검색어.
     *
     * 검색어 **입력** 화면은 이번 범위 밖이라, 검색바를 누르면 시안과 같은 결과가 바로 뜨도록
     * 고정값을 넘긴다. 입력 화면이 붙으면 이 상수는 사라진다.
     */
    const val SAMPLE_QUERY = "성수 카페"

    /** FS-10 시트 제목의 "조건에 맞는 곳 28곳". */
    const val NEARBY_TOTAL_COUNT = 28

    /** FS-10-List 시트 제목의 "검색 결과 24". */
    const val SEARCH_TOTAL_COUNT = 24

    /** FS-10 지도 탭에서 보여줄 주변 장소. */
    val nearbyPlaces: List<MapPlaceVO> =
        listOf(
            MapPlaceVO(
                id = 1L,
                name = "어니언 성수",
                category = "카페",
                subCategory = "베이커리",
                rating = 4.8,
                walkingMinutes = 6,
                latitude = 37.5445,
                longitude = 127.0559,
                saved = true,
                openNow = true,
                closingTime = "22:00",
            ),
            MapPlaceVO(
                id = 2L,
                name = "센터커피 로스터스",
                category = "카페",
                subCategory = "로스터리",
                rating = 4.6,
                walkingMinutes = 8,
                latitude = 37.5451,
                longitude = 127.0534,
                openNow = true,
                closingTime = "21:00",
            ),
            MapPlaceVO(
                id = 3L,
                name = "대림창고 카페",
                category = "카페",
                subCategory = "전시",
                rating = 4.6,
                walkingMinutes = 9,
                latitude = 37.5424,
                longitude = 127.0552,
                openNow = true,
                closingTime = "22:00",
            ),
            MapPlaceVO(
                id = 4L,
                name = "아우어베이커리 성수",
                category = "디저트",
                subCategory = "베이커리",
                rating = 4.5,
                walkingMinutes = 8,
                latitude = 37.5439,
                longitude = 127.0578,
                saved = true,
                openNow = true,
                closingTime = "20:00",
            ),
            MapPlaceVO(
                id = 5L,
                name = "성수동 소금집",
                category = "맛집",
                subCategory = "델리",
                rating = 4.4,
                walkingMinutes = 11,
                latitude = 37.5462,
                longitude = 127.0571,
                openNow = true,
                closingTime = "22:00",
            ),
            MapPlaceVO(
                id = 6L,
                name = "언더스탠드에비뉴",
                category = "편집숍",
                rating = 4.2,
                walkingMinutes = 14,
                latitude = 37.5416,
                longitude = 127.0525,
                openNow = false,
                closingTime = "19:00",
            ),
            MapPlaceVO(
                id = 7L,
                name = "에스팩토리 갤러리",
                category = "전시·갤러리",
                rating = 4.3,
                walkingMinutes = 12,
                latitude = 37.5432,
                longitude = 127.0596,
                openNow = true,
                closingTime = "18:00",
            ),
            MapPlaceVO(
                id = 8L,
                name = "성수 바 웨이브",
                category = "술집·바",
                rating = 4.5,
                walkingMinutes = 10,
                latitude = 37.5471,
                longitude = 127.0548,
                openNow = false,
                closingTime = "02:00",
            ),
        )

    /** FS-10-ResultMap / FS-10-List 의 "성수 카페" 검색 결과. 순서가 곧 리스트의 순위다. */
    val searchResultPlaces: List<MapPlaceVO> =
        listOf(
            MapPlaceVO(
                id = 1L,
                name = "어니언 성수",
                category = "카페",
                subCategory = "베이커리",
                rating = 4.8,
                walkingMinutes = 6,
                latitude = 37.5445,
                longitude = 127.0559,
                saved = true,
                openNow = true,
                closingTime = "22:00",
            ),
            MapPlaceVO(
                id = 3L,
                name = "대림창고 카페",
                category = "카페",
                subCategory = "전시",
                rating = 4.6,
                walkingMinutes = 9,
                latitude = 37.5424,
                longitude = 127.0552,
                openNow = true,
                closingTime = "22:00",
            ),
            MapPlaceVO(
                id = 9L,
                name = "센터커피 성수",
                category = "카페",
                subCategory = "로스터리",
                rating = 4.7,
                walkingMinutes = 12,
                latitude = 37.5451,
                longitude = 127.0534,
                openNow = true,
                closingTime = "21:00",
            ),
            MapPlaceVO(
                id = 4L,
                name = "아우어베이커리 성수",
                category = "카페",
                subCategory = "디저트",
                rating = 4.5,
                walkingMinutes = 8,
                latitude = 37.5439,
                longitude = 127.0578,
                saved = true,
                openNow = true,
                closingTime = "20:00",
            ),
        )

    /** FS-10 의 `+9` 배지. 화면 오른쪽 위에 뭉쳐 있는 장소 묶음. */
    val nearbyClusters: List<MapClusterVO> =
        listOf(MapClusterVO(latitude = 37.5479, longitude = 127.0592, count = 9))

    /** FS-10-ResultMap 의 `+13` 배지. */
    val searchClusters: List<MapClusterVO> =
        listOf(MapClusterVO(latitude = 37.5476, longitude = 127.0575, count = 13))

    /** FS-10-ResultMap 상단의 세분화 칩. 검색어에 딸린 값이라 고정 카테고리와 별개다. */
    val searchRefinements: List<String> =
        listOf("카페", "베이커리", "브런치", "로스터리", "디저트")

    /**
     * 고정 카테고리 필터를 mock 목록에 적용한다.
     * 실제 구현에서는 서버가 걸러 주므로 이 함수도 함께 사라진다.
     */
    fun filterByCategory(
        places: List<MapPlaceVO>,
        category: MapCategory,
    ): List<MapPlaceVO> =
        if (category == MapCategory.ALL) {
            places
        } else {
            places.filter { it.category == category.mockLabel() }
        }

    private fun MapCategory.mockLabel(): String =
        when (this) {
            MapCategory.ALL -> ""
            MapCategory.CAFE -> "카페"
            MapCategory.RESTAURANT -> "맛집"
            MapCategory.BAR -> "술집·바"
            MapCategory.GALLERY -> "전시·갤러리"
            MapCategory.SELECT_SHOP -> "편집숍"
            MapCategory.DESSERT -> "디저트"
        }
}
