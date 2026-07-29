package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.saved.SavedCourseVO

/**
 * 코스 카드: 썸네일(태그 칩 + 북마크 배지) + 제목 + "장소 N곳 · @handle".
 * 저장함(FS-14)과 홈에서 공통으로 쓰는 코스 요약 카드다.
 */
@Composable
fun SavedCourseCard(
    course: SavedCourseVO,
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
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = color.contentDefaultLevel0.copy(alpha = 0.12f),
                    spotColor = color.contentDefaultLevel0.copy(alpha = 0.16f),
                ).clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(356f / 126f)
                    .background(color.imagePlaceholder),
        ) {
            if (course.thumbnailUrl.isNotBlank()) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            // 상단 좌측: 지역·카테고리 태그 칩(흰 배경).
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
                    text = course.tagLabel,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel1,
                )
            }
            // 상단 우측: 저장(북마크) 배지.
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color.bgDefaultLevel1)
                        .clickable(onClick = onBookmarkClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_bookmark_filled_24),
                    contentDescription = "저장 취소",
                    tint = color.contentDefaultLevel0,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            DsText(
                text = course.title,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            DsText(
                text = "${course.placeLabel} · @${course.authorHandle}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
