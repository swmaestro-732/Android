package com.chillsam.courmy.course.presentation

import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
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
import com.chillsam.courmy.common.presentation.helper.FeatureFlags
import com.chillsam.courmy.common.presentation.ui.modifier.cardElevation
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.presentation.component.CourseRouteMap
import com.chillsam.courmy.course.presentation.component.PlaceDetailSheet
import com.chillsam.courmy.course.presentation.component.PlaceSheetCourse
import com.chillsam.courmy.course.presentation.component.dashedBorder
import com.chillsam.courmy.course.presentation.component.toRoutePoints

/**
 * 코스 상세 화면(Figma FS-11, 펼친 버전). 히어로(커버·카테고리·제목) → 요약 스탯 → 소개 →
 * "코스 속 장소" 목록(접기/펼치기) → "코스 한눈에 보기" 지도 → 작성자 밴드 →
 * 하단 "코스 저장하기" 액션바로 구성한다. 표시 전용 화면으로 [detail] 데이터를 그대로 렌더링한다.
 *
 * 지도는 전체 동선만 보여 주고 제스처를 막는다. 장소별 위치를 둘러보는 건 장소를 눌러 뜨는
 * [PlaceDetailSheet] 의 지도 몫이다.
 *
 * 작성자 정보의 위치는 [LayoutVariants.AUTHOR_AT_BOTTOM] 로 갈린다.
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
    // 지도의 "자세히" 로 열면 시트를 코스 전체 보기 상태로 시작한다.
    var openSheetInOverview by remember { mutableStateOf(false) }
    val placeDetailViewModel: PlaceDetailViewModel = hiltViewModel()
    val placeDetailState by placeDetailViewModel.uiState.collectAsStateWithLifecycle()
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
            if (!LayoutVariants.AUTHOR_AT_BOTTOM) {
                // 밴드는 화면 가로 전체를 채우므로 본문 좌우 패딩 밖에 둔다.
                // 위 배치에서는 히어로 이미지에 바로 붙고 아래쪽으로 요약 스탯과 끊는다.
                AuthorBand(
                    detail = detail,
                    onAuthorClick = actions.onAuthorClick,
                    onToggleFollow = actions.onFollowAuthor,
                    dividerBelow = true,
                )
            }
            Column(
                modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                StatsRow(detail = detail)
                DsText(
                    text = detail.description,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel1,
                    maxLines = Int.MAX_VALUE,
                )
                // 작성자가 직접 단 해시태그. 서버가 파생한 카테고리(themeLabels)와 달리 소개 아래에 둔다.
                if (detail.tags.isNotEmpty()) {
                    DsText(
                        text = detail.tags.joinToString(" ") { "#$it" },
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentAccent,
                        maxLines = Int.MAX_VALUE,
                    )
                }
                // 코스 소개(제목·스탯·설명)와 장소 목록을 가르는 선.
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color.borderDefaultLevel0),
                )
                PlacesSection(
                    places = detail.places,
                    authorName = detail.authorName,
                    onPlaceClick = { place ->
                        openSheetInOverview = false
                        selectedPlace = place
                        placeDetailViewModel.open(placeId = place.placeId)
                    },
                )
                // 장소를 다 훑은 뒤 전체 동선을 확인하는 자리다.
                CourseMapSection(
                    places = detail.places,
                    onPlaceClick = { place ->
                        openSheetInOverview = false
                        selectedPlace = place
                        placeDetailViewModel.open(placeId = place.placeId)
                    },
                    onOpenDetail = { place ->
                        openSheetInOverview = true
                        selectedPlace = place
                        placeDetailViewModel.open(placeId = place.placeId)
                    },
                )
            }
            if (LayoutVariants.AUTHOR_AT_BOTTOM) {
                // 아래 배치에서는 위쪽으로 본문과 끊고 아래는 스크롤 끝까지 열어 둔다.
                AuthorBand(
                    detail = detail,
                    onAuthorClick = actions.onAuthorClick,
                    onToggleFollow = actions.onFollowAuthor,
                    dividerBelow = false,
                )
            }
            Spacer(Modifier.height(14.dp))
        }
        DetailBottomBar(
            isMine = detail.isMine,
            isSaved = detail.isSaved,
            isSaving = isSaving,
            isDeleting = isDeleting,
            actions = actions,
        )
    }
    val currentPlace = selectedPlace
    if (currentPlace != null) {
        val dismiss = {
            selectedPlace = null
            openSheetInOverview = false
            placeDetailViewModel.clear()
        }
        // 시트 내용은 코스 데이터로 즉시 그리고, API 로 받는 주소만 도착하는 대로 채운다.
        // 응답을 기다렸다 띄우면 다른 장소로 옮길 때마다 시트가 닫혔다 다시 열린다.
        // 어느 장소로 옮길지(이동 바·핀 탭·장소 이동 줄)는 시트가 정하고, 여기서는 받아서 다시 조회한다.
        PlaceDetailSheet(
            coursePlace = currentPlace,
            course =
                PlaceSheetCourse(
                    title = detail.title,
                    category = detail.themeLabels.joinToString(" · "),
                    // 걸어갈 수 없는 구간이 섞이면 walkText 가 비므로 빈 값을 빼고 잇는다.
                    summary =
                        listOf(detail.placeCountText, detail.walkText)
                            .filter { it.isNotBlank() }
                            .joinToString(" · "),
                    authorName = detail.authorName,
                    places = detail.places,
                ),
            address = placeDetailState.place?.address.orEmpty(),
            onDismiss = dismiss,
            onSelectPlace = { target ->
                selectedPlace = target
                placeDetailViewModel.open(placeId = target.placeId)
            },
            startInOverview = openSheetInOverview,
        )
        // 주소를 못 받아도 나머지는 멀쩡하므로 알리기만 하고 시트는 닫지 않는다.
        LaunchedEffect(placeDetailState.errorMessage) {
            placeDetailState.errorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
            // 카테고리 칩은 제목 위에 둔다. 서버 themes 를 한 줄로 이어 붙인 값이다.
            val category = detail.themeLabels.joinToString(" · ")
            if (category.isNotBlank()) {
                Box(
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(color.bgDefaultLevel1)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    DsText(
                        text = category,
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel1,
                    )
                }
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
 *
 * @param emptyIconRes URL 이 비었을 때 가운데 그릴 아이콘. 프로필처럼 "무엇이 빠졌는지" 가 분명한
 *   자리에만 넘긴다 — 코스 커버·장소 사진은 회색 자리만 두는 편이 낫다.
 */
