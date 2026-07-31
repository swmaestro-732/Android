package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.PlaceDetailVO
import kotlinx.coroutines.launch

private val SHEET_TOP_SHAPE = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

/** 사진이 없을 때 자리만 잡아둘 placeholder 개수. */
private const val PHOTO_PLACEHOLDER_COUNT = 4

/** 장소 상세 시트의 액션 콜백 묶음. 파라미터 수를 줄이려 홀더로 전달한다. */
@Immutable
data class PlaceDetailSheetActions(
    val onSave: () -> Unit,
    val onShare: () -> Unit,
    val onDirections: () -> Unit,
    val onAddToCourse: () -> Unit,
)

/**
 * 장소 상세 바텀시트(Figma FS-10-Sheet). 코스 상세의 장소 행 화살표를 누르면 하단에서 올라온다.
 * 상단(히어로)에서 아래로 드래그하면 닫히고, 콘텐츠는 스크롤한다. 표시 전용이며 액션은 [actions]로 주입한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailSheet(
    place: PlaceDetailVO,
    onDismiss: () -> Unit,
    actions: PlaceDetailSheetActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    // 닫기 버튼으로 닫을 때도 시트가 아래로 내려가는 모션을 태운 뒤 실제로 닫는다.
    val animatedDismiss = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
        Unit
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        // 커스텀 히어로 위에 핸들·오버레이 버튼을 직접 그리므로 기본 드래그 핸들은 끈다.
        dragHandle = null,
        containerColor = color.bgDefaultLevel0,
        shape = SHEET_TOP_SHAPE,
        // 히어로가 상단까지 꽉 차도록 시트 상단 흰 여백(inset)을 없앤다(히어로에서 statusBar 를 직접 처리).
        contentWindowInsets = { WindowInsets(0) },
    ) {
        // 히어로부터 하단 버튼까지 한 덩어리로 스크롤한다. 최상단에서 아래로 드래그하면 시트가 닫힌다.
        // 스크롤 끝에서 시트 드래그와 오버스크롤 바운스가 충돌해 흔들리는 것을 막으려 오버스크롤을 끈다.
        CompositionLocalProvider(LocalOverscrollFactory provides null) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                PlaceHero(
                    place = place,
                    onCollapse = animatedDismiss,
                    onShare = actions.onShare,
                    onSave = actions.onSave,
                )
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    PlaceTitleBlock(place = place)
                    PlaceActionButtons(onSave = actions.onSave, onDirections = actions.onDirections)
                    PlacePhotoRow(imageUrls = place.imageUrls)
                    if (place.tags.isNotEmpty()) PlaceTagRow(tags = place.tags)
                    PlaceInfoCard(address = place.address, hoursText = place.hoursText)
                }
                AddToCourseBar(onAddToCourse = actions.onAddToCourse)
            }
        }
    }
}

/** 상단 히어로: 이미지 + 그래버 핸들 + 오버레이 버튼(닫기·공유·저장). */
@Composable
private fun PlaceHero(
    place: PlaceDetailVO,
    onCollapse: () -> Unit,
    onShare: () -> Unit,
    onSave: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(200.dp),
    ) {
        PlaceImage(url = place.heroImageUrl, modifier = Modifier.fillMaxWidth().height(200.dp), shape = SHEET_TOP_SHAPE)
        // 그래버 핸들·오버레이 버튼은 상태바를 피해 배치한다(히어로는 상태바 밑까지 그려진다).
        Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.bgDefaultLevel1.copy(alpha = 0.9f)),
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SheetCircleButton(
                    iconRes = R.drawable.ic_chevron_down_24,
                    contentDescription = "닫기",
                    onClick = onCollapse,
                )
                Spacer(Modifier.weight(1f))
                SheetCircleButton(iconRes = R.drawable.ic_share_24, contentDescription = "공유", onClick = onShare)
                Spacer(Modifier.width(10.dp))
                // 아직 저장 전이라 채운 아이콘 대신 아웃라인 북마크만 보여준다.
                SheetCircleButton(iconRes = R.drawable.ic_tab_bookmark_24, contentDescription = "저장", onClick = onSave)
            }
        }
    }
}

