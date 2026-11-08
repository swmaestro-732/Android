package com.chillsam.courmy.main.data.aichat.dto

import com.chillsam.courmy.common.entity.category.toCourseTagLabel
import com.chillsam.courmy.common.entity.format.formatCount
import com.chillsam.courmy.main.entity.aichat.AiChatAnswer
import com.chillsam.courmy.main.entity.aichat.AiChatInputVO
import com.chillsam.courmy.main.entity.aichat.AiChatStep
import com.chillsam.courmy.main.entity.aichat.AiChatTurnVO
import com.chillsam.courmy.main.entity.aichat.QuickReplyVO
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import kotlinx.serialization.Serializable

/**
 * `POST /service/v1/ai-chat/messages` 요청·응답 DTO. 봉투는 공통 계약 `{ code, message, data }`.
 * 응답 코스는 홈 추천 코스와 같은 필드라 [HomeCourseVO] 로 매핑해 카드 UI 를 재사용한다.
 */
@Serializable
data class AiChatMessageRequest(
    val sessionId: String? = null,
    val type: String,
    val value: String? = null,
    val text: String? = null,
    val location: AiChatLocationDTO? = null,
)

@Serializable
data class AiChatLocationDTO(
    val latitude: Double,
    val longitude: Double,
)

@Serializable
data class AiChatEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: AiChatTurnDTO? = null,
)

@Serializable
data class AiChatTurnDTO(
    val sessionId: String? = null,
    val step: String? = null,
    val messages: List<AiChatBubbleDTO>? = null,
    val quickReplies: List<AiChatQuickReplyDTO>? = null,
    val input: AiChatInputDTO? = null,
    val courses: List<AiChatCourseDTO>? = null,
)

@Serializable
data class AiChatBubbleDTO(
    val text: String? = null,
)

@Serializable
data class AiChatQuickReplyDTO(
    val label: String? = null,
    val value: String? = null,
    val requiresLocation: Boolean = false,
)

@Serializable
data class AiChatInputDTO(
    val enabled: Boolean = true,
    val placeholder: String? = null,
)

@Serializable
data class AiChatCourseDTO(
    val id: Long? = null,
    val title: String? = null,
    val coverImageUrl: String? = null,
    val theme: String? = null,
    val themes: List<String>? = null,
    val area: String? = null,
    val placeCount: Int? = null,
    val walkingMinutes: Int? = null,
    val likesCnt: Int? = null,
    val savesCnt: Int? = null,
)

fun AiChatAnswer.toRequest(): AiChatMessageRequest =
    AiChatMessageRequest(
        sessionId = sessionId,
        type = type.name,
        value = value,
        text = text,
        location = location?.let { AiChatLocationDTO(it.latitude, it.longitude) },
    )

internal fun AiChatTurnDTO.toVO(): AiChatTurnVO =
    AiChatTurnVO(
        sessionId = sessionId.orEmpty(),
        step = stepOf(step),
        messages = messages.orEmpty().mapNotNull { it.text?.takeIf(String::isNotBlank) },
        quickReplies =
            quickReplies
                .orEmpty()
                .filter { it.label != null && it.value != null }
                .map { QuickReplyVO(it.label.orEmpty(), it.value.orEmpty(), it.requiresLocation) },
        input = input?.let { AiChatInputVO(it.enabled, it.placeholder.orEmpty()) } ?: AiChatInputVO(),
        courses =
            courses
                .orEmpty()
                .filter { it.id != null }
                .map { it.toCourseVO() },
    )

private fun AiChatCourseDTO.toCourseVO(): HomeCourseVO =
    HomeCourseVO(
        id = id.toString(),
        title = title.orEmpty(),
        coverImageUrl = coverImageUrl.orEmpty(),
        categoryLabel = theme.toCourseTagLabel(),
        saveCountText = formatCount(savesCnt ?: 0),
        saveCount = savesCnt?.coerceAtLeast(0),
        likeCountText = likesCnt?.let { formatCount(it.coerceAtLeast(0)) }.orEmpty(),
    )

/** 서버 step 문자열 → enum. 모르는 값은 UNKNOWN 으로 받아 앱이 깨지지 않게 한다. */
private fun stepOf(raw: String?): AiChatStep =
    when (raw) {
        "REGION" -> AiChatStep.REGION
        "THEME" -> AiChatStep.THEME
        "PLACE" -> AiChatStep.PLACE
        "PLACE_CONFIRM" -> AiChatStep.PLACE_CONFIRM
        "RESULT" -> AiChatStep.RESULT
        else -> AiChatStep.UNKNOWN
    }
