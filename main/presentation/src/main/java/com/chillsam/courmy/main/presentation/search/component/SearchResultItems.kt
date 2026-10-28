package com.chillsam.courmy.main.presentation.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.PlaceThumbnail
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO
import com.chillsam.courmy.main.presentation.component.CourseLikeCount

private val CardShape = RoundedCornerShape(14.dp)
private val ThumbShape = RoundedCornerShape(10.dp)

/** 검색 결과의 장소 한 줄(FS-18). 썸네일 · 이름 · 요약 · 저장 토글. */
@Composable
internal fun SearchPlaceRow(
    place: MapPlaceVO,
    onClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick)
                .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PlaceThumbnail(
            url = place.thumbnailUrl,
            category = place.category,
            modifier = Modifier.size(58.dp),
            shape = ThumbShape,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_star_24),
                    contentDescription = null,
                    tint = color.contentRating,
                    modifier = Modifier.size(13.dp),
                )
                DsText(
                    text = place.summaryLine(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        SaveToggleIcon(saved = place.saved, onClick = onSaveClick)
    }
}

/** `4.8 · 카페 · 베이커리 · 도보 6분` — 앞의 별 아이콘은 호출부가 따로 그린다. */
private fun MapPlaceVO.summaryLine(): String =
    listOfNotNull(
        rating.takeIf { it > 0 }?.toString(),
        category,
        subCategory,
        walkingMinutes?.let { "도보 ${it}분" },
    ).joinToString(" · ")

/** 검색 결과의 코스 카드(FS-18 코스 탭). 커버 위 카테고리 배지 + 제목 + 요약. */
@Composable
internal fun SearchCourseCard(
    course: SearchCourseVO,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
    ) {
        Box {
            ResultImage(
                url = course.coverImageUrl.ifEmpty { null },
                modifier = Modifier.fillMaxWidth().height(150.dp),
            )
            if (course.categoryLabel.isNotEmpty()) {
                DsText(
                    text = course.categoryLabel,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel1,
                    modifier =
                        Modifier
                            .padding(12.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(color.bgDefaultLevel1)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DsText(
                text = course.title,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CourseLikeCount(count = course.likeCountText)
                DsText(
                    text = course.summaryLine(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** 전체 탭의 "관련 코스" 카드. 세로로 긴 카드 대신 썸네일이 왼쪽에 붙는 가로형이다. */
@Composable
internal fun SearchCourseCompactCard(
    course: SearchCourseVO,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(color.bgDefaultLevel1)
                .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ResultImage(
            url = course.coverImageUrl.ifEmpty { null },
            modifier = Modifier.size(width = 104.dp, height = 85.dp),
        )
        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DsText(
                text = course.title,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CourseLikeCount(count = course.likeCountText)
                DsText(
                    text = course.summaryLine(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/** `4 스팟 · 3시간 · 지호님 · 1.2k명 따라감`. 비어 있는 값은 빼고 잇는다. */
private fun SearchCourseVO.summaryLine(): String =
    listOfNotNull(
        saveCountText.ifEmpty { null }?.let { "저장 $it" },
        spotCount.takeIf { it > 0 }?.let { "$it 스팟" },
        durationHours.takeIf { it > 0 }?.let { "${it}시간" },
        authorName.ifEmpty { null },
        followCountText.ifEmpty { null }?.let { "$it 따라감" },
    ).joinToString(" · ")

/** `장소 모두 보기 →` 처럼 구역 아래에 놓이는 더 보기 버튼. */
@Composable
internal fun SearchShowAllButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(CardShape)
                .background(color.bgDefaultLevel1)
                .border(width = 1.dp, color = color.borderDefaultLevel1, shape = CardShape)
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel1,
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            tint = color.contentDefaultLevel2,
            modifier = Modifier.padding(start = 4.dp).size(14.dp),
        )
    }
}

/** 저장(북마크) 토글. 저장되면 채운 아이콘 + 강조색으로 바뀐다. */
@Composable
private fun SaveToggleIcon(
    saved: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Icon(
        painter =
            painterResource(
                if (saved) R.drawable.ic_bookmark_filled_24 else R.drawable.ic_tab_bookmark_24,
            ),
        contentDescription = if (saved) "저장 해제" else "저장",
        tint = if (saved) color.contentAccent else color.contentDefaultLevel3,
        modifier =
            modifier
                .size(22.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
    )
}

/** 코스 결과의 커버 이미지. 비율이 달라도 빈 공간 없이 채우도록 크롭하고, 없으면 자리 지킴 색으로 남는다. */
@Composable
private fun ResultImage(
    url: String?,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(0.dp),
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(modifier = modifier.clip(shape).background(color.imagePlaceholder)) {
        if (!url.isNullOrEmpty()) {
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
