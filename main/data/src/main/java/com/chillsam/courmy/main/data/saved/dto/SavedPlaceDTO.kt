package com.chillsam.courmy.main.data.saved.dto

import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.saved.SavedPlaceVO
import kotlinx.serialization.Serializable

/**
 * `GET /service/v1/my/saved-places`(저장함 · 장소 탭) 응답 DTO.
 * 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 응답의 집계(`totalCount`·`unvisitedCount`·`visitedCount`·`categoryCounts`)와 좌표·도보 시간은
 * 화면에 해당 UI(필터 칩·지도)가 생기면 함께 받는다(`ignoreUnknownKeys = true`). [wiki-needed]
 */
@Serializable
data class SavedPlaceEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: SavedPlaceScreenDTO? = null,
)

@Serializable
data class SavedPlaceScreenDTO(
    val savedPlaces: List<SavedPlaceItemDTO>? = null,
    /** 다음 페이지 커서. 마지막 페이지면 null. */
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
)

@Serializable
data class SavedPlaceItemDTO(
    /** 저장 레코드 id. 저장 취소·방문 처리는 [placeId] 를 쓴다. */
    val id: Long? = null,
    val placeId: Long? = null,
    /** 서버 장소 카테고리 코드(예: "cafe"). */
    val category: String? = null,
    val visited: Boolean = false,
    val place: SavedPlaceSummaryDTO? = null,
)

@Serializable
data class SavedPlaceSummaryDTO(
    val name: String? = null,
    val category: String? = null,
    /** 지역(예: "성수"). 값이 없으면 null 이라 라벨에서 생략한다. */
    val area: String? = null,
    val imageUrl: String? = null,
)

fun SavedPlaceScreenDTO.toPageVO(): CursorPageVO<SavedPlaceVO> =
    CursorPageVO(
        items = toVOList(),
        nextCursor = nextCursor?.takeIf { it.isNotBlank() },
        // 커서가 없으면 더 받을 수 없으므로, 서버가 hasNext=true 로 줘도 끝으로 본다(코스 탭과 동일).
        hasNext = hasNext && !nextCursor.isNullOrBlank(),
    )

internal fun SavedPlaceScreenDTO.toVOList(): List<SavedPlaceVO> =
    savedPlaces
        .orEmpty()
        // placeId 가 없으면 저장을 취소할 수도, 방문 처리할 수도 없어 카드로 쓸 수 없다.
        .filter { it.placeId != null }
        .map { item ->
            val place = item.place
            SavedPlaceVO(
                placeId = requireNotNull(item.placeId),
                name = place?.name.orEmpty(),
                // 레코드의 category 가 비면 장소 요약의 category 로 대신한다(같은 enum).
                categoryLabel = (item.category ?: place?.category).toPlaceCategoryLabel(),
                area = place?.area.orEmpty(),
                imageUrl = place?.imageUrl.orEmpty(),
                visited = item.visited,
            )
        }
