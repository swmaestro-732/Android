package com.chillsam.courmy.course.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.SkeletonBlock
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding

/** 실제 상세의 232dp 커버, 작성자, 본문, 장소 순서에 맞춘다. */
@Composable
internal fun CourseDetailSkeleton() {
    LazyColumn(Modifier.fillMaxSize().semantics { contentDescription = "코스를 불러오는 중" }) {
        item { SkeletonBlock(Modifier.fillMaxWidth().height(232.dp), RoundedCornerShape(0.dp)) }
        item {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SkeletonBlock(Modifier.size(40.dp), CircleShape)
                SkeletonBlock(Modifier.width(120.dp).height(16.dp))
            }
        }
        item {
            Column(
                Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(20.dp))
                SkeletonBlock(Modifier.fillMaxWidth().height(16.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.8f).height(16.dp))
                SkeletonBlock(Modifier.width(100.dp).height(22.dp))
                repeat(2) { PlaceTimelineSkeletonRow() }
            }
        }
    }
}

/** 계획 화면의 장소 타임라인과 하단 지도 자리. 상단바는 호출부에서 유지한다. */
@Composable
internal fun PlanDetailSkeleton() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().semantics { contentDescription = "콘텐츠를 불러오는 중" },
        contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item { SkeletonBlock(Modifier.width(110.dp).height(22.dp)) }
        items(3) { PlaceTimelineSkeletonRow() }
        item { SkeletonBlock(Modifier.width(130.dp).height(22.dp)) }
        item { SkeletonBlock(Modifier.fillMaxWidth().height(220.dp), RoundedCornerShape(18.dp)) }
    }
}

@Composable
private fun PlaceTimelineSkeletonRow() {
    Row(
        Modifier.fillMaxWidth().height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBlock(Modifier.size(24.dp), CircleShape)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SkeletonBlock(Modifier.fillMaxWidth(0.65f).height(20.dp))
            SkeletonBlock(Modifier.fillMaxWidth(0.45f).height(14.dp))
        }
        SkeletonBlock(Modifier.size(20.dp))
    }
}

/** 평균/별점 분포와 실제 리뷰 카드 크기를 따른다. */
@Composable
internal fun CourseReviewListSkeleton(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().semantics { contentDescription = "후기를 불러오는 중" }) {
        ReviewListTopBar(onBack = onBack)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SkeletonBlock(Modifier.width(88.dp).height(96.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        repeat(5) { SkeletonBlock(Modifier.fillMaxWidth().height(8.dp)) }
                    }
                }
            }
            item { SkeletonBlock(Modifier.width(180.dp).height(32.dp), CircleShape) }
            items(3) { ReviewCardSkeleton() }
        }
    }
}

@Composable
private fun ReviewCardSkeleton() {
    Column(
        Modifier
            .fillMaxWidth()
            .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            SkeletonBlock(Modifier.size(34.dp), CircleShape)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SkeletonBlock(Modifier.width(100.dp).height(14.dp))
                SkeletonBlock(Modifier.width(120.dp).height(12.dp))
            }
        }
        SkeletonBlock(Modifier.fillMaxWidth().height(16.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.7f).height(16.dp))
    }
}

/** 초안 목록에는 이미지가 없으므로 제목/날짜/삭제 자리만 그린다. */
@Composable
internal fun DraftListSkeleton() {
    LazyColumn(
        modifier = Modifier.semantics { contentDescription = "임시저장한 코스를 불러오는 중" },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(4) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1, RoundedCornerShape(18.dp))
                    .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.75f).height(20.dp))
                    SkeletonBlock(Modifier.width(100.dp).height(14.dp))
                }
                Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    SkeletonBlock(Modifier.size(18.dp), CircleShape)
                }
            }
        }
    }
}

/** 검색 결과 행의 44dp 이미지, 두 줄 텍스트, 선택 표시 자리. */
@Composable
internal fun PlaceSearchSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().semantics { contentDescription = "목록을 불러오는 중" },
        contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(6) {
            Row(
                Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SkeletonBlock(Modifier.size(44.dp), RoundedCornerShape(10.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.75f).height(18.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.45f).height(14.dp))
                }
                SkeletonBlock(Modifier.size(24.dp), CircleShape)
            }
        }
    }
}

@Composable
internal fun CourseEditSkeleton() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().semantics { contentDescription = "콘텐츠를 불러오는 중" },
        contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item { SkeletonBlock(Modifier.width(100.dp).height(22.dp)) }
        items(2) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SkeletonBlock(Modifier.width(80.dp).height(14.dp))
                SkeletonBlock(Modifier.fillMaxWidth().height(52.dp), RoundedCornerShape(12.dp))
            }
        }
        item { SkeletonBlock(Modifier.width(120.dp).height(22.dp)) }
        item { PlaceTimelineSkeletonRow() }
    }
}

@Composable
internal fun CourseCreateSkeleton() {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().semantics { contentDescription = "코스 작성 화면을 불러오는 중" },
    ) {
        SkeletonBlock(
            Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp).height(48.dp),
        )
        SkeletonBlock(Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding).height(36.dp))
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { SkeletonBlock(Modifier.width(130.dp).height(24.dp)) }
            items(3) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1, RoundedCornerShape(16.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SkeletonBlock(Modifier.width(12.dp).height(24.dp))
                    SkeletonBlock(Modifier.size(48.dp), RoundedCornerShape(11.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SkeletonBlock(Modifier.fillMaxWidth(0.8f).height(18.dp))
                        SkeletonBlock(Modifier.fillMaxWidth(0.5f).height(14.dp))
                    }
                    SkeletonBlock(Modifier.size(22.dp), CircleShape)
                }
            }
        }
        SkeletonBlock(
            Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp).height(52.dp),
        )
    }
}

@Composable
internal fun CommentListSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth().semantics { contentDescription = "목록을 불러오는 중" },
        contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        reverseLayout = true,
    ) {
        items(4) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SkeletonBlock(Modifier.size(36.dp), CircleShape)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBlock(Modifier.width(130.dp).height(16.dp))
                    SkeletonBlock(Modifier.fillMaxWidth().height(14.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.7f).height(14.dp))
                }
            }
        }
    }
}

/** 코스 데이터 도착 전의 따라가기 미리보기. 이미 받은 지도는 호출부가 그대로 유지한다. */
@Composable
internal fun CourseFollowSkeleton() {
    Column(
        Modifier.fillMaxSize().statusBarsPadding().semantics { contentDescription = "따라갈 코스를 불러오는 중" },
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SkeletonBlock(Modifier.fillMaxWidth(0.7f).height(20.dp))
            SkeletonBlock(Modifier.width(120.dp).height(14.dp))
        }
        SkeletonBlock(Modifier.fillMaxWidth().weight(1f), RoundedCornerShape(0.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ScreenHorizontalPadding,
                    vertical = 16.dp,
                ).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            SkeletonBlock(Modifier.width(110.dp).height(20.dp))
            repeat(2) {
                Row(
                    Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SkeletonBlock(Modifier.size(24.dp), CircleShape)
                    SkeletonBlock(Modifier.size(42.dp), RoundedCornerShape(10.dp))
                    SkeletonBlock(Modifier.weight(1f).height(18.dp))
                }
            }
            SkeletonBlock(Modifier.fillMaxWidth().height(48.dp), RoundedCornerShape(12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CourseDetailSkeletonPreview() {
    DesignSystemTheme { CourseDetailSkeleton() }
}

@Preview(showBackground = true)
@Composable
private fun PlaceSearchSkeletonPreview() {
    DesignSystemTheme { PlaceSearchSkeleton() }
}
