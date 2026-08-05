package com.chillsam.courmy.course.presentation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.presentation.component.PlaceDetailSheet
import com.chillsam.courmy.course.presentation.component.PlaceDetailSheetActions
import com.chillsam.courmy.course.presentation.component.dashedBorder
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.NaverMapComposable
import com.naver.maps.map.compose.PathOverlay
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberUpdatedMarkerState
import com.naver.maps.map.overlay.OverlayImage

/**
 * 코스 상세 화면(Figma FS-11, 펼친 버전). 히어로(제목·작성자) → 요약 스탯 → 소개 →
 * "코스 속 장소" 목록(접기/펼치기) → "코스 경로" 지도 → 하단 "코스 저장하기" 액션바로 구성한다.
 * 표시 전용 화면으로 [detail] 데이터를 그대로 렌더링한다.
 */
@Composable
fun CourseDetailScreen(
    detail: CourseDetailVO,
    isSaving: Boolean,
    isDeleting: Boolean,
    actions: CourseDetailActions,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    // 장소 행 화살표를 누르면 해당 장소 상세 시트를 띄운다(null 이면 시트 닫힘).
    var selectedPlace by remember { mutableStateOf<CourseDetailPlaceVO?>(null) }
    val placeDetailViewModel: PlaceDetailViewModel = hiltViewModel()
    val placeDetailState by placeDetailViewModel.uiState.collectAsStateWithLifecycle()
    // 시트 내부 액션(저장·길찾기·코스 추가 등)은 아직 미연동이라 안내만 한다. [wiki-needed]
    val notReady = { Toast.makeText(context, "준비 중이에요", Toast.LENGTH_SHORT).show() }
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
        ) {
            DetailHero(detail = detail, onBack = actions.onBack)
            // 작성자 라인은 화면 가로 전체를 채우는 흰색 밴드라 좌우 패딩 밖에 둔다.
            AuthorRow(
                detail = detail,
                onAuthorClick = actions.onAuthorClick,
                onFollow = actions.onFollowAuthor,
            )
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                StatsRow(detail = detail)
                DsText(
                    text = detail.description,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel1,
                    maxLines = Int.MAX_VALUE,
                )
                PlacesSection(
                    places = detail.places,
                    authorName = detail.authorName,
                    onPlaceClick = { place ->
                        selectedPlace = place
                        // 도보 안내는 장소가 아니라 이 코스의 다음 장소까지 값이라 여기서 넘긴다.
                        placeDetailViewModel.open(
                            placeId = place.placeId,
                            walkText = place.walkToNextText.orEmpty(),
                        )
                    },
                )
                CourseRouteSection(places = detail.places)
                Spacer(Modifier.height(14.dp))
            }
        }
        DetailBottomBar(
            isMine = detail.isMine,
            isSaved = detail.isSaved,
            isSaving = isSaving,
            isDeleting = isDeleting,
            actions = actions,
        )
    }
    if (selectedPlace != null) {
        val dismiss = {
            selectedPlace = null
            placeDetailViewModel.clear()
        }
        // 로드가 끝나기 전에는 시트를 띄우지 않는다(빈 값으로 잠깐 그려졌다 바뀌는 깜빡임 방지).
        placeDetailState.place?.let { place ->
            PlaceDetailSheet(
                place = place,
                onDismiss = dismiss,
                actions =
                    PlaceDetailSheetActions(
                        onSave = notReady,
                        onShare = notReady,
                        onDirections = notReady,
                        onAddToCourse = notReady,
                    ),
            )
        }
        // 실패하면 토스트로 알리고 시트를 닫는다(빈 시트를 남겨 두지 않는다).
        LaunchedEffect(placeDetailState.errorMessage) {
            placeDetailState.errorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }
    }
}

