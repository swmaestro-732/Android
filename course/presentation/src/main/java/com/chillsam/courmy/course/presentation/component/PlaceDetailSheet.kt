package com.chillsam.courmy.course.presentation.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.createBitmap
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.presentation.LayoutVariants
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
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val SHEET_TOP_SHAPE = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

/** 시트 위에 남길 여백(상태바 아래). 뒤 화면이 살짝 보여 "덮인 시트"라는 걸 알려 준다. */
private val SHEET_TOP_GAP = 16.dp

/** 예전 배치에서 상단 지도 히어로가 차지하는 높이. */
private val MAP_HERO_HEIGHT = 200.dp

/** 장소 1곳만 보여주므로 코스 경로 지도(14.0)보다 가깝게 당긴다. */
private const val PLACE_MAP_ZOOM = 16.0

/** 오버레이 카드 배경 불투명도. 지도가 비쳐 보이되 글자는 읽히는 선. */
private const val OVERLAY_ALPHA = 0.95f

/** 팁이 길어도 지도를 다 덮지 않도록 제한하는 줄 수. */
private const val TIP_MAX_LINES = 5

/** 주소 앞 핀 아이콘 크기. 주소 글자(textRegularXS)에 묻히지 않을 만큼만 키운다. */
private val ADDRESS_ICON_SIZE = 14.dp

/** 손잡이 위아래 여백. 손가락으로 잡기 좋게 막대 자체(4dp)보다 넉넉히 준다. */
private val DRAG_HANDLE_PADDING = 10.dp

/** 하단 오버레이(코스 버튼·팁 카드)끼리, 그리고 이동 바와의 간격. 지도를 덜 가리게 좁게 잡는다. */
private val BOTTOM_OVERLAY_GAP = 8.dp

/** 패널 높이의 이 비율만큼 끌어내리면 닫는다. 살짝 건드린 것과 내리려는 동작을 가른다. */
private const val DISMISS_DRAG_FRACTION = 0.2f

/** 등장·퇴장·되돌림 모션 길이(ms). */
private const val SHEET_ANIM_MS = 220

/** 순번 마커 지름(dp). 그릴 때 화면 밀도를 곱해 px 로 쓴다. */
private const val MARKER_SIZE_DP = 26

/** 지금 보고 있는 장소의 마커 지름(dp). 나머지보다 확실히 크게 잡아 눈에 띄게 한다. */
private const val FOCUSED_MARKER_SIZE_DP = 40

/**
 * 전체 보기에서 핀이 오버레이 뒤로 숨지 않도록 두는 여백. 위쪽은 제목 카드 + 장소 칩,
 * 아래쪽은 코스 버튼 + 팁 카드가 가리는 높이를 어림한 값이다.
 */
private val OVERVIEW_FIT_PADDING_TOP = 210.dp
private val OVERVIEW_FIT_PADDING_BOTTOM = 190.dp
private val OVERVIEW_FIT_PADDING_SIDE = 40.dp

/** 장소 이동 칩의 이름 최대 폭. 이름이 길어도 칩 하나가 화면을 다 먹지 않게 한다. */
private val JUMP_CHIP_NAME_MAX_WIDTH = 120.dp

/** 장소를 잇는 점선 패턴(칠하는 길이, 비우는 길이). */
private val ROUTE_DASH_PATTERN = arrayOf(8.dp, 6.dp)

/**
 * 시트가 쓰는 코스 쪽 정보 묶음. 장소 하나가 아니라 코스 전체에 걸린 값이라 따로 모았다
 * (파라미터 수를 줄이려는 목적도 겸한다).
 */
@Immutable
data class PlaceSheetCourse(
    val title: String,
    val category: String,
    val summary: String,
    val authorName: String,
    val places: List<CourseDetailPlaceVO>,
)

