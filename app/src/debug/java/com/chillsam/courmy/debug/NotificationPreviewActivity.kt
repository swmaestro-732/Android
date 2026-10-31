package com.chillsam.courmy.debug

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.domain.navigation.NavSignal
import com.chillsam.courmy.common.domain.push.PushTarget
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalStatusBarState
import com.chillsam.courmy.common.presentation.helper.NavigationHelperImpl
import com.chillsam.courmy.common.presentation.helper.StatusBarState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.entity.notification.NotificationItemVO
import com.chillsam.courmy.main.presentation.my.MyContent
import com.chillsam.courmy.main.presentation.my.MyProfileUIState
import com.chillsam.courmy.main.presentation.notification.NotificationInboxContent
import com.chillsam.courmy.main.presentation.notification.NotificationInboxIntent
import com.chillsam.courmy.main.presentation.notification.NotificationInboxUIState
import com.chillsam.courmy.push.PushNotificationRenderer
import com.google.firebase.messaging.RemoteMessage
import kotlinx.collections.immutable.toImmutableList
import java.time.Instant

/** 예시 데이터로 실제 Compose와 시스템 알림 renderer를 확인한다. 릴리스 APK에는 포함되지 않는다. */
class NotificationPreviewActivity : ComponentActivity() {
    private var inbox by mutableStateOf(false)
    private var state by mutableStateOf(previewState())
    private val navigation = NavigationHelperImpl()
    private val permission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) postPush()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        inbox = intent.getBooleanExtra("inbox", false) || intent.hasExtra("notificationId")
        if (intent.getBooleanExtra("empty", false)) state = NotificationInboxUIState(unreadCount = 0)
        setContent {
            DesignSystemTheme {
                CompositionLocalProvider(
                    LocalNavigationHelper provides navigation,
                    LocalStatusBarState provides remember { StatusBarState() },
                ) {
                    LaunchedEffect(Unit) {
                        navigation.navigationFlow.collect { signal ->
                            inbox = signal !is NavSignal.Back
                        }
                    }
                    BackHandler(inbox) { inbox = false }
                    Column(Modifier.fillMaxSize().background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0)) {
                        Box(Modifier.weight(1f)) {
                            if (inbox) {
                                NotificationInboxContent(state, ::handleIntent, { inbox = false })
                            } else {
                                MyContent(
                                    profile = previewProfile(),
                                    uiState = MyProfileUIState(isLoading = false),
                                    onLoadMore = {},
                                    onRetryPlans = {},
                                    onSelectTab = {},
                                    unreadCount = state.unreadCount,
                                )
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            DsText("미리보기 · 예시 알림", style = DesignSystemThemeImpl.typeScale.textExtraXS)
                            TextButton(onClick = ::requestPush) {
                                DsText("푸시 미리보기", style = DesignSystemThemeImpl.typeScale.textStrongS)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        inbox = true
    }

    private fun handleIntent(intent: NotificationInboxIntent) {
        state =
            when (intent) {
                is NotificationInboxIntent.Open -> {
                    state.withRead(intent.item.id).let { next ->
                        next.copy(unreadCount = next.items.count { !it.isRead }.toLong())
                    }
                }

                NotificationInboxIntent.MarkAllRead -> {
                    state.copy(
                        items = state.items.map { it.copy(isRead = true) }.toImmutableList(),
                        unreadCount = 0,
                    )
                }

                else -> {
                    state
                }
            }
    }

    private fun requestPush() {
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            postPush()
        }
    }

    private fun postPush() {
        val data =
            mapOf(
                "type" to "COURSE_COMMENTED",
                "targetType" to "COURSE",
                "targetId" to "1",
                "notificationId" to "900101",
                "title" to "Courmy",
                "body" to "[미리보기] 지민님이 내 코스에 댓글을 남겼어요. 성수에서 보내는 느긋한 오후",
            )
        val message = RemoteMessage.Builder("preview").setData(data).build()
        PushNotificationRenderer(this).show(
            message,
            requireNotNull(PushTarget.parse(data)),
            Intent(this, NotificationPreviewActivity::class.java),
        )
    }
}

private fun previewProfile() =
    MyProfileVO(
        nickname = "나영",
        handle = "courmy_day",
        bio = "걷다가 발견한 좋은 곳들을 모아요.",
        myCourseCount = 0,
        followerCount = "128",
        followingCount = "64",
    )

private fun previewState(): NotificationInboxUIState {
    val now = Instant.now()
    val types =
        listOf(
            "COURSE_COMMENTED",
            "USER_FOLLOWED",
            "COURSE_LIKED",
            "COURSE_BOOKMARKED",
            "COURSE_REVIEWED",
            "COURSE_TRACE_COMPLETED",
        )
    val names = listOf("지민", "서연", "민수", "하늘", "유진", "도윤")
    val titles = listOf("성수에서 보내는 느긋한 오후", null, "주말엔 연남동 골목 산책", "성수에서 보내는 느긋한 오후", "한강 따라 노을 산책", "주말엔 연남동 골목 산책")
    val minutes = listOf(3L, 18L, 46L, 120L, 1440L, 2880L)
    val rows =
        types.mapIndexed { index, type ->
            val at = now.minusSeconds(minutes[index] * 60).toString()
            NotificationItemVO(
                id = 100L - index,
                type = type,
                targetType =
                    if (type ==
                        "USER_FOLLOWED"
                    ) {
                        "USER"
                    } else {
                        "COURSE"
                    },
                targetId = 1,
                actorCount = 1,
                lastActorId = index + 1L,
                actorNickname = names[index],
                actorProfileImageUrl = null,
                courseTitle = titles[index],
                isRead = index >= 3,
                createdAt = at,
                updatedAt = at,
            )
        }
    return NotificationInboxUIState(items = rows.toImmutableList(), unreadCount = 3)
}