/** 상단 히어로: 어두운 배경 위 뒤로가기, 카테고리 칩, 코스 제목. */
@Composable
private fun DetailHero(
    detail: CourseDetailVO,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(232.dp)
                .background(color.contentDefaultLevel0),
    ) {
        if (detail.coverImageUrl.isNotBlank()) {
            AsyncImage(
                model = detail.coverImageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onError = { Log.w("CourseDetail", "배너 이미지 로드 실패: ${detail.coverImageUrl}", it.result.throwable) },
            )
            // 밝은 커버 위에서도 흰 제목이 읽히도록 하단으로 갈수록 어두워지는 스크림.
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    color.contentDefaultLevel0.copy(alpha = 0f),
                                    color.contentDefaultLevel0.copy(alpha = 0.6f),
                                ),
                            ),
                        ),
            )
        }
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            HeroCircleButton(
                iconRes = R.drawable.ic_chevron_left_24,
                contentDescription = "뒤로가기",
                onClick = onBack,
            )
        }
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.bgDefaultLevel1)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                DsText(
                    text = detail.category,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel1,
                )
            }
            DsText(
                text = detail.title,
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentOnAccent,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun HeroCircleButton(
    iconRes: Int,
    contentDescription: String?,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = color.contentDefaultLevel0,
            modifier = Modifier.size(22.dp),
        )
    }
}

/**
 * 코스 상세 공용 이미지. URL 이 있으면 Coil 로 로드하고, 비어 있으면 placeholder 배경만 그린다.
 * 로딩 전/실패 시에도 [color.imagePlaceholder] 배경이 자리를 잡는다.
 */
@Composable
private fun CourseImage(
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
            // 어떤 이미지 URL 이 왜 안 떴는지(404/타임아웃 등)를 로그로 남긴다.
            onError = { Log.w("CourseDetail", "이미지 로드 실패: $url", it.result.throwable) },
        )
    }
}

/** 작성자 행: 아바타 · 이름/핸들 · 팔로우 버튼. */
@Composable
private fun AuthorRow(
    detail: CourseDetailVO,
    onAuthorClick: () -> Unit,
    onFollow: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .clickable(onClick = onAuthorClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CourseImage(
                url = detail.authorImageUrl,
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DsText(
                    text = detail.authorName,
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                )
                DsText(
                    text = detail.authorHandle,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
        }
        // 내 코스면 나를 팔로우할 수 없고, 이미 팔로우 중이면 다시 노출할 이유가 없다.
        if (!detail.isMine && !detail.isFollowingAuthor) {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.bgAccent)
                        .clickable(onClick = onFollow)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                DsText(
                    text = "팔로우",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentOnAccent,
                )
            }
        }
    }
}

/** 요약 스탯 칩 3개: 장소 수 · 도보 · 따라감. 흰 배경 + 옅은 테두리 pill. */
@Composable
private fun StatsRow(detail: CourseDetailVO) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        StatChip(text = detail.placeCountText)
        StatChip(text = detail.walkText)
        StatChip(text = detail.followerText)
    }
}

@Composable
private fun StatChip(text: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(9999.dp)
    Box(
        modifier =
            Modifier
                .clip(shape)
                .background(color.bgDefaultLevel1)
                .border(1.dp, color.borderDefaultLevel0, shape)
                .padding(horizontal = 13.dp, vertical = 6.dp),
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel1,
        )
    }
}

/** 접힘 상태에서 콤팩트하게 보여줄 장소 수. 초과분은 "더보기"로 접는다. */
private const val COLLAPSED_VISIBLE_PLACES = 2

/** 이 수를 초과(4곳 이상)할 때만 "나머지 N곳 더보기"로 접는다. 3곳까지는 전부 노출. */
private const val COLLAPSE_THRESHOLD = 3

/** "코스 속 장소" 섹션: 헤더(접기/펼치기) + 펼침(사진·팁) / 접힘(콤팩트 타임라인) 목록. */
@Composable
private fun PlacesSection(
    places: List<CourseDetailPlaceVO>,
    authorName: String,
    onPlaceClick: (CourseDetailPlaceVO) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    val color = DesignSystemThemeImpl.designSystemColor
    // 소개 문단과의 간격을 조금 더 준다.
    Column(
        modifier = Modifier.padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // "코스 속 장소" 는 기본색, 개수("N곳") 만 accent 로 강조한다.
            Row(modifier = Modifier.weight(1f)) {
                DsText(
                    text = "코스 속 장소 ",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                )
                DsText(
                    text = "${places.size}곳",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentAccent,
                )
            }
            DsText(
                text = if (expanded) "접기" else "펼치기",
                modifier = Modifier.clickable { expanded = !expanded },
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
            )
        }
        if (expanded) {
            places.forEach { place ->
                DetailPlaceItem(place = place, authorName = authorName, onClick = { onPlaceClick(place) })
                place.walkToNextText?.let { RouteConnector(text = it) }
            }
        } else {
            CollapsedPlaces(
                places = places,
                onExpand = { expanded = true },
                onPlaceClick = onPlaceClick,
            )
        }
    }
}