/**
 * 장소 상세 바텀시트(Figma FS-10-Sheet). 코스 상세의 장소 행 화살표를 누르면 하단에서 올라온다.
 *
 * 상태바 아래 [SHEET_TOP_GAP] 만 남기고 화면을 채우며, 지도가 하단 이동 바를 뺀 나머지를 전부 차지하고
 * 그 위에 정보(상단)·작성자 팁과 사진(하단)이 떠 있다.
 *
 * 순번·이름·카테고리·좌표·작성자 팁·사진은 코스 상세가 이미 들고 있는 [coursePlace] 를 그대로 쓴다.
 * 코스 응답에 없는 [address] 만 장소 상세 API(`GET /service/v1/places/{placeId}`)에서 받아 넘긴다.
 * 덕분에 이전/다음으로 옮길 때 API 응답을 기다리지 않고 즉시 다시 그릴 수 있고, 주소만 잠시 뒤
 * 채워진다(아직 못 받았으면 빈 문자열). [wiki-needed]
 *
 * 지도에는 [coursePlace] 한 곳이 아니라 [places] 전체를 순번 마커로 찍고 점선으로 잇는다.
 * 현재 장소가 어디쯤인지 코스 안에서 보이게 하기 위해서다. 카메라는 [coursePlace] 를 따라간다.
 *
 * 보는 장소를 바꾸는 길은 셋이며 모두 [onSelectPlace] 로 모인다.
 * 하단 이동 바(이전/다음), 지도의 흐린 핀 탭, 코스 전체 보기에서 뜨는 장소 이동 줄.
 * 처음/마지막 장소면 이동 바의 해당 방향 버튼이 비활성으로 그려진다.
 */
@Composable
fun PlaceDetailSheet(
    coursePlace: CourseDetailPlaceVO,
    course: PlaceSheetCourse,
    address: String,
    onDismiss: () -> Unit,
    onSelectPlace: (CourseDetailPlaceVO) -> Unit,
    modifier: Modifier = Modifier,
) {
    val places = course.places
    val color = DesignSystemThemeImpl.designSystemColor
    val scope = rememberCoroutineScope()
    // 코스 전체 보기 모드. 켜면 카메라가 모든 핀을 담고, 몇 번째 장소로 갈지 고르는 줄이 나온다.
    var overview by remember { mutableStateOf(false) }
    // 다른 장소로 옮길 때는 전체 보기를 풀어 그 장소에 카메라를 맞춘다.
    val selectPlace = { place: CourseDetailPlaceVO ->
        overview = false
        onSelectPlace(place)
    }
    // 슬라이드 거리는 화면 높이로 잡는다. 측정된 패널 높이에 묶으면 그 값이 갱신될 때
    // LaunchedEffect 가 취소돼 패널이 화면 밖에 멈춘 채 남는다(시트가 안 뜨는 것처럼 보인다).
    val slideDistance =
        with(LocalDensity.current) {
            LocalConfiguration.current.screenHeightDp.dp
                .toPx()
        }
    // 패널이 세로로 얼마나 밀려 있는지(px). 손잡이를 끌면 손가락을 따라오고, 놓으면 되돌아가거나 닫힌다.
    val offsetY = remember { Animatable(slideDistance) }
    // 아래에서 위로 올라오는 등장 모션. 화면당 한 번만 돈다.
    LaunchedEffect(Unit) { offsetY.animateTo(0f, tween(SHEET_ANIM_MS)) }
    // 닫을 때도 아래로 미끄러진 뒤 실제로 사라지게 한다.
    val animatedDismiss: () -> Unit = {
        scope.launch {
            offsetY.animateTo(slideDistance, tween(SHEET_ANIM_MS))
            onDismiss()
        }
        Unit
    }
    Dialog(
        onDismissRequest = onDismiss,
        // 기본 다이얼로그 폭 제한을 풀어 화면을 꽉 채운다. 바깥(상단 여백) 탭으로도 닫는다.
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(modifier = modifier.fillMaxSize()) {
            // 상태바 + 여백만큼 비워 두고, 그 빈 곳을 누르면 닫힌다(시트 바깥을 누른 것과 같다).
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(SHEET_TOP_GAP)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = animatedDismiss,
                        ),
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .offset { IntOffset(0, offsetY.value.roundToInt()) }
                        .clip(SHEET_TOP_SHAPE)
                        .background(color.bgDefaultLevel0),
            ) {
                SheetDragHandle(
                    // 손가락을 따라 내려가되 위로는 안 넘어가게 0 에서 막는다.
                    onDrag = { delta ->
                        scope.launch { offsetY.snapTo((offsetY.value + delta).coerceAtLeast(0f)) }
                    },
                    onDragStopped = {
                        if (offsetY.value > slideDistance * DISMISS_DRAG_FRACTION) {
                            animatedDismiss()
                        } else {
                            scope.launch { offsetY.animateTo(0f, tween(SHEET_ANIM_MS)) }
                        }
                    },
                    onTap = animatedDismiss,
                )
                if (LayoutVariants.PLACE_SHEET_FULL_MAP) {
                    FullMapLayout(
                        coursePlace = coursePlace,
                        course = course,
                        address = address,
                        overview = overview,
                        onToggleOverview = { overview = !overview },
                        onSelectPlace = selectPlace,
                    )
                } else {
                    MapHeroLayout(coursePlace = coursePlace, address = address)
                }
            }
        }
    }
}

