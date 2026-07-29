package com.chillsam.courmy.main.presentation.saved

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.login.LoginPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.entity.saved.SavedCourseVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

/**
 * 저장(북마크) 화면(FS-14). 세션 로그인 여부에 따라
 * 저장한 코스 리스트([SavedCourseList]) / 게스트 잠금([GuestSaved]) 을 분기한다.
 */
@Composable
fun SavedPage(modifier: Modifier = Modifier) {
    val session = LocalSessionUiState.current
    if (session.isLoggedIn) {
        SavedCourseList(modifier = modifier)
    } else {
        GuestSaved(modifier = modifier)
    }
}

/** 로그인 상태: 저장한 코스 카드 리스트. */
@Composable
private fun SavedCourseList(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    var courses by remember { mutableStateOf(SavedCourseVO.sample) }
    var pendingRemoval by remember { mutableStateOf<SavedCourseVO?>(null) }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
        ) {
            DsText(
                text = "저장한 코스",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp),
            )
            courses.forEach { course ->
                SavedCourseCard(
                    course = course,
                    onClick = { navigationHelper.navigateByRoute(CourseDetailPage.route(course.id)) },
                    onBookmarkClick = { pendingRemoval = course },
                    modifier = Modifier.padding(bottom = 16.dp),
                )
            }
        }

        CourmyBottomBar(
            selectedTab = MainTab.SAVED,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.MY -> navigationHelper.navigateTo(MyPage)
                    else -> Unit
                }
            },
        )
    }

    pendingRemoval?.let { course ->
        UnsaveConfirmDialog(
            onConfirm = {
                courses = courses.filterNot { it.id == course.id }
                pendingRemoval = null
            },
            onDismiss = { pendingRemoval = null },
        )
    }
}

/**
 * 저장 취소 확인 다이얼로그. 코스 작성 나가기 다이얼로그(ExitConfirmDialog)와 동일한 톤
 * (스크림 + 중앙 라운드 카드 + 초록 기본/하위 텍스트 버튼). 확인 시 저장함에서 제거한다.
 */
@Composable
private fun UnsaveConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    // 커스텀 오버레이라 시스템 Back 이 화면을 이탈시키지 않고 다이얼로그만 닫도록 가로챈다.
    BackHandler(onBack = onDismiss)
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .noRippleClickable(onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.bgDefaultLevel0)
                    .noRippleClickable {}
                    .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsText(
                text = "저장을 취소할까요?",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "저장을 취소하면 이 코스가\n저장함에서 사라져요.",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                textAlign = TextAlign.Center,
                maxLines = Int.MAX_VALUE,
            )
            Spacer(Modifier.height(12.dp))
            DialogFilledButton(
                text = "저장 취소",
                background = color.contentDanger,
                textColor = color.contentOnAccent,
                onClick = onConfirm,
            )
            DialogTextButton(
                text = "저장 유지",
                textColor = color.contentDefaultLevel1,
                onClick = onDismiss,
            )
        }
    }
}

/** 다이얼로그용 채움 버튼(예: danger 배경의 파괴적 액션). */
@Composable
private fun DialogFilledButton(
    text: String,
    background: Color,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(background)
                .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = textColor,
        )
    }
}

@Composable
private fun DialogTextButton(
    text: String,
    textColor: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = text,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = textColor,
        )
    }
}

/** 리플 없는 클릭(스크림/카드 배경 소비용). */
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier =
    this.composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        )
    }

/** 저장한 코스 카드: 썸네일(태그 칩 + 북마크 배지) + 제목 + "장소 N곳 · @handle". */
@Composable
private fun SavedCourseCard(
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

/** 비로그인 상태: 저장 잠금 안내 + 로그인 유도. */
@Composable
private fun GuestSaved(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp),
        ) {
            DsText(
                text = "저장",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(width = 96.dp, height = 110.dp)
                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(color.bgDefaultLevel1),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bookmark_filled_24),
                        contentDescription = null,
                        tint = color.contentAccent,
                        modifier = Modifier.size(38.dp),
                    )
                }
                DsText(
                    text = "마음에 든 장소를\n여기에 모아보세요",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 20.dp),
                )
                DsText(
                    text = "로그인하면 저장한 장소가 지도 위에 모이고,\n바로 나만의 코스로 이어집니다.",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 10.dp),
                )
                DsButton(
                    text = "로그인 / 회원가입",
                    onClick = { navigationHelper.navigateTo(LoginPage) },
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                )
            }
        }

        CourmyBottomBar(
            selectedTab = MainTab.SAVED,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.MY -> navigationHelper.navigateTo(GuestMyPage)
                    else -> Unit
                }
            },
        )
    }
}
