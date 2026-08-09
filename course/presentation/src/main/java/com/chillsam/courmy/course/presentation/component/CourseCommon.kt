package com.chillsam.courmy.course.presentation.component

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.media.rememberAccentedImagePicker
import com.chillsam.courmy.common.presentation.media.rememberAccentedImagesPicker
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

/**
 * 장소 사이 경로 커넥터: 도보 아이콘 + "도보 9분".
 * 걸어서 갈 수 없는 구간이면 호출부가 그 문구를 그대로 넘긴다.
 */
@Composable
fun CourseRouteConnector(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_walk_24),
            contentDescription = null,
            tint = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
            modifier = Modifier.size(14.dp),
        )
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
        )
    }
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
 * 사진 담기 행: 담은 사진 썸네일들 + 남은 자리가 있으면 추가 슬롯.
 * 코스 썸네일(①)·장소 사진(②)에서 공통으로 쓴다. 실제 선택은 시스템 Photo Picker.
 * [showCount] 가 false 면 "n/max" 표시 없이 "＋" 만 보여준다(단일 선택 썸네일용).
 */
@Composable
internal fun CoursePhotoRow(
    photos: List<String>,
    maxPhotos: Int,
    onPhotosChange: (List<String>) -> Unit,
    showCount: Boolean = true,
) {
    // 6장을 다 담으면 슬롯(56dp)이 화면 가로를 넘어 추가 슬롯이 잘린다. 가로로 밀어 볼 수 있게 한다.
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        photos.forEach { uri ->
            FilledPhotoSlot(uri = uri, onRemove = { onPhotosChange(photos - uri) })
        }
        if (photos.size < maxPhotos) {
            AddPhotoSlot(
                count = photos.size,
                max = maxPhotos,
                showCount = showCount,
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
    showCount: Boolean,
    onPicked: (List<String>) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val remaining = max - count
    // 선택한 사진은 5MB 이하만 통과시킨다(용량 조회는 IO 에서, 초과·미상은 제외 후 토스트 안내).
    // PickMultipleVisualMedia 는 maxItems >= 2 를 요구하므로 남은 자리가 1 이면 단일 선택 피커를 쓴다.
    val multiLauncher =
        rememberAccentedImagesPicker(remaining.coerceAtLeast(2)) { uris ->
            if (uris.isNotEmpty()) {
                scope.launch {
                    val within = filterWithinSizeLimit(context, uris)
                    if (within.isNotEmpty()) onPicked(within)
                }
            }
        }
    val singleLauncher =
        rememberAccentedImagePicker { uri ->
            if (uri != null) {
                scope.launch {
                    val within = filterWithinSizeLimit(context, listOf(uri))
                    if (within.isNotEmpty()) onPicked(within)
                }
            }
        }
    // 시스템 포토피커에 앱 강조색 + 선택 순서 번호 배지를 입힌다(미지원 기기는 무시하고 정상 동작).
    val accentColor =
        DesignSystemThemeImpl.designSystemColor.bgAccent
            .toArgb()
            .toLong() and 0xFFFFFFFFL
    Column(
        modifier =
            Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .dashedBorder(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0, cornerRadius = 10.dp)
                .clickable {
                    val request =
                        PickVisualMediaRequest
                            .Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .setOrderedSelection(true)
                            .setAccentColor(accentColor)
                            .build()
                    if (remaining <= 1) singleLauncher.launch(request) else multiLauncher.launch(request)
                },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = "＋",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentAccent,
        )
        if (showCount) {
            DsText(
                text = "$count/$max",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentAccent,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/** 코스 이미지 1장당 최대 용량(5MB). */
private const val MAX_IMAGE_BYTES = 5L * 1024 * 1024

/** 5MB 이하 이미지 URI 만 문자열로 반환한다. 용량 조회는 IO 에서 하고, 초과·미상은 제외한 뒤 토스트로 알린다. */
private suspend fun filterWithinSizeLimit(
    context: Context,
    uris: List<Uri>,
): List<String> {
    if (uris.isEmpty()) return emptyList()
    // 용량을 확인할 수 없는(null) URI 는 5MB 우회를 막기 위해 제외한다.
    val within =
        withContext(Dispatchers.IO) {
            uris.filter { uri ->
                val size = uriSizeBytes(context, uri)
                size != null && size <= MAX_IMAGE_BYTES
            }
        }
    if (within.size < uris.size) {
        Toast.makeText(context, "5MB 이하 이미지만 추가할 수 있어요.", Toast.LENGTH_SHORT).show()
    }
    return within.map { it.toString() }
}

/** content URI 의 바이트 크기. SIZE 컬럼 → 파일 디스크립터 순으로 조회하고, 끝내 알 수 없으면 null. */
private fun uriSizeBytes(
    context: Context,
    uri: Uri,
): Long? {
    val fromColumn =
        context.contentResolver
            .query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            ?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (index >= 0 && cursor.moveToFirst() && !cursor.isNull(index)) cursor.getLong(index) else null
            }
    if (fromColumn != null) return fromColumn
    return runCatching {
        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use { it.length }
    }.getOrNull()?.takeIf { it >= 0 }
}