/**
 * 현재 배치: 지도가 시트를 꽉 채우고 그 위에 정보가 떠 있다.
 * 코스 전체 보기·순번 핀 탭·이전/다음 이동이 모두 여기 붙어 있다.
 */
@Composable
private fun ColumnScope.FullMapLayout(
    coursePlace: CourseDetailPlaceVO,
    course: PlaceSheetCourse,
    address: String,
    overview: Boolean,
    onToggleOverview: () -> Unit,
    onSelectPlace: (CourseDetailPlaceVO) -> Unit,
) {
    val places = course.places
    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
        val focus = coursePlace.latLngOrNull()
        if (focus != null) {
            CourseRouteMap(
                focus = focus,
                focusOrder = coursePlace.order,
                points = remember(places) { places.toRoutePoints() },
                overview = overview,
                onMarkerClick = onSelectPlace,
            )
        } else {
            NoLocationPlaceholder()
        }
        SheetTopOverlay(
            overview = overview,
            coursePlace = coursePlace,
            address = address,
            course = course,
            onSelectPlace = onSelectPlace,
            modifier = Modifier.align(Alignment.TopStart),
        )
        PlaceBottomOverlay(
            // 전체 보기에서는 특정 장소의 팁이 위 코스 정보와 어긋나므로 감춘다.
            tip = if (overview) "" else coursePlace.tip,
            authorName = course.authorName,
            overview = overview,
            onToggleOverview = onToggleOverview,
            modifier = Modifier.align(Alignment.BottomStart),
        )
    }
    val index = places.indexOf(coursePlace)
    // 전체 보기에서는 보고 있는 장소가 없으므로 앞뒤로 옮길 대상도 없다(둘 다 비활성).
    PlaceStepBar(
        onPrevious =
            places
                .getOrNull(index - 1)
                ?.takeIf { index > 0 && !overview }
                ?.let { { onSelectPlace(it) } },
        onNext =
            places
                .getOrNull(index + 1)
                ?.takeIf { index >= 0 && !overview }
                ?.let { { onSelectPlace(it) } },
    )
}

/**
 * 예전 배치: 지도를 상단 히어로([MAP_HERO_HEIGHT])로만 두고, 그 아래에 장소 정보를 쌓고
 * 하단에 길찾기 바를 고정한다. 코스 전체 보기·순번 핀·이전/다음은 이 배치에 없다.
 */