@Composable
private fun SheetCircleButton(
    iconRes: Int,
    contentDescription: String?,
    onClick: () -> Unit,
    tint: Color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** 제목·카테고리 + 평점 + 영업 상태 블록. */
@Composable
private fun PlaceTitleBlock(place: PlaceDetailVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = place.category,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DsText(
                    text = "★",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentRating,
                )
                DsText(
                    text = place.rating,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                )
            }
            DsText(
                text = "리뷰 ${place.reviewCountText} · 저장 ${place.savedCountText}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StatusPill(isOpen = place.isOpen, text = place.openStatusText)
            DsText(
                text = "${place.walkText} · ${place.areaText}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
    }
}

/** 영업 상태 pill: 상태 점 + 문구. 영업 중이면 success 색으로 강조한다. */
@Composable
private fun StatusPill(
    isOpen: Boolean,
    text: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val accent = if (isOpen) color.contentSuccess else color.contentDefaultLevel2
    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(accent.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(accent),
        )
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = accent,
        )
    }
}

/** 저장 · 길찾기 아웃라인 버튼 2개(가로 균등). */
@Composable
private fun PlaceActionButtons(
    onSave: () -> Unit,
    onDirections: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedActionButton(
            iconRes = R.drawable.ic_tab_bookmark_24,
            text = "저장",
            onClick = onSave,
            modifier = Modifier.weight(1f),
        )
        OutlinedActionButton(
            iconRes = R.drawable.ic_location_24,
            text = "길찾기",
            onClick = onDirections,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun OutlinedActionButton(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier =
            modifier
                .height(48.dp)
                .clip(shape)
                .border(1.dp, color.borderDefaultLevel0, shape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = color.contentDefaultLevel1,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(8.dp))
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
    }
}

/** 사진 가로 스크롤. 사진이 없으면 placeholder 로 자리를 잡아 둔다(이미지 삽입 영역). */
@Composable
private fun PlacePhotoRow(imageUrls: List<String>) {
    val photos = imageUrls.ifEmpty { List(PHOTO_PLACEHOLDER_COUNT) { "" } }
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        photos.forEach { url ->
            PlaceImage(url = url, modifier = Modifier.size(120.dp))
        }
    }
}

/** 특징 태그 pill 목록. 넘치면 다음 줄로 흐른다. */
@Composable
private fun PlaceTagRow(tags: List<String>) {
    val color = DesignSystemThemeImpl.designSystemColor
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags.forEach { tag ->
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.bgDefaultLevel1)
                        .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(9999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
            ) {
                DsText(
                    text = tag,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel1,
                )
            }
        }
    }
}

/** 주소·영업시간 정보 카드. */
@Composable
private fun PlaceInfoCard(
    address: String,
    hoursText: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(1.dp, color.borderDefaultLevel0, shape)
                .background(color.bgDefaultLevel1),
    ) {
        InfoRow(iconRes = R.drawable.ic_location_24, text = address)
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color.borderDefaultLevel0),
        )
        InfoRow(iconRes = R.drawable.ic_clock_24, text = hoursText)
    }
}

@Composable
private fun InfoRow(
    iconRes: Int,
    text: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = color.contentDefaultLevel2,
            modifier = Modifier.size(20.dp),
        )
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel1,
        )
    }
}

/** 하단 고정 액션바: "＋ 코스에 추가" 기본 버튼. */
@Composable
private fun AddToCourseBar(onAddToCourse: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.background(color.bgDefaultLevel1)) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color.borderDefaultLevel0),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(color.bgAccent)
                        .clickable(onClick = onAddToCourse),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_24),
                    contentDescription = null,
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                DsText(
                    text = "코스에 추가",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentOnAccent,
                )
            }
        }
    }
}

/**
 * 시트 공용 이미지. URL 이 있으면 Coil 로 로드하고, 비면 placeholder 배경만 그린다.
 * 로딩 전/실패 시에도 [imagePlaceholder] 배경이 자리를 잡는다.
 */
@Composable
private fun PlaceImage(
    url: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
) {
    val placeholder =
        modifier
            .clip(shape)
            .background(DesignSystemThemeImpl.designSystemColor.imagePlaceholder)
    if (url.isBlank()) {
        Box(modifier = placeholder)
    } else {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = placeholder,
            contentScale = ContentScale.Crop,
        )
    }
}
