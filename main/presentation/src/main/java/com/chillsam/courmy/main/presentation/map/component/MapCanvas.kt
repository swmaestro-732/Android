package com.chillsam.courmy.main.presentation.map.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.chillsam.courmy.common.presentation.component.placeIcon
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapCameraMove
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.CameraUpdateReason
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.NaverMapComposable
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** 지도 탐색 화면의 기본 확대 수준. 성수동 같은 동네 하나가 화면에 들어오는 정도. */
private const val EXPLORE_ZOOM = 15.0

/** 핀을 하나 골랐을 때 당겨 주는 확대 수준. */
private const val FOCUS_ZOOM = 16.0

/** 클러스터를 탭했을 때 더 당기는 확대 폭. 한 단계로는 묶음이 안 풀리는 경우가 많다. */
private const val CLUSTER_ZOOM_STEP = 2.0

/**
 * 장소 탐색용 지도(FS-10 계열 공용).
 *
 * 좌표가 없는 장소는 핀을 못 찍으므로 자동으로 빠진다. 선택된 핀만 크게 그리고 말풍선을 얹는다.
 * 선택한 핀으로 이동하고, [showAllPlaces] 일 때는 결과 좌표가 바뀌면 전체를 보여 준다.
 * 선택 해제나 시트 크기 변경만으로 사용자가 옮긴 카메라를 되돌리지 않는다.
 */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
