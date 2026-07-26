package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.util.formatCreatedAt
import com.chillsam.courmy.course.domain.CourseCreatePage

/**
 * 임시저장한 코스 목록 화면(FS-20). 세션 저장소([DraftListViewModel.drafts])를
 * "썸네일 + 코스명 + 저장 시각 + 이어서 편집" 카드 목록으로 노출한다.
 * 카드를 누르면 그 초안을 불러온 채로 코스 만들기 화면으로 이동해 이어 작성한다.
 */
@Composable
fun DraftListPage(
    viewModel: DraftListViewModel,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
    val drafts by viewModel.drafts.collectAsStateWithLifecycle()
    val color = DesignSystemThemeImpl.designSystemColor

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
    ) {
        DraftListHeader(
            count = drafts.size,
            onBack = { navigationHelper.navigateToBack() },
        )

        if (drafts.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "임시저장한 코스가 없어요",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(drafts, key = { it.id }) { draft ->
                    DraftCourseCard(
                        title = draft.title,
                        subtitle = formatCreatedAt(draft.savedAtMillis),
                        onContinue = {
                            // 이 초안(id)을 이어서 편집하도록 지정한 뒤 코스 만들기로 이동한다.
                            viewModel.beginEdit(draft.id)
                            navigationHelper.navigateTo(CourseCreatePage)
                        },
                    )
                }
            }
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

/** 임시저장 코스 1건 카드: 썸네일 + 코스명/저장 시각 + "이어서 편집" 액션. */
@Composable
private fun DraftCourseCard(
    title: String,
    subtitle: String,
    onContinue: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onContinue)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.borderDefaultLevel0),
        )
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
            DsText(
                text = subtitle,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(color.bgAccentSubtle)
                    .clickable(onClick = onContinue)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "이어서 편집",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
            )
        }
    }
}
