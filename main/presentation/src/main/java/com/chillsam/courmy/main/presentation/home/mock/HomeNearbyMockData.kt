package com.chillsam.courmy.main.presentation.home.mock

import com.chillsam.courmy.main.entity.map.MapPlaceVO

/**
 * 홈(FS-09) "근처 저장 장소" 섹션이 API 연동 전까지 쓰는 임시 데이터. 값은 시안 그대로다.
 *
 * 장소는 지도 탭·검색과 같은 [MapPlaceVO] 를 쓴다 — 홈의 근처 장소 줄이 보여주는 값
 * (이름·평점·카테고리·도보 시간)이 지도 시트의 장소 줄과 똑같아서, 같은 것을 두 벌로 두지 않는다
 * (SearchMockData 와 같은 선택).
 *
 * TODO(SCRUM-근처저장API): 근처 저장 장소 API 가 붙으면 이 파일을 지우고 UseCase 로 교체한다.
 */
internal object HomeNearbyMockData {
    /** 헤더 보조줄의 위치·날씨. 위치·날씨 연동 전까지 시안 고정값을 쓴다. */
    const val LOCATION_WEATHER = "성수동 · 흐림 18°"

    /** 근처에 저장해 둔 장소. 방문 여부 API 가 없어 화면에서는 모두 "미방문"으로 표기한다. */
    val places: List<MapPlaceVO> =
        listOf(
            MapPlaceVO(
                id = 1L,
                name = "어니언 성수",
                category = "카페",
                subCategory = "베이커리",
                rating = 4.8,
                walkingMinutes = 6,
                saved = true,
            ),
            MapPlaceVO(
                id = 2L,
                name = "대림창고 카페",
                category = "카페",
                subCategory = "전시",
                rating = 4.6,
                walkingMinutes = 9,
                saved = true,
            ),
            MapPlaceVO(
                id = 3L,
                name = "포인트오브뷰 성수",
                category = "소품샵",
                subCategory = "문구",
                rating = 4.7,
                walkingMinutes = 11,
                saved = true,
            ),
        )
}