@Composable
private fun CourseImage(
    url: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    contentScale: ContentScale = ContentScale.Crop,
    background: Color = DesignSystemThemeImpl.designSystemColor.imagePlaceholder,
    @DrawableRes emptyIconRes: Int? = null,
) {
    val placeholder =
        modifier
            .clip(shape)
            .background(background)
    if (url.isBlank()) {
        Box(modifier = placeholder, contentAlignment = Alignment.Center) {
            if (emptyIconRes != null) {
                Icon(
                    painter = painterResource(emptyIconRes),
                    contentDescription = null,
                    tint = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
                    modifier = Modifier.fillMaxSize(EMPTY_ICON_RATIO),
                )
            }
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = placeholder,
            contentScale = contentScale,
            // 어떤 이미지 URL 이 왜 안 떴는지(404/타임아웃 등)를 로그로 남긴다.
            onError = { Log.w("CourseDetail", "이미지 로드 실패: $url", it.result.throwable) },
        )
    }
}

/**
 * 작성자 밴드: 화면 가로를 꽉 채우는 구역. 위치는 [LayoutVariants.AUTHOR_AT_BOTTOM] 로 갈린다.
 *
 * 구분선은 본문과 맞닿는 한쪽에만 둔다([dividerBelow]). 위아래를 다 선으로 막으면 페이지와 같은
 * 배경색 위에 얇은 띠만 남아 밴드가 떠 보인다.
 */
@Composable
private fun AuthorBand(
    detail: CourseDetailVO,
    onAuthorClick: () -> Unit,
    onToggleFollow: () -> Unit,
    dividerBelow: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel0)) {
        if (!dividerBelow) BandDivider()
        Row(
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AuthorContent(
                detail = detail,
                onAuthorClick = onAuthorClick,
                onToggleFollow = onToggleFollow,
            )
        }
        if (dividerBelow) BandDivider()
    }
}

/** 작성자 밴드를 본문과 끊는 선. 좌우 여백 없이 화면 끝까지 긋는다. */
@Composable
private fun BandDivider() {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0),
    )
}

/**
 * 작성자 정보 본문: 아바타 · 이름/핸들 · 팔로우 버튼.
 * [AuthorBand] 컨테이너와 분리해 두어 배치가 바뀌어도 내용은 그대로 쓴다.
 */
