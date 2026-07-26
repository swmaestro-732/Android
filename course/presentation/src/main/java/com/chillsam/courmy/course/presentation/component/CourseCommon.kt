package com.chillsam.courmy.course.presentation.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 라운드 사각형 점선 테두리(장소 더 담기 · 사진 추가 슬롯). Compose 기본 border 는 점선 미지원이라 직접 그린다. */
fun Modifier.dashedBorder(
    color: Color,
    cornerRadius: Dp,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
): Modifier =
    drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style =
                Stroke(
                    width = strokeWidth.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength.toPx(), gapLength.toPx())),
                ),
        )
    }

/** "① 코스 정보" 형태의 번호 섹션 헤더. */
@Composable
fun CourseSectionHeader(
    number: Int,
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(18.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = number.toString(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentOnAccent,
            )
        }
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
    }
}

/** 상단바: ✕ 닫기 · 새 코스 만들기 · 임시저장. */
@Composable
fun CourseTopBar(
    onClose: () -> Unit,
    onSaveDraft: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                    .clickable(onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "✕",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            )
        }
        DsText(
            text = "새 코스 만들기",
            modifier = Modifier.weight(1f),
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
        DsText(
            text = "임시저장",
            modifier = Modifier.clickable(onClick = onSaveDraft).padding(6.dp),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
    }
}

/** 장소 사이 경로 커넥터: "도보 9분 · 경로 자동". */
@Composable
fun CourseRouteConnector(
    text: String,
    modifier: Modifier = Modifier,
) {
    DsText(
        text = "＋ $text",
        modifier = modifier.fillMaxWidth().padding(vertical = 2.dp),
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
    )
}

/** 카드 안 입력 항목 라벨(코스 이름·설명·썸네일·태그 등). */
@Composable
internal fun FieldLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
    )
}

/**
 * 사진 담기 행: 담은 사진 썸네일들 + 남은 자리가 있으면 "＋ n/max" 추가 슬롯.
 * 코스 썸네일(①)·장소 사진(②)에서 공통으로 쓴다. 실제 선택은 시스템 Photo Picker.
 */
@Composable
internal fun CoursePhotoRow(
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
