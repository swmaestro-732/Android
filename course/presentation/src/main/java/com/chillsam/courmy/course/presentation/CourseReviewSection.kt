package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsRatingStar
import com.chillsam.courmy.common.presentation.component.DsRatingStars
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseDetailVO
import com.chillsam.courmy.course.entity.CourseReviewTagVO
import com.chillsam.courmy.course.entity.CourseReviewVO

/**
 * 코스 상세 리뷰 섹션(Figma FS-11 하단). 헤더(평균 별점·리뷰 수·전체보기) + 리뷰 미리보기 카드.
 *
 * 서버가 상세 응답에 **미리보기 2개**만 담아 주므로([com.chillsam.courmy.course.entity.CourseDetailVO.reviews])
 * 이 섹션은 목록을 페이징하지 않는다. 나머지는 "전체보기" → 후기 전체보기 화면(FS-37)의 몫이며,
 * 그 화면이 `GET /service/v1/courses/{courseId}/reviews` 를 정렬·커서 페이징으로 받아 온다.
 */
@Composable
internal fun CourseReviewSection(
    detail: CourseDetailVO,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    // 위 지도 섹션과 붙지 않게 한 단 벌린다(장소 섹션과 같은 간격).
    Column(
        modifier = modifier.padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        ReviewSectionHeader(detail = detail, onSeeAll = onSeeAll)
        if (detail.reviews.isEmpty()) {
            // 리뷰 0개도 정상 상태다 — 섹션을 통째로 감추면 "리뷰를 남길 수 있는 코스" 라는 사실이 사라진다.
            DsText(
                text = "아직 후기가 없어요. 코스를 다녀온 경험을 나눠 주세요.",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                maxLines = Int.MAX_VALUE,
            )
        } else {
            detail.reviews.forEach { review -> ReviewCard(review = review) }
        }
    }
}

/** 헤더: "리뷰 ★ 4.8 · 128개" 와 우측 "전체보기". 리뷰가 없으면 평점·전체보기를 뺀다. */
@Composable
private fun ReviewSectionHeader(
    detail: CourseDetailVO,
    onSeeAll: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val hasReviews = detail.reviews.isNotEmpty()
    Row(
        modifier = Modifier.padding(bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DsText(
                text = "후기",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            if (hasReviews) {
                // 평균 별점은 숫자가 본체이므로 별 하나만 세워 두고 값을 붙인다(카드의 5칸 별점과 다르다).
                DsRatingStar(starSize = 15.dp)
                DsText(
                    text = detail.rating,
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                )
                DsText(
                    text = "· ${detail.reviewCountText}",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                )
            }
        }
        if (hasReviews) {
            DsText(
                text = "전체보기",
                modifier =
                    Modifier
                        .clickable(onClick = onSeeAll)
                        // 글자만 두면 탭 영역이 너무 좁다. 여백까지 누를 수 있게 패딩을 클릭 안쪽에 둔다.
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
            )
        }
    }
}

/**
 * 리뷰 카드 한 장: 작성자(아바타·닉네임·별점·상대날짜) + 본문 + 태그 + 첨부 사진.
 *
 * 상세 미리보기(FS-11)와 후기 전체보기(FS-37)가 같은 카드를 쓴다. 다른 건 태그 칩 노출 여부뿐이라
 * [showTags] 로 가른다 — 미리보기에서는 카드가 길어져 장소 목록을 밀어내므로 감춘다.
 *
 * [onDelete] 가 null 이 아니면 우측 상단에 삭제 버튼을 둔다. 내 후기일 때만 호출부가 넘긴다 —
 * 서버도 남의 리뷰 삭제를 거절하므로, 눌러 봐야 실패할 버튼을 보여주지 않는다.
 */
@Composable
internal fun ReviewCard(
    review: CourseReviewVO,
    modifier: Modifier = Modifier,
    showTags: Boolean = false,
    onDelete: (() -> Unit)? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val cardShape = RoundedCornerShape(16.dp)
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(cardShape)
                .border(1.dp, color.borderDefaultLevel0, cardShape)
                .background(color.bgDefaultLevel1)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CourseImage(
                url = review.authorImageUrl,
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                // 작성자 밴드와 같은 규칙 — 프로필 자리는 무엇이 빠졌는지 보이게 아이콘을 둔다.
                emptyIconRes = R.drawable.ic_tab_person_24,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                DsText(
                    text = review.author,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel0,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    DsRatingStars(rating = review.rating, starSize = 11.dp)
                    if (review.dateText.isNotBlank()) {
                        DsText(
                            text = "· ${review.dateText}",
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = color.contentDefaultLevel3,
                        )
                    }
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_trash_24),
                        contentDescription = "내 후기 삭제",
                        tint = color.contentDefaultLevel3,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
        if (review.body.isNotBlank()) {
            DsText(
                text = review.body,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel1,
                maxLines = Int.MAX_VALUE,
            )
        }
        if (showTags && review.tags.isNotEmpty()) {
            ReviewTagRow(tags = review.tags)
        }
        if (review.photoUrls.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                review.photoUrls.forEach { url ->
                    CourseImage(
                        url = url,
                        modifier = Modifier.size(88.dp),
                        shape = RoundedCornerShape(10.dp),
                    )
                }
            }
        }
    }
}

/**
 * 리뷰 태그 칩 줄(FS-37). 한 줄에 다 못 담으면 다음 줄로 흘린다 — 가로로 잘리면 뒤쪽 태그를
 * 볼 방법이 없고, 스크롤을 붙이면 세로 스크롤과 겹쳐 카드가 불편해진다.
 */
@Composable
private fun ReviewTagRow(tags: List<CourseReviewTagVO>) {
    val color = DesignSystemThemeImpl.designSystemColor
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        tags.forEach { tag ->
            DsText(
                // 이모지를 안 주는 태그도 있어 붙여 쓰고 앞뒤 공백을 정리한다.
                text = "${tag.icon} ${tag.label}".trim(),
                modifier =
                    Modifier
                        .clip(CircleShape)
                        .background(color.bgDefaultLevel0)
                        .padding(horizontal = 11.dp, vertical = 5.dp),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel0,
            )
        }
    }
}
