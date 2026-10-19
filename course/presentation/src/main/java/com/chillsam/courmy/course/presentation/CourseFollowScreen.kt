package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.presentation.component.CourseRouteMap
import com.chillsam.courmy.course.presentation.component.latLngOrNull
import com.chillsam.courmy.course.presentation.component.toRoutePoints
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

/**
 * 따라가기 화면(FS-21). [CourseFollowPhase] 에 따라 세 모습을 오간다.
 *
 * - 미리보기: 지도(전체 동선) + 동선 목록 + "이 코스 따라가기"
 * - 진행: 지도(지금 장소) + 자투리 추천 배너 + 다음 스팟 + "도착 · 방문 체크"
 * - 완주: 축하 + 통계(방문 스팟·소요 시간·이동 거리) + "리뷰 남기기"
 *
 * 자투리 추천은 서버 계약이 없어 자리만 잡아 두고 누르면 준비 중임을 알린다.
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
                PreviewPhase(detail = detail, onIntent = onIntent, onBack = onBack)
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
    onIntent: (CourseFollowIntent) -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    FollowTopBar(
        title = detail.title,
        subtitle =
            listOf(detail.placeCountText, detail.walkText)
                .filter { it.isNotBlank() }
                .joinToString(" · "),
        onBack = onBack,
    )
    FollowMap(detail = detail, focusIndex = null, modifier = Modifier.weight(1f))
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
            detail.places.forEach { place ->
                RoutePlaceRow(place = place, stateText = "예정", highlighted = false)
            }
        }
        Spacer(Modifier.height(14.dp))
        FollowPrimaryButton(text = "이 코스 따라가기", iconRes = R.drawable.ic_footprint_24) {
            onIntent(CourseFollowIntent.Start)
        }
    }
}

/** 진행: 지금 장소에 집중한 지도 + 자투리 배너 + 다음 스팟 + 방문 체크. */
@Composable
private fun ColumnScope.TrackingPhase(
    state: CourseFollowUIState,
    detail: CourseDetailVO,
    onIntent: (CourseFollowIntent) -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    FollowTopBar(
        title = "${detail.title} · ${state.visitedCount + 1}/${detail.places.size} 진행 중",
        subtitle = "",
        onBack = onBack,
    )
    FollowMap(detail = detail, focusIndex = state.currentIndex, modifier = Modifier.weight(1f))
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
        SparePickBanner()
        val current = state.currentPlace
        if (current != null) {
            DsText(
                text = "지금 스팟",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
            RoutePlaceRow(
                place = current,
                stateText = current.walkToNextText?.let { "다음까지 $it" } ?: "마지막 스팟",
                highlighted = true,
            )
        }
        FollowPrimaryButton(
            text = if (state.isRecording) "방문 기록 저장 중…" else "도착 · 방문 체크",
            iconRes = R.drawable.ic_check_24,
            enabled = !state.isRecording,
        ) {
            onIntent(CourseFollowIntent.CheckArrival)
        }
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
        Spacer(Modifier.height(16.dp))
        detail.places.lastOrNull()?.let { last ->
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(color.bgDefaultLevel1)
                        .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CourseImage(
                    url = last.imageUrls.firstOrNull().orEmpty(),
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(10.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    DsText(
                        text = "마지막 스팟 · ${last.name}",
                        style = DesignSystemThemeImpl.typeScale.textStrongS,
                        color = color.contentDefaultLevel0,
                    )
                    DsText(
                        text = "방문 완료",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel3,
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        SparePickBanner()
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
        FollowPrimaryButton(text = "리뷰 남기기", iconRes = null, onClick = onLeaveReview)
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

/** 동선 지도. [focusIndex] 가 null 이면 전체 보기, 아니면 그 장소에 집중한다. */
@Composable
private fun FollowMap(
    detail: CourseDetailVO,
    focusIndex: Int?,
    modifier: Modifier = Modifier,
) {
    val points = remember(detail.places) { detail.places.toRoutePoints() }
    val focusPlace = focusIndex?.let { detail.places.getOrNull(it) }
    val focus = focusPlace?.latLngOrNull() ?: points.firstOrNull()?.second
    Box(modifier = modifier.fillMaxWidth()) {
        if (focus != null) {
            CourseRouteMap(
                focus = focus,
                focusOrder = focusPlace?.order ?: 0,
                points = points,
                overview = focusIndex == null,
                onMarkerClick = {},
            )
        }
    }
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
 * 자투리 추천 배너. 서버 계약(trace/place-connection)이 아직 없어 자리만 잡고 준비 중임을 알린다.
 * TODO-API-SPEC: course-track 계약이 생기면 근처 한 곳 추천으로 잇는다.
 */
@Composable
private fun SparePickBanner() {
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color.contentDefaultLevel0)
                .clickable { Toast.makeText(context, "준비 중이에요.", Toast.LENGTH_SHORT).show() }
                .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            DsText(
                text = "아직 시간 여유 있으세요?",
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentOnAccent,
            )
            DsText(
                text = "근처 한 곳 더 추천받기",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.borderDefaultLevel0,
            )
        }
        DsText(
            text = "자투리 추천",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(color.bgDefaultLevel1)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
        )
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

/** 각 단계의 주 버튼. 하단 액션바 주 버튼과 같은 결(강조 배경 + 풀폭)이다. */
@Composable
private fun FollowPrimaryButton(
    text: String,
    iconRes: Int?,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
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
