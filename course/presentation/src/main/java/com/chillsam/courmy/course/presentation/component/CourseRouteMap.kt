package com.chillsam.courmy.course.presentation.component

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.NaverMapComposable
import com.naver.maps.map.compose.PolylineOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.naver.maps.map.overlay.OverlayImage

// 코스 경로 지도. 코스 상세의 "코스 한눈에 보기" 구역과 장소 상세 시트가 같이 쓴다.

/** 장소 1곳을 볼 때의 확대 수준. 코스 전체 보기보다 가깝게 당긴다. */
internal const val PLACE_MAP_ZOOM = 16.0

/** 순번 마커 지름(dp). 그릴 때 화면 밀도를 곱해 px 로 쓴다. */
private const val MARKER_SIZE_DP = 26

/** 지금 보고 있는 장소의 마커 지름(dp). 나머지보다 확실히 크게 잡아 눈에 띄게 한다. */
private const val FOCUSED_MARKER_SIZE_DP = 40

/** 장소를 잇는 점선 패턴(칠하는 길이, 비우는 길이). */
private val ROUTE_DASH_PATTERN = arrayOf(8.dp, 6.dp)

/**
 * 전체 보기에서 핀 주위에 두는 여백.
 *
 * 지도 위에 오버레이를 얹는 화면(장소 상세 시트)은 오버레이가 가리는 높이만큼 위·아래를 크게
 * 잡아야 핀이 카드 뒤로 숨지 않는다. 오버레이가 없으면 기본값(사방 40dp)으로 충분하다.
 */
@Immutable
data class RouteFitPadding(
    val top: Dp = 40.dp,
    val bottom: Dp = 40.dp,
    val side: Dp = 40.dp,
)

/**
 * 코스 경로 지도: 장소마다 순번 마커를 찍고 순서대로 점선으로 잇는다.
 *
 * 드래그·확대로 둘러볼 수 있고, [focus] 가 바뀌면(이전/다음·핀 탭) 그 위치로 카메라가 따라간다.
 * [overview] 면 대신 모든 핀이 들어오도록 경계에 맞춘다.
 */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun CourseRouteMap(
    focus: LatLng,
    focusOrder: Int,
    points: List<Pair<CourseDetailPlaceVO, LatLng>>,
    overview: Boolean,
    onMarkerClick: (CourseDetailPlaceVO) -> Unit,
    modifier: Modifier = Modifier,
    fitPadding: RouteFitPadding = RouteFitPadding(),
    scrollEnabled: Boolean = true,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val density = LocalDensity.current
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(focus, PLACE_MAP_ZOOM)
        }
    // 전체 보기면 모든 핀이 들어오게, 아니면 지금 장소로 확대해 맞춘다.
    LaunchedEffect(focus, overview, points) {
        val coords = points.map { it.second }
        if (overview && coords.size >= 2) {
            val bounds = LatLngBounds.Builder().apply { coords.forEach { include(it) } }.build()
            with(density) {
                cameraPositionState.animate(
                    CameraUpdate.fitBounds(
                        bounds,
                        fitPadding.side.roundToPx(),
                        fitPadding.top.roundToPx(),
                        fitPadding.side.roundToPx(),
                        fitPadding.bottom.roundToPx(),
                    ),
                )
            }
        } else {
            // 위치만 옮기면 전체 보기에서 넘어왔을 때 축소된 채라 장소가 안 보인다.
            // 확대 수준까지 같이 맞춰 항상 같은 눈높이로 보여 준다.
            cameraPositionState.animate(CameraUpdate.scrollAndZoomTo(focus, PLACE_MAP_ZOOM))
        }
    }
    NaverMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings =
            MapUiSettings(
                // 드래그·확대로 주변을 둘러볼 수 있게 연다. 기울이기·회전은 길 파악에 도움이 안 돼 막는다.
                // 스크롤되는 화면 안에 끼워 넣을 때는(코스 상세) 지도가 스크롤을 가로채지 않게 닫는다.
                isScrollGesturesEnabled = scrollEnabled,
                isZoomGesturesEnabled = scrollEnabled,
                isTiltGesturesEnabled = false,
                isRotateGesturesEnabled = false,
                isStopGesturesEnabled = true,
                isZoomControlEnabled = false,
                isScaleBarEnabled = false,
                isCompassEnabled = false,
                isLogoClickEnabled = false,
            ),
    ) {
        RouteOverlays(
            points = points,
            focusOrder = focusOrder,
            allFocused = overview,
            colors =
                RouteMarkerColors(
                    // 지도 배경과 겹쳐도 또렷하도록 경로선은 검정으로 둔다.
                    line = color.contentDefaultLevel0,
                    focusFillArgb = color.bgAccent.toArgb(),
                    restFillArgb = color.contentDefaultLevel2.toArgb(),
                    textArgb = color.contentOnAccent.toArgb(),
                ),
            onMarkerClick = onMarkerClick,
        )
    }
}

