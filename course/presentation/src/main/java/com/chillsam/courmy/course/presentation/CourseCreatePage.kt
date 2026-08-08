package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.StepProgressBar
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
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
    onSaveDraft: () -> Unit,
    onSaveCourse: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 화면에 들어올 때마다 초안을 새로 읽는다(ViewModel 의 init 이 아니라 여기서 부른다).
    // 나갈 때 이 화면의 백스택 엔트리를 걷어내므로 보통은 ViewModel 자체가 새로 생기지만,
    // 어떤 경로로 엔트리가 살아남더라도 "새로 만들기"가 이전 입력을 되살리지 않도록 보장한다.
    // 이 화면에는 하단 탭바가 없고 화면도 세로 고정이라, 편집 도중 컴포지션만 다시 만들어져
    // 입력이 날아갈 경로는 없다.
    LaunchedEffect(Unit) { viewModel.onIntent(CourseCreateIntent.Load) }

    // 서버 저장이 끝난 뒤에만 완성 화면으로 넘어간다(실패했는데 성공처럼 보이지 않게).
    LaunchedEffect(uiState.savedCourseId) {
        uiState.savedCourseId?.let { courseId ->
            // 사진이 빠진 채 저장됐으면 알려 준다(코스 자체는 만들어졌다).
            if (uiState.imagesMissing) {
                Toast.makeText(context, "사진은 저장되지 않았어요. 편집에서 다시 올려 주세요.", Toast.LENGTH_LONG).show()
            }
            onSaveCourse(courseId)
            // 이동을 끝냈으면 신호를 지운다. 남겨 두면 이 화면이 다시 보일 때 곧바로 또 이동한다.
            viewModel.onIntent(CourseCreateIntent.ConsumeSaved)
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(CourseCreateIntent.ConsumeSaveError)
        }
    }

    CourseCreateContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onClose = onClose,
        onSaveDraft = onSaveDraft,
        onRequestSave = { completed -> viewModel.onIntent(CourseCreateIntent.CompleteCourse(completed)) },
    )
}

@Composable
private fun CourseCreateContent(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
    onClose: () -> Unit,
    onSaveDraft: () -> Unit,
    onRequestSave: (CourseCompleteVO?) -> Unit,
) {
    var showPlaceSearch by remember { mutableStateOf(false) }
    var placeToRemove by remember { mutableStateOf<CoursePlaceVO?>(null) }
    var showExitConfirm by remember { mutableStateOf(false) }
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

        // 단계가 바뀌면 새 내용을 위에서부터 보여준다(이전 단계의 스크롤 위치가 남지 않도록).
        LaunchedEffect(uiState.step) { scrollState.scrollTo(0) }

        // 첫 단계가 아니면 시스템 뒤로가기는 화면을 닫지 않고 이전 단계로 돌아간다.
        BackHandler(enabled = uiState.step > CourseCreateUIState.FIRST_STEP) {
            onIntent(CourseCreateIntent.PrevStep)
        }

        // 전반적인 화면: 상단바·진행바와 하단 버튼은 고정하고 가운데 단계 내용만 스크롤한다.
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            CourseTopBar(
                onClose = { if (uiState.hasContent) showExitConfirm = true else onClose() },
                onSaveDraft = onSaveDraft,
                modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
            )
            StepProgressBar(
                step = uiState.step,
                total = CourseCreateUIState.LAST_STEP,
                modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
            )

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            viewportTopPx = it.positionInWindow().y
                            viewportHeightPx = it.size.height
                        }.verticalScroll(scrollState)
                        .padding(horizontal = ScreenHorizontalPadding)
                        .padding(top = 20.dp, bottom = 24.dp),
            ) {
                when (uiState.step) {
                    CourseCreateUIState.STEP_PLACES -> {
                        PlaceSection(
                            uiState = uiState,
                            onIntent = onIntent,
                            onAddPlace = { showPlaceSearch = true },
                            onRequestRemove = { placeToRemove = it },
                            scrollState = scrollState,
                            viewportTopPx = viewportTopPx,
                            viewportHeightPx = viewportHeightPx,
                        )
                    }

                    CourseCreateUIState.STEP_PLACE_RECORDS -> {
                        PlaceRecordSection(uiState = uiState, onIntent = onIntent)
                    }

                    CourseCreateUIState.STEP_COURSE_INFO -> {
                        InfoSection(uiState = uiState, onIntent = onIntent)
                    }

                    else -> {
                        VisibilitySection(uiState = uiState, onIntent = onIntent)
                    }
                }
            }

            StepBottomBar(
                uiState = uiState,
                onPrev = { onIntent(CourseCreateIntent.PrevStep) },
                onNext = { onIntent(CourseCreateIntent.NextStep) },
                onSave = { onRequestSave(uiState.toCompleteVO()) },
            )
        }

        if (showPlaceSearch) {
            PlaceSearchOverlay(
                onDismiss = { showPlaceSearch = false },
                maxPlaces = CourseCreateUIState.MAX_PLACES,
                existingCount = uiState.places.size,
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
                    // onSaveDraft 가 저장 후 홈으로 이동하므로 별도 onClose 는 부르지 않는다.
                    showExitConfirm = false
                    onSaveDraft()
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
        // 작성 중 고른 대표 사진(로컬 content URI). 업로드된 공개 URL 은 아직 손에 없다.
        thumbnailUrl = thumbnailPhotos.firstOrNull().orEmpty(),
        stops =
            places.mapIndexed { index, place ->
                CourseStopVO(
                    order = index + 1,
                    name = place.name,
                    category = place.category,
                    durationText = "",
                    imageUrl = place.thumbnailUrl,
                )
            },
    )
}

