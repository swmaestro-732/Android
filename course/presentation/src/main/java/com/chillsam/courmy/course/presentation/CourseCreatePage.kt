package com.chillsam.courmy.course.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
import com.chillsam.courmy.course.presentation.component.CourseTagCard
import com.chillsam.courmy.course.presentation.component.CourseTopBar
import com.chillsam.courmy.course.presentation.component.CourseVisibilitySegment
import com.chillsam.courmy.course.presentation.component.PlaceSearchOverlay
import com.chillsam.courmy.course.presentation.component.dashedBorder
import kotlinx.coroutines.isActive

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
    val scrollState = rememberScrollState()
    // 스크롤 뷰포트의 화면(window) 상단 y·높이(px). 장소 드래그 시 가장자리 자동 스크롤 판정에 쓴다.
    var viewportTopPx by remember { mutableStateOf(0f) }
    var viewportHeightPx by remember { mutableStateOf(0) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0),
    ) {
        // 로딩 화면
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = DesignSystemThemeImpl.designSystemColor.contentAccent,
            )
            return@Box
        }

        // 전반적인 화면
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .onGloballyPositioned {
                        viewportTopPx = it.positionInWindow().y
                        viewportHeightPx = it.size.height
                    }.verticalScroll(scrollState)
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
                scrollState = scrollState,
                viewportTopPx = viewportTopPx,
                viewportHeightPx = viewportHeightPx,
            )

            Spacer(Modifier.height(22.dp))

            VisibilitySection(uiState = uiState, onIntent = onIntent)
        }

        SaveBar(
            enabled = uiState.canSave,
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
        thumbnailPhotos = uiState.thumbnailPhotos,
        thumbnailMaxPhotos = CourseCreateUIState.MAX_THUMBNAIL_PHOTOS,
        actions =
            CourseInfoCardActions(
                onNameChange = { onIntent(CourseCreateIntent.ChangeName(it)) },
                onDescriptionChange = { onIntent(CourseCreateIntent.ChangeDescription(it)) },
                onThumbnailPhotosChange = { onIntent(CourseCreateIntent.ChangeThumbnailPhotos(it)) },
            ),
    )
}

