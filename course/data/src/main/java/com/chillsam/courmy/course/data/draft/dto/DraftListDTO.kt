package com.chillsam.courmy.course.data.draft.dto

import com.chillsam.courmy.common.entity.time.parseIsoUtcMillis
import com.chillsam.courmy.course.entity.DraftSummaryVO
import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/courses/drafts` 응답 DTO.
 *
 * 공통 엔벨로프의 [data] 가 **객체가 아니라 배열**이다(다른 목록 API 처럼 `{ items, nextCursor }` 로
 * 감싸져 있지 않다). 객체로 파싱하면 역직렬화 단계에서 예외가 난다. 커서도 없으므로 페이징하지 않는다.
 */
@Serializable
data class DraftListEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: List<DraftSummaryDTO>? = null,
)

/**
 * 임시저장 1건.
 *
 * [id] 는 **숫자**다. 마이페이지 코스 목록은 같은 코스 id 를 문자열로 주지만 이 응답은 Long 이라
 * 타입을 맞춰 받는다(문자열로 선언하면 파싱이 깨진다).
 *
 * [createdAt] 은 ISO-8601 UTC 문자열이다. 서버가 갱신 시각을 따로 주지 않아 목록의 "저장 시각"으로 쓴다.
 */
@Serializable
data class DraftSummaryDTO(
    val id: Long? = null,
    val authorId: Long? = null,
    val title: String? = null,
    val coverImageUrl: String? = null,
    val theme: String? = null,
    val createdAt: String? = null,
)

/** 응답 → 목록 VO. id 가 없는 항목은 이어서 작성·삭제를 걸 수 없어 버린다. */
fun DraftListEnvelope.toVOList(): List<DraftSummaryVO> =
    data.orEmpty().mapNotNull { dto ->
        val id = dto.id ?: return@mapNotNull null
        DraftSummaryVO(
            id = id,
            // 임시저장은 제목 없이도 만들 수 있어 서버가 빈 문자열을 준다. 목록이 빈 줄로 보이지 않게 채운다.
            title = dto.title?.takeIf { it.isNotBlank() } ?: DEFAULT_TITLE,
            // 못 읽으면 0 이라 화면이 시각 표시만 포기한다(초안 자체는 보여 줘야 한다).
            savedAtMillis = parseIsoUtcMillis(dto.createdAt) ?: 0L,
        )
    }

/** 제목 없이 저장한 초안의 목록 표시용 기본 제목. */
private const val DEFAULT_TITLE = "제목 없는 코스"
