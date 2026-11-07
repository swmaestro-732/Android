package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.presentation.component.CourseRouteMap
import com.chillsam.courmy.course.presentation.component.latLngOrNull
import com.chillsam.courmy.course.presentation.component.toRoutePoints
import com.naver.maps.geometry.LatLng
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** 하단 패널·버튼 모서리. 코스 상세 하단바와 같은 곡률로 맞춘다. */
private val FOLLOW_BUTTON_SHAPE = RoundedCornerShape(15.dp)
private val FOLLOW_PANEL_SHAPE = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

/** 동선 목록이 지도를 다 밀어내지 않게 제한하는 높이. */
private val ROUTE_LIST_MAX_HEIGHT = 230.dp

/** 진행 중 하단 패널의 턴바이턴 안내 목록 최대 높이. 안내가 길어도 패널이 화면을 덮지 않게 한다. */
private val ROUTE_GUIDE_MAX_HEIGHT = 140.dp

/**
 * 따라가기 화면(FS-21). [CourseFollowPhase] 에 따라 세 모습을 오간다.
 *
 * - 미리보기: 지도(전체 동선) + 동선 목록 + "이 코스 따라가기"
 * - 진행: 지도(지금 장소) + 다음 스팟 + "도착"(방문 체크)
 * - 완주: 축하 + 통계(방문 스팟·소요 시간·이동 거리) + "리뷰 남기기"
 *
 */
@Composable
internal fun CourseFollowScreen(
    state: CourseFollowUIState,
    onIntent: (CourseFollowIntent) -> Unit,
    onBack: () -> Unit,
    onLeaveReview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val detail = state.detail ?: return
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0)
                .statusBarsPadding(),
    ) {
        when (state.phase) {
            CourseFollowPhase.PREVIEW -> {
                PreviewPhase(
                    detail = detail,
                    routes = state.routes,
                    resumeVisitedCount = state.resumeVisitedCount,
                    onIntent = onIntent,
                    onBack = onBack,
                )
            }

            CourseFollowPhase.TRACKING -> {
                TrackingPhase(
                    state = state,
                    detail = detail,
                    onIntent = onIntent,
                    onBack = onBack,
                )
            }

            CourseFollowPhase.DONE -> {
                DonePhase(
                    state = state,
                    detail = detail,
                    onLeaveReview = onLeaveReview,
                    onSkip = onBack,
                )
            }
        }
    }
}

