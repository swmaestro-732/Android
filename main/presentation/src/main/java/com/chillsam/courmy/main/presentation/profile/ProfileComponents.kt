package com.chillsam.courmy.main.presentation.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO

// 마이(FS-15)·타유저(FS-15 OtherUserPage) 프로필이 공유하는 구성요소.
// 두 화면은 커버·아바타·통계·코스 그리드가 동일하고, 상단 액션 버튼과 통계 라벨,
// 팔로우 버튼 유무만 다르므로 그 부분만 파라미터로 뺀다.

/** 초록 커버 + 우상단 액션 슬롯 + 커버 하단에 걸친 원형 아바타. */
@Composable
fun ProfileCoverHeader(
    imageUrl: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        // 세로 그라디언트: 상단 bgAccent(Forest600) → 하단 bgAccentPressed(Forest700).
                        Brush.verticalGradient(
                            listOf(color.bgAccent, color.bgAccentPressed),
                        ),
                    ).clipToBounds(),
        ) {
            // 데코 원(커버 밖으로 넘치는 부분은 clip). Figma 원본은 좌하단 민트·우상단 흰색이며,
            // 팔레트에 없는 민트 원색 대신 저투명도 흰색 오버레이로 근사한다.
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-36).dp, y = 40.dp)
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(color.contentOnAccent.copy(alpha = 0.08f)),
            )
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-56).dp, y = 24.dp)
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(color.contentOnAccent.copy(alpha = 0.10f)),
            )
            Row(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                content = actions,
            )
        }
        // 커버 하단에 걸친 아바타(하단 절반이 아래로 넘침).
        // 흰색 링 + 소프트 드롭 섀도우로 커버·배경 위에서 떠 보이게 한다(Figma FS-15).
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 48.dp)
                    .size(96.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = CircleShape,
                        ambientColor = color.contentDefaultLevel0.copy(alpha = 0.5f),
                        spotColor = color.contentDefaultLevel0.copy(alpha = 0.5f),
                    ).clip(CircleShape)
                    .background(color.bgDefaultLevel1)
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(color.imagePlaceholder),
        ) {
            // URL 이 있으면 실제 프로필 사진, 없으면 placeholder 배경만 노출.
            if (imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "프로필 이미지",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

/**
 * 원형 프로필 아바타(홈 우상단·설정 프로필 행 등 작은 자리용).
 * URL 이 비어 있으면 placeholder 배경만 노출한다.
 */
@Composable
fun ProfileAvatar(
    imageUrl: String,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .background(color.imagePlaceholder),
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "프로필 이미지",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

/** 커버 위 원형 아이콘 버튼(공유·설정). */
@Composable
fun ProfileCoverIconButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.bgDefaultLevel0)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = color.contentDefaultLevel0,
            modifier = Modifier.size(18.dp),
        )
    }
}

/**
 * 코스 · 팔로워 · 팔로잉 3열 통계(항목 사이 세로 구분선).
 * [courseLabel] 은 마이가 "내 코스", 타유저가 "코스"로 다르다.
 */
@Composable
fun ProfileStatsRow(
    courseCount: Int,
    followerCount: String,
    followingCount: String,
    courseLabel: String,
    onFollowerClick: (() -> Unit)? = null,
    onFollowingClick: (() -> Unit)? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileStatItem(value = courseCount.toString(), label = courseLabel)
        ProfileStatDivider(color = color.borderDefaultLevel1)
        ProfileStatItem(value = followerCount, label = "팔로워", onClick = onFollowerClick)
        ProfileStatDivider(color = color.borderDefaultLevel1)
        ProfileStatItem(value = followingCount, label = "팔로잉", onClick = onFollowingClick)
    }
}

@Composable
private fun ProfileStatDivider(color: androidx.compose.ui.graphics.Color) {
    VerticalDivider(thickness = 1.dp, color = color, modifier = Modifier.height(28.dp))
}

@Composable
private fun ProfileStatItem(
    value: String,
    label: String,
    onClick: (() -> Unit)? = null,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = value,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        Spacer(Modifier.height(2.dp))
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
    }
}

/** 코스 2열 카드 그리드. */
@Composable
fun ProfileCoursesGrid(
    courses: List<ProfileCourseVO>,
    onCourseClick: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenHorizontalPadding, vertical = 16.dp)) {
        courses.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowItems.forEach { course ->
                    ProfileCourseCard(
                        course = course,
                        onClick = { onCourseClick(course.id) },
                        modifier = Modifier.weight(1f),
                    )
                }
                // 홀수 개일 때 마지막 칸 균형 맞춤.
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

/**
 * 코스 카드(썸네일 + 제목).
 *
 * Figma 는 썸네일에 "따라감" 배지, 제목 아래 "스팟 수" 부제를 두지만 서버 응답에 두 값이 없어
 * 현재는 렌더하지 않는다(가짜 숫자 대신 생략). 자세한 배경은 [ProfileCourseVO] 주석 참고.
 */
@Composable
private fun ProfileCourseCard(
    course: ProfileCourseVO,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .cardShadow(RoundedCornerShape(16.dp)),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
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
        }
        DsText(
            text = course.title,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        )
    }
}

/** 프로필 로딩 중 스피너. */
@Composable
fun ProfileLoading(modifier: Modifier = Modifier) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = color.contentAccent)
    }
}

/** 프로필 로드 실패 시 안내 + 재시도. */
@Composable
fun ProfileError(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsText(
            text = message ?: "프로필을 불러오지 못했습니다.",
            style = DesignSystemThemeImpl.typeScale.textRegularM,
            color = color.contentDefaultLevel1,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        DsButton(text = "다시 시도", onClick = onRetry)
    }
}
