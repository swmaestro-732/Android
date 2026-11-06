package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.domain.telemetry.buttonClick
import com.chillsam.courmy.common.domain.telemetry.selectionChange
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.StepProgressBar
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.telemetry.LocalTelemetry
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.component.BackTopBar

/**
 * 선택지는 코스 태그 12종이다([CourseTagVO]). 장소 카테고리 10종과 별개 체계다.
 *
 * 선택 상태·저장은 소문자 코드(`date` …)로 하고 카드에는 라벨을 보여준다. 서버 `likeThemes` 는
 * CourseCategory enum 이름(대문자)으로 검증하므로, 대문자 변환은 data 레이어가 요청 직전에 한다.
 */
private const val MIN_THEME_COUNT = 3

/** 관심 테마 편집 화면(FS-06). 최소 [MIN_THEME_COUNT]개 이상 카드를 선택한다. */
@Composable
fun InterestThemePage(
    modifier: Modifier = Modifier,
    onNext: ((List<String>) -> Unit)? = null,
    progressStep: Int? = null,
) {
    // 상단이 흰색이라 상태바도 같은 색으로 이어 붙인다. 회색으로 두면 띠만 분리돼 보인다.
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    // 편집 모드에서만 저장된 관심사를 읽고 되쓴다. 회원가입은 완료 화면까지 홀더로 나른다.
    val telemetry = LocalTelemetry.current
    val editing = onNext == null
    val screen = if (editing) "/my/interest/theme" else "/login/theme"
    val viewModel: InterestEditViewModel = hiltViewModel()
    val editState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(editing) { if (editing) viewModel.onIntent(InterestEditIntent.Load) }
    // 회원가입은 빈 선택으로 시작하고, 편집은 불러온 값이 도착하면 그 값으로 다시 채운다.
    var selected by remember(editState.loaded) {
        mutableStateOf(editState.themes.mapNotNull { CourseTagVO.fromCode(it)?.code }.toSet())
    }
    val canEdit = !editing || (editState.loaded && !editState.isSaving)
    LaunchedEffect(editState.saved) { if (editState.saved) navigationHelper.navigateToBack() }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        // 회원가입 플로우면 상단 진행 바, 편집이면 뒤로가기 바.
        if (progressStep != null) {
            StepProgressBar(
                step = progressStep,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(color.bgDefaultLevel1)
                        .statusBarsPadding()
                        .padding(
                            start = ScreenHorizontalPadding,
                            end = ScreenHorizontalPadding,
                            top = 12.dp,
                            bottom = 4.dp,
                        ),
            )
        } else {
            Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
                BackTopBar(title = "관심 테마", onBack = { navigationHelper.navigateToBack() })
            }
        }

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenHorizontalPadding),
        ) {
            DsText(
                text = "어떤 곳을\n좋아하세요?",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                maxLines = 2,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
            )
            // "N개 선택됨" 부분만 강조색(Figma FS-06).
            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                DsText(
                    text = "최소 ${MIN_THEME_COUNT}개 이상 · ",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
                DsText(
                    text = "${selected.size}개 선택됨",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentAccent,
                )
            }
            InterestThemeCards(
                selected = selected,
                enabled = canEdit,
                onToggle = { theme ->
                    telemetry.selectionChange(screen, "theme", theme, theme !in selected)
                    selected = if (theme in selected) selected - theme else selected + theme
                },
            )
        }

        if (editing) {
            InterestLoadFeedback(editState.isLoading, editState.loadError) {
                viewModel.onIntent(InterestEditIntent.Load)
            }
        }
        ThemeSaveButton(editState, editing, selected.size, canEdit) {
            telemetry.buttonClick(
                screen,
                if (editing) "save" else "next",
                mapOf("themes" to selected.sorted().joinToString(",")),
            )
            val themes = selected.toList()
            // 서버 저장이 성공한 경우에만 완료 신호를 받아 닫는다.
            onNext?.invoke(themes) ?: viewModel.onIntent(InterestEditIntent.SaveThemes(themes))
        }
    }
}

@Composable
private fun ThemeSaveButton(
    state: InterestEditUiState,
    editing: Boolean,
    selectedCount: Int,
    canEdit: Boolean,
    onContinue: () -> Unit,
) {
    if (editing && state.saveError) {
        DsText(
            text = "저장하지 못했어요. 선택한 테마를 유지했으니 다시 시도해 주세요.",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            maxLines = 2,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
    }
    DsButton(
        text =
            when {
                selectedCount < MIN_THEME_COUNT -> "최소 ${MIN_THEME_COUNT}개 이상 선택"
                editing -> "저장"
                else -> "다음"
            },
        enabled = canEdit && selectedCount >= MIN_THEME_COUNT,
        loading = editing && state.isSaving,
        onClick = onContinue,
        modifier =
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
    )
}