/**
 * 접힘 목록: 앞 [COLLAPSED_VISIBLE_PLACES]곳만 헤더 행 + "나머지 N곳 더보기".
 * 펼침 상태([DetailPlaceItem])와 장소 헤더의 위치·디자인을 동일하게 맞춰, 접기/펼치기 시 겹치도록 한다.
 */
@Composable
private fun CollapsedPlaces(
    places: List<CourseDetailPlaceVO>,
    onExpand: () -> Unit,
    onPlaceClick: (CourseDetailPlaceVO) -> Unit,
) {
    // 3곳까지는 전부 노출, 4곳 이상일 때만 앞 [COLLAPSED_VISIBLE_PLACES]곳 + "나머지 N곳 더보기".
    val visible = if (places.size > COLLAPSE_THRESHOLD) places.take(COLLAPSED_VISIBLE_PLACES) else places
    val remaining = places.size - visible.size
    val lineColor = DesignSystemThemeImpl.designSystemColor.bgAccent
    // 각 노드(배지)의 세로 중심을 측정해, 노드끼리 잇는 세로 연결선을 배경으로 그린다.
    // 행 자체는 펼침 헤더와 100% 동일한 레이아웃(패딩 없음)이라 접기/펼치기 시 위치가 어긋나지 않는다.
    var columnTop by remember { mutableStateOf(0f) }
    val nodeCenters = remember { mutableStateMapOf<Int, Float>() }
    val nodeCount = visible.size + if (remaining > 0) 1 else 0

    fun badgeModifier(index: Int): Modifier =
        Modifier.onGloballyPositioned { coords ->
            nodeCenters[index] = coords.positionInWindow().y - columnTop + coords.size.height / 2f
        }

    Column(
        modifier =
            Modifier
                .onGloballyPositioned { columnTop = it.positionInWindow().y }
                .drawBehind {
                    val centers = (0 until nodeCount).mapNotNull { nodeCenters[it] }
                    if (centers.size >= 2) {
                        val x = 12.dp.toPx()
                        drawLine(
                            color = lineColor,
                            start = Offset(x, centers.first()),
                            end = Offset(x, centers.last()),
                            strokeWidth = 2.dp.toPx(),
                        )
                    }
                },
    ) {
        visible.forEachIndexed { index, place ->
            CompactPlaceRow(
                place = place,
                badgeModifier = badgeModifier(index),
                onClick = { onPlaceClick(place) },
            )
            // 노드 사이(다음 장소로 이어질 때)에 도보 시간을 넣는다. 간격도 이 행이 만든다.
            if (index < visible.lastIndex || remaining > 0) {
                WalkLabel(text = place.walkToNextText)
            }
        }
        if (remaining > 0) {
            MorePlacesRow(
                remaining = remaining,
                onClick = onExpand,
                badgeModifier = badgeModifier(visible.size),
            )
        }
    }
}

/** 접힘 타임라인에서 노드와 노드 사이에 놓이는 도보 시간. 이름과 같은 x(선 오른쪽)에 정렬한다. */
@Composable
private fun WalkLabel(text: String?) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 36.dp, top = 10.dp, bottom = 10.dp),
    ) {
        if (text != null) {
            DsText(
                text = text,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
            )
        }
    }
}

/** 콤팩트 장소 행: 펼침 상태 [DetailPlaceItem] 의 헤더(번호·이름/카테고리·›)와 동일한 레이아웃. */
@Composable
private fun CompactPlaceRow(
    place: CourseDetailPlaceVO,
    onClick: () -> Unit,
    badgeModifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                badgeModifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color.bgAccent),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = place.order.toString(),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentOnAccent,
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = place.category,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        PlaceDetailChevron(onClick = onClick)
    }
}

