package com.chillsam.courmy.main.entity.area

/** 서버 법정동 코드와 연결된 지도 표시용 행정구역. 경계는 앱에 포함된 공개 데이터다. */
data class AreaMapVO(
    val area: AreaVO,
    val polygons: List<AreaPolygonVO>,
)

data class AreaPolygonVO(
    val outer: List<AreaCoordinateVO>,
    val holes: List<List<AreaCoordinateVO>> = emptyList(),
)

data class AreaCoordinateVO(
    val latitude: Double,
    val longitude: Double,
)
