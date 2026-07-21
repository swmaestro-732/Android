package com.chillsam.courmy.course.presentation.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CoursePlaceVO

/** 한마디 메모 최대 글자 수(Figma FS-34 인터랙션 "카드에서 바로 입력" 기준). */
private const val NOTE_MAX_LENGTH = 40

/** ② 장소 담기 — 장소 카드 1건. 한마디 메모는 카드에서 바로 편집한다. */
@Composable
fun CoursePlaceCard(
    place: CoursePlaceVO,
    onRemove: () -> Unit,
    onNoteChange: (String) -> Unit,
    onPhotosChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    val hasNote = place.note.isNotEmpty()
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .border(1.dp, DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0, shape),
    ) {
        PlaceHeader(place = place, onRemove = onRemove)
        // 메모 유무와 무관하게 동일한 컴포저블 트리를 유지하고 배경색만 토글해, 입력 중 포커스가 끊기지 않게 한다.
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        if (hasNote) {
                            DesignSystemThemeImpl.designSystemColor.bgAccentSubtle
                        } else {
                            DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1
                        },
                    ).padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            EditableNoteRow(note = place.note, onNoteChange = { onNoteChange(it.take(NOTE_MAX_LENGTH)) })
            PhotoRow(
                photos = place.photoUrls,
                maxPhotos = place.maxPhotos,
                onPhotosChange = onPhotosChange,
            )
        }
    }
}

@Composable
private fun PlaceHeader(
    place: CoursePlaceVO,
    onRemove: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        DsText(
            text = "⋮",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
        )
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0),
        )
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            )
            DsText(
                text = place.category,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
            )
        }
        Box(
            modifier =
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(1.dp, DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0, CircleShape)
                    .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "−",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
            )
        }
    }
}

/** 한마디 메모 인라인 편집 행: ✎ + 입력 필드(+ 글자 수). */
@Composable
private fun EditableNoteRow(
    note: String,
    onNoteChange: (String) -> Unit,
) {
    val hasNote = note.isNotEmpty()
    val accent = DesignSystemThemeImpl.designSystemColor.contentAccent
    val placeholderColor = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DsText(
            text = "✎",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = if (hasNote) accent else placeholderColor,
        )
        BasicTextField(
            value = note,
            onValueChange = onNoteChange,
            modifier = Modifier.weight(1f),
            textStyle =
                DesignSystemThemeImpl.typeScale.textRegularXS
                    .copy(color = accent),
            cursorBrush = SolidColor(accent),
            decorationBox = { innerTextField ->
                if (note.isEmpty()) {
                    DsText(
                        text = "이곳에 한마디 남기기…",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = placeholderColor,
                    )
                }
                innerTextField()
            },
        )
        if (hasNote) {
            DsText(
                text = "${note.length}/$NOTE_MAX_LENGTH",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = placeholderColor,
            )
        }
    }
}

@Composable
private fun PhotoRow(
    photos: List<String>,
    maxPhotos: Int,
    onPhotosChange: (List<String>) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        photos.forEach { uri ->
            FilledPhotoSlot(uri = uri, onRemove = { onPhotosChange(photos - uri) })
        }
        if (photos.size < maxPhotos) {
            AddPhotoSlot(
                count = photos.size,
                max = maxPhotos,
                onPicked = { picked -> onPhotosChange((photos + picked).take(maxPhotos)) },
            )
        }
    }
}

@Composable
private fun FilledPhotoSlot(
    uri: String,
    onRemove: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0),
        contentAlignment = Alignment.TopEnd,
    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier =
                Modifier
                    .padding(3.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0)
                    .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "✕",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
            )
        }
    }
}

@Composable
private fun AddPhotoSlot(
    count: Int,
    max: Int,
    onPicked: (List<String>) -> Unit,
) {
    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.PickMultipleVisualMedia(max),
        ) { uris ->
            if (uris.isNotEmpty()) onPicked(uris.map { it.toString() })
        }
    Column(
        modifier =
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .dashedBorder(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0, cornerRadius = 10.dp)
                .clickable {
                    launcher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = "＋",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentAccent,
        )
        DsText(
            text = "$count/$max",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentAccent,
            textAlign = TextAlign.Center,
        )
    }
}
