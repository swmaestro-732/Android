package com.chillsam.courmy.main.presentation.map.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.ContextCompat
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapBounds
import com.chillsam.courmy.main.entity.map.MapClusterVO
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.presentation.component.placeCategoryIcon
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationSource
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
 * 카메라는 [selectedPlaceId] 가 바뀔 때만 움직인다 — 사용자가 지도를 끌어 옮긴 뒤에도 목록을
 * 다시 그렸다는 이유로 원래 자리로 튕겨 돌아가면 안 되기 때문이다.
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
    recenterSignal: Int = 0,
    onBoundsChanged: ((MapBounds) -> Unit)? = null,
    locationSource: LocationSource? = null,
    locationTrackingMode: LocationTrackingMode = LocationTrackingMode.None,
    onLocationChange: ((android.location.Location) -> Unit)? = null,
    onCameraGesture: (() -> Unit)? = null,
) {
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(initialPosition, EXPLORE_ZOOM)
        }
    val selectedPosition = places.firstOrNull { it.id == selectedPlaceId }?.latLngOrNull()
    LaunchedEffect(selectedPlaceId, selectedPosition) {
        if (selectedPosition != null) {
            cameraPositionState.animate(CameraUpdate.scrollAndZoomTo(selectedPosition, FOCUS_ZOOM))
        }
    }
    val scope = rememberCoroutineScope()
    // 내 위치 버튼은 누를 때마다 반응해야 하는데, 좌표는 그대로라 값으로는 재실행을 못 건다.
    // 누른 횟수를 키로 삼아 "같은 곳으로 다시" 를 표현한다. 0 은 첫 구성이라 건너뛴다.
    LaunchedEffect(recenterSignal) {
        if (recenterSignal > 0) {
            cameraPositionState.animate(CameraUpdate.scrollAndZoomTo(initialPosition, EXPLORE_ZOOM))
        }
    }

    fun reportBounds() {
        val bounds = cameraPositionState.contentBounds ?: return
        onBoundsChanged?.invoke(
            MapBounds(
                bounds.southWest.latitude,
                bounds.southWest.longitude,
                bounds.northEast.latitude,
                bounds.northEast.longitude,
            ),
        )
    }
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            // 사용자가 지도를 끌면 추적(Follow)을 해제할 수 있도록 상위에 알린다(SCRUM-634).
            if (cameraPositionState.cameraUpdateReason == CameraUpdateReason.GESTURE) onCameraGesture?.invoke()
        } else {
            reportBounds()
        }
    }

    NaverMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLoaded = { reportBounds() },
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
        MapOverlays(
            places = places,
            clusters = clusters,
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
            remember(place.category, selected, density, color, context) {
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
                                    ContextCompat.getDrawable(context, placeCategoryIcon(place.category)),
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
