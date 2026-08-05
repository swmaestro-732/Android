package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.home.HomeCourseVO

/**
 * 홈(FS-09) 공개 코스 카드: 커버(카테고리 칩·북마크·제목) + 저장 수.
 *
 * TODO-API-SPEC: Figma 는 커버 아래에 작성자(아바타·이름·따라감 수), 코스 속 장소 사진 썸네일,
 * 장소명 요약을 보여주지만 `GET /service/v1/courses` 응답에는 `authorId` 만 있고 나머지 필드가 없다.
 * 채울 값이 없는 영역을 빈 placeholder 로 렌더하지 않고 생략한다.
 * 서버 `CourseFeedResponse.Item` 에 필드가 추가되면 이 영역을 되살린다. [wiki-needed]
 */
@Composable
fun HomeCourseCard(
    course: HomeCourseVO,
    isSaved: Boolean,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .cardShadow(RoundedCornerShape(18.dp))
                .clickable(onClick = onClick),
    ) {
        CourseCover(course = course, isSaved = isSaved, onBookmarkClick = onBookmarkClick)
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_tab_bookmark_24),
                contentDescription = null,
                tint = color.contentDefaultLevel3,
                modifier = Modifier.size(16.dp),
            )
            DsText(
                text = course.saveCountText,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
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
    isSaved: Boolean,
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
                        if (isSaved) R.drawable.ic_bookmark_filled_24 else R.drawable.ic_tab_bookmark_24,
                    ),
                contentDescription = if (isSaved) "저장 취소" else "저장",
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
