package com.chillsam.courmy.main.presentation.map

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.presentation.PlaceDetailViewModel
import com.chillsam.courmy.course.presentation.component.PlaceDetailSheet
import com.chillsam.courmy.main.entity.map.MapPlaceVO

/** 지도와 검색 목록에서 같은 장소 상세 시트를 연다. 목록의 정보는 조회 전에도 표시한다. */
@Composable
internal fun MapPlaceDetailSheet(
    place: MapPlaceVO,
    onDismiss: () -> Unit,
    viewModel: PlaceDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    DisposableEffect(place.id, viewModel) {
        viewModel.open(place.id)
        onDispose { viewModel.clear() }
    }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    val detail = state.place
    PlaceDetailSheet(
        coursePlace =
            CourseDetailPlaceVO(
                placeId = place.id,
                order = 1,
                name = detail?.name?.takeIf { it.isNotBlank() } ?: place.name,
                category = detail?.category?.takeIf { it.isNotBlank() } ?: place.category,
                photoCountText = "",
                tip = "",
                latitude = detail?.latitude ?: place.latitude,
                longitude = detail?.longitude ?: place.longitude,
            ),
        address = detail?.address?.takeIf { it.isNotBlank() } ?: place.address,
        reviews = state.reviews,
        isSaved = state.isSaved,
        onToggleSave = viewModel::toggleSave,
        onDismiss = onDismiss,
        onSelectPlace = {},
        onDeleteReview = viewModel::deleteReview,
    )
}
