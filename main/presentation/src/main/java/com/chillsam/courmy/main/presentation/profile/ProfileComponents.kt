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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO

// 마이(FS-15)·타유저(FS-15 OtherUserPage) 프로필이 공유하는 구성요소.
// 두 화면은 상단 프로필·코스 그리드가 같고, 상단 좌우 버튼(뒤로 가기·공유·설정)과 소개 유무,
// 팔로우 버튼 유무만 다르므로 그 부분만 파라미터·슬롯으로 뺀다.

/** 아바타 사진 지름. 오른쪽 3줄(닉네임·아이디·팔로우 수) 높이와 맞물리는 크기. */
private val AVATAR_SIZE = 92.dp

/** 아바타를 두르는 흰 테두리 두께. */
private val AVATAR_RING = 6.dp

/** 아바타 그림자 높이. 흰 배경 위에서 원의 경계를 만드는 유일한 장치라 넉넉히 준다. */
private val AVATAR_SHADOW_ELEVATION = 14.dp

/**
 * 프로필 상단 헤더(마이·타유저 공용).
 *
 * 커버 이미지 없이 아바타를 왼쪽에 두고 오른쪽에 닉네임·아이디·팔로우 수를 쌓는다.
 * 소개는 그 아래 전체 폭으로 흐른다. 전부 왼쪽 정렬이며 화면 좌우 여백을 따른다.
 *
 * @param onBack null 이면 뒤로 가기 버튼을 그리지 않는다(탭으로 진입하는 마이 화면).
 * @param bio null 이면 소개 줄 자체를 두지 않고(서버가 소개를 주지 않는 타유저 프로필),
 *   빈 문자열이면 "아직 소개가 없어요" 안내를 옅게 표시한다.
 * @param actions 우상단 버튼 슬롯([ProfileHeaderIconButton] 을 넣는다).
 */
@Composable
fun ProfileHeader(
    imageUrl: String,
    nickname: String,
    handle: String,
    followerCount: String,
    followingCount: String,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    bio: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = ScreenHorizontalPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                ProfileHeaderIconButton(
                    iconRes = R.drawable.ic_chevron_left_24,
                    contentDescription = "뒤로 가기",
                    onClick = onBack,
                )
            }
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), content = actions)
        }

        Row(
            // 아이콘 버튼 줄과 바짝 붙인다. 아바타 그림자가 여백처럼 보여서 넉넉히 주면 멀어 보인다.
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RingedAvatar(imageUrl = imageUrl)
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                DsText(
                    text = nickname,
                    style = DesignSystemThemeImpl.typeScale.titleExtraL,
                    color = color.contentDefaultLevel0,
                )
                // 핸들이 없으면(가입 전 프로필 등) "@" 한 글자만 남으므로 줄째 그리지 않는다.
                if (handle.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    DsText(
                        text = "@$handle",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = color.contentDefaultLevel2,
                    )
                }
                Spacer(Modifier.height(10.dp))
                FollowCounts(
                    followerCount = followerCount,
                    followingCount = followingCount,
                    onFollowerClick = onFollowerClick,
                    onFollowingClick = onFollowingClick,
                )
            }
        }

        if (bio == null) {
            Spacer(Modifier.height(20.dp))
        } else {
            // 소개가 없으면 빈 줄 대신 안내 문구를 옅게 표시해, 편집으로 채울 수 있음을 알린다.
            val hasBio = bio.isNotBlank()
            DsText(
                text = if (hasBio) bio else "아직 소개가 없어요",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = if (hasBio) color.contentDefaultLevel1 else color.contentDefaultLevel3,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 20.dp),
            )
        }
    }
}

/**
 * 흰 테두리 + 그림자를 두른 아바타.
 *
 * 테두리는 회색 배경과 대비로 경계를 만들고, 그림자는 원을 떠 보이게 한다.
 * 배경색이 밝아도 원이 묻히지 않도록 그림자는 짙고 넓게 잡는다.
 */
@Composable
private fun RingedAvatar(imageUrl: String) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .shadow(
                    elevation = AVATAR_SHADOW_ELEVATION,
                    shape = CircleShape,
                    ambientColor = color.contentDefaultLevel0.copy(alpha = 0.32f),
                    spotColor = color.contentDefaultLevel0.copy(alpha = 0.40f),
                ).clip(CircleShape)
                .background(color.bgDefaultLevel1)
                .padding(AVATAR_RING),
    ) {
        ProfileAvatar(imageUrl = imageUrl, size = AVATAR_SIZE)
    }
}

/** "팔로워 1.4k | 팔로잉 312". 라벨은 옅게, 숫자는 진하게 두어 숫자가 먼저 읽히게 한다. */
@Composable
private fun FollowCounts(
    followerCount: String,
    followingCount: String,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(verticalAlignment = Alignment.CenterVertically) {
        FollowCountItem(label = "팔로워", value = followerCount, onClick = onFollowerClick)
        Box(
            modifier =
                Modifier
                    .padding(horizontal = 14.dp)
                    .width(1.dp)
                    .height(12.dp)
                    .background(color.borderDefaultLevel1),
        )
        FollowCountItem(label = "팔로잉", value = followingCount, onClick = onFollowingClick)
    }
}

@Composable
private fun FollowCountItem(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
        )
        Spacer(Modifier.width(6.dp))
        DsText(
            text = value,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
    }
}

/** 프로필 헤더 상단의 원형 아이콘 버튼(뒤로 가기·공유·설정). 강조 배경 + 흰 아이콘. */
@Composable
fun ProfileHeaderIconButton(
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
                .background(color.bgAccent)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = color.contentOnAccent,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** 코스 그리드 위 구역 제목("내 코스" 등). */
@Composable
fun ProfileSectionLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textStrongS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = ScreenHorizontalPadding, end = ScreenHorizontalPadding, top = 20.dp),
    )
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
        contentAlignment = Alignment.Center,
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "프로필 이미지",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            // 사진을 아직 안 올린 사용자. 빈 회색 원만 두면 로딩이 덜 끝난 것처럼 보인다.
            Icon(
                painter = painterResource(R.drawable.ic_tab_person_24),
                contentDescription = "프로필 이미지 없음",
                tint = color.contentDefaultLevel3,
                modifier = Modifier.size(size * AVATAR_ICON_RATIO),
            )
        }
    }
}

/** 아바타 지름 대비 기본 아이콘 크기. 원 안에 여백을 남겨 아이콘이 갇혀 보이지 않게 한다. */
private const val AVATAR_ICON_RATIO = 0.55f

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
