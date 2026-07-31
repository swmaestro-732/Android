package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.home.HomeCourseVO

/** 사진 썸네일 줄에 최대로 보여줄 칸 수(초과 시 마지막 칸을 "+N" 배지로 대체). */
private const val MAX_THUMBS = 4

/**
 * 홈(FS-09) 공개 코스 카드: 커버(카테고리 칩·북마크·제목) + 작성자 + 장소 사진 썸네일 + 장소명.
 */
@Composable
fun HomeCourseCard(
    course: HomeCourseVO,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(18.dp),
                    ambientColor = color.contentDefaultLevel0.copy(alpha = 0.12f),
                    spotColor = color.contentDefaultLevel0.copy(alpha = 0.16f),
                ).clip(RoundedCornerShape(18.dp))
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
    ) {
        CourseCover(course = course, onBookmarkClick = onBookmarkClick)
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                HomeImage(url = course.authorImageUrl, modifier = Modifier.size(24.dp), shape = CircleShape)
                DsText(
                    text = "${course.authorName} · ${course.followText}",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
            if (course.placePhotoUrls.isNotEmpty()) {
                PlaceThumbRow(urls = course.placePhotoUrls)
            }
            DsText(
                text = course.placeNamesText,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** 커버: 이미지 + 하단 스크림 + 카테고리 칩(좌상) · 북마크(우상) · 제목(좌하). */
@Composable
private fun CourseCover(
    course: HomeCourseVO,
    onBookmarkClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(color.imagePlaceholder),
    ) {
        if (course.coverImageUrl.isNotBlank()) {
            AsyncImage(
                model = course.coverImageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        // 흰 제목이 밝은 커버에서도 읽히도록 하단으로 갈수록 어두워지는 스크림.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                color.contentDefaultLevel0.copy(alpha = 0f),
                                color.contentDefaultLevel0.copy(alpha = 0.55f),
                            ),
                        ),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(color.bgDefaultLevel1)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            DsText(
                text = course.categoryLabel,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
            )
        }
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.bgDefaultLevel1)
                    .clickable(onClick = onBookmarkClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter =
                    painterResource(
                        if (course.isSaved) R.drawable.ic_bookmark_filled_24 else R.drawable.ic_tab_bookmark_24,
                    ),
                contentDescription = if (course.isSaved) "저장 취소" else "저장",
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(18.dp),
            )
        }
        DsText(
            text = course.title,
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            style = DesignSystemThemeImpl.typeScale.titleExtraL,
            color = color.contentOnAccent,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/** 장소 사진 썸네일 줄. [MAX_THUMBS] 초과 시 마지막 칸을 "+N" 배지로 대체한다. */
@Composable
private fun PlaceThumbRow(urls: List<String>) {
    val color = DesignSystemThemeImpl.designSystemColor
    val overflow = urls.size > MAX_THUMBS
    val visibleCount = if (overflow) MAX_THUMBS - 1 else urls.size
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        urls.take(visibleCount).forEach { url ->
            HomeImage(
                url = url,
                modifier = Modifier.weight(1f).aspectRatio(1f),
                shape = RoundedCornerShape(10.dp),
            )
        }
        if (overflow) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.contentDefaultLevel1),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "+${urls.size - visibleCount}",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentOnAccent,
                )
            }
        }
        // 사진이 4개 미만이어도 카드마다 썸네일 크기가 같도록 남은 슬롯을 빈 칸으로 채운다.
        val usedSlots = visibleCount + if (overflow) 1 else 0
        repeat(MAX_THUMBS - usedSlots) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/** 홈 카드 공용 이미지. URL 이 있으면 Coil 로, 없으면 placeholder 배경만 그린다. */
@Composable
private fun HomeImage(
    url: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
) {
    val placeholder = modifier.clip(shape).background(DesignSystemThemeImpl.designSystemColor.imagePlaceholder)
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
