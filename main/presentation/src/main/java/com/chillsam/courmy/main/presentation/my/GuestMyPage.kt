package com.chillsam.courmy.main.presentation.my

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab

/**
 * 비로그인 마이 화면(FS-15-Guest). 게스트 환영 카드(로그인 유도)와
 * "가입하면 할 수 있어요" 안내 카드로 구성한다.
 */
@Composable
fun GuestMyPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
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
                text = "마이",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                modifier = Modifier.padding(vertical = 20.dp),
            )

            GuestWelcomeCard(
                onLogin = {
                    session.login()
                    navigationHelper.navigateTo(MyPage)
                },
            )

            DsText(
                text = "가입하면 할 수 있어요",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GuestBenefitCard(
                    iconRes = R.drawable.ic_tab_bookmark_24,
                    title = "코스 저장",
                    subtitle = "마음에 드는 코스 저장",
                    modifier = Modifier.weight(1f),
                )
                GuestBenefitCard(
                    iconRes = R.drawable.ic_notes_24,
                    title = "코스 만들기",
                    subtitle = "나만의 동선 설계",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        CourmyBottomBar(
            selectedTab = MainTab.MY,
            onTabSelected = { tab ->
                if (tab == MainTab.HOME) navigationHelper.navigateTo(HomePage)
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
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1)
                .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(16.dp))
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
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        DsText(
            text = subtitle,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
            textAlign = TextAlign.Start,
        )
    }
}
