package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsRatingStars
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.entity.CourseRatingBucketVO
import com.chillsam.courmy.course.entity.CourseReviewListVO
import com.chillsam.courmy.course.entity.CourseReviewSort
import com.chillsam.courmy.course.entity.CourseReviewVO
import kotlinx.collections.immutable.ImmutableList

/** 사진 띠 한 칸 크기(Figma FS-37: 88×88, radius 12). */
private val PHOTO_TILE = 88.dp

/**
 * 코스 후기 전체보기 화면(FS-37). 상단 바 → 평균·별점 분포 → 후기 사진 띠 → 정렬 탭 → 리뷰 목록.
 *
 * 목록이 길어질 수 있어 [LazyColumn] 으로 그리고, 집계·사진·정렬 탭은 함께 스크롤되는 헤더로 둔다.
 * 코스 상세의 리뷰 섹션과 카드를 공유한다([ReviewCard]) — 여기서는 태그 칩까지 보여준다.
 *
 * 정렬은 서버가 하므로 [onSortChange] 는 목록을 처음부터 다시 받게 한다.
 */
@Composable
fun CourseReviewListScreen(
    summary: CourseReviewListVO,
    reviews: ImmutableList<CourseReviewVO>,
    sort: CourseReviewSort,
    onBack: () -> Unit,
    onSortChange: (CourseReviewSort) -> Unit,
    modifier: Modifier = Modifier,
    isLoadingMore: Boolean = false,
    deletingReviewId: Long = 0L,
    canWriteReview: Boolean = false,
    onLoadMore: () -> Unit = {},
    onDeleteReview: (Long) -> Unit = {},
    onWriteReview: () -> Unit = {},
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val listState = rememberLazyListState()
    // 삭제를 물어볼 리뷰 id. 0 이면 확인 창을 띄우지 않는다 — 되돌릴 수 없어 한 번 확인한다.
    var pendingDeleteId by remember { mutableLongStateOf(0L) }
    LoadMoreOnScrollEnd(listState = listState, onLoadMore = onLoadMore)
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0),
    ) {
        ReviewListTopBar(onBack = onBack)
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 20.dp),
        ) {
            if (reviews.isNotEmpty()) {
                item(key = "summary") { RatingSummaryBand(summary = summary) }
            }
            if (summary.photoUrls.isNotEmpty()) {
                item(key = "photos") { ReviewPhotoBand(summary = summary) }
            }
            if (reviews.isNotEmpty()) {
                item(key = "sort") { SortTabs(selected = sort, onSelect = onSortChange) }
            }
            if (reviews.isEmpty()) {
                item(key = "empty") { ReviewListEmpty() }
            } else {
                // 서버 id 만으로 키를 만들면 id 를 못 받은 항목끼리 0 으로 겹쳐 런타임에 터진다.
                // 위치를 섞어 항상 유일하게 만든다.
                itemsIndexed(
                    items = reviews,
                    key = { index, review -> "${review.id}-$index" },
                ) { _, review ->
                    Box(
                        modifier =
                            Modifier.padding(
                                horizontal = ScreenHorizontalPadding,
                                vertical = 6.dp,
                            ),
                    ) {
                        ReviewCard(
                            review = review,
                            showTags = true,
                            // 삭제 중인 카드는 다시 누르지 못하게 버튼을 뗀다.
                            onDelete =
                                if (review.isMine && review.id > 0 && deletingReviewId == 0L) {
                                    { pendingDeleteId = review.id }
                                } else {
                                    null
                                },
                        )
                    }
                }
            }
            if (isLoadingMore) {
                item(key = "loadingMore") { LoadingMoreFooter() }
            }
        }
        if (canWriteReview) {
            ReviewWriteBar(onWriteReview = onWriteReview)
        }
    }
    if (pendingDeleteId != 0L) {
        DsConfirmDialog(
            title = "후기를 삭제할까요?",
            description = "삭제한 후기는 되돌릴 수 없어요.",
            confirmText = "삭제",
            dismissText = "취소",
            destructive = true,
            onConfirm = {
                onDeleteReview(pendingDeleteId)
                pendingDeleteId = 0L
            },
            onDismiss = { pendingDeleteId = 0L },
        )
    }
}

/** 하단 후기 작성 버튼. 이미 쓴 후기가 있으면 호출부가 이 줄을 아예 빼 버린다. */
@Composable
private fun ReviewWriteBar(onWriteReview: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .navigationBarsPadding()
                .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
    ) {
        DsButton(text = "후기 쓰기", onClick = onWriteReview, modifier = Modifier.fillMaxWidth())
    }
}