/**
 * 장소 상세로 이동하는 화살표. 아이콘(20dp)은 그대로 두되 접근성을 위해 터치 영역을 48dp 로 넓힌다.
 * 아이콘은 오른쪽 끝에 정렬해 기존 레이아웃(행 우측 끝)과 위치를 맞춘다.
 */
@Composable
private fun PlaceDetailChevron(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = "장소 상세",
            tint = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
            modifier = Modifier.size(20.dp),
        )
    }
}

/** "나머지 N곳 더보기" 행: 세로 연결선으로 이어진 점선 원(⋯) + 강조 텍스트. 누르면 펼친다. */
@Composable
private fun MorePlacesRow(
    remaining: Int,
    onClick: () -> Unit,
    badgeModifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                badgeModifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(color.bgDefaultLevel0)
                    .dashedBorder(color.contentAccent, cornerRadius = 12.dp, strokeWidth = 1.5.dp),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "⋯",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
            )
        }
        Box {
            DsText(
                text = "나머지 ${remaining}곳 더보기",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentAccent,
            )
        }
    }
}

/** 장소 1건: 순번·이름·카테고리 헤더 + 사진 + "Tip.{작성자}". 헤더 화살표로 장소 상세 시트를 연다. */
@Composable
private fun DetailPlaceItem(
    place: CourseDetailPlaceVO,
    authorName: String,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color.bgAccent),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = place.order.toString(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentOnAccent,
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DsText(
                    text = place.name,
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                )
                DsText(
                    text = place.category,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
            PlaceDetailChevron(onClick = onClick)
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            val urls = place.imageUrls
            if (urls.isEmpty()) {
                CourseImage(url = "", modifier = Modifier.fillMaxSize())
            } else {
                // 사진이 2장 이상이면 가로로 스와이프해 넘긴다.
                val pagerState = rememberPagerState(pageCount = { urls.size })
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    CourseImage(url = urls[page], modifier = Modifier.fillMaxSize())
                }
                // 현재 페이지를 반영하는 사진 인덱스 배지 (예: "2/2").
                Box(
                    modifier =
                        Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(color.contentDefaultLevel0)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    DsText(
                        text = "${pagerState.currentPage + 1}/${urls.size}",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentOnAccent,
                    )
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier =
                    Modifier
                        .width(3.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.bgAccent),
            )
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                DsText(
                    text = "Tip.$authorName",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentAccent,
                )
                DsText(
                    text = place.tip,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel1,
                    maxLines = Int.MAX_VALUE,
                )
            }
        }
    }
}

/** 장소 사이 도보 커넥터: 라인 — "↓ 도보 6분" — 라인. */
@Composable
private fun RouteConnector(text: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(color.borderDefaultLevel0),
        )
        DsText(
            text = "↓ $text",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel3,
        )
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(color.borderDefaultLevel0),
        )
    }
}

/**
 * "코스 경로" 섹션: 헤더 + 네이버 지도. 좌표가 있는 장소에 순번 핀을 찍고 경로선으로 잇는다.
 * 좌표가 하나도 없으면(예: API 미연동) 지도 대신 placeholder 를 보여준다.
 */
@Composable
private fun CourseRouteSection(places: List<CourseDetailPlaceVO>) {
    val color = DesignSystemThemeImpl.designSystemColor
    val points =
        places.mapNotNull { place ->
            val lat = place.latitude
            val lng = place.longitude
            if (lat != null && lng != null) place.order to LatLng(lat, lng) else null
        }
    var showFullMap by remember { mutableStateOf(false) }
    // "코스 경로" 헤더 위에 장소 목록과의 간격을 조금 더 준다.
    Column(
        modifier = Modifier.padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsText(
                text = "코스 경로",
                modifier = Modifier.weight(1f),
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            // 인라인 지도는 조작 불가라, 확대·이동해서 보려면 전체화면 지도로 연다.
            if (points.isNotEmpty()) {
                DsText(
                    text = "자세히 보기",
                    modifier = Modifier.clickable { showFullMap = true },
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentAccent,
                )
            }
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.imagePlaceholder)
                    .then(if (points.isNotEmpty()) Modifier.clickable { showFullMap = true } else Modifier),
            contentAlignment = Alignment.Center,
        ) {
            if (points.isEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.ic_tab_map_24),
                    contentDescription = "코스 경로 지도",
                    tint = color.contentDefaultLevel3,
                    modifier = Modifier.size(40.dp),
                )
            } else {
                CourseRouteMap(points = points, lineColor = color.bgAccent)
            }
            // 지도가 덮지 않도록 테두리를 맨 위에 덧그린다.
            Box(
                modifier =
                    Modifier
                        .matchParentSize()
                        .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(14.dp)),
            )
        }
    }
    if (showFullMap && points.isNotEmpty()) {
        CourseRouteFullMapDialog(
            points = points,
            lineColor = color.bgAccent,
            onDismiss = { showFullMap = false },
        )
    }
}