@Composable
private fun RowScope.AuthorContent(
    detail: CourseDetailVO,
    onAuthorClick: () -> Unit,
    onToggleFollow: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
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
            // 사진을 안 올린 작성자. 빈 회색 원만 두면 로딩이 덜 끝난 것처럼 보인다.
            emptyIconRes = R.drawable.ic_tab_person_24,
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
    // 내 코스에는 나를 팔로우할 대상이 없으므로 버튼 자체를 그리지 않는다.
    if (!detail.isMine) {
        AuthorFollowButton(
            isFollowing = detail.isFollowingAuthor,
            onClick = onToggleFollow,
        )
    }
}

/**
 * 작성자 팔로우 버튼.
 *
 * 팔로우 전에는 채운 강조 버튼("팔로우"), 팔로우 중에는 흐린 버튼("팔로잉")으로 바뀐다.
 * 예전에는 팔로우하면 버튼이 사라져 이 화면에서 팔로우를 풀 방법이 없었는데,
 * "팔로잉" 상태에서도 누르면 [CourseDetailIntent.ToggleFollowAuthor] 가 언팔로우로 동작한다.
 */
@Composable
private fun AuthorFollowButton(
    isFollowing: Boolean,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val background = if (isFollowing) color.bgDefaultLevel0 else color.bgAccent
    val contentColor = if (isFollowing) color.contentDefaultLevel3 else color.contentOnAccent
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(background)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        DsText(
            text = if (isFollowing) "팔로잉" else "팔로우",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = contentColor,
        )
    }
}

/** 요약 스탯 한 칸: 앞에 붙는 아이콘 + 문구. */
private data class CourseStat(
    @param:DrawableRes val iconRes: Int,
    val text: String,
)

/**
 * 요약 스탯 한 줄: "🚶도보 20분 · 👣1.2k 따라감".
 *
 * 칩 3개로 그리면 히어로의 카테고리 pill 과 칩이 위아래로 겹쳐 보여, 사실값인 스탯은
 * 아이콘 + 가운뎃점으로 이은 한 줄로 낮춰 둔다. 빈 값은 칸째 빼고 이어 붙인다.
 *
 * 장소 수는 두지 않는다 — 바로 아래 "코스 속 장소 N곳" 제목이 같은 값을 다시 말한다.
 */
@Composable
private fun StatsRow(detail: CourseDetailVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    val stats =
        listOf(
            CourseStat(R.drawable.ic_walk_24, detail.walkText),
            CourseStat(R.drawable.ic_footprint_24, detail.followerText),
        ).filter { it.text.isNotBlank() }
    if (stats.isEmpty()) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(STAT_ICON_GAP),
    ) {
        stats.forEachIndexed { index, stat ->
            if (index > 0) {
                DsText(
                    text = "·",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel3,
                )
            }
            Icon(
                painter = painterResource(stat.iconRes),
                contentDescription = null,
                tint = color.contentDefaultLevel2,
                modifier = Modifier.size(STAT_ICON_SIZE),
            )
            DsText(
                text = stat.text,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
            )
        }
    }
}

/** 스탯 아이콘 크기. 본문 글자(textRegularS)와 눈높이를 맞춘 값. */
private val STAT_ICON_SIZE = 16.dp

/** 아이콘·문구·가운뎃점 사이 간격. */
private val STAT_ICON_GAP = 4.dp

/** 접힘 상태에서 콤팩트하게 보여줄 장소 수. 초과분은 "더보기"로 접는다. */
private const val COLLAPSED_VISIBLE_PLACES = 2

/** 이 수를 초과(4곳 이상)할 때만 "나머지 N곳 더보기"로 접는다. 3곳까지는 전부 노출. */
private const val COLLAPSE_THRESHOLD = 3

/** 코스 지도 구역 높이. 스크롤 화면 안에 들어가므로 한 화면을 다 먹지 않을 만큼만 잡는다. */
private val CourseMapHeight = 220.dp

/**
 * 코스 한눈에 보기: 장소 핀과 동선을 한 장의 지도로 보여 준다.
 *
 * 좌표가 있는 장소가 없으면 구역째 그리지 않는다 — 빈 지도는 알려 주는 게 없다.
 * 지도 제스처는 막는다. 세로 스크롤 화면 안이라 지도가 스크롤을 가로채면 화면이 걸린다.
 * 대신 둘러보는 길을 둘 둔다 — 핀을 누르면 그 장소로([onPlaceClick]), "자세히" 를 누르면
 * 코스 전체 보기 상태로([onOpenDetail]) 장소 상세 시트가 열린다.
 */
