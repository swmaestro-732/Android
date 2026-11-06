package com.chillsam.courmy.main.data.area

import android.content.Context
import com.chillsam.courmy.main.domain.area.AreaMapRepository
import com.chillsam.courmy.main.entity.area.AreaCoordinateVO
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaMapVO
import com.chillsam.courmy.main.entity.area.AreaPolygonVO
import com.chillsam.courmy.main.entity.area.AreaVO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AreaMapRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : AreaMapRepository {
        override suspend fun getRegions(): List<AreaMapVO> =
            withContext(Dispatchers.IO) {
                val source =
                    context.assets
                        .open("area_map.json")
                        .bufferedReader()
                        .use { it.readText() }
                Json.decodeFromString<List<AreaMapDTO>>(source).map { it.toVO() }
            }
    }

@Serializable
internal data class AreaMapDTO(
    val code: String,
    val name: String,
    val fullName: String,
    val level: String,
    val polygons: List<AreaPolygonDTO>,
) {
    fun toVO(): AreaMapVO {
        val areaLevel = AreaLevel.valueOf(level)
        require(areaLevel == AreaLevel.SIDO || areaLevel == AreaLevel.SIGUNGU)
        require(code.length == if (areaLevel == AreaLevel.SIDO) 2 else 5)
        require(code.all(Char::isDigit) && name.isNotBlank() && polygons.isNotEmpty())
        return AreaMapVO(AreaVO(code, name, fullName, areaLevel), polygons.map { it.toVO() })
    }
}

@Serializable
internal data class AreaPolygonDTO(
    val outer: List<List<Double>>,
    val holes: List<List<List<Double>>> = emptyList(),
) {
    fun toVO() = AreaPolygonVO(outer.toCoordinates(), holes.map { it.toCoordinates() })

    private fun List<List<Double>>.toCoordinates(): List<AreaCoordinateVO> {
        require(size >= 4)
        return map { point ->
            require(point.size == 2 && point[0] in -90.0..90.0 && point[1] in -180.0..180.0)
            AreaCoordinateVO(point[0], point[1])
        }
    }
}
