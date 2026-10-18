package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.PlanDetailVO
import com.chillsam.courmy.course.presentation.component.PlaceDetailSheet
import com.chillsam.courmy.course.presentation.component.PlaceSheetCourse

@Composable
internal fun PlanDetailScreen(
    detail: PlanDetailVO,
    onBack: () -> Unit,
    onSourceCourse: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val places = remember(detail) { detail.toDisplayPlaces() }
    var selectedPlace by remember(detail.planId) { mutableStateOf<CourseDetailPlaceVO?>(null) }
    var openSheetInOverview by remember(detail.planId) { mutableStateOf(false) }
    Column(modifier.fillMaxSize().background(color.bgDefaultLevel0).verticalScroll(rememberScrollState())) {
        PlanHeader(onBack)
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 24.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            PlanSummary(detail)
            detail.sourceCourseId?.let { sourceId -> PlanSourceRow { onSourceCourse(sourceId) } }
            if (detail.memo.isNotBlank()) PlanMemo(detail.memo)
            Box(Modifier.fillMaxWidth().height(1.dp).background(color.borderDefaultLevel0))
            PlacesSection(
                places = places,
                authorName = "",
                title = "계획 속 장소",
                onPlaceClick = { selectedPlace = it },
                expandedPlaceContent = { place ->
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CompactPlaceRow(place = place, onClick = { selectedPlace = place })
                        PlanPlaceNotes(detail, place.order - 1)
                    }
                },
            )
            CourseMapSection(
                places = places,
                onPlaceClick = { selectedPlace = it },
                onOpenDetail = {
                    openSheetInOverview = true
                    selectedPlace = it
                },
                title = "계획 한눈에 보기",
            )
        }
    }
    selectedPlace?.let { place ->
        PlanPlaceDetail(
            place = place,
            course =
                PlaceSheetCourse(
                    title = detail.title,
                    category = "나의 계획",
                    summary =
                        listOf(
                            detail.plannedDate,
                            "${places.size}곳",
                        ).filter { it.isNotBlank() }.joinToString(" · "),
                    authorName = "",
                    places = places,
                ),
            startInOverview = openSheetInOverview,
            initialAddress =
                detail.places
                    .firstOrNull { it.placeId == place.placeId }
                    ?.address
                    .orEmpty(),
            onDismiss = {
                selectedPlace = null
                openSheetInOverview = false
            },
            onSelectPlace = { selectedPlace = it },
        )
    }
}

@Composable
private fun PlanHeader(onBack: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painterResource(R.drawable.arrow_back_24px),
                contentDescription = "뒤로",
                tint = color.contentDefaultLevel0,
            )
        }
        DsText("계획 상세", style = DesignSystemThemeImpl.typeScale.textStrongM, color = color.contentDefaultLevel0)
    }
}

@Composable
private fun PlanSummary(detail: PlanDetailVO) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        PlanStat(R.drawable.ic_calendar_24, "방문 날짜", detail.plannedDate.ifBlank { "아직 정하지 않았어요" }, Modifier.weight(1f))
        PlanStat(R.drawable.ic_location_24, "담은 장소", "${detail.places.size}곳", Modifier.weight(1f))
    }
}

@Composable
private fun PlanStat(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                tint = color.contentAccent,
                modifier = Modifier.size(16.dp),
            )
            DsText(label, style = DesignSystemThemeImpl.typeScale.textRegularXS, color = color.contentDefaultLevel2)
        }
        DsText(
            value,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
            maxLines = 2,
        )
    }
}

@Composable
private fun PlanSourceRow(onClick: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                color.bgAccentSubtle,
                RoundedCornerShape(12.dp),
            ).clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            DsText("코스에서 복제한 계획", style = DesignSystemThemeImpl.typeScale.textStrongS, color = color.contentAccent)
            DsText(
                "원본 코스 보기",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        Icon(
            painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            tint = color.contentAccent,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun PlanMemo(memo: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DsText("계획 메모", style = DesignSystemThemeImpl.typeScale.textStrongM, color = color.contentDefaultLevel0)
        DsText(
            memo,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel1,
            maxLines = Int.MAX_VALUE,
        )
    }
}

@Composable
private fun PlanPlaceNotes(
    detail: PlanDetailVO,
    index: Int,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val place = detail.places[index]
    Column(Modifier.padding(start = 36.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        place.address.takeIf { it.isNotBlank() }?.let {
            DsText(
                it,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                maxLines = Int.MAX_VALUE,
            )
        }
        if (place.memo.isNotBlank()) {
            DsText(
                place.memo,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel1,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color.bgDefaultLevel1,
                            RoundedCornerShape(8.dp),
                        ).padding(12.dp),
                maxLines = Int.MAX_VALUE,
            )
        }
    }
}

@Composable
private fun PlanPlaceDetail(
    place: CourseDetailPlaceVO,
    course: PlaceSheetCourse,
    startInOverview: Boolean,
    initialAddress: String,
    onDismiss: () -> Unit,
    onSelectPlace: (CourseDetailPlaceVO) -> Unit,
    viewModel: PlaceDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    DisposableEffect(place.placeId) {
        viewModel.open(place.placeId)
        onDispose { viewModel.clear() }
    }
    PlaceDetailSheet(
        course = course,
        startInOverview = startInOverview,
        coursePlace =
            place.copy(
                name = state.place?.name?.takeIf { it.isNotBlank() } ?: place.name,
                category = state.place?.category?.takeIf { it.isNotBlank() } ?: place.category,
                latitude = state.place?.latitude ?: place.latitude,
                longitude = state.place?.longitude ?: place.longitude,
            ),
        address = state.place?.address?.takeIf { it.isNotBlank() } ?: initialAddress,
        reviews = state.reviews,
        isSaved = state.isSaved,
        onToggleSave = viewModel::toggleSave,
        onDismiss = onDismiss,
        onSelectPlace = onSelectPlace,
        onDeleteReview = viewModel::deleteReview,
    )
}

private fun PlanDetailVO.toDisplayPlaces(): List<CourseDetailPlaceVO> =
    places.mapIndexed { index, place ->
        CourseDetailPlaceVO(
            placeId = place.placeId,
            order = index + 1,
            name = place.name.ifBlank { "장소 정보를 불러오지 못했어요" },
            category = place.categoryLabel,
            photoCountText = "",
            tip = place.memo,
            latitude = place.latitude,
            longitude = place.longitude,
            walkingMinutesToNext = place.walkingMinutesToNext,
            walkToNextText =
                place.walkingMinutesToNext?.takeIf { index < places.lastIndex }?.let {
                    if (it <
                        0
                    ) {
                        "도보 이동 불가"
                    } else {
                        "도보 ${it}분"
                    }
                },
        )
    }

@Preview
@Composable
private fun PlanHeaderPreview() {
    DesignSystemTheme { PlanHeader({}) }
}
