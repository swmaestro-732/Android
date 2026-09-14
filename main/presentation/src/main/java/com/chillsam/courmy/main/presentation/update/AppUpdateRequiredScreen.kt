package com.chillsam.courmy.main.presentation.update

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding

private val IconBadgeSize = 88.dp
private val IconSize = 40.dp

private const val DEFAULT_TITLE = "새 버전으로 업데이트해 주세요"
private const val DEFAULT_MESSAGE = "지금 쓰는 버전은 더 이상 지원되지 않아요.\n스토어에서 업데이트한 뒤 다시 이용해 주세요."

/**
 * 서버가 426 을 내렸을 때 앱 전체를 덮는 강제 업데이트 안내.
 *
 * 다이얼로그가 아니라 화면인 이유: 오프라인 차단은 스플래시 한 곳에서만 걸리지만 426 은 어느 화면에서
 * 쓰던 중에도 내려온다. 반투명 다이얼로그로 덮으면 뒤에 남은 화면이 계속 눌러 볼 수 있는 것처럼 보이는데,
 * 실제로는 모든 요청이 426 으로 떨어져 아무것도 되지 않는다.
 *
 * 닫는 길을 두지 않는다 — 스토어에서 앱을 갱신하는 것 말고 사용자가 할 수 있는 일이 없다.
 * 뒤로가기는 막는 대신 **앱을 종료**시킨다. 막아 두면 나갈 방법이 사라지고, 뒤 화면으로 보내면
 * 다시 못 쓰는 화면으로 돌아갈 뿐이라 둘 다 답이 아니다.
 *
 * [message] 는 서버가 내려준 문구다(있으면 그대로 쓴다). 정책·기한 안내는 서버 배포로 바꿀 수 있어야 한다.
 *
 * 상태바 색은 [com.chillsam.courmy.main.presentation.navigation.RootComposable] 이 맡는다 —
 * 덮인 화면도 계속 컴포즈되며 자기 `StatusBarColor` 를 쓰기 때문에, 여기서 선언하면 어느 쪽이
 * 이길지가 컴포지션 순서에 달리게 된다.
 */
@Composable
fun AppUpdateRequiredScreen(
    message: String?,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current

    BackHandler { (context as? Activity)?.finish() }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel1)
                // 아래 화면으로 터치가 새지 않게 여기서 삼킨다(불투명해도 히트 테스트는 통과한다).
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                    // 배경은 시스템바 뒤까지 칠하고 콘텐츠만 안쪽으로 들인다(앱 전체 규칙).
                ).systemBarsPadding()
                .padding(horizontal = ScreenHorizontalPadding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(IconBadgeSize)
                        .clip(CircleShape)
                        .background(color.bgAccentSubtle),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_system_update_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color.contentAccent),
                    modifier = Modifier.size(IconSize),
                )
            }

            Spacer(Modifier.height(12.dp))

            DsText(
                text = DEFAULT_TITLE,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                textAlign = TextAlign.Center,
                maxLines = Int.MAX_VALUE,
            )
            DsText(
                text = message ?: DEFAULT_MESSAGE,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel2,
                textAlign = TextAlign.Center,
                maxLines = Int.MAX_VALUE,
            )

            Spacer(Modifier.height(20.dp))

            DsButton(
                text = "업데이트하러 가기",
                onClick = { context.openStoreListing() },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/**
 * 이 앱의 스토어 상세로 보낸다.
 *
 * `market://` 을 먼저 쓰면 Play 스토어 앱이 바로 열린다. 스토어 앱이 없는 기기(에뮬레이터·일부 중국향
 * 단말)에서는 `ActivityNotFoundException` 이 나므로 웹 스토어로 떨어뜨린다.
 */
private fun Context.openStoreListing() {
    runCatching { startActivity(storeIntent("market://details?id=$packageName")) }
        .onFailure {
            startActivity(storeIntent("https://play.google.com/store/apps/details?id=$packageName"))
        }
}

private fun storeIntent(url: String): Intent =
    Intent(Intent.ACTION_VIEW, url.toUri())
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

@Preview(showBackground = true)
@Composable
private fun AppUpdateRequiredScreenPreview() {
    DesignSystemTheme {
        AppUpdateRequiredScreen(message = null)
    }
}