/** 미리보기: 전체 동선 지도 + 장소 순서 + 시작 버튼. */
@Composable
private fun ColumnScope.PreviewPhase(
    detail: CourseDetailVO,
    routes: Map<Long, FollowRouteStatus>,
    resumeVisitedCount: Int,
    onIntent: (CourseFollowIntent) -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    FollowTopBar(
        title = detail.title,
        // 도보 시간은 빼고 장소 수만 — 진행 중에 구간별로 보여 주므로 미리보기 총합은 생략한다.
        subtitle = detail.placeCountText,
        onBack = onBack,
    )
    FollowMap(detail = detail, focusIndex = null, routes = routes, modifier = Modifier.weight(1f))
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(FOLLOW_PANEL_SHAPE)
                .background(color.bgDefaultLevel1)
                .padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp)
                .navigationBarsPadding(),
    ) {
        DsText(
            text = "코스 동선 ${detail.places.size}",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        Spacer(Modifier.height(10.dp))
        Column(
            modifier =
                Modifier
                    .heightIn(max = ROUTE_LIST_MAX_HEIGHT)
                    .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // 이어가기면 이미 방문한 앞 장소만 "방문함"으로 표시하고, 나머지는 상태 문구 없이 둔다(안 간 곳은 당연히 예정이라 생략).
            detail.places.forEachIndexed { index, place ->
                RoutePlaceRow(
                    place = place,
                    stateText = if (index < resumeVisitedCount) "방문함" else "",
                    highlighted = resumeVisitedCount > 0 && index == resumeVisitedCount,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        FollowPrimaryButton(
            text = if (resumeVisitedCount > 0) "이어가기" else "이 코스 따라가기",
            iconRes = R.drawable.ic_footprint_24,
            modifier = Modifier.fillMaxWidth(),
        ) {
            onIntent(CourseFollowIntent.Start)
        }
    }
}

/** 진행: 지금 장소에 집중한 지도 + 다음 스팟 + 방문 체크. */
@Composable
private fun ColumnScope.TrackingPhase(
    state: CourseFollowUIState,
    detail: CourseDetailVO,
    onIntent: (CourseFollowIntent) -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var showEndConfirm by remember { mutableStateOf(false) }
    FollowTopBar(
        title = "${detail.title} · ${state.visitedCount + 1}/${detail.places.size} 진행 중",
        subtitle = "",
        onBack = onBack,
    )
    FollowMap(
        detail = detail,
        focusIndex = state.currentIndex,
        routes = state.routes,
        modifier = Modifier.weight(1f),
    )
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(FOLLOW_PANEL_SHAPE)
                .background(color.bgDefaultLevel1)
                .padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp)
                .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val current = state.currentPlace
        if (current != null) {
            DsText(
                text = "다음 스팟",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
            // 도보 시간은 아래 요약 줄에서 보여 주므로 카드에서는 빼고, 첫 스팟만 "여기서 출발" 표시.
            RoutePlaceRow(
                place = current,
                stateText = if (state.previousPlace == null) "여기서 출발" else "",
                highlighted = true,
            )
            WalkingRouteSection(
                status = state.incomingRouteStatus,
                destName = current.name,
                isFirstSpot = state.previousPlace == null,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FollowPrimaryButton(
                text = if (state.isRecording) "방문 기록 저장 중…" else "도착",
                iconRes = R.drawable.ic_check_24,
                modifier = Modifier.weight(1f),
                enabled = !state.isRecording,
            ) {
                onIntent(CourseFollowIntent.CheckArrival)
            }
            FollowEndButton(onClick = { showEndConfirm = true })
        }
    }
    if (showEndConfirm) {
        DsConfirmDialog(
            title = "따라가기를 종료할까요?",
            description = "지금 종료하면 진행 화면을 나가요. 나중에 코스에서 다시 이어갈 수 있어요.",
            onConfirm = {
                showEndConfirm = false
                onBack()
            },
            onDismiss = { showEndConfirm = false },
        )
    }
}

/** 완주: 축하 + 통계 + 후기 유도. */
@Composable
private fun ColumnScope.DonePhase(
    state: CourseFollowUIState,
    detail: CourseDetailVO,
    onLeaveReview: () -> Unit,
    onSkip: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenHorizontalPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(48.dp))
        Box(
            modifier =
                Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(color.bgAccent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check_24),
                contentDescription = null,
                tint = color.contentOnAccent,
                modifier = Modifier.size(30.dp),
            )
        }
        Spacer(Modifier.height(20.dp))
        DsText(
            text = "코스 완주!\n수고했어요",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = color.contentDefaultLevel0,
            maxLines = 2,
        )
        Spacer(Modifier.height(8.dp))
        DsText(
            text = detail.title,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel3,
        )
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FollowStatCard(value = "${state.visitedCount}", label = "방문 스팟")
            FollowStatCard(value = elapsedText(state), label = "소요 시간")
            FollowStatCard(value = distanceText(detail.places), label = "이동 거리")
        }
        Spacer(Modifier.height(24.dp))
        // 완주한 전체 스팟을 순서대로 — 마지막 한 곳만이 아니라 다녀온 코스 여정을 돌아보게 한다.
        DsText(
            text = "다녀온 코스",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        Spacer(Modifier.height(10.dp))
        CompletedCourseTimeline(places = detail.places)
        Spacer(Modifier.height(16.dp))
    }
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenHorizontalPadding)
                .navigationBarsPadding()
                .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FollowPrimaryButton(
            text = "리뷰 남기기",
            iconRes = null,
            modifier = Modifier.fillMaxWidth(),
            onClick = onLeaveReview,
        )
        DsText(
            text = "건너뛰기",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel3,
            modifier =
                Modifier
                    .clickable(onClick = onSkip)
                    .padding(12.dp),
        )
    }
}

