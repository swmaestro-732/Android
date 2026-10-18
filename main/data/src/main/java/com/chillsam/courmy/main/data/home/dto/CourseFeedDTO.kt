package com.chillsam.courmy.main.data.home.dto

import com.chillsam.courmy.common.entity.category.toCourseTagLabel
import com.chillsam.courmy.common.entity.format.formatCount
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import kotlinx.serialization.Serializable

/**
 * `GET /service/v2/home/recommended-courses`(추천 코스 전체보기) 응답 DTO.
 * 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 응답에는 `area`·`placeCount`·`walkingMinutes` 도 오지만 화면이 쓰지 않아 매핑하지 않는다
 * (카드 메타를 "N 스팟 · 시간" 으로 바꿀 때 함께 싣는다 — [HomeCourseVO] 주석 참고).
 */
@Serializable
data class CourseFeedEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseFeedDTO? = null,
)

@Serializable
data class CourseFeedDTO(
    val courses: List<CourseFeedItemDTO>? = null,
    /** 다음 페이지 커서(base64 키셋). 마지막 페이지면 null 이다. */
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
)

@Serializable
data class CourseFeedItemDTO(
    val id: Long? = null,
    val title: String? = null,
    val coverImageUrl: String? = null,
    /** 서버 `CourseCategory` enum 이름. 미선택 코스(draft 유래)는 null 로 온다. */
    val theme: String? = null,
    val savesCnt: Int? = null,
    val likesCnt: Int? = null,
)

fun CourseFeedDTO.toPageVO(): CursorPageVO<HomeCourseVO> =
    CursorPageVO(
        items = toVOList(),
        nextCursor = nextCursor?.takeIf { it.isNotBlank() },
        // 커서가 없으면 더 받을 수 없으므로, 서버가 hasNext=true 로 줘도 끝으로 본다.
        hasNext = hasNext && !nextCursor.isNullOrBlank(),
    )

/** 카드 목록만 변환한다. 페이지 단위 변환은 [toPageVO] 를 쓴다(모듈 내부 + 매핑 테스트용). */
internal fun CourseFeedDTO.toVOList(): List<HomeCourseVO> =
    courses
        .orEmpty()
        // id 가 없으면 상세로 이동할 수 없어 카드로 쓸 수 없다.
        .filter { it.id != null }
        .map { item ->
            HomeCourseVO(
                id = item.id.toString(),
                title = item.title.orEmpty(),
                coverImageUrl = item.coverImageUrl.orEmpty(),
                categoryLabel = item.theme.toCourseTagLabel(),
                saveCountText = formatCount(item.savesCnt ?: 0),
                likeCountText = item.likesCnt?.let { formatCount(it.coerceAtLeast(0)) }.orEmpty(),
            )
        }
