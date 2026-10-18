package com.chillsam.courmy.course.presentation

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsDialogScaffold
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.entity.CourseCommentVO
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** 댓글 시트가 차지할 화면 높이 비율. 뒤의 코스가 살짝 비쳐 "덮인 것"으로 읽히게 다 채우지 않는다. */
private const val COMMENT_SHEET_HEIGHT_FRACTION = 0.72f

/** 시트 모서리·손잡이·모션 값은 장소 시트([PlaceDetailSheet])와 같게 맞춘다 — 같은 앱의 시트로 읽히게. */
private val SHEET_TOP_SHAPE = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
private val DRAG_HANDLE_PADDING = 10.dp
private const val DISMISS_DRAG_FRACTION = 0.2f
private const val SHEET_ANIM_MS = 220

/** 댓글 작성자 아바타. 서버 계약 전이라 사진이 없어 자리 표시 아이콘으로 그린다. */
private val COMMENT_AVATAR_SIZE = 36.dp

/** 하단 입력줄의 원형 등록 버튼. 입력창 높이와 비슷해야 한 줄로 읽힌다. */
private val SUBMIT_BUTTON_SIZE = 44.dp

/**
 * 댓글 하단 시트. 하단 액션바의 댓글 버튼으로 연다.
 *
 * 댓글을 본문에 깔지 않고 시트로 뺀 이유 — 코스 상세는 장소·지도·후기로 이미 길다. 댓글까지
 * 이어 붙이면 코스를 다 훑은 뒤에야 닿고, 반대로 댓글만 보려는 사람은 한참 스크롤해야 한다.
 *
 * 컨테이너는 `ModalBottomSheet` 가 아니라 [Dialog] 로 직접 만든다. `ModalBottomSheet` 는 키보드가
 * 올라오면 시트 **창 자체**를 키보드 위까지로 줄이는데, 그 아래로 뒤 화면(하단 액션바)이 스크림도
 * 없이 비쳐 시트 회색 아래 흰 조각이 남는다. 보통 키보드가 그 자리를 다 덮어 안 보이지만,
 * 떠 있는 키보드(에뮬레이터 하드웨어 키보드 툴바 등)에서는 그대로 드러난다. [Dialog] 에
 * `decorFitsSystemWindows = false` 를 주면 창이 줄지 않고 인셋만 내려오므로, 시트 배경이 화면
 * 바닥까지 유지된 채 [imePadding] 으로 입력줄만 키보드 위로 올라온다.
 * (장소 시트가 [Dialog] 를 쓰는 이유는 다르다 — 그쪽은 지도 팬 제스처 충돌. 해당 파일 주석 참고.)
 *
 * 입력줄은 시트 **하단**에 고정한다. 목록이 위에서 스크롤되고 입력이 키보드 바로 위에 붙는
 * 통상의 댓글 배치가, 입력창을 머리에 두는 것보다 "쓰면서 다른 댓글을 본다"에 맞다.
 */