internal fun MapCanvas(
    places: List<MapPlaceVO>,
    clusters: List<MapClusterVO>,
    selectedPlaceId: Long?,
    initialPosition: LatLng,
    onPlaceClick: (Long) -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onBoundsChanged: ((MapBounds) -> Unit)? = null,
    locationSource: LocationSource? = null,
    locationTrackingMode: LocationTrackingMode = LocationTrackingMode.None,
    onLocationChange: ((android.location.Location) -> Unit)? = null,
    onCameraGesture: (() -> Unit)? = null,
    showAllPlaces: Boolean = false,
    cameraMove: MapCameraMove? = null,
    initialBounds: MapBounds? = null,
    hidePins: Boolean = false,
    onCameraMoving: (() -> Unit)? = null,
    onDestinationReached: ((Long, MapBounds) -> Unit)? = null,
) {
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(initialPosition, EXPLORE_ZOOM)
        }
    var initialBoundsApplied by rememberSaveable { mutableStateOf(false) }
    var mapLoaded by remember { mutableStateOf(false) }
    val selectedPosition = places.firstOrNull { it.id == selectedPlaceId }?.latLngOrNull()
    val overviewPositions = if (showAllPlaces) places.mapNotNull { it.latLngOrNull() }.distinct() else emptyList()
    val fitPadding = with(LocalDensity.current) { 48.dp.roundToPx() }
    MapCameraFocus(cameraPositionState, selectedPlaceId, selectedPosition, overviewPositions, fitPadding)
    val scope = rememberCoroutineScope()

    val moveInFlight =
        MapDestinationMotion(
            cameraPositionState,
            mapLoaded,
            cameraMove,
            fitPadding,
            onCameraMoving,
            onDestinationReached,
        )

    val canReportBounds = mapLoaded && !moveInFlight && cameraMove == null
    ObserveMapCamera(cameraPositionState, canReportBounds, onBoundsChanged, onCameraMoving, onCameraGesture)

    NaverMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            if (!initialBoundsApplied) {
                initialBoundsApplied = true
                initialBounds?.let {
                    cameraPositionState.move(
                        CameraUpdate.fitBounds(
                            LatLngBounds(LatLng(it.swLat, it.swLng), LatLng(it.neLat, it.neLng)),
                            0,
                        ),
                    )
                }
            }
            mapLoaded = true
            cameraPositionState.reportBounds(cameraMove == null, onBoundsChanged)
        },
        contentPadding = contentPadding,
        // 현위치 추적(SCRUM-634). 권한 요청은 FusedLocationSource 가 활성화 시점에 처리한다.
        locationSource = locationSource,
        properties = MapProperties(locationTrackingMode = locationTrackingMode),
        onLocationChange = { location -> onLocationChange?.invoke(location) },
        uiSettings =
            MapUiSettings(
                // 주변을 둘러보는 화면이라 이동·확대는 열고, 길 파악에 도움 안 되는 기울이기·회전은 막는다.
                isScrollGesturesEnabled = true,
                isZoomGesturesEnabled = true,
                isTiltGesturesEnabled = false,
                isRotateGesturesEnabled = false,
                isZoomControlEnabled = false,
                isScaleBarEnabled = false,
                isCompassEnabled = false,
                isLogoClickEnabled = false,
            ),
        // 빈 지도를 누르면 말풍선을 접는다.
        onMapClick = { _, _ -> onMapClick() },
    ) {
        val pinsHidden = hidePins || moveInFlight || (onCameraMoving != null && cameraPositionState.isMoving)
        MapOverlays(
            places = if (pinsHidden) emptyList() else places,
            clusters = if (pinsHidden) emptyList() else clusters,
            selectedPlaceId = selectedPlaceId,
            onPlaceClick = onPlaceClick,
            onClusterClick = { position ->
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdate.scrollAndZoomTo(position, cameraPositionState.position.zoom + CLUSTER_ZOOM_STEP),
                    )
                }
            },
        )
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
@NaverMapComposable
private fun MapOverlays(
    places: List<MapPlaceVO>,
    clusters: List<MapClusterVO>,
    selectedPlaceId: Long?,
    onPlaceClick: (Long) -> Unit,
    onClusterClick: (LatLng) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val density = LocalDensity.current.density
    val context = LocalContext.current

    clusters.forEach { cluster ->
        val icon =
            remember(cluster.count, density) {
                clusterPinIcon(
                    density = density,
                    count = cluster.count,
                    fillArgb = color.bgAccent.toArgb(),
                    textArgb = color.contentOnAccent.toArgb(),
                )
            }
        val position = LatLng(cluster.latitude, cluster.longitude)
        Marker(
            state = rememberUpdatedMarkerState(position = position),
            icon = icon,
            anchor = Offset(0.5f, 0.5f),
            zIndex = CLUSTER_Z,
            onClick = {
                // 묶음을 누르면 확대해서 푼다 — 격자가 좁아지며 개별 핀으로 갈라진다(SCRUM-633).
                onClusterClick(position)
                true
            },
        )
    }

    places.forEach { place ->
        val position = place.latLngOrNull() ?: return@forEach
        val selected = place.id == selectedPlaceId
        val icon =
            remember(place.category, place.detailCategory, selected, density, color, context) {
                placePinIcon(
                    density = density,
                    style =
                        PlacePinStyle(
                            fillArgb = color.bgAccentSubtle.toArgb(),
                            strokeArgb = if (selected) color.contentAccent.toArgb() else color.bgDefaultLevel1.toArgb(),
                            labelBackgroundArgb = color.contentDefaultLevel0.toArgb(),
                            labelTextArgb = color.contentOnAccent.toArgb(),
                            categoryIcon =
                                requireNotNull(
                                    ContextCompat.getDrawable(
                                        context,
                                        placeIcon(place.category, place.detailCategory),
                                    ),
                                ).mutate().apply {
                                    setTint(color.contentAccent.toArgb())
                                },
                        ),
                    selected = selected,
                )
            }
        if (selected) {
            // 말풍선은 별도 마커로 그린다 — 핀 비트맵에 넣으면 라벨 폭만큼 터치 영역이 커져
            // 동그라미 밖을 눌러도 상세가 열린다. 탭은 소비만 하고 아무것도 하지 않는다.
            val label = place.markerLabel()
            val labelIcon =
                remember(label, density, color) {
                    placeLabelIcon(
                        density = density,
                        label = label,
                        backgroundArgb = color.contentDefaultLevel0.toArgb(),
                        textArgb = color.contentOnAccent.toArgb(),
                    )
                }
            Marker(
                state = rememberUpdatedMarkerState(position = position),
                icon = labelIcon,
                anchor = Offset(0.5f, 1f),
                zIndex = SELECTED_LABEL_Z,
                onClick = { true },
            )
        }
        Marker(
            state = rememberUpdatedMarkerState(position = position),
            icon = icon,
            // 비트맵 아래쪽 꼬리 끝이 실제 좌표를 가리킨다.
            anchor = Offset(0.5f, 1f),
            zIndex = if (selected) SELECTED_PIN_Z else PIN_Z,
            onClick = {
                onPlaceClick(place.id)
                // true 를 돌려 지도의 기본 클릭 처리(=onMapClick 으로 선택 해제)를 막는다.
                true
            },
        )
    }
}

private const val PIN_Z = 0
private const val CLUSTER_Z = 2
private const val SELECTED_PIN_Z = 3
private const val SELECTED_LABEL_Z = 2

/** 선택된 핀 말풍선 문구. 시안의 `어니언 성수 · ★4.8` 형태. */
private fun MapPlaceVO.markerLabel(): String = if (rating > 0) "$name · ★$rating" else name

