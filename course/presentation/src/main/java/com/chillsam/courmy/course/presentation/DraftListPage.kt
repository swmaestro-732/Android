package com.chillsam.courmy.course.presentation

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.common.presentation.util.formatCreatedAt
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.course.entity.DraftSummaryVO

/**
 * 임시저장한 코스 목록 화면(FS-20). 서버에 남아 있는 초안을
 * "코스명 + 저장 시각 + 삭제" 카드 목록으로 노출한다.
 * 카드를 누르면 그 코스 id 를 실어 코스 만들기 화면으로 이동해 이어서 작성한다.
 */
@Composable
fun DraftListPage(
    viewModel: DraftListViewModel,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val color = DesignSystemThemeImpl.designSystemColor
    var pendingDelete by remember { mutableStateOf<DraftSummaryVO?>(null) }

    // 화면에 들어올 때마다 목록을 다시 읽는다(ViewModel 의 init 이 아니라 여기서 부른다).
    // 작성 화면에서 임시저장하고 돌아오면 서버에는 새 초안이 있는데, 한 번만 읽으면 목록에 없다.
    LaunchedEffect(Unit) { viewModel.onIntent(DraftListIntent.Load) }

    // 목록 조회 실패는 화면 안에서 재시도로 안내한다. 토스트는 삭제 실패처럼 지나가는 알림에만 쓴다.
    LaunchedEffect(uiState.errorMessage, uiState.loadFailed) {
        val message = uiState.errorMessage
        if (message != null && !uiState.loadFailed) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(DraftListIntent.ConsumeError)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding),
        ) {
            DraftListHeader(
                count = uiState.drafts.size,
                onBack = { navigationHelper.navigateToBack() },
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = color.contentAccent,
                        )
                    }

                    uiState.loadFailed -> {
                        DraftListRetry(
                            message = uiState.errorMessage ?: "임시저장한 코스를 불러오지 못했어요.",
                            onRetry = { viewModel.onIntent(DraftListIntent.Load) },
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    uiState.isEmpty -> {
                        DsText(
                            text = "임시저장한 코스가 없어요",
                            modifier = Modifier.align(Alignment.Center),
                            style = DesignSystemThemeImpl.typeScale.textRegularS,
                            color = color.contentDefaultLevel2,
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(uiState.drafts, key = { it.id }) { draft ->
                                DraftCourseCard(
                                    title = draft.title,
                                    // 시각을 못 읽은 초안(0)은 틀린 날짜를 보여 주느니 비워 둔다.
                                    subtitle =
                                        draft.savedAtMillis
                                            .takeIf { it > 0 }
                                            ?.let { formatCreatedAt(it) }
                                            .orEmpty(),
                                    onContinue = {
                                        navigationHelper.navigateByRoute(CourseCreatePage.route(draft.id))
                                    },
                                    // 되돌릴 수 없으므로 바로 지우지 않고 확인을 받는다.
                                    onDelete = { pendingDelete = draft },
                                )
                            }
                        }
                    }
                }
            }
        }

        pendingDelete?.let { draft ->
            DsConfirmDialog(
                title = "임시저장을 삭제할까요?",
                description = "\"${draft.title}\" 은(는) 삭제하면 되돌릴 수 없어요.",
                destructive = true,
                onConfirm = {
                    viewModel.onIntent(DraftListIntent.Delete(draft.id))
                    pendingDelete = null
                },
                onDismiss = { pendingDelete = null },
            )
        }
    }
}

/** 상단바: ‹ 뒤로 · "임시저장" · 저장 개수, 그리고 안내 문구. */
@Composable
private fun DraftListHeader(
    count: Int,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onBack),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "‹",
                    style = DesignSystemThemeImpl.typeScale.titleExtraL,
                    color = color.contentDefaultLevel0,
                )
            }
            DsText(
                text = "임시저장",
                modifier = Modifier.weight(1f),
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "${count}개",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        DsText(
            text = "저장해 둔 코스를 이어서 만들 수 있어요.",
            modifier = Modifier.padding(top = 8.dp),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
    }
}

/** 조회 실패 안내 + 다시 시도. 초안이 서버에 있어 네트워크가 끊기면 목록이 비는 것과 구분해야 한다. */
@Composable
private fun DraftListRetry(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = message,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
        Spacer(Modifier.height(12.dp))
        DsButton(text = "다시 시도", variant = DsButtonVariant.Secondary, onClick = onRetry)
    }
}

/**
 * 임시저장 코스 1건 카드: 코스명/저장 시각 + 삭제(✕).
 *
 * 카드 전체가 "이어서 편집" 이라 별도 버튼을 두지 않는다(같은 동작을 두 곳에 두면 ✕ 와 나란히 놓여
 * 어느 쪽이 주 동작인지 흐려진다).
 */
@Composable
private fun DraftCourseCard(
    title: String,
    subtitle: String,
    onContinue: () -> Unit,
    onDelete: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .cardShadow(RoundedCornerShape(18.dp))
                .clickable(onClick = onContinue)
                .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DsText(
                text = title,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotEmpty()) {
                DsText(
                    text = subtitle,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
        }
        Box(
            modifier =
                Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.close_small_24),
                contentDescription = "삭제",
                tint = color.contentDefaultLevel3,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
