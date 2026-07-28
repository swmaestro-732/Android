package com.chillsam.courmy.main.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.presentation.component.SignupProgressBar

/** 온보딩 완료 화면(FS-08). 추천 코스를 보여주고 "Courmy 시작하기"로 로그인 완료 후 홈으로 진입. */
@Composable
fun OnboardingCompletePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val color = DesignSystemThemeImpl.designSystemColor

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel1).statusBarsPadding()) {
        SignupProgressBar(step = 4, modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp))
        Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp)) {
            Box(
                modifier =
                    Modifier
                        .padding(top = 28.dp)
                        .size(56.dp)
                        .shadow(6.dp, CircleShape, spotColor = color.contentDefaultLevel0.copy(alpha = 0.3f))
                        .clip(CircleShape)
                        .background(color.bgAccent),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check_24),
                    contentDescription = null,
                    tint = color.contentOnAccent,
                    modifier = Modifier.size(30.dp),
                )
            }
            DsText(
                text = "준비 완료!\n지호님 추천 코스를 찾았어요",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                maxLines = 2,
                modifier = Modifier.padding(top = 20.dp),
            )
            DsText(
                text = "감성 카페·전시 취향과 성수동을 바탕으로 첫 코스를 골라뒀어요.",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
                maxLines = 2,
                modifier = Modifier.padding(top = 12.dp),
            )
            Spacer(Modifier.height(32.dp))
            RecommendedCourseCard(
                title = "비 오는 날 성수 카페 코스",
                meta = "4 스팟 · 3시간",
            )
        }
        Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
            DsButton(
                text = "Courmy 시작하기",
                onClick = {
                    session.login()
                    navigationHelper.navigateReplace(HomePage)
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun RecommendedCourseCard(
    title: String,
    meta: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color.bgAccent)
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.contentOnAccent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bookmark_filled_24),
                contentDescription = null,
                tint = color.contentOnAccent,
                modifier = Modifier.size(24.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DsText(
                text = title,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentOnAccent,
            )
            DsText(
                text = meta,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentOnAccent.copy(alpha = 0.75f),
            )
        }
    }
}