/** 좌표들의 중심에 두고, 2개 이상이면 모든 핀이 들어오도록 경계(fitBounds)에 맞춘 카메라 상태. */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun rememberRouteCameraState(coords: List<LatLng>): CameraPositionState {
    val center =
        LatLng(
            coords.map { it.latitude }.average(),
            coords.map { it.longitude }.average(),
        )
    val cameraPositionState =
        rememberCameraPositionState {
            position = CameraPosition(center, 14.0)
        }
    LaunchedEffect(coords) {
        if (coords.size >= 2) {
            val bounds = LatLngBounds.Builder().apply { coords.forEach { include(it) } }.build()
            cameraPositionState.move(CameraUpdate.fitBounds(bounds, ROUTE_MAP_FIT_PADDING))
        }
    }
    return cameraPositionState
}

/** 지도 위 오버레이: 장소 순번 핀 + 경로선. 인라인/전체 지도 공통. */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
@NaverMapComposable
private fun RouteOverlays(
    points: List<Pair<Int, LatLng>>,
    lineColor: Color,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val density = LocalDensity.current.density
    val fillArgb = lineColor.toArgb()
    val textArgb = color.contentOnAccent.toArgb()
    val coords = points.map { it.second }
    if (coords.size >= 2) {
        PathOverlay(
            coords = coords,
            width = 3.dp,
            outlineWidth = 1.dp,
            color = lineColor,
            outlineColor = lineColor,
        )
    }
    points.forEach { (order, position) ->
        // 기본 삼각형 핀 대신 순번 숫자를 넣은 원형 마커(앱 장소 번호 배지와 동일 톤).
        val icon =
            remember(order, fillArgb, textArgb, density) {
                numberedMarkerIcon(
                    number = order,
                    fillArgb = fillArgb,
                    textArgb = textArgb,
                    sizePx = (28 * density).toInt(),
                )
            }
        Marker(
            state = rememberUpdatedMarkerState(position = position),
            icon = icon,
            anchor = Offset(0.5f, 0.5f),
        )
    }
}

/** 순번 숫자가 들어간 원형 마커 아이콘을 그린다(초록 원 + 흰 숫자 + 흰 테두리). */
private fun numberedMarkerIcon(
    number: Int,
    fillArgb: Int,
    textArgb: Int,
    sizePx: Int,
): OverlayImage {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val c = sizePx / 2f
    val strokeWidth = sizePx * 0.08f
    val radius = c - strokeWidth
    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = fillArgb }
    val stroke =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textArgb
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
        }
    canvas.drawCircle(c, c, radius, fill)
    canvas.drawCircle(c, c, radius, stroke)
    val text =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textArgb
            textAlign = Paint.Align.CENTER
            textSize = sizePx * 0.5f
            typeface = Typeface.DEFAULT_BOLD
        }
    val fm = text.fontMetrics
    canvas.drawText(number.toString(), c, c - (fm.ascent + fm.descent) / 2f, text)
    return OverlayImage.fromBitmap(bitmap)
}

