package com.chillsam.courmy.main.data.saved.dto

import com.chillsam.courmy.common.entity.category.toCourseTagLabel
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.saved.SavedCourseFolderVO
import com.chillsam.courmy.main.entity.saved.SavedCourseVO
import com.chillsam.courmy.main.entity.saved.SavedCoursesVO
import kotlinx.serialization.Serializable

/**
 * `GET /service/v1/my/saved-courses`(저장함 · 코스 탭) 응답 DTO.
 * 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 폴더·완주 필터의 개수와 커서 페이징 정보를 함께 변환한다.
 */
@Serializable
data class SavedCourseEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: SavedCourseScreenDTO? = null,
)

@Serializable
data class SavedCourseScreenDTO(
    val totalCount: Int? = null,
    val uncompletedCount: Int? = null,
    val completedCount: Int? = null,
    val folders: List<SavedCourseFolderDTO>? = null,
    val savedCourses: List<SavedCourseItemDTO>? = null,
    /** 다음 페이지 커서. 마지막 페이지면 null. */
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
)

@Serializable
data class SavedCourseItemDTO(
    /** 저장 레코드 id. 화면 이동·저장 취소는 [courseId] 를 쓴다. */
    val id: Long? = null,
    val courseId: Long? = null,
    val completed: Boolean = false,
    val completedAt: String? = null,
    val isMine: Boolean = false,
    val course: SavedCourseSummaryDTO? = null,
)

@Serializable
data class SavedCourseSummaryDTO(
    val title: String? = null,
    val coverImageUrl: String? = null,
    /** 지역(예: "성수"). 코스에 값이 없으면 null 이라 칩에서 생략한다. */
    val area: String? = null,
    /** 공통 카테고리 코드(단일). [themes] 의 첫 값 — 구버전 앱 호환용. 없으면 null. */
    val theme: String? = null,
    /** 코스 테마 목록(카테고리 enum 이름, 선언 순서). 코스는 테마가 여러 개일 수 있다. 없으면 빈 배열/null. */
    val themes: List<String>? = null,
    val placeCount: Int? = null,
    val durationText: String? = null,
    val author: SavedCourseAuthorDTO? = null,
)

@Serializable
data class SavedCourseAuthorDTO(
    val handle: String? = null,
    val nickname: String? = null,
)

fun SavedCourseScreenDTO.toPageVO(): CursorPageVO<SavedCourseVO> =
    CursorPageVO(
        items = toVOList(),
        nextCursor = nextCursor?.takeIf { it.isNotBlank() },
        // 커서가 없으면 더 받을 수 없으므로, 서버가 hasNext=true 로 줘도 끝으로 본다.
        hasNext = hasNext && !nextCursor.isNullOrBlank(),
    )

internal fun SavedCourseScreenDTO.toVOList(): List<SavedCourseVO> =
    savedCourses
        .orEmpty()
        // courseId 가 없으면 상세로 갈 수도, 저장을 취소할 수도 없어 카드로 쓸 수 없다.
        .filter { it.courseId != null }
        .map { item ->
            val course = item.course
            SavedCourseVO(
                id = item.courseId.toString(),
                tagLabel = tagLabelOf(course?.area, course?.theme, course?.themes),
                title = course?.title.orEmpty(),
                placeLabel = "장소 ${course?.placeCount ?: 0}곳",
                // 핸들이 없는 작성자는 닉네임으로 대신 표기한다(카드에 빈 줄을 남기지 않게).
                authorHandle = course?.author?.handle ?: course?.author?.nickname.orEmpty(),
                thumbnailUrl = course?.coverImageUrl.orEmpty(),
                durationText = course?.durationText.orEmpty(),
                isMine = item.isMine,
                completedAt =
                    item.completedAt
                        ?.takeIf { item.completed }
                        ?.substringBefore("T")
                        .orEmpty(),
            )
        }

/**
 * "성수 · 데이트 · 카페 투어" 형태 — 지역 뒤에 코스 테마 라벨을 모두 이어 붙인다(코스 상세와 같은 톤).
 *
 * 테마는 복수 [themes] 를 우선 쓰고, 없으면 구버전 단일 [theme] 로 폴백한다. 서버가 주는 값은
 * 카테고리 코드(enum 이름)라 라벨로 바꾸며, 알아보지 못한 코드는 칩을 지저분하게 하지 않게 건너뛴다.
 * 지역·테마가 전부 비면 빈 문자열(칩을 렌더하지 않는다).
 */
private fun tagLabelOf(
    area: String?,
    theme: String?,
    themes: List<String>?,
): String {
    val themeCodes = themes?.takeIf { it.isNotEmpty() } ?: listOfNotNull(theme)
    val themeLabels = themeCodes.mapNotNull { it.toCourseTagLabel().takeIf { label -> label.isNotBlank() } }
    return (listOfNotNull(area?.takeIf { it.isNotBlank() }) + themeLabels).joinToString(" · ")
}

@Serializable
data class SavedCourseFolderDTO(
    val id: Long? = null,
    val name: String? = null,
    val count: Int? = null,
)

fun SavedCourseScreenDTO.toScreenVO() =
    SavedCoursesVO(
        page = toPageVO(),
        totalCount = totalCount ?: 0,
        uncompletedCount = uncompletedCount ?: 0,
        completedCount = completedCount ?: 0,
        folders =
            folders.orEmpty().mapNotNull { folder ->
                folder.id?.let { SavedCourseFolderVO(it, folder.name.orEmpty(), folder.count ?: 0) }
            },
    )
