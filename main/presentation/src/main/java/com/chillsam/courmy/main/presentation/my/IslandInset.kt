package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.translation.catalogLabel
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.area.AreaMapVO
import com.chillsam.courmy.main.entity.area.AreaPolygonVO
import com.chillsam.courmy.main.presentation.R

/** 원본 좌표/지역 코드는 유지하며, 먼 도서의 표시 위치와 축척만 분리한다. */
internal enum class IslandInset(
    val title: String,
    val county: String,
) {
    BAEKRYEONG("백령·대청", "28720"),
    YEONPYEONG("연평도 일대", "28720"),
    ULLEUNG("울릉도", "47940"),
    DOKDO("독도", "47940"),
}

internal fun AreaPolygonVO.islandInset(code: String): IslandInset? {
    val west = outer.minOf { it.longitude }
    val east = outer.maxOf { it.longitude }
    return when {
        code.startsWith("28") && east < 125.0 -> IslandInset.BAEKRYEONG
        code.startsWith("28") && east < 126.0 && outer.minOf { it.latitude } > 37.5 -> IslandInset.YEONPYEONG
        code.startsWith("47") && west > 131.0 -> IslandInset.DOKDO
        code.startsWith("47") && west > 130.0 -> IslandInset.ULLEUNG
        else -> null
    }
}

internal fun AreaMapVO.mainMapRegion(): AreaMapVO? =
    copy(polygons = polygons.filter { it.islandInset(area.code) == null }).takeIf { it.polygons.isNotEmpty() }

@Composable
internal fun BoxScope.RegionMapInsets(
    state: RegionMapUiState,
    onIntent: (RegionMapIntent) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val insets =
        remember(state.regions, state.provinceCode) {
            islandShapes(state)
        }
    if (insets.isEmpty()) return
    insets.groupBy { it.first.county }.forEach { (county, group) ->
        val west = county == "28720"
        val insetDescription = group.map { catalogLabel(it.first.title) }.joinToString()
        val selected = state.selected.any { county.startsWith(it.code) }
        Column(
            Modifier
                .align(if (west) Alignment.TopStart else Alignment.TopEnd)
                .padding(start = 12.dp, end = 12.dp, top = if (west && state.provinceCode != null) 64.dp else 12.dp)
                .width(84.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DsText(
                stringResource(R.string.main_region_map_inset_separate_scale),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
            )
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.bgDefaultLevel1)
                    .border(
                        1.dp,
                        if (selected) color.contentAccent else color.borderDefaultLevel0,
                        RoundedCornerShape(8.dp),
                    ).clickable(enabled = !state.isSaving) {
                        onIntent(
                            if (state.provinceCode ==
                                null
                            ) {
                                RegionMapIntent.Browse(county.take(2))
                            } else {
                                RegionMapIntent.Toggle(county)
                            },
                        )
                    }.semantics {
                        contentDescription =
                            insetDescription + ", " +
                            group
                                .first()
                                .second.region.area.fullName
                        this.selected = selected
                    }.padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    group.forEach { (_, shape) ->
                        Canvas(Modifier.weight(1f).height(32.dp)) {
                            val scale =
                                minOf(size.width / shape.bounds.width, size.height / shape.bounds.height) * 0.85f
                            withTransform({
                                translate(
                                    size.width / 2 - shape.bounds.center.x * scale,
                                    size.height / 2 - shape.bounds.center.y * scale,
                                )
                                scale(scale, scale, Offset.Zero)
                            }) {
                                drawPath(
                                    shape.path,
                                    if (selected) {
                                        lerp(
                                            color.bgAccentSubtle,
                                            color.bgAccent,
                                            0.2f,
                                        )
                                    } else {
                                        color.bgDefaultLevel1
                                    },
                                )
                                drawPath(shape.path, color.borderAccent, style = Stroke(1.dp.toPx() / scale))
                            }
                        }
                    }
                }
                DsText(
                    catalogLabel(if (west) "백령·대청·연평" else "울릉도·독도"),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
        }
    }
}

private fun islandShapes(state: RegionMapUiState): List<Pair<IslandInset, RegionShape>> =
    IslandInset.entries.mapNotNull { inset ->
        val region = state.regions.firstOrNull { it.area.code == inset.county } ?: return@mapNotNull null
        if (state.provinceCode != null && !inset.county.startsWith(state.provinceCode)) return@mapNotNull null
        val polygons = region.polygons.filter { it.islandInset(inset.county) == inset }
        if (polygons.isEmpty()) null else inset to RegionShape(region.copy(polygons = polygons))
    }
