package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CoursePlaceVO

/** ② 장소 담기 — 장소 카드 1건. */
@Composable
fun CoursePlaceCard(
    place: CoursePlaceVO,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .border(1.dp, DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0, shape),
    ) {
        PlaceHeader(place = place, onRemove = onRemove)
        if (place.note.isNotEmpty()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(DesignSystemThemeImpl.designSystemColor.bgAccentSubtle)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                NoteRow(text = place.note, isPlaceholder = false)
                PhotoRow(place = place)
            }
        } else {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                NoteRow(text = "이곳에 한마디 남기기…", isPlaceholder = true)
            }
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
                PhotoRow(place = place)
            }
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

@Composable
private fun NoteRow(
    text: String,
    isPlaceholder: Boolean,
) {
    val contentColor =
        if (isPlaceholder) {
            DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3
        } else {
            DesignSystemThemeImpl.designSystemColor.contentAccent
        }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DsText(text = "✎", style = DesignSystemThemeImpl.typeScale.textRegularXS, color = contentColor)
        DsText(
            text = text,
            modifier = Modifier.weight(1f),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = contentColor,
            maxLines = Int.MAX_VALUE,
        )
    }
}

@Composable
private fun PhotoRow(place: CoursePlaceVO) {
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        place.photoUrls.forEach { _ ->
            FilledPhotoSlot()
        }
        AddPhotoSlot(count = place.photoUrls.size, max = place.maxPhotos)
    }
}

@Composable
private fun FilledPhotoSlot() {
    Box(
        modifier =
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0),
        contentAlignment = Alignment.TopEnd,
    ) {
        Box(
            modifier =
                Modifier
                    .padding(3.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0),
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
) {
    Column(
        modifier =
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .dashedBorder(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0, cornerRadius = 10.dp),
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