@Composable
private fun ColumnScope.MapHeroLayout(
    coursePlace: CourseDetailPlaceVO,
    address: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    val coordinate = coursePlace.latLngOrNull()
    Box(modifier = Modifier.fillMaxWidth().height(MAP_HERO_HEIGHT)) {
        if (coordinate != null) SinglePlaceMap(coordinate = coordinate) else NoLocationPlaceholder()
    }
    CompositionLocalProvider(LocalOverscrollFactory provides null) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DsText(
                    text = coursePlace.name,
                    style = DesignSystemThemeImpl.typeScale.titleExtraL,
                    color = color.contentDefaultLevel0,
                )
                if (coursePlace.category.isNotBlank()) {
                    DsText(
                        text = coursePlace.category,
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel2,
                    )
                }
            }
            if (address.isNotBlank()) HeroAddressCard(address = address)
        }
    }
    // 좌표가 없으면 보낼 목적지가 없어 길찾기 바 대신 제스처 바 여백만 남긴다.
    if (coordinate != null) {
        DirectionsBar(onClick = { context.openNaverMapDirections(coursePlace) })
    } else {
        Spacer(Modifier.navigationBarsPadding())
    }
}

/** 예전 배치의 주소 카드(테두리 있는 박스). 현재 배치는 제목 카드 안에 본문으로 넣는다. */
@Composable
private fun HeroAddressCard(address: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, color.borderDefaultLevel0, shape)
                .background(color.bgDefaultLevel1)
                .padding(14.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_location_24),
            contentDescription = null,
            tint = color.contentDefaultLevel2,
            modifier = Modifier.size(20.dp),
        )
        DsText(
            text = address,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel1,
            maxLines = Int.MAX_VALUE,
        )
    }
}

/** 예전 배치의 하단 고정 길찾기 바. */
@Composable
private fun DirectionsBar(onClick: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.background(color.bgDefaultLevel1)) {
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(color.borderDefaultLevel0))
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp)
                    .height(56.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(color.bgAccent)
                    .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_location_24),
                contentDescription = null,
                tint = color.contentOnAccent,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            DsText(
                text = "길찾기",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentOnAccent,
            )
        }
    }
}

/** 예전 배치의 히어로 지도: 장소 1곳만 찍고 조작은 막는다(아래 내용 스크롤과 충돌 방지). */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun SinglePlaceMap(coordinate: LatLng) {
    val cameraPositionState =
        rememberCameraPositionState { position = CameraPosition(coordinate, PLACE_MAP_ZOOM) }
    LaunchedEffect(coordinate) {
        cameraPositionState.animate(CameraUpdate.scrollAndZoomTo(coordinate, PLACE_MAP_ZOOM))
    }
    NaverMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings =
            MapUiSettings(
                isScrollGesturesEnabled = false,
                isZoomGesturesEnabled = false,
                isTiltGesturesEnabled = false,
                isRotateGesturesEnabled = false,
                isStopGesturesEnabled = false,
                isZoomControlEnabled = false,
                isScaleBarEnabled = false,
                isCompassEnabled = false,
                isLogoClickEnabled = false,
            ),
    ) {
        Marker(state = rememberUpdatedMarkerState(position = coordinate))
    }
}

/**
 * 예전 배치의 길찾기: 네이버 지도 앱의 도보 경로를 연다. 앱이 없으면 웹 지도로 떨어뜨린다.
 * `startActivity` 는 패키지 가시성(`<queries>`) 없이도 대상을 찾아 주므로 설치 여부는 미리 조회하지
 * 않고 [ActivityNotFoundException] 으로 판단한다.
 */
private fun Context.openNaverMapDirections(place: CourseDetailPlaceVO) {
    val latitude = place.latitude ?: return
    val longitude = place.longitude ?: return
    val name = Uri.encode(place.name)
    val appUri = "nmap://route/walk?dlat=$latitude&dlng=$longitude&dname=$name&appname=$packageName"
    val webUri = "https://map.naver.com/p/directions/-/$longitude,$latitude,$name/-/walk"
    if (!startViewIntent(appUri) && !startViewIntent(webUri)) {
        Toast.makeText(this, "지도를 열 수 없어요", Toast.LENGTH_SHORT).show()
    }
}