/** 좌표가 있는 장소만 골라 (장소, 좌표) 쌍으로 만든다. 순서는 그대로 유지한다. */
fun List<CourseDetailPlaceVO>.toRoutePoints(): List<Pair<CourseDetailPlaceVO, LatLng>> =
    mapNotNull { place -> place.latLngOrNull()?.let { place to it } }

/** 좌표가 한쪽만 있으면 쓸 수 없다(엉뚱한 지점에 핀이 찍힌다). 둘 다 있을 때만 만든다. */
fun CourseDetailPlaceVO.latLngOrNull(): LatLng? {
    val latitude = this.latitude
    val longitude = this.longitude
    return if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
}

/** 순번 마커·경로선에 쓰는 색 묶음. 파라미터 수를 줄이려 홀더로 전달한다. */
@Immutable
private data class RouteMarkerColors(
    val line: Color,
    val focusFillArgb: Int,
    val restFillArgb: Int,
    val textArgb: Int,
)

/** 지도 위 오버레이: 순번 마커 + 장소를 잇는 점선. */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
@NaverMapComposable
private fun RouteOverlays(
    points: List<Pair<CourseDetailPlaceVO, LatLng>>,
    focusOrder: Int,
    allFocused: Boolean,
    colors: RouteMarkerColors,
    onMarkerClick: (CourseDetailPlaceVO) -> Unit,
) {
    val density = LocalDensity.current.density
    if (points.size >= 2) {
        PolylineOverlay(
            coords = points.map { it.second },
            width = 3.dp,
            color = colors.line,
            pattern = ROUTE_DASH_PATTERN,
        )
    }
    points.forEach { (place, position) ->
        // 지금 보고 있는 장소는 크고 진한 핀, 나머지는 작고 흐린 핀으로 찍어 어디를 보는지 알 수 있게 한다.
        // 코스 전체 보기에서는 특정 장소를 보는 게 아니므로 전부 진한 핀으로 그린다.
        val focused = allFocused || place.order == focusOrder
        val fillArgb = if (focused) colors.focusFillArgb else colors.restFillArgb
        val sizeDp = if (focused) FOCUSED_MARKER_SIZE_DP else MARKER_SIZE_DP
        val icon =
            remember(place.order, fillArgb, colors.textArgb, sizeDp, density) {
                numberedMarkerIcon(
                    number = place.order,
                    fillArgb = fillArgb,
                    textArgb = colors.textArgb,
                    sizePx = (sizeDp * density).toInt(),
                )
            }
        Marker(
            state = rememberUpdatedMarkerState(position = position),
            icon = icon,
            anchor = Offset(0.5f, 0.5f),
            // 겹칠 때 지금 보고 있는 핀이 위로 오게 한다.
            zIndex = if (focused) 1 else 0,
            // 흐린 핀을 누르면 그 장소가 활성화된다. true 를 돌려 지도 기본 동작을 막는다.
            onClick = {
                onMarkerClick(place)
                true
            },
        )
    }
}

/** 순번 숫자가 들어간 원형 마커 아이콘을 그린다(채운 원 + 흰 숫자 + 흰 테두리). */
private fun numberedMarkerIcon(
    number: Int,
    fillArgb: Int,
    textArgb: Int,
    sizePx: Int,
): OverlayImage {
    val bitmap = createBitmap(sizePx, sizePx)
    val canvas = Canvas(bitmap)
    val center = sizePx / 2f
    val strokeWidth = sizePx * 0.08f
    val radius = center - strokeWidth
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fillArgb }
    val stroke =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textArgb
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }
    canvas.drawCircle(center, center, radius, fill)
    canvas.drawCircle(center, center, radius, stroke)
    val text =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textArgb
            textAlign = Paint.Align.CENTER
            textSize = sizePx * 0.5f
            typeface = Typeface.DEFAULT_BOLD
        }
    val metrics = text.fontMetrics
    canvas.drawText(number.toString(), center, center - (metrics.ascent + metrics.descent) / 2f, text)
    return OverlayImage.fromBitmap(bitmap)
}