@Composable
private fun CourseMapSection(
    places: List<CourseDetailPlaceVO>,
    onPlaceClick: (CourseDetailPlaceVO) -> Unit,
    onOpenDetail: (CourseDetailPlaceVO) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val points = remember(places) { places.toRoutePoints() }
    val first = points.firstOrNull() ?: return
    Column(
        modifier = Modifier.padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsText(
                text = "코스 한눈에 보기",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                modifier = Modifier.weight(1f),
            )
            // 전체 보기로 열 때도 시트는 장소 하나를 기준으로 뜨므로 첫 장소를 넘긴다.
            DsText(
                text = "자세히",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
                modifier = Modifier.clickable { onOpenDetail(first.first) },
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(CourseMapHeight)
                    // 표면이 흰색이 아니라(지도 타일 자리) 그림자만 받고 배경은 직접 깐다.
                    .cardElevation(RoundedCornerShape(16.dp))
                    .background(color.imagePlaceholder),
        ) {
            CourseRouteMap(
                focus = first.second,
                // 특정 장소를 보는 게 아니라 전체 동선을 보는 자리라 핀을 모두 진하게 그린다.
                focusOrder = 0,
                points = points,
                overview = true,
                onMarkerClick = onPlaceClick,
                scrollEnabled = false,
            )
        }
    }
}