/** 상단 바: 뒤로가기 + "따라간 후기". */
@Composable
private fun ReviewListTopBar(onBack: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left_24),
                contentDescription = "뒤로",
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(24.dp),
            )
        }
        DsText(
            text = "코스 후기",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
    }
}

/** 평균 별점(큰 숫자·별·개수) + 별점 분포 막대. */
@Composable
private fun RatingSummaryBand(summary: CourseReviewListVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .padding(horizontal = ScreenHorizontalPadding, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            DsText(
                text = summary.averageRatingText,
                style = DesignSystemThemeImpl.typeScale.displayExtraXL,
                color = color.contentDefaultLevel0,
            )
            DsRatingStars(rating = summary.averageStars, starSize = 11.dp)
            DsText(
                text = summary.totalCountText,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            summary.distribution.forEach { bucket -> RatingBar(bucket = bucket) }
        }
    }
}

/** 별점 분포 한 줄: "5" + 채움 막대. */
@Composable
private fun RatingBar(bucket: CourseRatingBucketVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DsText(
            text = bucket.rating.toString(),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(color.borderDefaultLevel0),
        ) {
            // 비율 0 이면 fillMaxWidth(0f) 가 폭 0 이라 막대가 아예 그려지지 않는다.
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(bucket.ratio)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(color.contentRating),
            )
        }
    }
}

/** "후기 사진 86" + 모두 보기 + 사진 4칸(마지막은 "+82" 오버레이). */
@Composable
private fun ReviewPhotoBand(summary: CourseReviewListVO) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .padding(top = 16.dp, bottom = 17.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                DsText(
                    text = "후기 사진",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel0,
                )
                DsText(
                    text = summary.photoCountText,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
            // 사진 모아보기 화면(FS-23)이 아직 없다. 리뷰 섹션의 "전체보기" 와 같은 이유로 비활성이다.
            DsText(
                text = "모두 보기",
                modifier = Modifier.semantics { disabled() },
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
        }
        Row(
            modifier = Modifier.padding(start = ScreenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            summary.photoUrls.forEachIndexed { index, url ->
                val isLast = index == summary.photoUrls.lastIndex
                Box {
                    CourseImage(
                        url = url,
                        modifier = Modifier.size(PHOTO_TILE),
                        shape = RoundedCornerShape(12.dp),
                    )
                    // 마지막 칸만 어둡게 덮고 남은 장수를 얹는다.
                    if (isLast && summary.morePhotoCountText.isNotBlank()) {
                        Box(
                            modifier =
                                Modifier
                                    .size(PHOTO_TILE)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color.contentDefaultLevel0),
                            contentAlignment = Alignment.Center,
                        ) {
                            DsText(
                                text = summary.morePhotoCountText,
                                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                                color = color.contentOnAccent,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 정렬 탭: 선택된 쪽만 채운 알약, 나머지는 테두리만. */
@Composable
private fun SortTabs(
    selected: CourseReviewSort,
    onSelect: (CourseReviewSort) -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier.padding(
                start = ScreenHorizontalPadding,
                end = ScreenHorizontalPadding,
                top = 14.dp,
                bottom = 4.dp,
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CourseReviewSort.entries.forEach { sort ->
            val isSelected = sort == selected
            DsText(
                text = sort.label,
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(if (isSelected) color.contentDefaultLevel0 else color.bgDefaultLevel1)
                        .then(
                            if (isSelected) {
                                Modifier
                            } else {
                                Modifier.border(1.dp, color.borderDefaultLevel0, CircleShape)
                            },
                        ).clickable { onSelect(sort) }
                        .padding(horizontal = 15.dp, vertical = 8.dp),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = if (isSelected) color.contentOnAccent else color.contentDefaultLevel1,
            )
        }
    }
}

/** 리뷰가 0건인 코스. 집계 밴드는 그대로 두고 목록 자리만 안내로 채운다. */
@Composable
private fun ReviewListEmpty() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenHorizontalPadding, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        DsText(
            text = "첫 후기를 기다리고 있어요",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        )
        DsText(
            text = "코스를 다녀온 경험을 나눠 주세요.\n별점만 남겨도 다음 여행에 도움이 돼요.",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
            textAlign = TextAlign.Center,
            maxLines = Int.MAX_VALUE,
        )
    }
}
