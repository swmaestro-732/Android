package com.chillsam.courmy.main.presentation.settings

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsSwitch
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.ProfileEditPage
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.component.BackTopBar
import com.chillsam.courmy.main.presentation.my.MyViewModel
import com.chillsam.courmy.main.presentation.profile.ProfileAvatar

/** 설정 화면(FS-28). 프로필·알림·계정을 iOS식 그룹 카드로 구성한다. */
@Composable
fun SettingsPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    val color = DesignSystemThemeImpl.designSystemColor
    val accountViewModel: AccountViewModel = hiltViewModel()
    val accountState by accountViewModel.uiState.collectAsStateWithLifecycle()
    val myViewModel: MyViewModel = hiltViewModel()
    val myState by myViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pushOn by remember { mutableStateOf(true) }
    var recommendOn by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    // 로그아웃/탈퇴 완료 → 세션 해제 후 게스트 홈으로.
    LaunchedEffect(accountState.result) {
        if (accountState.result != null) {
            session.logout()
            navigationHelper.navigateReplace(HomePage)
            accountViewModel.onIntent(AccountIntent.ConsumeResult)
        }
    }
    LaunchedEffect(accountState.errorMessage) {
        accountState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            accountViewModel.onIntent(AccountIntent.ConsumeError)
        }
    }

    Box(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(modifier = Modifier.fillMaxSize()) {
            BackTopBar(title = "설정", onBack = { navigationHelper.navigateToBack() }, boxed = true)

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(8.dp))
                SettingsCard {
                    ProfileRow(
                        profile = myState.profile,
                        onEdit = { navigationHelper.navigateTo(ProfileEditPage) },
                    )
                }

                SectionLabel("알림")
                SettingsCard {
                    ToggleRow(label = "푸시 알림", checked = pushOn, onCheckedChange = { pushOn = it })
                    CardDivider()
                    ToggleRow(label = "코스 추천 알림", checked = recommendOn, onCheckedChange = { recommendOn = it })
                }

                SectionLabel("계정")
                SettingsCard {
                    MenuRow(label = "개인정보 보호", onClick = {})
                    CardDivider()
                    MenuRow(label = "공지·도움말", onClick = {})
                    CardDivider()
                    DangerRow(label = "로그아웃", onClick = { accountViewModel.onIntent(AccountIntent.Logout) })
                    CardDivider()
                    DangerRow(label = "회원 탈퇴", onClick = { showWithdrawDialog = true })
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        if (accountState.isLoading) {
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

    if (showWithdrawDialog) {
        WithdrawConfirmDialog(
            onConfirm = {
                showWithdrawDialog = false
                accountViewModel.onIntent(AccountIntent.Withdraw)
            },
            onDismiss = { showWithdrawDialog = false },
        )
    }
}

/** 흰색 라운드 + 테두리 그룹 카드. 내부 행 사이는 [CardDivider] 로 구분. */
@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1)
                .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(16.dp)),
    ) {
        content()
    }
}

@Composable
private fun CardDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = DesignSystemThemeImpl.designSystemColor.borderDefaultLevel0,
    )
}

@Composable
private fun ProfileRow(
    profile: MyProfileVO?,
    onEdit: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ProfileAvatar(imageUrl = profile?.profileImageUrl.orEmpty(), size = 54.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            DsText(
                text = profile?.nickname.orEmpty(),
                style = DesignSystemThemeImpl.typeScale.textRegularM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "@${profile?.handle.orEmpty()}",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .border(1.dp, color.borderDefaultLevel1, RoundedCornerShape(9999.dp))
                    .clickable(onClick = onEdit)
                    .padding(horizontal = 14.dp, vertical = 7.dp),
        ) {
            DsText(
                text = "편집",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    DsText(
        text = text,
        style = DesignSystemThemeImpl.typeScale.textRegularXS,
        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel2,
        modifier = Modifier.padding(start = 6.dp, top = 20.dp, bottom = 8.dp),
    )
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        DsSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun MenuRow(
    label: String,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right_24),
            contentDescription = null,
            tint = color.contentDefaultLevel3,
            modifier = Modifier.size(22.dp),
        )
    }
}

/** 파괴적 액션 행(로그아웃·회원 탈퇴). danger 색 텍스트. */
@Composable
private fun DangerRow(
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = label,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDanger,
        )
    }
}

/** 회원 탈퇴 확인 다이얼로그(스크림 + 중앙 카드 + danger 확인/취소). */
@Composable
private fun WithdrawConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.bgDefaultLevel1)
                    .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsText(
                text = "정말 탈퇴할까요?",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                // 수동 줄바꿈을 없애고 줄 수 제한을 풀어(큰 글꼴 배율에서도) 잘리지 않고 자연스럽게 줄바꿈되게 한다.
                text = "탈퇴하면 계정과 코스 정보가 삭제되고 되돌릴 수 없어요.",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
                textAlign = TextAlign.Center,
                maxLines = Int.MAX_VALUE,
            )
            Spacer(Modifier.height(12.dp))
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color.contentDanger)
                        .clickable(onClick = onConfirm),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "탈퇴하기",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentOnAccent,
                )
            }
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "취소",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel1,
                )
            }
        }
    }
}