@Composable
private fun InfoSection(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
) {
    CourseSectionHeader(number = CourseCreateUIState.STEP_COURSE_INFO, title = "코스 정보")
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

/**
 * Step 2 — 장소별 기록. Step 1 에서 담은 순서 그대로 각 장소에 한마디와 사진을 남긴다.
 *
 * 사진은 코스 저장의 필수 조건이라(장소마다 1장 이상) 여기서 다 채워야 다음으로 넘어간다.
 * 순서 변경·삭제는 Step 1 의 일이므로 드래그 손잡이를 감춘다.
 */
@Composable
private fun PlaceRecordSection(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
) {
    CourseSectionHeader(number = CourseCreateUIState.STEP_PLACE_RECORDS, title = "장소별 기록")
    Spacer(Modifier.height(6.dp))
    DsText(
        text = "장소마다 사진 1장은 꼭 필요해요. 한마디는 남기지 않아도 괜찮아요.",
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
    )
    Spacer(Modifier.height(12.dp))
    uiState.places.forEach { place ->
        CoursePlaceCard(
            place = place,
            onRemove = { },
            onNoteChange = { onIntent(CourseCreateIntent.ChangePlaceNote(place.id, it)) },
            onPhotosChange = { onIntent(CourseCreateIntent.ChangePlacePhotos(place.id, it)) },
            showDragHandle = false,
        )
        // 다음 장소로의 도보 시간. 마지막 장소이거나 계산이 안 된 구간은 빈 문자열이라 자리만 띄운다.
        if (place.walkText.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            CourseRouteConnector(text = place.walkText)
            Spacer(Modifier.height(6.dp))
        } else {
            Spacer(Modifier.height(10.dp))
        }
    }
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
    val color = DesignSystemThemeImpl.designSystemColor
    val haptics = LocalHapticFeedback.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        CourseSectionHeader(number = CourseCreateUIState.STEP_PLACES, title = "장소 고르기", modifier = Modifier.weight(1f))
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
        // 집어 올린 느낌: 살짝 확대 + 그림자. 놓으면 스프링으로 원래 크기·높이로 정착.
        val liftScale by animateFloatAsState(targetValue = if (isDragged) DRAG_LIFT_SCALE else 1f, label = "lift")
        val liftElevation by animateDpAsState(
            targetValue = if (isDragged) DRAG_LIFT_ELEVATION else 0.dp,
            label = "liftElevation",
        )

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
                // 카드에만 그림자를 준다(아래 경로 커넥터까지 들리면 어색하다).
                modifier =
                    Modifier.shadow(
                        elevation = liftElevation,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = color.contentDefaultLevel0.copy(alpha = DRAG_AMBIENT_ALPHA),
                        spotColor = color.contentDefaultLevel0.copy(alpha = DRAG_SPOT_ALPHA),
                    ),
                onRemove = { onRequestRemove(place) },
                onNoteChange = { onIntent(CourseCreateIntent.ChangePlaceNote(place.id, it)) },
                onPhotosChange = { onIntent(CourseCreateIntent.ChangePlacePhotos(place.id, it)) },
                // 이 단계는 "무엇을 담고 어떤 순서로 갈지"만 정한다. 기록은 다음 단계에서.
                showRecord = false,
                dragHandleModifier =
                    Modifier.pointerInput(place.id) {
                        detectDragGestures(
                            onDragStart = {
                                // 집었다는 걸 손끝으로도 알린다(목록 재정렬의 관례).
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
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
                                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
            // 도보 시간은 장소를 확정하고 넘어간 "장소별 기록" 단계에서 보여 준다.
            Spacer(Modifier.height(8.dp))
        }
    }
    Spacer(Modifier.height(2.dp))
    AddPlaceButton(
        onClick = onAddPlace,
        atMax = uiState.places.size >= CourseCreateUIState.MAX_PLACES,
    )
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

/** 집어 올린 카드가 목록 위로 떠 보이도록. 너무 크면 아래 카드를 가린다. */
private val DRAG_LIFT_ELEVATION = 16.dp
private const val DRAG_LIFT_SCALE = 1.03f
private const val DRAG_AMBIENT_ALPHA = 0.24f
private const val DRAG_SPOT_ALPHA = 0.34f

@Composable
private fun AddPlaceButton(
    onClick: () -> Unit,
    atMax: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    // 최대(10곳) 도달 시 비활성화하고 안내 문구로 바꾼다.
    val label = if (atMax) "장소는 최대 ${CourseCreateUIState.MAX_PLACES}곳까지 담을 수 있어요" else "＋ 장소 더 담기"
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                // Level1(Gray300)이 Level0(Gray400)보다 진하다 — 팔레트 번호가 명도 순이 아니다.
                .dashedBorder(color.borderDefaultLevel1, cornerRadius = 16.dp)
                .clickable(enabled = !atMax, onClick = onClick)
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(9.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = if (atMax) color.contentDefaultLevel3 else color.contentDefaultLevel1,
        )
    }
}

@Composable
private fun VisibilitySection(
    uiState: CourseCreateUIState,
    onIntent: (CourseCreateIntent) -> Unit,
) {
    CourseSectionHeader(number = CourseCreateUIState.STEP_SETTINGS, title = "코스 설정")
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
private fun StepBottomBar(
    uiState: CourseCreateUIState,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val isLast = uiState.step == CourseCreateUIState.LAST_STEP
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .navigationBarsPadding()
                .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (uiState.step > CourseCreateUIState.FIRST_STEP) {
                Box(modifier = Modifier.weight(1f)) {
                    DsButton(text = "이전", variant = DsButtonVariant.Secondary, onClick = onPrev)
                }
            }
            Box(modifier = Modifier.weight(NEXT_BUTTON_WEIGHT)) {
                DsButton(
                    // 못 넘어가는 상태면 버튼 자리에 그 이유를 띄운다(별도 안내 문구 없이).
                    text = uiState.stepBlockedReason ?: if (isLast) "코스 저장하기" else "다음",
                    // 저장 중에는 중복 탭을 막는다(업로드 + 생성이라 왕복이 길다).
                    enabled = uiState.canGoNext && !uiState.isSaving,
                    loading = isLast && uiState.isSaving,
                    onClick = if (isLast) onSave else onNext,
                )
            }
        }
    }
}

/** "다음"이 "이전"보다 넓어야 진행 방향이 읽힌다. */
private const val NEXT_BUTTON_WEIGHT = 2f
