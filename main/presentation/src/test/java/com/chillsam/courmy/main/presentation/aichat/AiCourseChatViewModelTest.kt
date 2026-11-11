package com.chillsam.courmy.main.presentation.aichat

import android.content.Context
import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.domain.error.HttpResponseStatus
import com.chillsam.courmy.common.domain.telemetry.ProductEvent
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.main.domain.aichat.AiChatRepository
import com.chillsam.courmy.main.domain.aichat.SendAiChatMessageUseCase
import com.chillsam.courmy.main.entity.aichat.AiChatAnswer
import com.chillsam.courmy.main.entity.aichat.AiChatAnswerType
import com.chillsam.courmy.main.entity.aichat.AiChatInputVO
import com.chillsam.courmy.main.entity.aichat.AiChatStep
import com.chillsam.courmy.main.entity.aichat.AiChatTurnVO
import com.chillsam.courmy.main.entity.aichat.QuickReplyVO
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.presentation.testResources
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class AiCourseChatViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = FakeRepository()
    private val telemetry = mockk<Telemetry>(relaxed = true)

    @Before
    fun setUp() = Dispatchers.setMain(dispatcher)

    @After
    fun tearDown() = Dispatchers.resetMain()

    private fun viewModel(language: String = "ko") =
        AiCourseChatViewModel(
            mockk<Context>(),
            SendAiChatMessageUseCase(repository),
            testResources(language),
            telemetry,
        ).also {
            it.onIntent(AiCourseChatIntent.Start)
        }

    @Test
    fun `chat events count start once and accepted messages without text content`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.Start)
            vm.onIntent(AiCourseChatIntent.SendText("   "))
            vm.onIntent(AiCourseChatIntent.SendText("private input"))
            advanceUntilIdle()
            verify(exactly = 1) {
                telemetry.logEvent(ProductEvent.AiChatOpen, mapOf("screen" to "/aichat", "source" to "chat_start"))
            }
            verify(exactly = 1) {
                telemetry.logEvent(
                    ProductEvent.AiChatMessageSent,
                    mapOf("screen" to "/aichat", "answer_type" to "text", "step" to "REGION"),
                )
            }
        }

    @Test
    fun `English and Japanese greetings and input hints use localized resources`() =
        runTest(dispatcher) {
            for (language in listOf("en", "ja")) {
                val vm = viewModel(language)
                val state = vm.uiState.value
                assertTrue(state.messages.isNotEmpty())
                assertTrue(state.messages.none { message -> message.text.any { it in '가'..'힣' } })
                assertTrue(state.inputPlaceholder.isNotBlank())
                assertTrue(state.inputPlaceholder.none { it in '가'..'힣' })
            }
        }

    @Test
    fun `현재 구 칩은 좌표 기반 내 근처 대신 지역 이름을 전송한다`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.RegionChipTap("종로구"))
            advanceUntilIdle()
            assertEquals(listOf(AiChatAnswer(null, AiChatAnswerType.TEXT, text = "종로구")), repository.answers)
        }

    @Test
    fun `최초 진입은 서버 요청 없이 지역 질문을 한 번만 표시하고 공백 입력은 무시한다`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.Start)
            vm.onIntent(AiCourseChatIntent.SendText("   "))
            advanceUntilIdle()

            assertEquals(1, vm.uiState.value.messages.size)
            assertEquals(AiChatStep.REGION, vm.uiState.value.step)
            assertTrue(vm.uiState.value.showRegionChips)
            assertTrue(
                vm.uiState.value.regionChips
                    .isEmpty(),
            )
            assertTrue(repository.answers.isEmpty())
        }

    @Test
    fun `지역 테마 장소 결과를 이어가며 서버 세션과 칩 값을 그대로 전송한다`() =
        runTest(dispatcher) {
            val theme = QuickReplyVO("카페 투어", "CAFETOUR")
            val noPlace = QuickReplyVO("없어요", "NO_PLACE")
            val course = HomeCourseVO(id = "42", title = "성수 카페 코스")
            repository.respond = { turn(AiChatStep.THEME, replies = listOf(theme)) }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("  성수  "))
            advanceUntilIdle()
            assertNull(repository.answers.single().sessionId)
            assertEquals("성수", repository.answers.single().text)
            assertEquals(listOf(theme), vm.uiState.value.quickReplies)
            assertFalse(vm.uiState.value.showRegionChips)

            repository.respond = { turn(AiChatStep.PLACE, replies = listOf(noPlace)) }
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(theme))
            advanceUntilIdle()
            assertEquals("session-1", repository.answers.last().sessionId)
            assertEquals(AiChatAnswerType.CHIP, repository.answers.last().type)
            assertEquals("CAFETOUR", repository.answers.last().value)

            repository.respond = {
                turn(AiChatStep.RESULT).copy(messages = listOf("추천을 찾았어요", "이 코스는 어때요?"), courses = listOf(course))
            }
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(noPlace))
            advanceUntilIdle()
            assertEquals(AiChatStep.RESULT, vm.uiState.value.step)
            assertEquals(
                listOf(course),
                vm.uiState.value.messages
                    .last()
                    .courses,
            )
            assertTrue(
                vm.uiState.value.messages
                    .dropLast(1)
                    .all { it.courses.isEmpty() },
            )
            assertFalse(vm.uiState.value.isAiTyping)
        }

    @Test
    fun `직접 입력이 금지된 장소 확인 단계도 후보 칩으로 계속 진행한다`() =
        runTest(dispatcher) {
            val candidate = QuickReplyVO("성수점", "PLACE_42")
            repository.respond = { turn(AiChatStep.PLACE_CONFIRM, enabled = false, replies = listOf(candidate)) }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수 카페"))
            advanceUntilIdle()
            vm.onIntent(AiCourseChatIntent.SendText("직접 입력 금지"))
            advanceUntilIdle()
            assertEquals(1, repository.answers.size)

            repository.respond = { turn(AiChatStep.RESULT) }
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(candidate))
            advanceUntilIdle()
            assertEquals(2, repository.answers.size)
            assertEquals("PLACE_42", repository.answers.last().value)
            assertEquals(AiChatStep.RESULT, vm.uiState.value.step)
        }

    @Test
    fun `직접 입력이 금지된 결과에서도 처음부터 칩은 세션을 폐기한다`() =
        runTest(dispatcher) {
            val restart = QuickReplyVO("처음부터", QuickReplyVO.VALUE_RESTART)
            repository.respond = { turn(AiChatStep.RESULT, enabled = false, replies = listOf(restart)) }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수 추천"))
            advanceUntilIdle()
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(restart))
            assertEquals(AiChatStep.REGION, vm.uiState.value.step)
            assertEquals(1, vm.uiState.value.messages.size)
            assertEquals(1, repository.answers.size)

            vm.onIntent(AiCourseChatIntent.SendText("홍대"))
            advanceUntilIdle()
            assertNull(repository.answers.last().sessionId)
        }

    @Test
    fun `응답 대기 중 텍스트와 칩 중복 입력을 무시한다`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<AiChatTurnVO>()
            repository.respond = { pending.await() }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수"))
            advanceUntilIdle()
            assertTrue(vm.uiState.value.isAiTyping)
            assertFalse(vm.uiState.value.inputEnabled)
            vm.onIntent(AiCourseChatIntent.SendText("홍대"))
            vm.onIntent(AiCourseChatIntent.RegionChipTap("강남"))
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(QuickReplyVO("테마", "DATE")))
            advanceUntilIdle()
            assertEquals(1, repository.answers.size)
            assertEquals(
                listOf("성수"),
                vm.uiState.value.messages
                    .filter { it.role == ChatRole.USER }
                    .map { it.text },
            )
            pending.complete(turn())
            advanceUntilIdle()
            assertFalse(vm.uiState.value.isAiTyping)
        }

    @Test
    fun `요청 직후 연속 입력도 첫 답변만 보낸다`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수"))
            vm.onIntent(AiCourseChatIntent.SendText("홍대"))
            advanceUntilIdle()
            assertEquals(listOf("성수"), repository.answers.map { it.text })
            assertEquals(
                listOf("성수"),
                vm.uiState.value.messages
                    .filter { it.role == ChatRole.USER }
                    .map { it.text },
            )
        }

    @Test
    fun `일반 오류 후 입력을 복구하고 같은 세션에서 재시도한다`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수"))
            advanceUntilIdle()
            repository.respond = { throw IOException("offline") }
            vm.onIntent(AiCourseChatIntent.SendText("카페"))
            advanceUntilIdle()
            assertFalse(vm.uiState.value.isAiTyping)
            assertTrue(vm.uiState.value.inputEnabled)
            assertTrue(
                vm.uiState.value.messages
                    .last()
                    .text
                    .contains("다시 시도"),
            )

            repository.respond = { turn(AiChatStep.RESULT) }
            vm.onIntent(AiCourseChatIntent.SendText("카페"))
            advanceUntilIdle()
            assertEquals("session-1", repository.answers.last().sessionId)
            assertEquals(AiChatStep.RESULT, vm.uiState.value.step)
        }

    @Test
    fun `404 세션 만료는 안내와 지역 질문으로 초기화하고 새 세션을 요청한다`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수"))
            advanceUntilIdle()
            repository.respond = { throw HttpResponseException(HttpResponseStatus.NotFound, 404, "/ai-chat/messages") }
            vm.onIntent(AiCourseChatIntent.SendText("카페"))
            advanceUntilIdle()
            assertEquals(AiChatStep.REGION, vm.uiState.value.step)
            assertEquals(2, vm.uiState.value.messages.size)
            assertTrue(
                vm.uiState.value.messages
                    .first()
                    .text
                    .contains("만료"),
            )
            assertTrue(vm.uiState.value.inputEnabled)
            assertFalse(vm.uiState.value.isAiTyping)

            repository.respond = { turn() }
            vm.onIntent(AiCourseChatIntent.SendText("홍대"))
            advanceUntilIdle()
            assertNull(repository.answers.last().sessionId)
        }

    @Test
    fun `응답 대기 중 처음부터는 이전 요청을 취소하고 이전 결과를 표시하지 않는다`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<AiChatTurnVO>()
            repository.respond = { pending.await() }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수"))
            advanceUntilIdle()
            vm.onIntent(AiCourseChatIntent.Restart)
            pending.complete(turn(AiChatStep.RESULT))
            advanceUntilIdle()
            assertEquals(AiChatStep.REGION, vm.uiState.value.step)
            assertEquals(1, vm.uiState.value.messages.size)
            assertFalse(vm.uiState.value.isAiTyping)
            assertTrue(vm.uiState.value.inputEnabled)
        }

    @Test
    fun `말풍선이 없는 결과 응답에서도 추천 코스를 표시한다`() =
        runTest(dispatcher) {
            val course = HomeCourseVO(id = "42", title = "성수 카페 코스")
            repository.respond = { turn(AiChatStep.RESULT).copy(messages = emptyList(), courses = listOf(course)) }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수 추천"))
            advanceUntilIdle()
            assertEquals(AiChatStep.RESULT, vm.uiState.value.step)
            assertEquals(
                listOf(course),
                vm.uiState.value.messages
                    .flatMap { it.courses },
            )
        }

    @Test
    fun `후보 선택 실패는 직접 입력 금지를 유지하고 같은 후보로 재시도할 수 있다`() =
        runTest(dispatcher) {
            val candidate = QuickReplyVO("성수점", "PLACE_42")
            repository.respond = { turn(AiChatStep.PLACE_CONFIRM, enabled = false, replies = listOf(candidate)) }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수 카페"))
            advanceUntilIdle()
            repository.respond = { throw IOException("offline") }
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(candidate))
            advanceUntilIdle()

            assertEquals(AiChatStep.PLACE_CONFIRM, vm.uiState.value.step)
            assertFalse(vm.uiState.value.isAiTyping)
            assertFalse(vm.uiState.value.inputEnabled)
            assertEquals(listOf(candidate), vm.uiState.value.quickReplies)
            vm.onIntent(AiCourseChatIntent.SendText("금지된 입력"))
            advanceUntilIdle()
            assertEquals(2, repository.answers.size)

            repository.respond = { turn(AiChatStep.RESULT) }
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(candidate))
            advanceUntilIdle()
            assertEquals(3, repository.answers.size)
            assertEquals(repository.answers[1], repository.answers[2])
            assertEquals(AiChatStep.RESULT, vm.uiState.value.step)
        }

    @Test
    fun `직접 입력이 금지된 결과에서 더 추천받기는 같은 세션에 코스를 추가한다`() =
        runTest(dispatcher) {
            val more = QuickReplyVO("더 추천받기", "MORE")
            val first = HomeCourseVO(id = "42", title = "첫 추천")
            val next = HomeCourseVO(id = "43", title = "다음 추천")
            repository.respond = {
                turn(AiChatStep.RESULT, enabled = false, replies = listOf(more)).copy(courses = listOf(first))
            }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수 추천"))
            advanceUntilIdle()
            repository.respond = {
                turn(AiChatStep.RESULT, enabled = false, replies = listOf(more)).copy(courses = listOf(next))
            }
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(more))
            advanceUntilIdle()

            assertEquals(2, repository.answers.size)
            assertEquals(AiChatAnswer("session-1", AiChatAnswerType.CHIP, value = "MORE"), repository.answers.last())
            assertEquals(
                listOf(first, next),
                vm.uiState.value.messages
                    .flatMap { it.courses },
            )
            assertFalse(vm.uiState.value.inputEnabled)
            assertFalse(vm.uiState.value.isAiTyping)
        }

    @Test
    fun `단계가 변경된 뒤 이전 칩과 지역 칩 이벤트는 무시한다`() =
        runTest(dispatcher) {
            val old = QuickReplyVO("카페 투어", "CAFETOUR")
            val current = QuickReplyVO("없어요", "NO_PLACE")
            repository.respond = { turn(AiChatStep.THEME, replies = listOf(old)) }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("성수"))
            advanceUntilIdle()
            repository.respond = { turn(AiChatStep.PLACE, replies = listOf(current)) }
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(old))
            advanceUntilIdle()
            val state = vm.uiState.value

            vm.onIntent(AiCourseChatIntent.QuickReplyTap(old))
            vm.onIntent(AiCourseChatIntent.RegionChipTap("종로구"))
            vm.onIntent(AiCourseChatIntent.QuickReplyTap(QuickReplyVO("처음부터", "RESTART")))
            advanceUntilIdle()
            assertEquals(2, repository.answers.size)
            assertEquals(state, vm.uiState.value)
        }

    @Test
    fun `빠른 응답도 900ms 동안 생각 중 표시를 유지한다`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("종로구"))
            runCurrent()
            assertTrue(vm.uiState.value.isAiTyping)
            assertEquals(AiChatStep.REGION, vm.uiState.value.step)
            advanceTimeBy(899)
            runCurrent()
            assertTrue(vm.uiState.value.isAiTyping)
            advanceTimeBy(1)
            runCurrent()
            assertFalse(vm.uiState.value.isAiTyping)
            assertEquals(AiChatStep.THEME, vm.uiState.value.step)
        }

    @Test
    fun `느린 응답에는 생각 중 최소 시간 이후 추가 지연이 없다`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<AiChatTurnVO>()
            repository.respond = { pending.await() }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("종로구"))
            advanceTimeBy(2000)
            pending.complete(turn())
            runCurrent()
            assertFalse(vm.uiState.value.isAiTyping)
        }

    @Test
    fun `생각 중 초기화하면 늦은 응답이 첫 화면을 덮지 않는다`() =
        runTest(dispatcher) {
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("종로구"))
            runCurrent()
            vm.onIntent(AiCourseChatIntent.Restart)
            advanceUntilIdle()
            assertEquals(AiChatStep.REGION, vm.uiState.value.step)
            assertEquals(1, vm.uiState.value.messages.size)
            assertFalse(vm.uiState.value.isAiTyping)
        }

    @Test
    fun `오류는 생각 중 최소 시간을 기다리지 않고 안내한다`() =
        runTest(dispatcher) {
            repository.respond = { throw IOException("offline") }
            val vm = viewModel()
            vm.onIntent(AiCourseChatIntent.SendText("종로구"))
            runCurrent()
            assertFalse(vm.uiState.value.isAiTyping)
            assertTrue(vm.uiState.value.inputEnabled)
            assertTrue(
                vm.uiState.value.messages
                    .last()
                    .text
                    .contains("다시 시도"),
            )
            assertEquals(0L, testScheduler.currentTime)
        }

    private class FakeRepository : AiChatRepository {
        val answers = mutableListOf<AiChatAnswer>()
        var respond: suspend (AiChatAnswer) -> AiChatTurnVO = { turn() }

        override suspend fun sendMessage(answer: AiChatAnswer): AiChatTurnVO {
            answers += answer
            return respond(answer)
        }
    }

    private companion object {
        fun turn(
            step: AiChatStep = AiChatStep.THEME,
            enabled: Boolean = true,
            replies: List<QuickReplyVO> = emptyList(),
        ) = AiChatTurnVO("session-1", step, listOf("다음 질문"), replies, AiChatInputVO(enabled, "입력해 주세요"), emptyList())
    }
}