/** ACTION_VIEW 로 [uri] 를 연다. 처리할 앱이 없으면 false. */
private fun Context.startViewIntent(uri: String): Boolean =
    try {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(uri)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
        true
    } catch (e: ActivityNotFoundException) {
        Log.d("PlaceDetailSheet", "길찾기 대상 앱 없음: $uri", e)
        false
    }

/**
 * 시트 최상단 손잡이. 아래로 끌거나 탭하면 닫힌다.
 *
 * 컨테이너로 `ModalBottomSheet` 를 쓰지 않는 이유가 여기 있다. Material3 1.4 의 `ModalBottomSheet` 는
 * 시트 **전체**를 draggable 로 만들고 이를 끄는 파라미터가 없어, 지도를 끌 때마다 시트가 따라 내려왔다.
 * 지도 쪽에서 포인터 이벤트를 소비해 막아 보면 이번엔 Compose 가 지도(AndroidView)로 취소를 보내
 * 지도 팬이 죽는다. 그래서 시트를 [Dialog] 로 직접 만들고, 내리는 제스처는 이 손잡이에만 붙였다.
 */
@Composable
private fun SheetDragHandle(
    onDrag: (Float) -> Unit,
    onDragStopped: () -> Unit,
    onTap: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onTap,
                ).draggable(
                    state = rememberDraggableState(onDelta = onDrag),
                    orientation = Orientation.Vertical,
                    onDragStopped = { onDragStopped() },
                ).padding(vertical = DRAG_HANDLE_PADDING),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(color.borderDefaultLevel0),
        )
    }
}

/**
 * 지도 위 상단: 정보 카드 + 장소 이동 줄.
 *
 * 카드 내용은 모드에 따라 갈린다. 평소에는 보고 있는 장소(순번·이름·카테고리·주소),
 * 코스 전체 보기에서는 코스 자체(제목·요약)를 보여 준다. 장소 이동 줄은 전체 보기에서만 나오며
 * 카드 바로 아래에 붙는다.
 */
@Composable
private fun SheetTopOverlay(
    overview: Boolean,
    coursePlace: CourseDetailPlaceVO,
    address: String,
    course: PlaceSheetCourse,
    onSelectPlace: (CourseDetailPlaceVO) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (overview) {
            CourseInfoCard(title = course.title, category = course.category, summary = course.summary)
        } else {
            PlaceInfoCard(coursePlace = coursePlace, address = address)
        }
        if (overview) {
            // 전체 보기에서는 특정 장소를 보고 있는 게 아니라 어느 칩도 선택 상태로 두지 않는다.
            PlaceJumpRow(
                places = course.places,
                currentOrder = null,
                onSelectPlace = onSelectPlace,
            )
        }
    }
}

