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
    /** 공통 카테고리 소문자 코드. */
    val theme: String? = null,
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
                tagLabel = tagLabelOf(course?.area, course?.theme),
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

/** "성수 · 데이트" 형태. 한쪽이 비면 남은 하나만, 둘 다 없으면 빈 문자열(칩을 렌더하지 않는다). */
private fun tagLabelOf(
    area: String?,
    theme: String?,
): String =
    listOfNotNull(
        area?.takeIf { it.isNotBlank() },
        theme.toCourseTagLabel().takeIf { it.isNotBlank() },
    ).joinToString(" · ")

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
