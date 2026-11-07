package com.chillsam.courmy.course.presentation.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.RoutePointVO
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.NaverMapComposable
import com.naver.maps.map.compose.PolylineOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.naver.maps.map.overlay.OverlayImage

// 코스 경로 지도. 코스 상세의 "코스 한눈에 보기" 구역과 장소 상세 시트가 같이 쓴다.

/** 장소 1곳을 볼 때의 확대 수준. 코스 전체 보기보다 가깝게 당긴다. */
internal const val PLACE_MAP_ZOOM = 16.0

/** 순번 마커 지름(dp). 그릴 때 화면 밀도를 곱해 px 로 쓴다. */
private const val MARKER_SIZE_DP = 22

/** 지금 보고 있는 장소의 마커 지름(dp). 나머지보다 확실히 크게 잡아 눈에 띄게 한다. */
private const val FOCUSED_MARKER_SIZE_DP = 34

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
 *
 * [onMapClick] 은 핀 밖 지도 영역을 눌렀을 때다. 미니맵처럼 지도 자체가 "눌러서 크게 보기"
 * 진입점인 자리에서 쓴다(핀 탭은 [onMarkerClick] 으로 따로 받는다).
 *
 * [routePaths] 는 **출발 장소 placeId** 로 색인한 실제 도보 경로 좌표다. 한 구간에 경로가 있으면 그 선을
 * 실선으로 긋고, 없으면(미로드·도보 불가·조회 실패) 두 핀을 점선 직선으로 잇는다(폴백).
 *
 * [showMyLocation] 이면 기기 현위치(파란 점)를 지도에 얹고 "내 위치" 버튼을 띄운다(따라가기에서만 켠다).
 * 권한은 버튼을 처음 누를 때 요청하고, 카메라는 코스 경로에 두되 버튼을 누르면 현위치로 옮긴다.
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
    onMapClick: (() -> Unit)? = null,
    routePaths: Map<Long, List<LatLng>> = emptyMap(),
    showMyLocation: Boolean = false,
    // 진행 중 "지금 스팟으로 오는 구간"만 강조한다(B-②). 그 구간(= order 로 도착하는 구간)은 악센트색·굵게,
    // 나머지는 흐리게. null 이면 전 구간을 같은 색으로 긋는다(미리보기·코스 상세·장소 시트 기존 동작).
    highlightToOrder: Int? = null,
    // 진행 중 카메라에 함께 담을 두 점(직전·지금 스팟). 설정되고 전체 보기가 아니면 이 둘을 한 화면에 맞춘다.
    fitPoints: List<LatLng>? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val density = LocalDensity.current
    val context = LocalContext.current
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(focus, PLACE_MAP_ZOOM)
        }
    var loaded by remember { mutableStateOf(false) }
    var viewport by remember { mutableStateOf(IntSize.Zero) }

    // 현위치 표시(따라가기). 권한·위치 구독은 FusedLocationSource 가 맡고, 오버레이(파란 점)만 켠다 —
    // 카메라는 코스 경로를 따라가게 두고(NoFollow) "내 위치" 버튼을 눌렀을 때만 현위치로 옮긴다.
    // 버튼·권한 요청·리센터는 [MyLocationButton] 이 맡고, 여기선 오버레이에 필요한 것만 둔다.
    val locationSource = if (showMyLocation) rememberFusedLocationSource() else null
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    val trackingMode =
        if (showMyLocation && hasLocationPermission) LocationTrackingMode.NoFollow else LocationTrackingMode.None
    // 전체 보기면 모든 핀이, 진행 중이면 "직전·지금 스팟" 두 점이 들어오게, 그 외엔 지금 장소로 확대해 맞춘다.
    LaunchedEffect(focus, overview, points, loaded, viewport, fitPadding, fitPoints) {
        if (!loaded || viewport == IntSize.Zero) return@LaunchedEffect
        // 네이티브 지도에 변경된 레이아웃 크기가 적용된 다음 카메라 경계를 맞춘다.
        withFrameNanos { }
        val coords = points.map { it.second }
        val fitCoords = if (overview) coords else fitPoints.orEmpty()
        if (fitCoords.size >= 2) {
            val bounds = LatLngBounds.Builder().apply { fitCoords.forEach { include(it) } }.build()
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
    Box(modifier = modifier.fillMaxSize()) {
        NaverMap(
            modifier = Modifier.fillMaxSize().onSizeChanged { viewport = it },
            onMapLoaded = { loaded = true },
            cameraPositionState = cameraPositionState,
            onMapClick = { _, _ -> onMapClick?.invoke() },
            locationSource = locationSource,
            properties = MapProperties(locationTrackingMode = trackingMode),
            onLocationChange = { myLocation = LatLng(it.latitude, it.longitude) },
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
                highlightToOrder = highlightToOrder,
                routePaths = routePaths,
                colors =
                    RouteMarkerColors(
                        // 지도 배경과 겹쳐도 또렷하도록 경로선은 검정으로 둔다. 강조 모드일 땐 지금 구간만 악센트색.
                        line = color.contentDefaultLevel0,
                        accentLine = color.contentAccent,
                        dimLine = color.contentDefaultLevel3,
                        focusFillArgb = color.bgAccent.toArgb(),
                        restFillArgb = color.contentDefaultLevel2.toArgb(),
                        textArgb = color.contentOnAccent.toArgb(),
                    ),
                onMarkerClick = onMarkerClick,
            )
        }
        if (showMyLocation) {
            MyLocationButton(
                cameraPositionState = cameraPositionState,
                myLocation = myLocation,
                hasPermission = hasLocationPermission,
                onPermissionGranted = { hasLocationPermission = true },
            )
        }
    }
}