/** 인라인 코스 경로 지도: 모든 핀을 보여주되 조작(제스처·컨트롤)은 막는다. */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun CourseRouteMap(
    points: List<Pair<Int, LatLng>>,
    lineColor: Color,
) {
    NaverMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = rememberRouteCameraState(points.map { it.second }),
        uiSettings =
            MapUiSettings(
                isScrollGesturesEnabled = false,
                isZoomGesturesEnabled = false,
                isTiltGesturesEnabled = false,
                isRotateGesturesEnabled = false,
                isStopGesturesEnabled = false,
                isZoomControlEnabled = false,
                isScaleBarEnabled = false,
                isCompassEnabled = false,
                isLogoClickEnabled = false,
            ),
    ) {
        RouteOverlays(points = points, lineColor = lineColor)
    }
}

/** "자세히 보기" 전체화면 지도: 확대·이동 가능한 인터랙티브 지도 + 닫기 버튼. */
@OptIn(ExperimentalNaverMapApi::class)
@Composable
private fun CourseRouteFullMapDialog(
    points: List<Pair<Int, LatLng>>,
    lineColor: Color,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val cameraPositionState = rememberRouteCameraState(points.map { it.second })
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color.bgDefaultLevel1),
        ) {
            NaverMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                // 네이티브 줌 컨트롤은 끄고 아래에서 커스텀 버튼(우하단)으로 대체한다.
                uiSettings = MapUiSettings(isZoomControlEnabled = false),
            ) {
                RouteOverlays(points = points, lineColor = lineColor)
            }
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(16.dp),
            ) {
                HeroCircleButton(
                    iconRes = R.drawable.close_small_24,
                    contentDescription = "닫기",
                    onClick = onDismiss,
                )
            }
            // 확대/축소 버튼 — 우하단.
            Column(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.bgDefaultLevel1),
            ) {
                MapZoomButton(symbol = "+", onClick = { cameraPositionState.move(CameraUpdate.zoomIn()) })
                Box(
                    modifier =
                        Modifier
                            .width(40.dp)
                            .height(1.dp)
                            .background(color.borderDefaultLevel0),
                )
                MapZoomButton(symbol = "−", onClick = { cameraPositionState.move(CameraUpdate.zoomOut()) })
            }
        }
    }
}

/** 전체 지도용 확대/축소 버튼 1개. */
@Composable
private fun MapZoomButton(
    symbol: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.size(40.dp).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = symbol,
            style = DesignSystemThemeImpl.typeScale.textRegularM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
    }
}

/** fitBounds 여백(px). 핀 아이콘·캡션이 지도 가장자리에 잘리지 않게 넉넉히 둔다. */
private const val ROUTE_MAP_FIT_PADDING = 96

/**
 * 하단 고정 액션바: 공유 버튼 + 코스 저장 버튼. 상단에 옅은 구분선.
 * [isSaved] 면 "저장됨"으로 바뀌고, 누르면 저장이 취소된다.
 */
@Composable
private fun DetailBottomBar(
    isMine: Boolean,
    isSaved: Boolean,
    isSaving: Boolean,
    isDeleting: Boolean,
    actions: CourseDetailActions,
) {
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val buttonShape = RoundedCornerShape(15.dp)
            Box(
                modifier =
                    Modifier
                        .width(52.dp)
                        .height(56.dp)
                        .clip(buttonShape)
                        .border(1.dp, color.borderDefaultLevel0, buttonShape)
                        .background(color.bgDefaultLevel1)
                        .clickable(onClick = actions.onShare),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share_24),
                    contentDescription = "공유",
                    tint = color.contentDefaultLevel1,
                    modifier = Modifier.size(22.dp),
                )
            }
            if (isMine) {
                // 내 코스는 저장할 이유가 없다. 편집·삭제를 대신 노출한다.
                BottomBarButton(
                    text = "코스 편집하기",
                    shape = buttonShape,
                    background = color.bgAccent,
                    contentColor = color.contentOnAccent,
                    enabled = !isDeleting,
                    onClick = actions.onEditCourse,
                )
                BottomBarButton(
                    text = if (isDeleting) "삭제 중…" else "코스 삭제하기",
                    shape = buttonShape,
                    background = color.bgDefaultLevel1,
                    contentColor = color.contentDanger,
                    enabled = !isDeleting,
                    borderColor = color.borderDefaultLevel0,
                    onClick = actions.onDeleteCourse,
                )
            } else {
                BottomBarButton(
                    text = if (isSaved) "저장됨" else "코스 저장하기",
                    shape = buttonShape,
                    background = if (isSaved) color.bgAccentSubtle else color.bgAccent,
                    contentColor = if (isSaved) color.contentDefaultLevel1 else color.contentOnAccent,
                    enabled = !isSaving,
                    iconRes = if (isSaved) R.drawable.ic_bookmark_filled_24 else R.drawable.ic_tab_bookmark_24,
                    onClick = actions.onSaveCourse,
                )
            }
        }
    }
}

