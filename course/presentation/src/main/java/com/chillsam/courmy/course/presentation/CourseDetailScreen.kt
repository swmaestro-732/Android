package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.presentation.component.dashedBorder

/**
 * 코스 상세 화면(Figma FS-11, 펼친 버전). 히어로(제목·작성자) → 요약 스탯 → 소개 →
 * "코스 속 장소" 목록(접기/펼치기) → "코스 경로" 지도 → 하단 "코스 저장하기" 액션바로 구성한다.
 * 표시 전용 화면으로 [detail] 데이터를 그대로 렌더링한다.
 */
@Composable
fun CourseDetailScreen(
    detail: CourseDetailVO,
    onBack: () -> Unit,
    onFollowAuthor: () -> Unit,
    onShare: () -> Unit,
    onSaveCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
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
            DetailHero(detail = detail, onBack = onBack)
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Spacer(Modifier.height(4.dp))
                AuthorRow(detail = detail, onFollow = onFollowAuthor)
                StatsRow(detail = detail)
                DsText(
                    text = detail.description,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel1,
                    maxLines = Int.MAX_VALUE,
                )
                PlacesSection(places = detail.places)
                CourseRouteSection()
                Spacer(Modifier.height(12.dp))
            }
        }
        DetailBottomBar(onShare = onShare, onSaveCourse = onSaveCourse)
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
    onFollow: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CourseImage(
            url = detail.authorImageUrl,
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
        // 이미 팔로우 중이면 버튼을 숨긴다(팔로우 안 한 경우에만 노출).
        if (!detail.isFollowingAuthor) {
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
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

/** 접힘 상태에서 콤팩트하게 보여줄 장소 수. 나머지는 "더보기"로 접는다. */
private const val COLLAPSED_VISIBLE_PLACES = 2

/** "코스 속 장소" 섹션: 헤더(접기/펼치기) + 펼침(사진·팁) / 접힘(콤팩트 타임라인) 목록. */
@Composable
private fun PlacesSection(places: List<CourseDetailPlaceVO>) {
    var expanded by remember { mutableStateOf(true) }
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsText(
                text = "코스 속 장소 ${places.size}곳",
                modifier = Modifier.weight(1f),
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = if (expanded) "접기" else "펼치기",
                modifier = Modifier.clickable { expanded = !expanded },
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
            )
        }
        if (expanded) {
            places.forEach { place ->
                DetailPlaceItem(place = place)
                place.walkToNextText?.let { RouteConnector(text = it) }
            }
        } else {
            CollapsedPlaces(places = places, onExpand = { expanded = true })
        }
    }
}

/** 접힘 목록: 앞 [COLLAPSED_VISIBLE_PLACES]곳만 콤팩트 행 + 타임라인 연결선 + "나머지 N곳 더보기". */
@Composable
private fun CollapsedPlaces(
    places: List<CourseDetailPlaceVO>,
    onExpand: () -> Unit,
) {
    val visible = places.take(COLLAPSED_VISIBLE_PLACES)
    val remaining = places.size - visible.size
    Column {
        visible.forEachIndexed { index, place ->
            CompactPlaceRow(
                place = place,
                connectAbove = index > 0,
                connectBelow = index < visible.lastIndex || remaining > 0,
            )
        }
        if (remaining > 0) {
            MorePlacesRow(remaining = remaining, onClick = onExpand)
        }
    }
}

/** 콤팩트 장소 행: 번호(타임라인) · 썸네일 · 이름/카테고리 · › . */
@Composable
private fun CompactPlaceRow(
    place: CourseDetailPlaceVO,
    connectAbove: Boolean,
    connectBelow: Boolean,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceRail(connectAbove = connectAbove, connectBelow = connectBelow) {
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(color.bgAccent),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = place.order.toString(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentOnAccent,
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f).padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CourseImage(
                url = place.imageUrls.firstOrNull().orEmpty(),
                modifier = Modifier.size(56.dp),
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(color.bgAccentSubtle),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_right_24),
                    contentDescription = null,
                    tint = color.contentAccent,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

/** "나머지 N곳 더보기" 행: 점선 원(⋯) 타임라인 마감 + 강조 텍스트. 누르면 펼친다. */
@Composable
private fun MorePlacesRow(
    remaining: Int,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaceRail(connectAbove = true, connectBelow = false) {
            Box(
                modifier = Modifier.size(28.dp).dashedBorder(color.borderDefaultLevel0, cornerRadius = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "⋯",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                )
            }
        }
        Box(modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp)) {
            DsText(
                text = "나머지 ${remaining}곳 더보기",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentAccent,
            )
        }
    }
}

