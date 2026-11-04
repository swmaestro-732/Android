package com.chillsam.courmy.main.presentation.my

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.color.DesignSystemSemanticColors
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.area.AreaCoordinateVO
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaMapVO
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.tan

/** 도로·장소 타일 없이 실제 행정구역 경계만 그린다. 지도 SDK/위치 권한/네트워크를 사용하지 않는다. */
@Composable
internal fun RegionMapCanvas(
    state: RegionMapUiState,
    onIntent: (RegionMapIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = DesignSystemThemeImpl.designSystemColor
    val shapes = remember(state.regions) { state.regions.mapNotNull { it.mainMapRegion() }.map(::RegionShape) }
    val provinceShapes = remember(shapes) { shapes.filter { it.region.area.level == AreaLevel.SIDO } }
    val focused = shapes.firstOrNull { it.region.area.code == state.provinceCode }
    val backgroundShapes = focused?.let(::listOf) ?: provinceShapes
    val visible =
        remember(shapes, state.provinceCode) {
            val codes = state.visibleRegions.map { it.area.code }.toSet()
            shapes.filter { it.region.area.code in codes }.ifEmpty { listOfNotNull(focused) }
        }
    val bounds =
        remember(provinceShapes, focused) {
            focused?.bounds
                ?: provinceShapes.map { it.bounds }.combinedBounds()
        }
    var zoom by remember(state.provinceCode) { mutableFloatStateOf(1f) }
    var pan by remember(state.provinceCode) { mutableStateOf(Offset.Zero) }
    var hasGesture by remember(state.provinceCode) { mutableStateOf(false) }
    val density = LocalDensity.current
    val padding = with(density) { 24.dp.toPx() }
    val stroke = with(density) { 1.dp.toPx() }
    BoxWithConstraints(modifier.fillMaxSize().clipToBounds().background(colors.bgDefaultLevel0)) {
        val width = with(density) { maxWidth.toPx() }
        val height = with(density) { maxHeight.toPx() }
        val baseScale =
            minOf(
                (width - padding * 2) / bounds.width,
                (height - padding * 2) / bounds.height,
            ).coerceAtLeast(1f)
        val cameraAnimation = tween<Float>(if (hasGesture) 0 else 400)
        val scale by animateFloatAsState(baseScale * zoom, cameraAnimation, label = "regionScale")
        val x by animateFloatAsState(
            width / 2 - bounds.center.x * baseScale * zoom + pan.x,
            cameraAnimation,
            label = "regionX",
        )
        val y by animateFloatAsState(
            height / 2 - bounds.center.y * baseScale * zoom + pan.y,
            cameraAnimation,
            label = "regionY",
        )
        val translation = Offset(x, y)
        val tap by rememberUpdatedState<(Offset) -> Unit> { screenPoint ->
            val hit = visible.lastOrNull { it.contains((screenPoint - translation) / scale) }
            hit?.let {
                onIntent(
                    if (state.provinceCode ==
                        null
                    ) {
                        RegionMapIntent.Browse(it.region.area.code)
                    } else {
                        RegionMapIntent.Toggle(it.region.area.code)
                    },
                )
            }
        }
        Canvas(
            Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription =
                        "${focused?.region?.area?.shortName ?: "대한민국"} 행정구역 지도. 아래 지역 목록에서도 선택할 수 있습니다."
                }.pointerInput(Unit) { detectTapGestures { tap(it) } }
                .pointerInput(state.provinceCode, width, height) {
                    detectTransformGestures { _, delta, zoomChange, _ ->
                        hasGesture = true
                        zoom = (zoom * zoomChange).coerceIn(1f, 6f)
                        val maxX = ((bounds.width * baseScale * zoom - width) / 2 + padding).coerceAtLeast(0f)
                        val maxY = ((bounds.height * baseScale * zoom - height) / 2 + padding).coerceAtLeast(0f)
                        pan = Offset((pan.x + delta.x).coerceIn(-maxX, maxX), (pan.y + delta.y).coerceIn(-maxY, maxY))
                    }
                },
        ) {
            withTransform({
                translate(x, y)
                scale(scale, scale, Offset.Zero)
            }) {
                backgroundShapes.forEach { shape ->
                    drawPath(shape.path, state.regionFill(shape.region.area.code, colors))
                    drawPath(shape.path, colors.borderAccent, style = Stroke(stroke / scale))
                }
                visible.forEach { shape ->
                    drawPath(shape.path, state.regionFill(shape.region.area.code, colors))
                    drawPath(
                        shape.path,
                        colors.borderAccent,
                        style =
                            Stroke(stroke / scale),
                    )
                }
            }
        }
    }
}

private fun RegionMapUiState.regionFill(
    code: String,
    colors: DesignSystemSemanticColors,
): Color =
    when {
        selected.any { code.startsWith(it.code) } -> lerp(colors.bgAccentSubtle, colors.bgAccent, 0.2f)
        selected.any { it.code.startsWith(code) } -> colors.bgAccentSubtle
        else -> colors.bgDefaultLevel1
    }

internal class RegionShape(
    val region: AreaMapVO,
) {
    val rings =
        region.polygons.map { polygon ->
            (listOf(polygon.outer) + polygon.holes).map { ring -> ring.map { it.project() } }
        }
    val path =
        Path().apply {
            fillType = PathFillType.EvenOdd
            rings.flatten().forEach { ring ->
                moveTo(ring.first().x, ring.first().y)
                ring.drop(1).forEach { lineTo(it.x, it.y) }
                close()
            }
        }
    val bounds = path.getBounds()

    fun contains(point: Offset): Boolean =
        bounds.contains(point) &&
            rings.any { polygon ->
                pointInRing(point, polygon.first()) && polygon.drop(1).none { pointInRing(point, it) }
            }
}

private fun List<Rect>.combinedBounds(): Rect =
    if (isEmpty()) {
        Rect(0f, 0f, 1f, 1f)
    } else {
        Rect(
            minOf { it.left },
            minOf { it.top },
            maxOf { it.right },
            maxOf { it.bottom },
        )
    }

private fun AreaCoordinateVO.project(): Offset =
    Offset(
        longitude.toFloat(),
        (-ln(tan(PI / 4 + latitude * PI / 360)) * 180 / PI).toFloat(),
    )

internal fun pointInRing(
    point: Offset,
    ring: List<Offset>,
): Boolean {
    var inside = false
    var previous = ring.last()
    for (current in ring) {
        if ((current.y > point.y) != (previous.y > point.y) &&
            point.x < (previous.x - current.x) * (point.y - current.y) / (previous.y - current.y) + current.x
        ) {
            inside = !inside
        }
        previous = current
    }
    return inside
}
