package com.chillsam.courmy.main.presentation.saved

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsDialogScaffold
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseFolderVO

@Composable
internal fun SavedCourseFilters(
    state: SavedCoursesUIState,
    onIntent: (SavedCoursesIntent) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(
            Modifier.weight(1f).horizontalScroll(rememberScrollState()).selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SavedFilterChip("전체 ${state.totalCount}", state.selectedFolderId == null) {
                onIntent(SavedCoursesIntent.SelectFolder(null))
            }
            state.folders.forEach { folder ->
                SavedFilterChip("${folder.name} ${folder.count}", state.selectedFolderId == folder.id) {
                    onIntent(SavedCoursesIntent.SelectFolder(folder.id))
                }
            }
        }
        IconButton(onClick = { onIntent(SavedCoursesIntent.ShowCreateFolder) }) {
            Icon(painterResource(R.drawable.ic_add_24), contentDescription = "폴더 추가", tint = color.contentDefaultLevel0)
        }
    }
    SavedStatusTabs(
        selectedSecond = state.completed,
        firstLabel = "안 가봄 ${state.uncompletedCount}",
        secondLabel = "완주 ${state.completedCount}",
        onSelect = { onIntent(SavedCoursesIntent.SelectCompleted(it)) },
    )
    if (state.showCreateFolder) CreateFolderDialog(state, onIntent)
}

@Composable
private fun CreateFolderDialog(
    state: SavedCoursesUIState,
    onIntent: (SavedCoursesIntent) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    DsDialogScaffold(
        title = "폴더 추가",
        description = "원하는 이름으로 코스를 모아보세요. (최대 ${CourseFolderVO.NAME_MAX_LENGTH}자)",
        onDismiss = { onIntent(SavedCoursesIntent.DismissCreateFolder) },
        actions = {
            DsTextField(
                value = name,
                onValueChange = { if (it.length <= CourseFolderVO.NAME_MAX_LENGTH) name = it },
                placeholder = "예: 주말 나들이",
                enabled = !state.isCreatingFolder,
                isError = state.folderError != null,
                modifier = Modifier.fillMaxWidth(),
            )
            state.folderError?.let {
                DsText(
                    it,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = DesignSystemThemeImpl.designSystemColor.contentDanger,
                    maxLines = 3,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
            Row(Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DsButton(
                    "취소",
                    Modifier.weight(1f),
                    variant = DsButtonVariant.Secondary,
                    enabled = !state.isCreatingFolder,
                    onClick = { onIntent(SavedCoursesIntent.DismissCreateFolder) },
                )
                DsButton(
                    "추가",
                    Modifier.weight(1f),
                    enabled = name.isNotBlank() && !state.isCreatingFolder,
                    loading = state.isCreatingFolder,
                    onClick = { onIntent(SavedCoursesIntent.CreateFolder(name)) },
                )
            }
        },
    )
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, widthDp = 390)
@Composable
private fun SavedCourseFiltersPreview() {
    com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme {
        Column(Modifier.padding(20.dp)) {
            SavedHeader(SAVED_TAB_COURSES, {})
            SavedCourseFilters(SavedCoursesUIState(totalCount = 12, uncompletedCount = 8, completedCount = 4), {})
        }
    }
}