/** 상단 바: 뒤로 + 제목(+부제). 진행 중에는 제목에 진행도를 함께 싣는다. */
@Composable
private fun FollowTopBar(
    title: String,
    subtitle: String,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left_24),
            contentDescription = "뒤로",
            tint = color.contentDefaultLevel0,
            modifier =
                Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onBack)
                    .padding(8.dp)
                    .size(22.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = title,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotBlank()) {
                DsText(
                    text = subtitle,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                )
            }
        }
        Spacer(Modifier.width(38.dp))
    }
}

/** 동선 지도. [focusIndex] 가 null 이면 전체 보기, 아니면 그 장소에 집중한다. [routes] 로 실제 도보 경로를 실선으로 긋는다. */
@Composable
private fun FollowMap(
    detail: CourseDetailVO,
    focusIndex: Int?,
    routes: Map<Long, FollowRouteStatus>,
    modifier: Modifier = Modifier,
) {
    val points = remember(detail.places) { detail.places.toRoutePoints() }
    val routePaths = remember(routes) { routes.toRoutePaths() }
    val focusPlace = focusIndex?.let { detail.places.getOrNull(it) }
    // 진행 중엔 "직전 스팟 → 지금 스팟" 구간을 비춘다 — 그 구간만 강조하고(B-②), 카메라는 **다음 장소까지의
    // 실제 도보 경로 전체**를 한 화면에 담는다. 경로가 없으면(폴백) 두 핀만 맞춘다.
    val previousPlace = focusIndex?.takeIf { it > 0 }?.let { detail.places.getOrNull(it - 1) }
    val incomingPath = previousPlace?.let { routePaths[it.placeId] }?.takeIf { it.size >= 2 }
    val focus = focusPlace?.latLngOrNull() ?: points.firstOrNull()?.second
    val fitPoints =
        incomingPath ?: listOfNotNull(previousPlace?.latLngOrNull(), focusPlace?.latLngOrNull()).takeIf { it.size == 2 }
    Box(modifier = modifier.fillMaxWidth()) {
        if (focus != null) {
            CourseRouteMap(
                focus = focus,
                focusOrder = focusPlace?.order ?: 0,
                points = points,
                overview = focusIndex == null,
                // 전체 보기(미리보기)는 강조 없이 전 구간, 진행 중은 지금 스팟으로 오는 구간만 강조.
                highlightToOrder = if (focusIndex == null) null else focusPlace?.order,
                fitPoints = fitPoints,
                onMarkerClick = {},
                routePaths = routePaths,
                showMyLocation = true,
            )
        }
    }
}

/** 경로 상태 맵을 지도용 폴리라인 좌표로 바꾼다 — 걸을 수 있고 좌표가 2개 이상인 구간만 실선으로 그린다. */
private fun Map<Long, FollowRouteStatus>.toRoutePaths(): Map<Long, List<LatLng>> =
    mapNotNull { (fromPlaceId, status) ->
        val route = (status as? FollowRouteStatus.Ready)?.route?.takeIf { it.walkable } ?: return@mapNotNull null
        val coords = route.path.mapNotNull { it.latLngOrNull() }
        if (coords.size < 2) null else fromPlaceId to coords
    }.toMap()

/**
 * 진행 중 "직전 스팟 → 지금 스팟" 구간의 도보 안내(비실시간 요약).
 *
 * - 걸을 수 있으면 "지금 스팟까지 · 거리 · 도보 N분" 요약만 크게 보이고, 상세 턴바이턴은 "가는 길 설명"으로 접는다.
 * - 첫 스팟([isFirstSpot])은 들어오는 구간이 없어 "여기서 코스를 시작해요"만.
 * - 조회 중이면 안내 문구, 도보 불가·조회 실패면 아무것도 그리지 않는다.
 */
