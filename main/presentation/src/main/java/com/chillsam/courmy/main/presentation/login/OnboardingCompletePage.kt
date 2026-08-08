package com.chillsam.courmy.main.presentation.login

import android.widget.Toast
import androidx.annotation.DrawableRes
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.StepProgressBar
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.entity.auth.SignupProfile

/** 온보딩 완료 화면(FS-08). 가입을 마쳤음을 알리고 "Courmy 시작하기"로 로그인 완료 후 홈으로 진입. */
@Composable
fun OnboardingCompletePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val color = DesignSystemThemeImpl.designSystemColor
    val viewModel: SignupViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // 사용자가 입력한 닉네임으로 인사(비어 있으면 기본 호칭).
    val displayName = SignupSelectionStore.nickname.ifBlank { "회원" }

    // 가입 성공 → 세션 로그인 + 홈으로. 실패 → 안내.
    LaunchedEffect(uiState.done) {
        if (uiState.done) {
            session.login()
            SignupSelectionStore.clear()
            navigationHelper.navigateReplace(HomePage)
            viewModel.onIntent(SignupIntent.ConsumeDone)
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(SignupIntent.ConsumeError)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel1)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            StepProgressBar(
                step = 4,
                modifier =
                    Modifier.padding(
                        start = ScreenHorizontalPadding,
                        end = ScreenHorizontalPadding,
                        top = 12.dp,
                    ),
            )
            Column(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = ScreenHorizontalPadding)) {
                // 위아래 여백을 1:3 으로 나눠 콘텐츠를 위쪽 1/4 지점에 둔다.
                // 위로 바짝 붙이면 아래만 뻥 뚫려 보이고, 가운데로 내리면 진행바와 너무 떨어진다.
                Spacer(Modifier.weight(CONTENT_TOP_WEIGHT))
                Box(
                    modifier =
                        Modifier
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
                    text = "가입 완료!\n${displayName}님, 환영합니다!",
                    style = DesignSystemThemeImpl.typeScale.titleExtraL,
                    color = color.contentDefaultLevel0,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 20.dp),
                )
                Spacer(Modifier.height(36.dp))
                DsText(
                    text = "이제 이런 걸 할 수 있어요",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                )
                Spacer(Modifier.height(12.dp))
                StartGuideRow(
                    iconRes = R.drawable.ic_add_24,
                    title = "나만의 코스 만들기",
                    description = "가 본 장소를 순서대로 엮어 코스로 남겨요.",
                )
                StartGuideRow(
                    iconRes = R.drawable.ic_bookmark_filled_24,
                    title = "마음에 든 코스 저장",
                    description = "저장한 코스는 저장 탭에서 다시 볼 수 있어요.",
                )
                StartGuideRow(
                    iconRes = R.drawable.ic_heart_24,
                    title = "취향 맞는 유저 팔로우",
                    description = "관심 있는 사람의 새 코스를 놓치지 않아요.",
                )
                Spacer(Modifier.weight(CONTENT_BOTTOM_WEIGHT))
            }
            Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
                DsButton(
                    text = "Courmy 시작하기",
                    enabled = !uiState.isLoading,
                    onClick = {
                        // 홀더에 담아둔 프로필로 회원가입 API 호출.
                        // 관심 지역은 검색 API 가 주는 법정동코드를 그대로 areaCodes 로 보낸다.
                        // TODO-API-SPEC: 관심 테마는 아직 라벨→tagId 매핑이 없어(태그 목록 API 필요)
                        //  likeTagIds 를 채우지 못한다. 기기에는 이름으로 남겨 화면 표시에만 쓴다. [wiki-needed]
                        // 이미지가 이미 http URL 이면 그대로 싣고, 로컬 uri 면 가입 성공 후 업로드하도록 넘긴다
                        // (presign 은 액세스 토큰이 필요해 가입 전에는 호출할 수 없다).
                        val picked = SignupSelectionStore.profileImageUrl
                        val remoteUrl = picked?.takeIf { it.startsWith("http") }
                        viewModel.onIntent(
                            SignupIntent.Submit(
                                profile =
                                    SignupProfile(
                                        nickname = SignupSelectionStore.nickname,
                                        handle = SignupSelectionStore.handle,
                                        profileImageUrl = remoteUrl,
                                        areaCodes = SignupSelectionStore.regionCodes,
                                    ),
                                localImageUri = picked?.takeIf { remoteUrl == null },
                                interestThemes = SignupSelectionStore.themes,
                                interestRegions = SignupSelectionStore.regions,
                            ),
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
                )
            }
        }
        if (uiState.isLoading) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(color.contentDefaultLevel0.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = color.contentAccent)
            }
        }
    }
}

/**
 * 가입 직후 "이제 이런 걸 할 수 있어요" 안내 한 줄.
 *
 * 아이콘은 각 기능을 실제로 쓸 때 만나는 것과 같은 것을 쓴다(만들기=홈 FAB 의 `+`,
 * 저장=북마크, 팔로우=하트). 처음 보는 화면에서 익힌 모양이 그대로 이어지도록.
 */
@Composable
private fun StartGuideRow(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.bgAccent.copy(alpha = GUIDE_ICON_BG_ALPHA)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = color.contentAccent,
                modifier = Modifier.size(22.dp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            DsText(
                text = title,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = description,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
    }
}

/** 안내 아이콘 배경. 강조색을 옅게 깔아 아이콘만 도드라지게 한다. */
private const val GUIDE_ICON_BG_ALPHA = 0.12f

/** 남는 세로 공간을 위:아래 = 1:3 으로 나눈다. */
private const val CONTENT_TOP_WEIGHT = 1f
private const val CONTENT_BOTTOM_WEIGHT = 3f
