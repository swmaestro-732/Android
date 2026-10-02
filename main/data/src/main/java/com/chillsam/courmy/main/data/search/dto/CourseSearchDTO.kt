package com.chillsam.courmy.main.data.search.dto

import com.chillsam.courmy.common.entity.category.toCourseTagLabel
import com.chillsam.courmy.main.data.profile.dto.formatCount
import com.chillsam.courmy.main.entity.search.SearchCourseVO
import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/courses/search` 응답 DTO. 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 응답에 스팟 수·소요 시간·작성자 이름이 없어 [SearchCourseVO] 의 해당 필드는 기본값으로 둔다
 * — 카드 컴포넌트가 0·빈 값을 렌더하지 않으므로 그 줄만 빠진 채 그려진다. [wiki-needed]
 */
@Serializable
data class CourseSearchEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseSearchDTO? = null,
)

@Serializable
data class CourseSearchDTO(
    val courses: List<CourseSearchItemDTO>? = null,
    /** 다음 페이지 커서. 마지막 페이지면 null 이다(화면이 첫 페이지만 써서 아직 매핑하지 않는다). */
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
)

@Serializable
data class CourseSearchItemDTO(
    val id: Long? = null,
    val authorId: Long? = null,
    val title: String? = null,
    val coverImageUrl: String? = null,
    /** 서버 `CourseCategory` enum 이름. 미선택 코스는 null 로 온다. */
    val theme: String? = null,
    /** 지역 표시 문자열(예: "성수"). */
    val area: String? = null,
    val likesCnt: Int? = null,
    val savesCnt: Int? = null,
    val createdAt: String? = null,
)

internal fun CourseSearchDTO.toVOList(): List<SearchCourseVO> =
    courses
        .orEmpty()
        // id 가 없으면 상세로 이동할 수 없어 카드로 쓸 수 없다.
        .filter { it.id != null }
        .map { item ->
            SearchCourseVO(
                id = item.id.toString(),
                title = item.title.orEmpty(),
                coverImageUrl = item.coverImageUrl.orEmpty(),
                // 배지는 "지역 · 테마" 꼴(FS-18). 한쪽이 비면 그 항목만 뺀다.
                categoryLabel =
                    listOf(item.area.orEmpty(), item.theme.toCourseTagLabel())
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                // "따라감"은 저장 수로 집계한다 — 검색 응답에서 사용자 반응 수치는 저장이 가장 가깝다.
                followCountText = "${formatCount(item.savesCnt ?: 0)}명",
            )
        }
