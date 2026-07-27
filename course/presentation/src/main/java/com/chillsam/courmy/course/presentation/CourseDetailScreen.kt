package com.chillsam.courmy.course.presentation

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailPlaceVO
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseReviewVO
import com.chillsam.courmy.course.presentation.component.dashedBorder

/**
 * 코스 상세 화면(Figma FS-11). 히어로(제목·작성자) → 요약 스탯 → 소개 →
 * "코스 속 장소" 목록(접기/펼치기) → 리뷰 → 하단 "이 코스 따라가기" 액션바로 구성한다.
 * 표시 전용 화면으로 [detail] 데이터를 그대로 렌더링한다.
 */
@Composable
fun CourseDetailScreen(
    detail: CourseDetailVO,
    onBack: () -> Unit,
    onFollowAuthor: () -> Unit,
    onFollowCourse: () -> Unit,
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
                ReviewsSection(detail = detail)
                Spacer(Modifier.height(12.dp))
            }
        }
        DetailBottomBar(onFollowCourse = onFollowCourse)
    }
}

/** 상단 히어로: 어두운 배경 위 뒤로/북마크/공유, 카테고리 칩, 코스 제목. */
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
            // 밝은 커버 위에서도 흰 제목·버튼이 읽히도록 하단으로 갈수록 어두워지는 스크림.
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
            HeroCircleButton(symbol = "‹", onClick = onBack)
            Spacer(Modifier.weight(1f))
            HeroCircleButton(symbol = "🔖", onClick = {})
            Spacer(Modifier.width(8.dp))
            HeroCircleButton(symbol = "↗", onClick = {})
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
                        .background(color.bgDefaultLevel0)
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
    symbol: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = symbol,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        )
    }
}

/**
 * 코스 상세 공용 이미지. URL 이 있으면 Coil 로 로드하고, 비어 있으면 placeholder 배경만 그린다.
 * 로딩 전/실패 시에도 [color.borderDefaultLevel0] 배경이 자리를 잡는다.
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

/** 요약 스탯 칩 3개: 장소 수 · 도보 · 따라감. */
@Composable
private fun StatsRow(detail: CourseDetailVO) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        StatChip(text = "📍 ${detail.placeCountText}")
        StatChip(text = "🚶 ${detail.walkText}")
        StatChip(text = "👥 ${detail.followerText}")
    }
}

@Composable
private fun StatChip(text: String) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(9999.dp))
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .padding(horizontal = 12.dp, vertical = 7.dp),
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
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
                DsText(
                    text = "›",
                    style = DesignSystemThemeImpl.typeScale.textRegularM,
                    color = color.contentAccent,
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
            DsText(
                text = "›",
                style = DesignSystemThemeImpl.typeScale.textRegularM,
                color = color.contentDefaultLevel3,
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

/** 장소 사이 도보 커넥터: "↓ 도보 6분". */
@Composable
private fun RouteConnector(text: String) {
    DsText(
        text = "↓ $text",
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
        textAlign = TextAlign.Center,
    )
}

/** 리뷰 섹션: 평점 헤더 + 리뷰 카드. */
@Composable
private fun ReviewsSection(detail: CourseDetailVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DsText(
                text = "리뷰",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "★ ${detail.rating}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentRating,
            )
            DsText(
                text = "· ${detail.reviewCountText}",
                modifier = Modifier.weight(1f),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
            DsText(
                text = "전체보기",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentAccent,
            )
        }
        detail.reviews.forEach { review -> ReviewCard(review = review) }
    }
}

@Composable
private fun ReviewCard(review: CourseReviewVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(color.bgDefaultLevel1)
                .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CourseImage(
                url = review.authorImageUrl,
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
            )
            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                DsText(
                    text = review.author,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel0,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    DsText(
                        text = starText(review.rating),
                        style = DesignSystemThemeImpl.typeScale.textExtraXS,
                        color = color.contentRating,
                    )
                    DsText(
                        text = "· ${review.dateText}",
                        style = DesignSystemThemeImpl.typeScale.textExtraXS,
                        color = color.contentDefaultLevel2,
                    )
                }
            }
        }
        DsText(
            text = review.body,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel1,
            maxLines = Int.MAX_VALUE,
        )
        if (review.photoUrls.isNotEmpty()) {
            // 좁은 화면 오버플로 방지: 리뷰 사진은 최대 3장까지만 노출한다.
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                review.photoUrls.take(3).forEach { url ->
                    CourseImage(
                        url = url,
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(10.dp),
                    )
                }
            }
        }
    }
}

/** 하단 고정 액션바: 지도/경로 아이콘 + "이 코스 따라가기" 기본 버튼. */
@Composable
private fun DetailBottomBar(onFollowCourse: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel0)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BottomIconButton(symbol = "🗺")
        BottomIconButton(symbol = "✂")
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.bgAccent)
                    .clickable(onClick = onFollowCourse)
                    .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            DsText(
                text = "✦ 이 코스 따라가기",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentOnAccent,
            )
        }
    }
}

@Composable
private fun BottomIconButton(symbol: String) {
    Box(
        modifier =
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = symbol,
            style = DesignSystemThemeImpl.typeScale.textRegularM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
        )
    }
}

private fun starText(rating: Int): String {
    val filled = rating.coerceIn(0, 5)
    return "★".repeat(filled) + "☆".repeat(5 - filled)
}
