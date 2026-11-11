package com.chillsam.courmy.debug

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.helper.LocalStatusBarState
import com.chillsam.courmy.common.presentation.helper.SessionUiState
import com.chillsam.courmy.common.presentation.helper.StatusBarState
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.main.domain.aichat.AiChatRepository
import com.chillsam.courmy.main.domain.aichat.SendAiChatMessageUseCase
import com.chillsam.courmy.main.entity.aichat.AiChatAnswer
import com.chillsam.courmy.main.entity.aichat.AiChatInputVO
import com.chillsam.courmy.main.entity.aichat.AiChatStep
import com.chillsam.courmy.main.entity.aichat.AiChatTurnVO
import com.chillsam.courmy.main.entity.aichat.QuickReplyVO
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.presentation.aichat.AiCourseChatPage
import com.chillsam.courmy.main.presentation.aichat.AiCourseChatViewModel
import kotlinx.coroutines.delay

/** 실제 화면과 ViewModel을 사용하며 서버 응답만 대체하는 debug 전용 데모. */
class AiChatPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val chat =
            ViewModelProvider(
                this,
                object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        AiCourseChatViewModel(applicationContext, SendAiChatMessageUseCase(PreviewRepository())) as T
                },
            )[AiCourseChatViewModel::class.java]
        val session = SessionUiState().apply { login() }
        val statusBar = StatusBarState()
        setContent {
            DesignSystemTheme {
                CompositionLocalProvider(
                    LocalSessionUiState provides session,
                    LocalStatusBarState provides statusBar,
                ) {
                    AiCourseChatPage(
                        onBack = { finish() },
                        onLogin = {},
                        onCourseClick = {
                            Toast.makeText(this, "예시 코스입니다.", Toast.LENGTH_SHORT).show()
                        },
                        viewModel = chat,
                    )
                }
            }
        }
    }
}

private class PreviewRepository : AiChatRepository {
    private var step = AiChatStep.REGION

    override suspend fun sendMessage(answer: AiChatAnswer): AiChatTurnVO {
        delay(600)
        if (answer.sessionId == null) step = AiChatStep.REGION
        step =
            when (step) {
                AiChatStep.REGION -> AiChatStep.THEME
                AiChatStep.THEME -> AiChatStep.PLACE
                AiChatStep.PLACE -> if (answer.value == "NO_PLACE") AiChatStep.RESULT else AiChatStep.PLACE_CONFIRM
                else -> AiChatStep.RESULT
            }
        val text =
            when (step) {
                AiChatStep.THEME -> "어떤 분위기의 코스를 찾으세요?"
                AiChatStep.PLACE -> "꼭 가고 싶은 장소가 있나요?"
                AiChatStep.PLACE_CONFIRM -> "어느 장소를 말씀하셨나요?"
                else -> "이런 코스는 어떠세요? (미리보기 예시 데이터)"
            }
        val replies =
            when (step) {
                AiChatStep.THEME -> {
                    listOf(
                        QuickReplyVO("데이트", "DATE"),
                        QuickReplyVO("힐링·산책", "HEALING"),
                        QuickReplyVO("맛집·먹방", "FOOD"),
                        QuickReplyVO("카페 투어", "CAFETOUR"),
                        QuickReplyVO("문화", "CULTURE"),
                        QuickReplyVO("자연", "NATURE"),
                        QuickReplyVO("야경", "NIGHTVIEW"),
                        QuickReplyVO("쇼핑", "SHOPPING"),
                        QuickReplyVO("역사", "TRADITION"),
                        QuickReplyVO("액티비티", "ACTIVITY"),
                        QuickReplyVO("가족 나들이", "FAMILY"),
                        QuickReplyVO("혼놀", "SOLO"),
                        QuickReplyVO("상관없어요", "ANY"),
                    )
                }

                AiChatStep.PLACE -> {
                    listOf(QuickReplyVO("없어요", "NO_PLACE"))
                }

                AiChatStep.PLACE_CONFIRM -> {
                    listOf(
                        QuickReplyVO("성수점 (예시)", "PLACE_1"),
                        QuickReplyVO("서울숲점 (예시)", "PLACE_2"),
                    )
                }

                else -> {
                    listOf(QuickReplyVO("더 추천받기", "MORE"), QuickReplyVO("처음부터", "RESTART"))
                }
            }
        return AiChatTurnVO(
            sessionId = "preview-session",
            step = step,
            messages = listOf(text),
            quickReplies = replies,
            input =
                AiChatInputVO(
                    step == AiChatStep.THEME || step == AiChatStep.PLACE,
                    "메시지를 입력하세요",
                ),
            courses =
                if (step == AiChatStep.RESULT) {
                    listOf(
                        HomeCourseVO(
                            id = "preview-1",
                            title = "성수 카페와 서울숲 산책",
                            categoryLabel = "카페 투어",
                            saveCountText = "24",
                        ),
                        HomeCourseVO(
                            id = "preview-2",
                            title = "골목 카페에서 보내는 오후",
                            categoryLabel = "데이트",
                            saveCountText = "18",
                        ),
                        HomeCourseVO(
                            id = "preview-3",
                            title = "천천히 발견하는 동네의 매력과 조용한 골목 산책, 작은 카페에서 여유롭게 쉬어가는 오후",
                            categoryLabel = "힐링",
                            likeCountText = "32",
                        ),
                    )
                } else {
                    emptyList()
                },
        )
    }
}