@Composable
private fun WalkingRouteSection(
    status: FollowRouteStatus?,
    destName: String,
    isFirstSpot: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    if (isFirstSpot) {
        DsText(
            text = "여기서 코스를 시작해요",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel3,
        )
        return
    }
    when (status) {
        FollowRouteStatus.Loading -> {
            DsText(
                text = "도보 경로 불러오는 중…",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
        }

        is FollowRouteStatus.Ready -> {
            if (status.route.walkable) {
                WalkingRouteSummary(route = status.route, destName = destName)
            }
        }

        null, FollowRouteStatus.Failed -> {
            Unit
        }
    }
}

/** 걸을 수 있는 구간: "지금 스팟까지 · 거리 · 도보 N분" 요약 + 접는 턴바이턴("가는 길 설명"). */
@Composable
private fun WalkingRouteSummary(
    route: com.chillsam.courmy.course.entity.TraceRouteVO,
    destName: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var guidesOpen by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val summary =
            listOfNotNull(
                destName.takeIf { it.isNotBlank() }?.let { "$it 까지" },
                routeDistanceText(route.distanceMeters),
                route.durationMinutes?.let { "도보 ${it}분" },
            ).joinToString(" · ")
        DsText(
            text = summary.ifBlank { "상세 도보 경로" },
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        if (route.guides.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { guidesOpen = !guidesOpen }
                        .padding(vertical = 2.dp),
            ) {
                DsText(
                    text = if (guidesOpen) "가는 길 설명 접기" else "가는 길 설명",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentAccent,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_down_24),
                    contentDescription = null,
                    tint = color.contentAccent,
                    modifier = Modifier.size(14.dp).rotate(if (guidesOpen) 180f else 0f),
                )
            }
            if (guidesOpen) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(color.bgDefaultLevel0)
                            .heightIn(max = ROUTE_GUIDE_MAX_HEIGHT)
                            .verticalScroll(rememberScrollState())
                            .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    route.guides.forEachIndexed { index, guide ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            // 순서 번호를 작은 원에 담아 "몇 번째 안내"인지 한눈에 보이게 한다.
                            Box(
                                modifier =
                                    Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(color.bgAccentSubtle),
                                contentAlignment = Alignment.Center,
                            ) {
                                DsText(
                                    text = "${index + 1}",
                                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                                    color = color.contentAccent,
                                )
                            }
                            DsText(
                                text = guide.description,
                                style = DesignSystemThemeImpl.typeScale.textRegularS,
                                color = color.contentDefaultLevel1,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f).padding(top = 1.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 도보 거리 표기: 1km 미만은 "320m", 이상은 "1.2km". null 이면 표기하지 않는다. */
private fun routeDistanceText(meters: Int?): String? {
    if (meters == null) return null
    return if (meters < 1_000) "${meters}m" else String.format(Locale.US, "%.1fkm", meters / 1000.0)
}

/** 동선 목록 한 줄: 순번 배지 + 썸네일 + 이름·카테고리 + 상태. 진행 중 현재 장소는 테두리로 강조. */
@Composable
private fun RoutePlaceRow(
    place: CourseDetailPlaceVO,
    stateText: String,
    highlighted: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .then(
                    if (highlighted) {
                        Modifier
                            .border(1.dp, color.contentAccent, shape)
                            .background(color.bgDefaultLevel1)
                    } else {
                        Modifier.background(color.bgDefaultLevel0)
                    },
                ).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (highlighted) color.bgAccent else color.bgAccentSubtle),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = place.order.toString(),
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = if (highlighted) color.contentOnAccent else color.contentAccent,
            )
        }
        CourseImage(
            url = place.imageUrls.firstOrNull().orEmpty(),
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            DsText(
                text = listOf(place.category, stateText).filter { it.isNotBlank() }.joinToString(" · "),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * 완주 화면의 "다녀온 코스" 타임라인. 방문한 전체 스팟을 순서대로 세우고
 * 배지 사이를 세로선으로 이어, 코스를 끝까지 걸은 여정을 한눈에 돌아보게 한다.
 */
@Composable
private fun CompletedCourseTimeline(places: List<CourseDetailPlaceVO>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        places.forEachIndexed { index, place ->
            CompletedSpotRow(
                place = place,
                isFirst = index == 0,
                isLast = index == places.lastIndex,
            )
        }
    }
}

/** 타임라인 한 줄: 연결선 레일(순번 배지) + 썸네일 + 이름·카테고리 + "방문 완료". */
@Composable
private fun CompletedSpotRow(
    place: CourseDetailPlaceVO,
    isFirst: Boolean,
    isLast: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // 배지 + 연결선 레일. 위/아래 반쪽 선이 이웃 행과 맞닿아 끊김 없는 타임라인이 된다.
        Box(
            modifier =
                Modifier
                    .width(24.dp)
                    .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            if (!isFirst) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .width(2.dp)
                            .fillMaxHeight(0.5f)
                            .background(color.bgAccentSubtle),
                )
            }
            if (!isLast) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .width(2.dp)
                            .fillMaxHeight(0.5f)
                            .background(color.bgAccentSubtle),
                )
            }
            Box(
                modifier =
                    Modifier
                        .size(23.dp)
                        .clip(CircleShape)
                        .background(color.bgAccent),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = place.order.toString(),
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentOnAccent,
                )
            }
        }
        CourseImage(
            url = place.imageUrls.firstOrNull().orEmpty(),
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (place.category.isNotBlank()) {
                DsText(
                    text = place.category,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check_24),
                contentDescription = null,
                tint = color.contentAccent,
                modifier = Modifier.size(14.dp),
            )
            DsText(
                text = "방문 완료",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
            )
        }
    }
}

