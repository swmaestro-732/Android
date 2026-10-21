package com.chillsam.courmy.course.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.entity.category.PlaceCategoryVO
import com.chillsam.courmy.course.entity.PlaceReviewTagCatalog

@Composable
internal fun PlaceReviewWriteDialog(
    placeId: Long,
    categories: List<PlaceCategoryVO>,
    onClose: () -> Unit,
    onWritten: () -> Unit,
    viewModel: CourseReviewWriteViewModel = hiltViewModel(key = "place-review-$placeId"),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val close = {
        if (!state.isSubmitting) {
            viewModel.onIntent(CourseReviewWriteIntent.ConsumeSubmitted)
            onClose()
        }
    }
    LaunchedEffect(state.submittedReviewId) {
        if (state.submittedReviewId != 0L) {
            viewModel.onIntent(CourseReviewWriteIntent.ConsumeSubmitted)
            onWritten()
        }
    }
    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        CourseReviewWriteScreen(
            state = state,
            onIntent = viewModel::onIntent,
            onSubmit = { viewModel.onIntent(CourseReviewWriteIntent.SubmitPlace(placeId)) },
            onClose = close,
            tags = PlaceReviewTagCatalog.forCategories(categories),
            isPlace = true,
        )
    }
}