/** 타임라인 레일: 세로 연결선(위/아래 선택) 위에 노드(번호 원/점선 원)를 중앙 배치. */
@Composable
private fun PlaceRail(
    connectAbove: Boolean,
    connectBelow: Boolean,
    node: @Composable () -> Unit,
) {
    val lineColor = DesignSystemThemeImpl.designSystemColor.bgAccent
    Box(
        modifier = Modifier.width(28.dp).fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        if (connectAbove) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .width(2.dp)
                        .fillMaxHeight(0.5f)
                        .background(lineColor),
            )
        }
        if (connectBelow) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .width(2.dp)
                        .fillMaxHeight(0.5f)
                        .background(lineColor),
            )
        }
        node()
    }
}

/** 장소 1건: 순번·이름·카테고리 헤더 + 사진 + "지호님 팁". */
@Composable
private fun DetailPlaceItem(place: CourseDetailPlaceVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
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
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
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
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right_24),
                contentDescription = null,
                tint = color.contentDefaultLevel3,
                modifier = Modifier.size(20.dp),
            )
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
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier =
                    Modifier
                        .width(3.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(color.bgAccent),
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                DsText(
                    text = "지호님 팁",
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
        horizontalArrangement = Arrangement.spacedBy(9.dp),
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
 * "코스 경로" 섹션: 헤더 + 지도 영역. 네이버 지도 SDK 연동 전까지 placeholder 로 자리를 잡는다.
 * TODO(map-in-course): 네이버 지도(NaverMap) 로 교체하고 장소 좌표로 핀·경로선을 그린다.
 */
@Composable
private fun CourseRouteSection() {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DsText(
            text = "코스 경로",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.imagePlaceholder),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_tab_map_24),
                contentDescription = "코스 경로 지도",
                tint = color.contentDefaultLevel3,
                modifier = Modifier.size(40.dp),
            )
        }
    }
}

/** 하단 고정 액션바: 공유 버튼 + "코스 저장하기" 기본 버튼. 상단에 옅은 구분선. */
@Composable
private fun DetailBottomBar(
    onShare: () -> Unit,
    onSaveCourse: () -> Unit,
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
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                        .clickable(onClick = onShare),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share_24),
                    contentDescription = "공유",
                    tint = color.contentDefaultLevel1,
                    modifier = Modifier.size(22.dp),
                )
            }
            Row(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(buttonShape)
                        .background(color.bgAccent)
                        .clickable(onClick = onSaveCourse),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tab_bookmark_24),
                    contentDescription = null,
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                DsText(
                    text = "코스 저장하기",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentOnAccent,
                )
            }
        }
    }
}

@Preview(heightDp = 1400)
@Composable
private fun CourseDetailScreenPreview() {
    DesignSystemTheme {
        CourseDetailScreen(
            detail = previewCourseDetail,
            onBack = {},
            onFollowAuthor = {},
            onShare = {},
            onSaveCourse = {},
        )
    }
}

/** Preview·더미 테스트용 샘플. 실제 데이터는 서버에서 courseId 로 조회한다. */
private val previewCourseDetail =
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
                ),
                CourseDetailPlaceVO(
                    order = 2,
                    name = "대림창고 갤러리",
                    category = "전시 · 카페",
                    photoCountText = "1/2",
                    tip = "천장 높은 공간이라 사진이 잘 나와요. 안쪽 전시도 꼭 보세요.",
                    walkToNextText = "도보 5분",
                ),
                CourseDetailPlaceVO(
                    order = 3,
                    name = "센터커피 로스터스",
                    category = "카페 · 디저트",
                    photoCountText = "1/2",
                    tip = "로스팅 향이 진해요. 핸드드립 한 잔 시켜서 잠깐 앉았다 가기 좋아요.",
                    walkToNextText = "도보 4분",
                ),
                CourseDetailPlaceVO(
                    order = 4,
                    name = "소금집 델리",
                    category = "와인 · 안주",
                    photoCountText = "1/1",
                    tip = "마무리로 와인 한 잔. 안주는 관자 카르파초 강추예요.",
                    walkToNextText = null,
                ),
            ),
        rating = "4.8",
        reviewCountText = "128개",
        reviews = emptyList(),
    )