/** 하단 액션바의 가로 확장 버튼. 저장·편집·삭제가 같은 형태를 공유한다. */
@Composable
private fun RowScope.BottomBarButton(
    text: String,
    shape: Shape,
    background: Color,
    contentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    iconRes: Int? = null,
    borderColor: Color? = null,
) {
    Row(
        modifier =
            Modifier
                .weight(1f)
                .height(56.dp)
                .clip(shape)
                .background(background)
                .then(if (borderColor != null) Modifier.border(1.dp, borderColor, shape) else Modifier)
                .clickable(enabled = enabled, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = contentColor,
        )
    }
}

@Preview(heightDp = 1400)
@Composable
private fun CourseDetailScreenPreview() {
    DesignSystemTheme {
        CourseDetailScreen(
            detail = courseDetailSample,
            isSaving = false,
            isDeleting = false,
            actions =
                CourseDetailActions(
                    onBack = {},
                    onAuthorClick = {},
                    onFollowAuthor = {},
                    onShare = {},
                    onSaveCourse = {},
                    onEditCourse = {},
                    onDeleteCourse = {},
                ),
        )
    }
}

/**
 * Preview용 더미(성수동 좌표 포함). presentation 전용 프리뷰 픽스처다.
 */
internal val courseDetailSample: CourseDetailVO =
    CourseDetailVO(
        title = "비 오는 날 성수 감성 카페 코스",
        coverImageUrl = "",
        category = "성수 · 데이트",
        authorName = "지호님",
        authorHandle = "@jiho_routes",
        authorImageUrl = "",
        placeCountText = "4곳",
        walkText = "도보 20분",
        followerText = "1.2k 따라감",
        description = "비가 오면 더 예쁜 성수 카페만 골라 담았어요. 전부 도보로 이어지고, 장소마다 제 팁을 남겨뒀으니 참고하세요 🌧",
        places =
            listOf(
                CourseDetailPlaceVO(
                    order = 1,
                    name = "어니언 성수",
                    category = "카페 · 베이커리",
                    photoCountText = "1/3",
                    tip = "통창 자리 꼭 앉으세요. 비 오는 날 이 뷰가 진짜 최고예요. 팡도르는 나오자마자!",
                    walkToNextText = "도보 6분",
                    latitude = 37.5447,
                    longitude = 127.0561,
                ),
                CourseDetailPlaceVO(
                    order = 2,
                    name = "대림창고 갤러리",
                    category = "전시 · 카페",
                    photoCountText = "1/2",
                    tip = "천장 높은 공간이라 사진이 잘 나와요. 안쪽 전시도 꼭 보세요.",
                    walkToNextText = "도보 5분",
                    latitude = 37.5410,
                    longitude = 127.0552,
                ),
                CourseDetailPlaceVO(
                    order = 3,
                    name = "센터커피 로스터스",
                    category = "카페 · 디저트",
                    photoCountText = "1/2",
                    tip = "로스팅 향이 진해요. 핸드드립 한 잔 시켜서 잠깐 앉았다 가기 좋아요.",
                    walkToNextText = "도보 4분",
                    latitude = 37.5433,
                    longitude = 127.0575,
                ),
                CourseDetailPlaceVO(
                    order = 4,
                    name = "소금집 델리",
                    category = "와인 · 안주",
                    photoCountText = "1/1",
                    tip = "마무리로 와인 한 잔. 안주는 관자 카르파초 강추예요.",
                    walkToNextText = null,
                    latitude = 37.5428,
                    longitude = 127.0532,
                ),
            ),
        rating = "4.8",
        reviewCountText = "128개",
        reviews = emptyList(),
    )
