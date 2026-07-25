package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseCompleteVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CourseStopVO
import com.chillsam.courmy.course.presentation.component.CourseInfoCard
import com.chillsam.courmy.course.presentation.component.CourseInfoCardActions
import com.chillsam.courmy.course.presentation.component.CoursePlaceCard
import com.chillsam.courmy.course.presentation.component.CourseRouteConnector
import com.chillsam.courmy.course.presentation.component.CourseSectionHeader
import com.chillsam.courmy.course.presentation.component.CourseTopBar
import com.chillsam.courmy.course.presentation.component.CourseVisibilitySegment
import com.chillsam.courmy.course.presentation.component.PlaceSearchOverlay
import com.chillsam.courmy.course.presentation.component.dashedBorder
import kotlinx.coroutines.delay

/**
 * 코스 만들기 화면.
 *
 * 상단바·저장바의 네비게이션([onClose]·[onSaveDraft]·[onSaveCourse])은 대상 화면이 다른 모듈(main)에
 * 있어 course 모듈이 직접 참조할 수 없으므로, 호출부([AppRouteRegistry])에서 콜백으로 주입한다.
 */
@Composable
fun CourseCreatePage(
    viewModel: CourseCreateViewModel,
    onClose: () -> Unit,
    onSaveDraft: (String) -> Unit,
    onSaveCourse: (CourseCompleteVO?) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CourseCreateContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onClose = onClose,
        onSaveDraft = onSaveDraft,
        onSaveCourse = onSaveCourse,
    )
}

@Composable
private fun CourseCreateContent(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
    onClose: () -> Unit,
    onSaveDraft: (String) -> Unit,
    onSaveCourse: (CourseCompleteVO?) -> Unit,
) {
    var showPlaceSearch by remember { mutableStateOf(false) }
    var placeToRemove by remember { mutableStateOf<CoursePlaceVO?>(null) }
    var showExitConfirm by remember { mutableStateOf(false) }
    var showSavedToast by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = DesignSystemThemeImpl.designSystemColor.contentAccent,
            )
            return@Box
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 116.dp),
        ) {
            CourseTopBar(
                onClose = { if (uiState.hasContent) showExitConfirm = true else onClose() },
                onSaveDraft = {
                    onSaveDraft(uiState.draftTitle())
                    showSavedToast = true
                },
            )
            Spacer(Modifier.height(20.dp))
            InfoSection(uiState = uiState, onIntent = onIntent)
            Spacer(Modifier.height(22.dp))
            PlaceSection(
                uiState = uiState,
                onIntent = onIntent,
                onAddPlace = { showPlaceSearch = true },
                onRequestRemove = { placeToRemove = it },
            )
            Spacer(Modifier.height(22.dp))
            VisibilitySection(uiState = uiState, onIntent = onIntent)
        }

        SaveBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onSave = { onSaveCourse(uiState.toCompleteVO()) },
        )

        SavedDraftToast(
            visible = showSavedToast,
            onHidden = { showSavedToast = false },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (showPlaceSearch) {
            PlaceSearchOverlay(
                onDismiss = { showPlaceSearch = false },
                onConfirm = { picked ->
                    onIntent(CourseCreateIntent.AddPlaces(picked))
                    showPlaceSearch = false
                },
            )
        }

        placeToRemove?.let { place ->
            RemovePlaceConfirmSheet(
                placeName = place.name,
                onConfirm = {
                    onIntent(CourseCreateIntent.RemovePlace(place.id))
                    placeToRemove = null
                },
                onDismiss = { placeToRemove = null },
            )
        }

        if (showExitConfirm) {
            ExitConfirmDialog(
                onSaveAndExit = {
                    onSaveDraft(uiState.draftTitle())
                    showExitConfirm = false
                    onClose()
                },
                onDiscard = {
                    showExitConfirm = false
                    onClose()
                },
                onCancel = { showExitConfirm = false },
            )
        }
    }
}

/** 미저장 변경(입력한 값)이 있는지. ✕ 로 나갈 때 확인 다이얼로그를 띄울지 판단한다. */
private val CourseCreateUIState.hasContent: Boolean
    get() = name.isNotBlank() || description.isNotBlank() || tags.isNotEmpty() || places.isNotEmpty()

/** 임시저장 목록에 노출할 제목. 이름이 비면 기본값. */
private fun CourseCreateUIState.draftTitle(): String = name.ifBlank { "제목 없는 코스" }

/**
 * 입력값 → 코스 완성(FS-34-Done) 데이터 매핑. 이름/장소가 비어 있으면 null 을 반환해
 * 호출부가 예시 데이터로 폴백하게 한다. 장소 체류 시간은 작성 화면에 없어 비워 둔다.
 */
private fun CourseCreateUIState.toCompleteVO(): CourseCompleteVO? {
    if (name.isBlank() && places.isEmpty()) return null
    return CourseCompleteVO(
        category = tags.take(2).joinToString(" · ").ifBlank { "코스" },
        title = name.ifBlank { "새 코스" },
        summaryText = "${places.size} 스팟",
        stops =
            places.mapIndexed { index, place ->
                CourseStopVO(
                    order = index + 1,
                    name = place.name,
                    category = place.category,
                    durationText = "",
                )
            },
    )
}

@Composable
private fun InfoSection(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
) {
    CourseSectionHeader(number = 1, title = "코스 정보")
    Spacer(Modifier.height(10.dp))
    CourseInfoCard(
        name = uiState.name,
        description = uiState.description,
        tags = uiState.tags,
        suggestedTags = uiState.suggestedTags,
        actions =
            CourseInfoCardActions(
                onNameChange = { onIntent(CourseCreateIntent.ChangeName(it)) },
                onDescriptionChange = { onIntent(CourseCreateIntent.ChangeDescription(it)) },
                onRemoveTag = { onIntent(CourseCreateIntent.RemoveTag(it)) },
                onAddTag = { onIntent(CourseCreateIntent.AddTag(it)) },
            ),
    )
}

@Composable
private fun PlaceSection(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
    onAddPlace: () -> Unit,
    onRequestRemove: (CoursePlaceVO) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CourseSectionHeader(number = 2, title = "장소 담기", modifier = Modifier.weight(1f))
        DsText(
            text = "${uiState.places.size}곳 · 드래그로 순서 변경",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
        )
    }
    Spacer(Modifier.height(12.dp))
    uiState.places.forEach { place ->
        CoursePlaceCard(
            place = place,
            onRemove = { onRequestRemove(place) },
            onNoteChange = { onIntent(CourseCreateIntent.ChangePlaceNote(place.id, it)) },
            onPhotosChange = { onIntent(CourseCreateIntent.ChangePlacePhotos(place.id, it)) },
        )
        if (place.walkText.isNotEmpty()) {
            CourseRouteConnector(text = place.walkText)
        } else {
            Spacer(Modifier.height(8.dp))
        }
    }
    Spacer(Modifier.height(2.dp))
    AddPlaceButton(onClick = onAddPlace)
}

@Composable
private fun AddPlaceButton(onClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .dashedBorder(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0, cornerRadius = 16.dp)
                .clickable(onClick = onClick)
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = "＋ 장소 더 담기",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
        )
    }
}

@Composable
private fun VisibilitySection(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
) {
    CourseSectionHeader(number = 3, title = "공개 설정")
    Spacer(Modifier.height(11.dp))
    CourseVisibilitySegment(
        selected = uiState.visibility,
        onSelect = { onIntent(CourseCreateIntent.ChangeVisibility(it)) },
    )
}

@Composable
private fun SaveBar(
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        DsButton(text = "코스 저장하기", onClick = onSave)
    }
}
