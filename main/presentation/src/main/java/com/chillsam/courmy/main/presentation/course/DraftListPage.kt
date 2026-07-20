package com.chillsam.courmy.main.presentation.course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.course.CourseCreatePage

/**
 * 임시저장한 코스 목록 화면. 세션 저장소([CourseSessionStore.drafts])를 리스트(제목 + 저장 시각)로 노출.
 * 항목을 누르면 그 제목을 채운 채로 코스 만들기 화면으로 이동해 이어 작성한다.
 */
@Composable
fun DraftListPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val drafts by CourseSessionStore.drafts.collectAsState()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
    ) {
        DsText(
            text = "임시저장",
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            modifier = Modifier.padding(vertical = 20.dp),
        )

        if (drafts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "임시저장한 코스가 없어요",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(drafts) { draft ->
                    DraftRow(
                        title = draft.title,
                        savedAtMillis = draft.createdAtMillis,
                        onClick = {
                            navigationHelper.navigateByRoute(CourseCreatePage.route(draft.title))
                        },
                    )
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
                    )
                }
            }
        }
    }
}

@Composable
private fun DraftRow(
    title: String,
    savedAtMillis: Long,
    onClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        DsText(
            text = formatCreatedAt(savedAtMillis),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
    }
}