/** 코스 전체 보기용 카드: 코스 제목 + 요약(장소 수·도보). */
@Composable
private fun CourseInfoCard(
    title: String,
    category: String,
    summary: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(color.bgDefaultLevel1.copy(alpha = OVERLAY_ALPHA))
                .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = color.contentDefaultLevel0,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (category.isNotBlank()) {
            DsText(
                text = category,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
            )
        }
        if (summary.isNotBlank()) {
            DsText(
                text = summary,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
    }
}

/** 평소 카드: 순번·장소명·카테고리·주소. */
@Composable
private fun PlaceInfoCard(
    coursePlace: CourseDetailPlaceVO,
    address: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(color.bgDefaultLevel1.copy(alpha = OVERLAY_ALPHA))
                .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 코스 상세 장소 행·지도 마커와 같은 순번 배지.
        Box(
            modifier =
                Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color.bgAccent),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = coursePlace.order.toString(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentOnAccent,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DsText(
                text = coursePlace.name,
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
            )
            if (coursePlace.category.isNotBlank()) {
                DsText(
                    text = coursePlace.category,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                )
            }
            // 주소는 카드 테두리 없이 이 블록 안에 본문으로 둔다. 두 줄로 접힐 수 있어
            // 핀 아이콘은 위쪽에 맞춘다.
            if (address.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location_24),
                        contentDescription = null,
                        tint = color.contentDefaultLevel2,
                        modifier = Modifier.size(ADDRESS_ICON_SIZE),
                    )
                    DsText(
                        text = address,
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/** 지도 위 하단: 코스 전체 보기 버튼(우측) + 작성자 팁 카드. 버튼이 팁 카드 위에 뜬다. */
@Composable
private fun PlaceBottomOverlay(
    tip: String,
    authorName: String,
    overview: Boolean,
    onToggleOverview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        // 좌우는 위 제목 카드와 같은 16dp, 아래(이동 바와의 간격)와 버튼~팁 사이는 더 좁게 붙인다.
        modifier =
            modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = BOTTOM_OVERLAY_GAP),
        verticalArrangement = Arrangement.spacedBy(BOTTOM_OVERLAY_GAP),
        horizontalAlignment = Alignment.End,
    ) {
        OverviewButton(active = overview, onClick = onToggleOverview)
        PlaceTipCard(tip = tip, authorName = authorName)
    }
}

/** 코스 전체 보기 토글. 켜면 강조색으로 눌린 상태를 보여 준다. */
@Composable
private fun OverviewButton(
    active: Boolean,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (active) color.bgAccent else color.bgDefaultLevel1.copy(alpha = OVERLAY_ALPHA))
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_route_24),
            contentDescription = if (active) "현재 장소로 돌아가기" else "코스 전체 보기",
            tint = if (active) color.contentOnAccent else color.contentDefaultLevel0,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** 몇 번째 장소로 갈지 고르는 가로 줄. 지금 보고 있는 장소는 강조색으로 둔다. */
@Composable
private fun PlaceJumpRow(
    places: List<CourseDetailPlaceVO>,
    currentOrder: Int?,
    onSelectPlace: (CourseDetailPlaceVO) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        places.forEach { place ->
            val current = currentOrder != null && place.order == currentOrder
            Row(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(
                            if (current) color.bgAccent else color.bgDefaultLevel1.copy(alpha = OVERLAY_ALPHA),
                        ).clickable { onSelectPlace(place) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                DsText(
                    text = place.order.toString(),
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = if (current) color.contentOnAccent else color.contentAccent,
                )
                DsText(
                    text = place.name,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = if (current) color.contentOnAccent else color.contentDefaultLevel1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = JUMP_CHIP_NAME_MAX_WIDTH),
                )
            }
        }
    }
}

/**
 * 작성자 팁 카드. 코스 상세의 "Tip.{작성자}" 와 같은 강조 바 + 문구 구성이다.
 * 팁이 없으면 아무것도 그리지 않는다.
 */
@Composable
private fun PlaceTipCard(
    tip: String,
    authorName: String,
    modifier: Modifier = Modifier,
) {
    if (tip.isBlank()) return
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        // 좌우 여백은 바깥 오버레이가 이미 잡는다. 여기서 또 주면 위 제목 카드보다 안쪽으로 들어간다.
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(color.bgDefaultLevel1.copy(alpha = OVERLAY_ALPHA))
                .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .width(3.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(color.bgAccent),
        )
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            DsText(
                text = "Tip.$authorName",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
            )
            DsText(
                text = tip,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel1,
                maxLines = TIP_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** 하단 고정 이동 바: 코스의 이전·다음 장소로 옮긴다. 끝단이면 해당 버튼을 비활성으로 그린다. */
@Composable
private fun PlaceStepBar(
    onPrevious: (() -> Unit)?,
    onNext: (() -> Unit)?,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.background(color.bgDefaultLevel1)) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color.borderDefaultLevel0),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StepButton(
                iconRes = R.drawable.ic_chevron_left_24,
                text = "이전",
                onClick = onPrevious,
                iconFirst = true,
            )
            StepButton(
                iconRes = R.drawable.ic_chevron_right_24,
                text = "다음",
                onClick = onNext,
                iconFirst = false,
            )
        }
    }
}