@Composable
internal fun CourseCommentSheet(
    courseId: Long,
    onDismiss: () -> Unit,
) {
    // 시트가 자기 ViewModel 을 들고 서버 댓글(/api/v1/courses/{id}/comments)을 직접 다룬다 —
    // 상세 ViewModel 에 섞으면 좋아요·저장과 생명주기가 엉킨다(시트 전용 상태라서).
    val viewModel: CourseCommentsViewModel = hiltViewModel()
    LaunchedEffect(courseId) { viewModel.onIntent(CourseCommentsIntent.Load(courseId)) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val session = LocalSessionUiState.current

    val context = LocalContext.current
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        viewModel.onIntent(CourseCommentsIntent.ConsumeMessage)
    }

    var editTarget by remember { mutableStateOf<CourseCommentVO?>(null) }
    var deleteTargetId by remember { mutableStateOf<Long?>(null) }

    val color = DesignSystemThemeImpl.designSystemColor
    val scope = rememberCoroutineScope()
    // 슬라이드 거리는 화면 높이로 잡는다. 측정값에 묶으면 갱신 때 애니메이션이 끊긴다(장소 시트 선례).
    val slideDistance =
        with(LocalDensity.current) {
            LocalConfiguration.current.screenHeightDp.dp
                .toPx()
        }
    val offsetY = remember { Animatable(slideDistance) }
    LaunchedEffect(Unit) { offsetY.animateTo(0f, tween(SHEET_ANIM_MS)) }
    val animatedDismiss: () -> Unit = {
        scope.launch {
            offsetY.animateTo(slideDistance, tween(SHEET_ANIM_MS))
            onDismiss()
        }
        Unit
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                // 기본 다이얼로그 폭 제한을 풀어 화면을 꽉 채운다.
                usePlatformDefaultWidth = false,
                // 창을 키보드에 맞춰 줄이지 않는다 — 위 본문 주석의 흰 조각 문제.
                decorFitsSystemWindows = false,
            ),
    ) {
        Column(modifier = Modifier.fillMaxSize().imePadding()) {
            // 시트 위 빈 영역. 누르면 닫힌다(시트 바깥을 누른 것과 같다).
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = animatedDismiss,
                        ),
            )
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(COMMENT_SHEET_HEIGHT_FRACTION)
                        .offset { IntOffset(0, offsetY.value.roundToInt()) }
                        .clip(SHEET_TOP_SHAPE)
                        .background(color.bgDefaultLevel0),
            ) {
                CommentSheetDragHandle(
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
                // 더 받을 페이지가 남았으면 수에 +를 붙인다 — 전체 집계는 응답에 없다.
                val countSuffix = if (state.hasNext) "+" else ""
                DsText(
                    text = if (state.comments.isEmpty()) "댓글" else "댓글 ${state.comments.size}$countSuffix",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                )
                CommentList(
                    state = state,
                    onRetry = { viewModel.onIntent(CourseCommentsIntent.Retry) },
                    onLoadMore = { viewModel.onIntent(CourseCommentsIntent.LoadMore) },
                    onEdit = { editTarget = it },
                    onDelete = { deleteTargetId = it.id },
                    modifier = Modifier.weight(1f),
                )
                if (session.isLoggedIn) {
                    CommentInputBar(
                        draft = state.draft,
                        enabled = !state.isSubmitting,
                        onCommentChange = { viewModel.onIntent(CourseCommentsIntent.ChangeDraft(it)) },
                        onSubmitComment = { viewModel.onIntent(CourseCommentsIntent.Write) },
                    )
                } else {
                    DsText(
                        text = "로그인하면 댓글을 쓸 수 있어요.",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(vertical = 12.dp),
                    )
                }
            }
        }
    }

    editTarget?.let { target ->
        EditCommentDialog(
            initialContent = target.content,
            isSaving = state.isSubmitting,
            onSave = { content ->
                editTarget = null
                viewModel.onIntent(CourseCommentsIntent.Edit(target.id, content))
            },
            onDismiss = { editTarget = null },
        )
    }
    deleteTargetId?.let { targetId ->
        DsConfirmDialog(
            title = "댓글을 삭제할까요?",
            description = "삭제한 댓글은 되돌릴 수 없어요.",
            confirmText = "삭제",
            dismissText = "취소",
            destructive = true,
            onConfirm = {
                deleteTargetId = null
                viewModel.onIntent(CourseCommentsIntent.Delete(targetId))
            },
            onDismiss = { deleteTargetId = null },
        )
    }
}

