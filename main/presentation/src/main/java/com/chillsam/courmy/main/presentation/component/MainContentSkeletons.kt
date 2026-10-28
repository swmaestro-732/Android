package com.chillsam.courmy.main.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.SkeletonBlock
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding

/** 각 목록의 실제 커버 비율과 제목·메타 위치를 유지한다. 바깥 여백은 목록이 소유한다. */
@Composable
internal fun CourseCardsSkeleton(layout: CourseSkeletonLayout = CourseSkeletonLayout.FEED) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(Modifier.fillMaxWidth().semantics { contentDescription = "코스를 불러오는 중" }, Arrangement.spacedBy(16.dp)) {
        repeat(2) {
            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(color.bgDefaultLevel1)) {
                val cover =
                    when (layout) {
                        CourseSkeletonLayout.FEED -> Modifier.height(170.dp)
                        CourseSkeletonLayout.SAVED -> Modifier.aspectRatio(356f / 126f)
                        CourseSkeletonLayout.SEARCH -> Modifier.height(150.dp)
                    }
                SkeletonBlock(Modifier.fillMaxWidth().then(cover), RoundedCornerShape(0.dp))
                Column(Modifier.padding(16.dp), Arrangement.spacedBy(8.dp)) {
                    if (layout != CourseSkeletonLayout.FEED) SkeletonBlock(Modifier.fillMaxWidth(0.85f).height(20.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.55f).height(14.dp))
                }
            }
        }
    }
}

/** 홈의 280dp 가로 추천 카드와 같은 공간을 예약한다. */
@Composable
internal fun RecommendedCardsSkeleton() {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp))
            .horizontalScroll(rememberScrollState(), enabled = false)
            .semantics { contentDescription = "추천 코스를 불러오는 중" }
            .padding(horizontal = ScreenHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(2) {
            Column(Modifier.width(280.dp).clip(RoundedCornerShape(16.dp)).background(color.bgDefaultLevel1)) {
                SkeletonBlock(Modifier.fillMaxWidth().aspectRatio(16f / 10f), RoundedCornerShape(0.dp))
                Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp), Arrangement.spacedBy(8.dp)) {
                    SkeletonBlock(Modifier.fillMaxWidth().height(18.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.65f).height(18.dp))
                    SkeletonBlock(Modifier.width(90.dp).height(14.dp))
                }
            }
        }
    }
}

/** 저장 장소 카드의 64dp 썸네일·두 줄 텍스트·저장 버튼 자리. */
@Composable
internal fun PlaceRowsSkeleton(
    modifier: Modifier = Modifier,
    count: Int = 3,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier.fillMaxWidth().semantics { contentDescription = "장소를 불러오는 중" }, Arrangement.spacedBy(12.dp)) {
        repeat(count) {
            Row(
                Modifier.fillMaxWidth().background(color.bgDefaultLevel1, RoundedCornerShape(16.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SkeletonBlock(Modifier.size(64.dp), RoundedCornerShape(12.dp))
                Column(Modifier.weight(1f), Arrangement.spacedBy(8.dp)) {
                    SkeletonBlock(Modifier.fillMaxWidth(0.85f).height(18.dp))
                    SkeletonBlock(Modifier.fillMaxWidth(0.65f).height(14.dp))
                }
                SkeletonBlock(Modifier.size(24.dp), CircleShape)
            }
        }
    }
}

/** 아바타+닉네임+소개와 2열 코스 그리드로 프로필의 실제 윤곽을 보여 준다. */
@Composable
internal fun ProfileContentSkeleton(
    modifier: Modifier = Modifier,
    showTabs: Boolean = true,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier
            .fillMaxSize()
            .background(
                color.bgDefaultLevel0,
            ).statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenHorizontalPadding)
            .semantics { contentDescription = "프로필을 불러오는 중" },
    ) {
        Spacer(Modifier.height(60.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            SkeletonBlock(Modifier.size(104.dp), CircleShape)
            Column(Modifier.weight(1f), Arrangement.spacedBy(10.dp)) {
                SkeletonBlock(Modifier.fillMaxWidth(0.7f).height(24.dp))
                SkeletonBlock(Modifier.fillMaxWidth(0.5f).height(16.dp))
                SkeletonBlock(Modifier.fillMaxWidth().height(16.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        SkeletonBlock(Modifier.fillMaxWidth(0.75f).height(16.dp))
        Spacer(Modifier.height(32.dp))
        if (showTabs) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                SkeletonBlock(Modifier.width(70.dp).height(24.dp))
                SkeletonBlock(Modifier.width(70.dp).height(24.dp))
            }
            Spacer(Modifier.height(20.dp))
        }
        repeat(2) {
            Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                repeat(2) {
                    Column(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(color.bgDefaultLevel1)) {
                        SkeletonBlock(Modifier.fillMaxWidth().aspectRatio(1.5f), RoundedCornerShape(0.dp))
                        Column(Modifier.padding(12.dp), Arrangement.spacedBy(8.dp)) {
                            SkeletonBlock(Modifier.fillMaxWidth().height(18.dp))
                            SkeletonBlock(Modifier.width(40.dp).height(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun FollowRowsSkeleton() {
    Column(Modifier.fillMaxWidth().semantics { contentDescription = "팔로우 목록을 불러오는 중" }) {
        repeat(5) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SkeletonBlock(Modifier.size(44.dp), CircleShape)
                Column(Modifier.weight(1f), Arrangement.spacedBy(6.dp)) {
                    SkeletonBlock(Modifier.width(100.dp).height(18.dp))
                    SkeletonBlock(Modifier.width(140.dp).height(14.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ProfileSkeletonPreview() {
    DesignSystemTheme { ProfileContentSkeleton() }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun SavedSkeletonPreview() {
    DesignSystemTheme { PlaceRowsSkeleton(Modifier.padding(20.dp)) }
}