/**
 * 지도 위 "내 위치" 버튼(흰 원 + 현위치 아이콘). 권한 요청·현위치 리센터까지 자체적으로 처리해
 * [CourseRouteMap] 의 복잡도를 낮춘다.
 *
 * 권한이 있으면 바로 현위치로 옮기고, 없으면 먼저 요청한 뒤 허용되면 옮긴다. 리센터는 현위치([myLocation])가
 * 들어온 다음 한 번만 일어난다(이후 지도 조작을 막지 않는다).
 */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun BoxScope.MyLocationButton(
    cameraPositionState: CameraPositionState,
    myLocation: LatLng?,
    hasPermission: Boolean,
    onPermissionGranted: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var pendingRecenter by remember { mutableStateOf(false) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.values.any { it }) {
                onPermissionGranted()
                pendingRecenter = true
            }
        }
    LaunchedEffect(myLocation, pendingRecenter) {
        val location = myLocation
        if (pendingRecenter && location != null) {
            cameraPositionState.animate(CameraUpdate.scrollTo(location))
            pendingRecenter = false
        }
    }
    Box(
        modifier =
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(color.bgDefaultLevel1)
                .clickable {
                    if (hasPermission) {
                        pendingRecenter = true
                    } else {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            ),
                        )
                    }
                },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_my_location_24),
            contentDescription = "내 위치",
            tint = color.contentDefaultLevel0,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** 위치 권한(정밀·대략 중 하나라도)이 있는지. 요청 자체는 FusedLocationSource·버튼 런처가 맡는다. */
private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

/** 좌표가 있는 장소만 골라 (장소, 좌표) 쌍으로 만든다. 순서는 그대로 유지한다. */
fun List<CourseDetailPlaceVO>.toRoutePoints(): List<Pair<CourseDetailPlaceVO, LatLng>> =
    mapNotNull { place -> place.latLngOrNull()?.let { place to it } }