/** 이동 버튼 1개. [onClick] 이 null 이면 흐린 색으로 두고 눌러도 반응하지 않는다. */
@Composable
private fun RowScope.StepButton(
    iconRes: Int,
    text: String,
    onClick: (() -> Unit)?,
    iconFirst: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val enabled = onClick != null
    val shape = RoundedCornerShape(15.dp)
    val background = if (enabled) color.bgAccent else color.bgDefaultLevel0
    val contentColor = if (enabled) color.contentOnAccent else color.contentDefaultLevel3
    Row(
        modifier =
            Modifier
                .weight(1f)
                .height(52.dp)
                .clip(shape)
                .background(background)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (iconFirst) {
            StepIcon(iconRes = iconRes, tint = contentColor)
            Spacer(Modifier.width(6.dp))
        }
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = contentColor,
        )
        if (!iconFirst) {
            Spacer(Modifier.width(6.dp))
            StepIcon(iconRes = iconRes, tint = contentColor)
        }
    }
}

@Composable
private fun StepIcon(
    iconRes: Int,
    tint: Color,
) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(20.dp),
    )
}

/** 좌표가 있는 장소만 (장소, 좌표) 로 추린다. 좌표가 없으면 지도에 찍을 수 없다. */
private fun List<CourseDetailPlaceVO>.toRoutePoints(): List<Pair<CourseDetailPlaceVO, LatLng>> =
    mapNotNull { place -> place.latLngOrNull()?.let { place to it } }

private fun CourseDetailPlaceVO.latLngOrNull(): LatLng? {
    val latitude = this.latitude
    val longitude = this.longitude
    // 좌표는 한쪽만 있으면 쓸 수 없다(엉뚱한 지점에 핀이 찍힌다).
    return if (latitude != null && longitude != null) LatLng(latitude, longitude) else null
}

/**
 * 코스 경로 지도: 장소마다 순번 마커를 찍고 순서대로 점선으로 잇는다.
 *
 * 드래그·확대로 둘러볼 수 있고, [focus] 가 바뀌면(이전/다음·핀 탭) 그 위치로 카메라가 따라간다.
 * [overview] 면 대신 모든 핀이 들어오도록 경계에 맞춘다.
 */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun CourseRouteMap(
    focus: LatLng,
    focusOrder: Int,
    points: List<Pair<CourseDetailPlaceVO, LatLng>>,
    overview: Boolean,
    onMarkerClick: (CourseDetailPlaceVO) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val density = LocalDensity.current
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(focus, PLACE_MAP_ZOOM)
        }
    // 전체 보기면 모든 핀이 들어오게, 아니면 지금 장소로 확대해 맞춘다.
    //
    // 지도 위·아래를 오버레이(제목 카드 + 장소 칩 / 코스 버튼 + 팁)가 가리므로 그만큼 여백을 더 준다.
    // 사방을 같은 값으로 주면 핀이 카드 뒤에 숨는다.
    LaunchedEffect(focus, overview, points) {
        val coords = points.map { it.second }
        if (overview && coords.size >= 2) {
            val bounds = LatLngBounds.Builder().apply { coords.forEach { include(it) } }.build()
            with(density) {
                cameraPositionState.animate(
                    CameraUpdate.fitBounds(
                        bounds,
                        OVERVIEW_FIT_PADDING_SIDE.roundToPx(),
                        OVERVIEW_FIT_PADDING_TOP.roundToPx(),
                        OVERVIEW_FIT_PADDING_SIDE.roundToPx(),
                        OVERVIEW_FIT_PADDING_BOTTOM.roundToPx(),
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
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings =
            MapUiSettings(
                // 드래그·확대로 주변을 둘러볼 수 있게 연다. 기울이기·회전은 길 파악에 도움이 안 돼 막는다.
                isScrollGesturesEnabled = true,
                isZoomGesturesEnabled = true,
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

/** 좌표가 없는 장소의 지도 자리. 빈 화면 대신 이유를 알려 준다. */
@Composable
private fun NoLocationPlaceholder() {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier = Modifier.fillMaxSize().background(color.imagePlaceholder),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = "위치 정보가 없어요",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
        )
    }
}
