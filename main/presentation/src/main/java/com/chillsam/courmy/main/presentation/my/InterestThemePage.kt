package com.chillsam.courmy.main.presentation.my

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.chillsam.courmy.common.entity.category.CourseCategoryVO
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.StepProgressBar
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.presentation.component.BackTopBar

/**
 * 선택지는 서버 코스 카테고리와 같은 목록이다([CourseCategoryVO]).
 *
 * 선택 상태·저장은 코드(`CAFETOUR` …)로 하고 칩에는 라벨을 보여준다 — 서버가 `likeThemes` 를
 * enum 으로 검증해서, 라벨을 보내면 `400 "존재하지 않는 관심 테마가 포함되어 있습니다"` 로 거부한다.
 */
private val THEME_OPTIONS = CourseCategoryVO.entries
private const val MIN_THEME_COUNT = 3

/** 관심 테마 편집 화면(FS-06). 최소 [MIN_THEME_COUNT]개 이상 칩을 선택한다. */
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
    val editing = onNext == null
    val viewModel: InterestEditViewModel = hiltViewModel()
    val editState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(editing) { if (editing) viewModel.onIntent(InterestEditIntent.Load) }
    // 회원가입은 빈 선택으로 시작하고, 편집은 불러온 값이 도착하면 그 값으로 다시 채운다.
    var selected by remember(editState.loaded) { mutableStateOf(editState.themes.toSet()) }
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
                style = DesignSystemThemeImpl.typeScale.textStrongM,
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
            ThemeChips(
                selected = selected,
                onToggle = { theme ->
                    selected = if (theme in selected) selected - theme else selected + theme
                },
            )
        }

        DsButton(
            // 회원가입 플로우면 "다음 · N개 선택됨", 편집이면 "저장".
            text = if (onNext != null) "다음 · ${selected.size}개 선택됨" else "저장",
            enabled = selected.size >= MIN_THEME_COUNT,
            onClick = {
                val themes = selected.toList()
                // 편집은 기기에 저장한 뒤 저장 완료 신호를 받아 닫는다(위 LaunchedEffect).
                onNext?.invoke(themes) ?: viewModel.onIntent(InterestEditIntent.SaveThemes(themes))
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeChips(
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        THEME_OPTIONS.forEach { theme ->
            // 선택 상태는 서버 코드로 관리하고 화면에는 라벨만 보여준다.
            DsChip(
                text = theme.label,
                selected = theme.name in selected,
                onClick = { onToggle(theme.name) },
            )
        }
    }
}
