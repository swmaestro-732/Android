package com.chillsam.courmy.main.presentation.my

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.login.LoginPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

/**
 * 비로그인 마이 화면(FS-15-Guest). 게스트 환영 카드(로그인 유도)와
 * "가입하면 할 수 있어요" 안내 카드로 구성한다.
 */
@Composable
fun GuestMyPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val onLogin = { navigationHelper.navigateTo(LoginPage) }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenHorizontalPadding),
        ) {
            DsText(
                text = "마이",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                modifier = Modifier.padding(vertical = 20.dp),
            )

            GuestWelcomeCard(onLogin = onLogin)

            DsText(
                text = "가입하면 할 수 있어요",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            )
            Row(modifier = Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GuestBenefitCard(
                    iconRes = R.drawable.ic_tab_bookmark_24,
                    title = "장소·코스 저장",
                    subtitle = "가고 싶은 장소와 코스를 모아보세요",
                    onClick = onLogin,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                GuestBenefitCard(
                    iconRes = R.drawable.ic_calendar_24,
                    title = "여행 계획",
                    subtitle = "원하는 장소로 나만의 일정을 짜보세요",
                    onClick = onLogin,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GuestBenefitCard(
                    iconRes = R.drawable.ic_review_write_24,
                    title = "방문 후기",
                    subtitle = "다녀온 장소의 경험을 나눠보세요",
                    onClick = onLogin,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
                GuestBenefitCard(
                    iconRes = R.drawable.ic_tab_person_24,
                    title = "친구 팔로우",
                    subtitle = "취향이 맞는 사람의 코스를 만나보세요",
                    onClick = onLogin,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        CourmyBottomBar(
            selectedTab = MainTab.MY,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.SAVED -> navigationHelper.navigateTo(SavedPage)
                    else -> Unit
                }
            },
        )
    }
}

@Composable
private fun GuestWelcomeCard(onLogin: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    // 세로 그라디언트(상단 진한 → 하단 bgAccent). Figma #234a30→#30623c 근사.
                    Brush.verticalGradient(listOf(color.bgAccentPressed, color.bgAccent)),
                ),
    ) {
        // 우하단 데코 원(카드 밖 넘침은 clip). Figma 민트 원색 대신 저투명도 흰색으로 근사.
        Box(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 36.dp, y = 44.dp)
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(color.contentOnAccent.copy(alpha = 0.08f)),
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(color.contentOnAccent.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_tab_person_24),
                        contentDescription = null,
                        tint = color.contentOnAccent,
                        modifier = Modifier.size(30.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DsText(
                        text = "게스트님, 반가워요",
                        style = DesignSystemThemeImpl.typeScale.titleExtraL,
                        color = color.contentOnAccent,
                    )
                    DsText(
                        text = "로그인하고 취향 코스를 받아보세요",
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentOnAccent,
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        .background(color.bgDefaultLevel1)
                        .clickable(onClick = onLogin)
                        .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "로그인 / 회원가입",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel0,
                )
            }
        }
    }
}

@Composable
private fun GuestBenefitCard(
    @DrawableRes iconRes: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .cardShadow(RoundedCornerShape(16.dp))
                .clickable(role = Role.Button, onClickLabel = "로그인 화면으로 이동", onClick = onClick)
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.bgAccentSubtle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = color.contentAccent,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.height(2.dp))
        DsText(
            text = title,
            maxLines = Int.MAX_VALUE,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        DsText(
            text = subtitle,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
            textAlign = TextAlign.Start,
            maxLines = Int.MAX_VALUE,
        )
    }
}