/** 완주 통계 카드 한 칸. 셋이 나란히 서므로 폭을 똑같이 나눈다. */
@Composable
private fun RowScope.FollowStatCard(
    value: String,
    label: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .background(color.bgDefaultLevel1)
                .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        DsText(
            text = value,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel3,
        )
    }
}

/** 각 단계의 주 버튼. 하단 액션바 주 버튼과 같은 결(강조 배경)이다. 폭은 [modifier] 로 정한다. */
@Composable
private fun FollowPrimaryButton(
    text: String,
    iconRes: Int?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .height(52.dp)
                .clip(FOLLOW_BUTTON_SHAPE)
                .background(color.bgAccent)
                .clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = color.contentOnAccent,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(7.dp))
        }
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentOnAccent,
        )
    }
}

/** 진행 중 "도착" 옆의 정사각형 종료 버튼. 눌러도 바로 나가지 않고 확인 모달을 한 번 더 띄운다. */
@Composable
private fun FollowEndButton(onClick: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(52.dp)
                .clip(FOLLOW_BUTTON_SHAPE)
                .background(color.bgDefaultLevel0)
                .border(1.dp, color.borderDefaultLevel1, FOLLOW_BUTTON_SHAPE)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = "종료",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel1,
        )
    }
}

/** 소요 시간 표시값. 1시간 미만은 "N분", 이상은 "H:MM". */
private fun elapsedText(state: CourseFollowUIState): String {
    val start = state.startedAtMillis ?: return "-"
    val end = state.finishedAtMillis ?: return "-"
    val minutes = ((end - start) / 60_000L).coerceAtLeast(0)
    return if (minutes < 60) "${minutes}분" else String.format(Locale.US, "%d:%02d", minutes / 60, minutes % 60)
}

/**
 * 이동 거리 표시값: 장소 좌표를 순서대로 이은 직선거리(Haversine) 합.
 * 실제 보행 경로보다 짧게 나오지만, 서버가 경로 거리를 주기 전까지의 근사치다.
 * TODO-API-SPEC: trace 계약에 이동 거리가 생기면 서버 값으로 바꾼다.
 */
private fun distanceText(places: List<CourseDetailPlaceVO>): String {
    val coords = places.mapNotNull { it.latLngOrNull() }
    if (coords.size < 2) return "-"
    var meters = 0.0
    for (i in 1 until coords.size) {
        meters +=
            haversineMeters(coords[i - 1].latitude, coords[i - 1].longitude, coords[i].latitude, coords[i].longitude)
    }
    return String.format(Locale.US, "%.1fkm", meters / 1000.0)
}

private fun haversineMeters(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double,
): Double {
    val r = 6_371_000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a =
        sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    return 2 * r * atan2(sqrt(a), sqrt(1 - a))
}
