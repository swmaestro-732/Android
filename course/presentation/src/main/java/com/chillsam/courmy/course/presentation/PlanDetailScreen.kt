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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsDialogScaffold
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.translation.rememberTaxonomyLabel
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.PlanDetailVO
import com.chillsam.courmy.course.presentation.component.PlaceDetailSheet
import com.chillsam.courmy.course.presentation.component.PlaceSheetCourse
import com.chillsam.courmy.course.presentation.component.PlannedDateField
import com.chillsam.courmy.course.presentation.R as CourseR

@Composable
internal fun PlanDetailScreen(
    detail: PlanDetailVO,
    onBack: () -> Unit,
    onSourceCourse: (Long) -> Unit,
    onEdit: (title: String, memo: String, plannedDate: String) -> Unit,
    onDelete: () -> Unit,
    isMutating: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val places = remember(detail) { detail.toDisplayPlaces() }
    var selectedPlace by remember(detail.planId) { mutableStateOf<CourseDetailPlaceVO?>(null) }
    var openSheetInOverview by remember(detail.planId) { mutableStateOf(false) }
    var editing by remember(detail.planId) { mutableStateOf(false) }
    var confirmingDelete by remember(detail.planId) { mutableStateOf(false) }
    Column(modifier.fillMaxSize().background(color.bgDefaultLevel0).verticalScroll(rememberScrollState())) {
        PlanHeader(
            onBack = onBack,
            isMutating = isMutating,
            onEdit = { editing = true },
            onDelete = { confirmingDelete = true },
        )
        Column(
            Modifier.padding(horizontal = 20.dp, vertical = 24.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            detail.sourceCourseId?.let { sourceId -> PlanSourceRow { onSourceCourse(sourceId) } }
            if (detail.memo.isNotBlank()) PlanMemo(detail.memo)
            if (detail.sourceCourseId != null || detail.memo.isNotBlank()) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(color.borderDefaultLevel0))
            }
            PlacesSection(
                places = places,
                authorName = "",
                title = stringResource(CourseR.string.course_plan_detail_places_title),
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
                onOpenDetail = {
                    openSheetInOverview = true
                    selectedPlace = it
                },
                title = stringResource(CourseR.string.course_plan_detail_map_title),
            )
        }
    }
    selectedPlace?.let { place ->
        PlanPlaceDetail(
            place = place,
            course =
                PlaceSheetCourse(
                    title = detail.title,
                    category =
                        rememberTaxonomyLabel(
                            places
                                .flatMap { it.category.split(" · ") }
                                .filter { it.isNotBlank() }
                                .distinct()
                                .joinToString(" · "),
                        ),
                    summary =
                        listOf(
                            detail.plannedDate,
                            stringResource(CourseR.string.course_plan_detail_place_count, places.size),
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

    if (editing) {
        EditPlanDialog(
            initialTitle = detail.title,
            initialMemo = detail.memo,
            initialDate = detail.plannedDate,
            isSaving = isMutating,
            onSave = { title, memo, date ->
                editing = false
                onEdit(title, memo, date)
            },
            onDismiss = { editing = false },
        )
    }
    if (confirmingDelete) {
        DsConfirmDialog(
            title = stringResource(CourseR.string.course_plan_detail_delete_title),
            description = stringResource(CourseR.string.course_plan_detail_delete_description),
            confirmText = stringResource(CourseR.string.course_plan_detail_delete_confirm),
            dismissText = stringResource(CourseR.string.course_plan_detail_delete_cancel),
            destructive = true,
            onConfirm = {
                confirmingDelete = false
                onDelete()
            },
            onDismiss = { confirmingDelete = false },
        )
    }
}

/**
 * 계획 수정 다이얼로그 — 제목·메모·방문 날짜만 고친다.
 * 장소 구성 변경은 작성 화면의 계획 편집 모드가 생기면 그쪽이 맡는다.
 */
@Composable
private fun EditPlanDialog(
    initialTitle: String,
    initialMemo: String,
    initialDate: String,
    isSaving: Boolean,
    onSave: (title: String, memo: String, plannedDate: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf(initialTitle) }
    var memo by remember { mutableStateOf(initialMemo) }
    var date by remember { mutableStateOf(initialDate) }
    DsDialogScaffold(
        title = stringResource(CourseR.string.course_plan_detail_edit_title),
        description = stringResource(CourseR.string.course_plan_detail_edit_description),
        onDismiss = onDismiss,
    ) {
        DsTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = stringResource(CourseR.string.course_plan_detail_edit_title_placeholder),
        )
        DsTextField(
            value = memo,
            onValueChange = { memo = it },
            placeholder = stringResource(CourseR.string.course_plan_detail_edit_memo_placeholder),
        )
        DsText(
            text = stringResource(CourseR.string.course_plan_detail_edit_date_label),
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
        PlannedDateField(plannedDate = date, onDateChange = { date = it })
        DsButton(
            text = stringResource(CourseR.string.course_plan_detail_edit_save),
            enabled = title.isNotBlank() && !isSaving,
            loading = isSaving,
            onClick = { onSave(title, memo, date) },
            modifier = Modifier.fillMaxWidth(),
        )
        DsButton(
            text = stringResource(CourseR.string.course_plan_detail_edit_cancel),
            variant = DsButtonVariant.Secondary,
            enabled = !isSaving,
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
internal fun PlanHeader(
    onBack: () -> Unit,
    isMutating: Boolean = false,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painterResource(R.drawable.arrow_back_24px),
                contentDescription = stringResource(CourseR.string.course_plan_detail_back),
                tint = color.contentDefaultLevel0,
            )
        }
        DsText(
            stringResource(CourseR.string.course_plan_detail_title),
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        // 계획은 항상 내 것이라(비공개) 코스 상세와 달리 소유 분기 없이 메뉴를 노출한다.
        if (onEdit != null && onDelete != null) {
            PlanSettingsMenu(isMutating = isMutating, onEdit = onEdit, onDelete = onDelete)
        }
    }
}

/** 헤더 우측 설정(⋮) 메뉴 — 수정·삭제. */
@Composable
private fun PlanSettingsMenu(
    isMutating: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painterResource(R.drawable.ic_more_vert_24),
                contentDescription = stringResource(CourseR.string.course_plan_detail_settings),
                tint = color.contentDefaultLevel0,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = color.bgDefaultLevel1,
        ) {
            DropdownMenuItem(
                text = {
                    DsText(
                        stringResource(CourseR.string.course_plan_detail_menu_edit),
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel0,
                    )
                },
                enabled = !isMutating,
                onClick = {
                    expanded = false
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = {
                    DsText(
                        if (isMutating) {
                            stringResource(CourseR.string.course_plan_detail_menu_processing)
                        } else {
                            stringResource(CourseR.string.course_plan_detail_menu_delete)
                        },
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDanger,
                    )
                },
                enabled = !isMutating,
                onClick = {
                    expanded = false
                    onDelete()
                },
            )
        }
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
            DsText(
                stringResource(CourseR.string.course_plan_detail_source_label),
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentAccent,
            )
            DsText(
                stringResource(CourseR.string.course_plan_detail_source_view_origin),
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
        DsText(
            stringResource(CourseR.string.course_plan_detail_memo_title),
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
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
                detailCategory = state.place?.detailCategory ?: place.detailCategory,
                latitude = state.place?.latitude ?: place.latitude,
                longitude = state.place?.longitude ?: place.longitude,
            ),
        address = state.place?.address?.takeIf { it.isNotBlank() } ?: initialAddress,
        openingHours = state.place?.openingHours.orEmpty(),
        reviews = state.reviews,
        reviewCategories = state.place?.categories.orEmpty(),
        onReviewWritten = viewModel::refreshReviews,
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
            detailCategory = place.detailCategory,
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