/**
 * 좌표가 한쪽만 있거나 위경도 범위를 벗어나면 쓸 수 없다. 둘 다 정상일 때만 만든다.
 *
 * 범위를 거르는 건 엉뚱한 지점에 핀이 찍히는 걸 막기 위함이기도 하지만, 이 값이 그대로 지도 SDK 의
 * 네이티브 레이어(libnavermap.so)까지 내려가기 때문이기도 하다. 거기서 죽으면 JVM 예외가 아니라
 * 프로세스가 통째로 내려가서 try/catch 로도 못 막는다. 들어올 때 거르는 게 유일한 방어다.
 *
 * NaN·Infinity 도 여기서 함께 걸러진다 — 범위 비교가 IEEE 규칙상 항상 false 라 자연히 제외된다.
 */
fun CourseDetailPlaceVO.latLngOrNull(): LatLng? {
    val latitude = this.latitude ?: return null
    val longitude = this.longitude ?: return null
    if (latitude !in VALID_LATITUDE || longitude !in VALID_LONGITUDE) return null
    return LatLng(latitude, longitude)
}

/** 경로 폴리라인 좌표. 범위를 벗어난 값은 지도 SDK 네이티브 레이어를 죽일 수 있어 [latLngOrNull] 과 같은 기준으로 거른다. */
fun RoutePointVO.latLngOrNull(): LatLng? {
    if (latitude !in VALID_LATITUDE || longitude !in VALID_LONGITUDE) return null
    return LatLng(latitude, longitude)
}

private val VALID_LATITUDE = -90.0..90.0
private val VALID_LONGITUDE = -180.0..180.0

/** 순번 마커·경로선에 쓰는 색 묶음. 파라미터 수를 줄이려 홀더로 전달한다. */
@Immutable
private data class RouteMarkerColors(
    val line: Color,
    val accentLine: Color,
    val dimLine: Color,
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
    highlightToOrder: Int?,
    routePaths: Map<Long, List<LatLng>>,
    colors: RouteMarkerColors,
    onMarkerClick: (CourseDetailPlaceVO) -> Unit,
) {
    val density = LocalDensity.current.density
    // 구간마다: 실제 도보 경로가 있으면 실선으로, 없으면 두 핀을 점선 직선으로 잇는다(폴백).
    points.zipWithNext().forEach { (from, to) ->
        RouteSegment(
            from = from.second,
            to = to.second,
            path = routePaths[from.first.placeId],
            // 강조 모드면 그 순번으로 **도착하는** 구간만 "지금 구간"으로 본다.
            isCurrentLeg = highlightToOrder != null && to.first.order == highlightToOrder,
            highlighting = highlightToOrder != null,
            colors = colors,
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

/**
 * 한 구간 선. 실제 도보 경로([path])가 있으면 실선, 없으면 두 핀을 점선 직선으로 잇는다(폴백).
 * 강조 모드([highlighting])에서 "지금 구간"([isCurrentLeg])은 악센트색·굵게, 나머지는 흐리게.
 */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
@NaverMapComposable
private fun RouteSegment(
    from: LatLng,
    to: LatLng,
    path: List<LatLng>?,
    isCurrentLeg: Boolean,
    highlighting: Boolean,
    colors: RouteMarkerColors,
) {
    val lineColor =
        when {
            !highlighting -> colors.line
            isCurrentLeg -> colors.accentLine
            else -> colors.dimLine
        }
    // 지금 가야 할 구간은 다른 구간 선과 겹쳐도 가려지지 않게 항상 위로 올린다(zIndex ↑).
    val lineZIndex = if (isCurrentLeg) 1 else 0
    if (path != null && path.size >= 2) {
        PolylineOverlay(
            coords = path,
            width = if (isCurrentLeg) 6.dp else 4.dp,
            color = lineColor,
            zIndex = lineZIndex,
        )
    } else {
        PolylineOverlay(
            coords = listOf(from, to),
            width = if (isCurrentLeg) 4.dp else 3.dp,
            color = lineColor,
            pattern = ROUTE_DASH_PATTERN,
            zIndex = lineZIndex,
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