/**
 * 위경도가 둘 다 정상일 때만 좌표를 만든다.
 *
 * 이 값이 지도 SDK 의 네이티브 레이어까지 그대로 내려가서, 범위를 벗어난 값이 들어가면 JVM 예외가
 * 아니라 프로세스가 통째로 죽는다(try/catch 로도 못 막는다). 들어올 때 거르는 게 유일한 방어다.
 * NaN·Infinity 도 범위 비교가 IEEE 규칙상 항상 false 라 여기서 함께 걸러진다.
 */
internal fun MapPlaceVO.latLngOrNull(): LatLng? {
    val lat = latitude ?: return null
    val lng = longitude ?: return null
    if (lat !in VALID_LATITUDE || lng !in VALID_LONGITUDE) return null
    return LatLng(lat, lng)
}

private val VALID_LATITUDE = -90.0..90.0
private val VALID_LONGITUDE = -180.0..180.0

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun MapCameraFocus(
    cameraPositionState: CameraPositionState,
    selectedPlaceId: Long?,
    selectedPosition: LatLng?,
    overviewPositions: List<LatLng>,
    fitPadding: Int,
) {
    LaunchedEffect(selectedPlaceId, selectedPosition) {
        selectedPosition?.let {
            cameraPositionState.animate(CameraUpdate.scrollAndZoomTo(it, FOCUS_ZOOM))
        }
    }
    LaunchedEffect(overviewPositions) {
        if (selectedPosition != null) return@LaunchedEffect
        when {
            overviewPositions.size == 1 -> {
                cameraPositionState.animate(CameraUpdate.scrollAndZoomTo(overviewPositions.single(), EXPLORE_ZOOM))
            }

            overviewPositions.size > 1 -> {
                val bounds = LatLngBounds.Builder().apply { overviewPositions.forEach { include(it) } }.build()
                cameraPositionState.animate(CameraUpdate.fitBounds(bounds, fitPadding))
            }
        }
    }
}

@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun MapDestinationMotion(
    cameraPositionState: CameraPositionState,
    mapLoaded: Boolean,
    cameraMove: MapCameraMove?,
    fitPadding: Int,
    onCameraMoving: (() -> Unit)?,
    onDestinationReached: ((Long, MapBounds) -> Unit)?,
): Boolean {
    var moveInFlight by remember { mutableStateOf(false) }
    val cameraMoving by rememberUpdatedState(onCameraMoving)
    val destinationReached by rememberUpdatedState(onDestinationReached)
    LaunchedEffect(mapLoaded, cameraMove?.id) {
        val move = cameraMove ?: return@LaunchedEffect
        if (!mapLoaded) return@LaunchedEffect
        moveInFlight = true
        cameraMoving?.invoke()
        try {
            val destination = LatLng(move.latitude, move.longitude)
            if (cameraPositionState.contentBounds?.contains(destination) == false) {
                val travelBounds =
                    LatLngBounds
                        .Builder()
                        .include(cameraPositionState.position.target)
                        .include(destination)
                        .build()
                cameraPositionState.animate(CameraUpdate.fitBounds(travelBounds, fitPadding), durationMs = 400)
            }
            cameraPositionState.animate(CameraUpdate.scrollAndZoomTo(destination, EXPLORE_ZOOM), durationMs = 700)
        } catch (cancelled: CancellationException) {
            if (!currentCoroutineContext().isActive) throw cancelled
            // 사용자가 애니메이션을 끊으면 실제 멈춘 영역을 조회한다.
        } finally {
            moveInFlight = false
        }
        snapshotFlow { cameraPositionState.isMoving }.first { !it }
        cameraPositionState.mapBounds()?.let { destinationReached?.invoke(move.id, it) }
    }

    return moveInFlight
}

private fun CameraPositionState.mapBounds(): MapBounds? =
    contentBounds?.let {
        MapBounds(it.southWest.latitude, it.southWest.longitude, it.northEast.latitude, it.northEast.longitude)
    }

@Composable
private fun ObserveMapCamera(
    camera: CameraPositionState,
    ready: Boolean,
    onBoundsChanged: ((MapBounds) -> Unit)?,
    onCameraMoving: (() -> Unit)?,
    onCameraGesture: (() -> Unit)?,
) {
    LaunchedEffect(camera.isMoving, ready) {
        if (camera.isMoving) {
            onCameraMoving?.invoke()
            if (camera.cameraUpdateReason == CameraUpdateReason.GESTURE) onCameraGesture?.invoke()
        } else {
            camera.reportBounds(ready, onBoundsChanged)
        }
    }
}

private fun CameraPositionState.reportBounds(
    ready: Boolean,
    onBoundsChanged: ((MapBounds) -> Unit)?,
) {
    if (!ready || isMoving) return
    mapBounds()?.let { onBoundsChanged?.invoke(it) }
}