/** 내 댓글 수정 다이얼로그. */
@Composable
private fun EditCommentDialog(
    initialContent: String,
    isSaving: Boolean,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var content by remember { mutableStateOf(initialContent) }
    DsDialogScaffold(
        title = "댓글 수정",
        description = "댓글은 1000자까지 쓸 수 있어요.",
        onDismiss = onDismiss,
    ) {
        DsTextField(value = content, onValueChange = { content = it }, placeholder = "댓글 내용")
        DsButton(
            text = "저장",
            enabled = content.isNotBlank() && !isSaving,
            loading = isSaving,
            onClick = { onSave(content) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** 손잡이. 탭하면 닫히고, 끌어내려도 닫힌다(장소 시트와 같은 제스처). */
@Composable
private fun CommentSheetDragHandle(
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

/** 댓글 목록. 입력줄이 하단에 고정되므로 목록만 스크롤된다. */
@Composable
private fun CommentList(
    state: CourseCommentsUIState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onEdit: (CourseCommentVO) -> Unit,
    onDelete: (CourseCommentVO) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = color.contentAccent)
            }
            return
        }

        state.errorMessage != null -> {
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DsText(
                        text = state.errorMessage,
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel2,
                    )
                    DsText(
                        text = "다시 시도",
                        style = DesignSystemThemeImpl.typeScale.textRegularM,
                        color = color.contentAccent,
                        modifier =
                            Modifier
                                .clickable(onClick = onRetry)
                                .padding(8.dp),
                    )
                }
            }
            return
        }

        state.comments.isEmpty() -> {
            // 비었을 때 목록 영역 가운데 안내 한 줄. 목록 머리에 붙이면 빈 공간이 고장처럼 보인다.
            Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                DsText(
                    text = "첫 댓글을 남겨 보세요.",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel3,
                )
            }
            return
        }
    }
    val listState = rememberLazyListState()
    LoadMoreOnScrollEnd(listState, onLoadMore = onLoadMore)
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        // 서버는 최신순으로 주지만 화면은 채팅처럼 아래로 쌓는다 — 새 댓글이 입력줄 바로 위에 생기고,
        // 위로 스크롤하면 이전 댓글을 이어 받는다(reverseLayout 이라 목록 "끝" = 화면 맨 위).
        reverseLayout = true,
    ) {
        items(state.comments, key = { it.id }) { comment ->
            CommentRow(comment = comment, onEdit = { onEdit(comment) }, onDelete = { onDelete(comment) })
        }
        if (state.isLoadingMore) {
            item { LoadingMoreFooter() }
        }
    }
}

@Composable
private fun CommentRow(
    comment: CourseCommentVO,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row {
        // 서버 계약 전이라 프로필 사진이 없다. 이미지 자리의 기본 배경 톤(imagePlaceholder)에 사람 아이콘.
        Box(
            modifier =
                Modifier
                    .size(COMMENT_AVATAR_SIZE)
                    .clip(CircleShape)
                    .background(color.imagePlaceholder),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_tab_person_24),
                contentDescription = null,
                tint = color.contentDefaultLevel3,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DsText(
                    // 응답에 작성자 프로필이 없어 임시 표기를 쓴다(CourseCommentVO 주석 참고).
                    text = if (comment.isMine) "나" else "작성자 ${comment.authorId}",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                )
                Spacer(Modifier.width(6.dp))
                DsText(
                    text = comment.dateText,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                )
            }
            DsText(
                text = comment.content,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel1,
                maxLines = Int.MAX_VALUE,
            )
        }
        if (comment.isMine) {
            MyCommentMenu(onEdit = onEdit, onDelete = onDelete)
        }
    }
}

/** 내 댓글 우측 ⋮ — 수정·삭제. */
@Composable
private fun MyCommentMenu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }, modifier = Modifier.size(28.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_more_vert_24),
                contentDescription = "내 댓글 메뉴",
                tint = color.contentDefaultLevel3,
                modifier = Modifier.size(18.dp),
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
                        "수정",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel0,
                    )
                },
                onClick = {
                    expanded = false
                    onEdit()
                },
            )
            DropdownMenuItem(
                text = {
                    DsText(
                        "삭제",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDanger,
                    )
                },
                onClick = {
                    expanded = false
                    onDelete()
                },
            )
        }
    }
}

/** 하단 고정 입력줄: 입력창 + 원형 등록 버튼. 위에 옅은 구분선으로 목록과 끊는다. */
@Composable
private fun CommentInputBar(
    draft: String,
    enabled: Boolean,
    onCommentChange: (String) -> Unit,
    onSubmitComment: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val canSubmit = draft.isNotBlank() && enabled
    Column(modifier = Modifier.fillMaxWidth()) {
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
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsTextField(
                value = draft,
                onValueChange = onCommentChange,
                placeholder = "댓글을 남겨 주세요",
                enabled = enabled,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier =
                    Modifier
                        .size(SUBMIT_BUTTON_SIZE)
                        .clip(CircleShape)
                        .background(if (canSubmit) color.bgAccent else color.borderDefaultLevel0)
                        .clickable(enabled = canSubmit, onClick = onSubmitComment),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_up_24),
                    contentDescription = "댓글 등록",
                    tint = if (canSubmit) color.contentOnAccent else color.contentDefaultLevel3,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