// 드래그 재정렬(제스처 콜백 + 자동 스크롤 + make-room) 핸들러라 분기가 많다. UI 이벤트 처리 복잡도.
@Suppress("CyclomaticComplexMethod")
@Composable
private fun PlaceSection(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
    onAddPlace: () -> Unit,
    onRequestRemove: (CoursePlaceVO) -> Unit,
    scrollState: ScrollState,
    viewportTopPx: Float,
    viewportHeightPx: Int,
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

    val places = uiState.places
    val placesState = rememberUpdatedState(places)
    // 슬롯(카드+커넥터) 실측: 화면(window) 상단 y·높이(px). 스크롤에도 값이 갱신돼 좌표가 강건하다.
    val slotTops = remember { mutableStateMapOf<Int, Float>() }
    val slotHeights = remember { mutableStateMapOf<Int, Int>() }
    // 드래그 중엔 리스트를 건드리지 않고(스냅 방지), 놓을 때 [dragTarget] 으로 한 번 커밋한다.
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(0f) }
    var dragTarget by remember { mutableStateOf(-1) }
    var autoScrollSpeed by remember { mutableStateOf(0f) }

    // 가장자리 자동 스크롤: edge zone 에 머무는 동안 프레임마다 스크롤한다.
    LaunchedEffect(autoScrollSpeed) {
        if (autoScrollSpeed != 0f) {
            while (isActive) {
                val consumed = scrollState.scrollBy(autoScrollSpeed)
                if (consumed == 0f) break
                // 스크롤한 만큼 dragOffset 을 더해, 손가락이 멈춰 있어도 카드가 화면상 같은
                // 위치(손가락 아래)에 머물게 한다. 없으면 카드만 콘텐츠와 함께 밀려 gap 이 생긴다.
                dragOffset += consumed
                dragTarget =
                    computeDragTarget(slotTops, slotHeights, draggedIndex, dragOffset, placesState.value.size)
                withFrameNanos { }
            }
        }
    }

    places.forEachIndexed { index, place ->
        val dragging = draggedIndex
        val isDragged = dragging == index
        val draggedHeight = (slotHeights[dragging] ?: 0).toFloat()
        // 드래그 대상이 낄 자리를 비켜주는 이동량(카드 한 칸 높이). 애니메이션으로 부드럽게.
        val makeRoom =
            when {
                dragging == null || dragTarget < 0 || isDragged -> 0f
                dragging < dragTarget && index in (dragging + 1)..dragTarget -> -draggedHeight
                dragging > dragTarget && index in dragTarget until dragging -> draggedHeight
                else -> 0f
            }
        // 드래그 중엔 스프링으로 부드럽게 자리를 비켜주고, 놓는 순간엔 snap 으로 즉시 0 이 되게 해
        // 리스트 재정렬(MovePlace)과 애니메이션이 겹쳐 튀는 것을 막는다.
        val animMakeRoom by animateFloatAsState(
            targetValue = makeRoom,
            animationSpec = if (dragging != null) spring() else snap(),
            label = "makeRoom",
        )
        val translationYValue = if (isDragged) dragOffset else animMakeRoom
        // 집어 올린 느낌: 살짝 확대. 놓으면 스프링으로 원래 크기로 정착.
        val liftScale by animateFloatAsState(targetValue = if (isDragged) 1.03f else 1f, label = "lift")

        Column(
            modifier =
                Modifier
                    .zIndex(if (isDragged) 1f else 0f)
                    .graphicsLayer {
                        translationY = translationYValue
                        scaleX = liftScale
                        scaleY = liftScale
                    }.onGloballyPositioned {
                        slotTops[index] = it.positionInWindow().y
                        slotHeights[index] = it.size.height
                    },
        ) {
            CoursePlaceCard(
                place = place,
                onRemove = { onRequestRemove(place) },
                onNoteChange = { onIntent(CourseCreateIntent.ChangePlaceNote(place.id, it)) },
                onPhotosChange = { onIntent(CourseCreateIntent.ChangePlacePhotos(place.id, it)) },
                dragHandleModifier =
                    Modifier.pointerInput(place.id) {
                        detectDragGestures(
                            onDragStart = {
                                val cur = placesState.value.indexOfFirst { it.id == place.id }
                                draggedIndex = cur
                                dragOffset = 0f
                                dragTarget = cur
                            },
                            onDrag = { change, amount ->
                                change.consume()
                                dragOffset += amount.y
                                val from = draggedIndex ?: return@detectDragGestures
                                dragTarget =
                                    computeDragTarget(slotTops, slotHeights, from, dragOffset, placesState.value.size)
                                val screenTop = (slotTops[from] ?: 0f) + dragOffset
                                val screenBottom = screenTop + (slotHeights[from] ?: 0)
                                autoScrollSpeed =
                                    when {
                                        screenTop < viewportTopPx + DRAG_EDGE_PX && scrollState.value > 0 -> {
                                            -DRAG_SCROLL_PX
                                        }

                                        screenBottom > viewportTopPx + viewportHeightPx - DRAG_EDGE_PX &&
                                            scrollState.value < scrollState.maxValue -> {
                                            DRAG_SCROLL_PX
                                        }

                                        else -> {
                                            0f
                                        }
                                    }
                            },
                            onDragEnd = {
                                val from = draggedIndex
                                if (from != null && dragTarget >= 0 && dragTarget != from) {
                                    onIntent(CourseCreateIntent.MovePlace(from, dragTarget))
                                }
                                draggedIndex = null
                                dragOffset = 0f
                                dragTarget = -1
                                autoScrollSpeed = 0f
                            },
                            onDragCancel = {
                                draggedIndex = null
                                dragOffset = 0f
                                dragTarget = -1
                                autoScrollSpeed = 0f
                            },
                        )
                    },
            )
            if (place.walkText.isNotEmpty()) {
                CourseRouteConnector(text = place.walkText)
            } else {
                Spacer(Modifier.height(8.dp))
            }
        }
    }
    Spacer(Modifier.height(2.dp))
    AddPlaceButton(onClick = onAddPlace)
}

/** 드래그 중 카드 중심([from] 슬롯 top + [dragOffset])이 넘어선 다른 슬롯 수 = 착지 인덱스. */
private fun computeDragTarget(
    slotTops: Map<Int, Float>,
    slotHeights: Map<Int, Int>,
    from: Int?,
    dragOffset: Float,
    count: Int,
): Int {
    if (from == null || from !in 0 until count) return -1
    val draggedCenter = (slotTops[from] ?: 0f) + dragOffset + (slotHeights[from] ?: 0) / 2f
    var target = 0
    for (i in 0 until count) {
        if (i == from) continue
        val center = (slotTops[i] ?: 0f) + (slotHeights[i] ?: 0) / 2f
        if (center < draggedCenter) target++
    }
    return target
}

private const val DRAG_EDGE_PX = 120f
private const val DRAG_SCROLL_PX = 18f

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
    CourseSectionHeader(number = 3, title = "코스 설정")
    Spacer(Modifier.height(10.dp))
    CourseTagCard(
        tags = uiState.tags,
        suggestedTags = uiState.suggestedTags,
        onAddTag = { onIntent(CourseCreateIntent.AddTag(it)) },
        onRemoveTag = { onIntent(CourseCreateIntent.RemoveTag(it)) },
    )
    Spacer(Modifier.height(12.dp))
    CourseVisibilitySegment(
        selected = uiState.visibility,
        onSelect = { onIntent(CourseCreateIntent.ChangeVisibility(it)) },
    )
}

@Composable
private fun SaveBar(
    enabled: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        DsButton(text = "코스 저장하기", enabled = enabled, onClick = onSave)
    }
}