/** "코스 속 장소" 섹션: 헤더(접기/펼치기) + 펼침(사진·팁) / 접힘(콤팩트 타임라인) 목록. */
@Composable
private fun PlacesSection(
    places: List<CourseDetailPlaceVO>,
    authorName: String,
    onPlaceClick: (CourseDetailPlaceVO) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    val color = DesignSystemThemeImpl.designSystemColor
    // 위쪽 소개·태그 묶음과 장소 목록 사이를 한 단 더 벌려 섹션이 나뉘어 보이게 한다.
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
            // 노드 사이 간격. 접힘 상태에서는 도보 시간을 쓰지 않고 연결선만 잇는다.
            if (index < visible.lastIndex || remaining > 0) {
                TimelineGap()
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

/**
 * 접힘 타임라인에서 노드와 노드 사이를 벌리는 간격.
 *
 * 접었을 때는 도보 시간을 보여 주지 않는다 — 훑어보는 상태라 장소 이름만 남기고, 구간 정보는
 * 펼친 목록([RouteConnector])에서 본다. 노드를 잇는 세로 연결선이 지나갈 자리는 남겨 둔다.
 */
@Composable
private fun TimelineGap() {
    Spacer(Modifier.fillMaxWidth().height(TIMELINE_GAP))
}

/** 접힘 타임라인의 노드 간격. 도보 시간 한 줄이 차지했던 높이를 그대로 쓴다. */
private val TIMELINE_GAP = 20.dp

/** 콤팩트 장소 행: 펼침 상태 [DetailPlaceItem] 의 헤더(번호·이름/카테고리·›)와 동일한 레이아웃. */
@Composable
private fun CompactPlaceRow(
    place: CourseDetailPlaceVO,
    onClick: () -> Unit,
    badgeModifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        // 펼침 상태와 마찬가지로 행 전체를 탭 영역으로 쓴다.
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
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
        PlaceDetailChevron()
    }
}

/**
 * 장소 상세로 이동한다는 표시. 탭은 헤더 행 전체가 받으므로 여기서는 클릭을 걸지 않고
 * 화살표 모양만 그린다(중복 클릭 영역을 만들지 않는다).
 */
@Composable
private fun PlaceDetailChevron() {
    Box(
        modifier = Modifier.size(48.dp),
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

/**
 * 장소 사진 영역 비율(가로:세로 = 4:5). 사진은 이 틀을 꽉 채우고 넘치는 부분을 자르므로
 * 장소마다 카드 높이가 같다. 비율을 바꾸면 잘리는 방향이 바뀐다(가로로 늘리면 세로 사진이 더 잘린다).
 */
private const val PLACE_PHOTO_RATIO = 4f / 5f

/** 빈 이미지 자리의 기본 아이콘 크기 비율. 여백을 남겨 아이콘이 갇혀 보이지 않게 한다. */
private const val EMPTY_ICON_RATIO = 0.55f

/**
 * 장소 1건: 순번·이름·카테고리 헤더 + 사진 + "Tip.{작성자}". 헤더 화살표로 장소 상세 시트를 연다.
 * 팁이 비어 있으면 팁 영역은 렌더하지 않는다.
 */
@Composable
private fun DetailPlaceItem(
    place: CourseDetailPlaceVO,
    authorName: String,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            // 화살표만 누를 수 있으면 조준하기 어려워 놓치기 쉽다. 헤더 행 전체를 탭 영역으로 쓴다.
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
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
            PlaceDetailChevron()
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(PLACE_PHOTO_RATIO)
                    .cardElevation(RoundedCornerShape(14.dp))
                    // 사진이 프레임을 꽉 채우므로 이 배경은 로딩 전·실패 시에만 보인다.
                    .background(color.imagePlaceholder),
            contentAlignment = Alignment.TopEnd,
        ) {
            val urls = place.imageUrls
            // 바깥 흰 프레임이 이미 모서리를 깎으므로 안쪽 이미지는 각지게 둔다
            // (둘 다 깎으면 모서리가 이중으로 패여 보인다).
            if (urls.isEmpty()) {
                CourseImage(url = "", modifier = Modifier.fillMaxSize(), shape = RectangleShape)
            } else {
                // 사진이 2장 이상이면 가로로 스와이프해 넘긴다.
                val pagerState = rememberPagerState(pageCount = { urls.size })
                // 목록에서는 잘린 사진만 보이므로, 누르면 전체화면으로 원본 비율을 보여 준다.
                var viewerPage by remember { mutableStateOf<Int?>(null) }
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                ) { page ->
                    // 폭에만 맞추면 가로 사진에서 위아래가 크게 비어 프레임이 깨져 보인다.
                    // 프레임을 꽉 채우고 넘치는 부분을 자른다(카드 높이도 장소마다 같아진다).
                    CourseImage(
                        url = urls[page],
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clickable { viewerPage = page },
                        shape = RectangleShape,
                        contentScale = ContentScale.Crop,
                    )
                }
                viewerPage?.let { start ->
                    PhotoViewerDialog(
                        urls = urls,
                        startPage = start,
                        onDismiss = { viewerPage = null },
                    )
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
        // 팁을 남기지 않은 장소에는 제목만 덩그러니 남으므로 섹션째 감춘다.
        if (place.tip.isNotBlank()) {
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
}

/**
 * 사진 전체화면 뷰어. 목록에서는 프레임에 맞춰 잘린 사진만 보이므로 여기서는 원본 비율을 다 보여 준다
 * ([ContentScale.Fit]). 좌우로 끌어 같은 장소의 다른 사진으로 넘기고, 배경을 누르거나 X 로 닫는다.
 */
@Composable
private fun PhotoViewerDialog(
    urls: List<String>,
    startPage: Int,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val pagerState = rememberPagerState(initialPage = startPage, pageCount = { urls.size })
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(color.contentDefaultLevel0)
                    // 사진 바깥 어디를 눌러도 닫힌다. 물결 효과는 전체화면에서 어색해 끈다.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                // 전체화면에서는 사진 노드가 화면을 다 차지하므로, 여기에 placeholder 회색을 칠하면
                // Fit 으로 남는 위아래 여백까지 회색이 된다. 배경은 뒤의 어두운 판이 맡는다.
                CourseImage(
                    url = urls[page],
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape,
                    contentScale = ContentScale.Fit,
                    background = Color.Transparent,
                )
            }
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.bgDefaultLevel1)
                        .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.close_small_24),
                    contentDescription = "닫기",
                    tint = color.contentDefaultLevel0,
                    modifier = Modifier.size(20.dp),
                )
            }
            if (urls.size > 1) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(20.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(color.bgDefaultLevel1)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    DsText(
                        text = "${pagerState.currentPage + 1}/${urls.size}",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel0,
                    )
                }
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
                    .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val buttonShape = RoundedCornerShape(15.dp)
            if (FeatureFlags.SHARE_ENABLED) {
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
                    // 저장됨은 옅은 강조 배경 위라, 글자도 강조색이어야 상태가 읽힌다.
                    contentColor = if (isSaved) color.contentAccent else color.contentOnAccent,
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
        themes = listOf("성수", "데이트", "감성카페"),
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
