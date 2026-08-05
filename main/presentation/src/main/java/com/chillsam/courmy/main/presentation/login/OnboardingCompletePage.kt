package com.chillsam.courmy.main.presentation.login

import android.widget.Toast
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
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.entity.auth.SignupProfile
import com.chillsam.courmy.main.presentation.component.SignupProgressBar

/** 온보딩 완료 화면(FS-08). 추천 코스를 보여주고 "Courmy 시작하기"로 로그인 완료 후 홈으로 진입. */
@Composable
fun OnboardingCompletePage(
    modifier: Modifier = Modifier,
    themes: List<String> = emptyList(),
    regions: List<String> = emptyList(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val color = DesignSystemThemeImpl.designSystemColor
    val viewModel: SignupViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // 앞서 고른 관심 테마·지역을 반영한 안내 문구(각각 최대 3개, 초과 시 "등").
    val subtitle = buildRecommendationSubtitle(themes = themes, regions = regions)
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
                    text = "준비 완료!\n${displayName}님 추천 코스를 찾았어요",
                    style = DesignSystemThemeImpl.typeScale.titleExtraL,
                    color = color.contentDefaultLevel0,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 20.dp),
                )
                DsText(
                    text = subtitle,
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Spacer(Modifier.height(32.dp))
                RecommendedCourseCard(
                    title = "비 오는 날 성수 카페 코스",
                    meta = "4 스팟",
                )
            }
            Box(modifier = Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
                DsButton(
                    text = "Courmy 시작하기",
                    enabled = !uiState.isLoading,
                    onClick = {
                        // 홀더에 담아둔 프로필로 회원가입 API 호출. 관심 태그·지역은 라벨→id 매핑이 아직
                        // 없어(백엔드 목록 API 필요) 이번엔 전송하지 않는다.
                        // 이미 http URL 이면 그대로 싣고, 로컬 uri 면 가입 성공 후 업로드하도록 넘긴다
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
                                    ),
                                localImageUri = picked?.takeIf { remoteUrl == null },
                            ),
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
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

/** 안내 문구에 한 번에 나열할 최대 항목 수. 초과분은 "등"으로 줄인다. */
private const val MAX_SUMMARY_ITEMS = 3

/**
 * 관심 테마·지역을 반영한 완료 안내 문구를 만든다.
 * 예) "감성 카페·전시 취향과 성수동을 바탕으로 첫 코스를 골라뒀어요."
 * 항목이 [MAX_SUMMARY_ITEMS]개를 넘으면 앞의 3개만 "·"로 잇고 "등"을 붙인다.
 */
private fun buildRecommendationSubtitle(
    themes: List<String>,
    regions: List<String>,
): String {
    if (themes.isEmpty() || regions.isEmpty()) {
        return "취향에 맞는 첫 코스를 골라뒀어요."
    }
    val themeText = summarize(themes)
    val regionText = summarize(regions)
    return "$themeText 취향과 $regionText${objectJosa(regionText)} 바탕으로 첫 코스를 골라뒀어요."
}

/** 최대 [MAX_SUMMARY_ITEMS]개까지 "·"로 잇고, 더 많으면 뒤에 "등"을 붙인다. */
private fun summarize(items: List<String>): String {
    val head = items.take(MAX_SUMMARY_ITEMS).joinToString("·")
    return if (items.size > MAX_SUMMARY_ITEMS) "$head 등" else head
}

/** 목적격 조사(을/를)를 마지막 글자의 받침 유무로 고른다. 한글이 아니면 "를". */
private fun objectJosa(word: String): String {
    val last = word.lastOrNull() ?: return "를"
    val hasFinalConsonant = last.code in 0xAC00..0xD7A3 && (last.code - 0xAC00) % 28 != 0
    return if (hasFinalConsonant) "을" else "를"
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
