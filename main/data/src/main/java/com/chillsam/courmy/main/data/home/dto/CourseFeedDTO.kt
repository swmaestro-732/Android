package com.chillsam.courmy.main.data.home.dto

import com.chillsam.courmy.main.data.profile.dto.formatCount
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import kotlinx.serialization.Serializable

/**
 * `GET /service/v1/courses`(공개 코스 피드) 응답 DTO. 봉투는 공통 계약 `{ code, message, data }`.
 *
 * 응답에는 `authorId`·`likesCnt`·`createdAt` 도 오지만 화면이 쓰지 않아 매핑하지 않는다
 * (작성자 표시에 필요한 이름·아바타가 응답에 없어 작성자 영역 자체를 렌더하지 않는다 —
 * [HomeCourseVO] 주석 참고).
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
)

@Serializable
data class CourseFeedItemDTO(
    val id: Long? = null,
    val title: String? = null,
    val coverImageUrl: String? = null,
    /** 서버 `CourseCategory` enum 이름. 미선택 코스(draft 유래)는 null 로 온다. */
    val theme: String? = null,
    val savesCnt: Int? = null,
)

fun CourseFeedDTO.toVOList(): List<HomeCourseVO> =
    courses
        .orEmpty()
        // id 가 없으면 상세로 이동할 수 없어 카드로 쓸 수 없다.
        .filter { it.id != null }
        .map { item ->
            HomeCourseVO(
                id = item.id.toString(),
                title = item.title.orEmpty(),
                coverImageUrl = item.coverImageUrl.orEmpty(),
                categoryLabel = item.theme.toCategoryLabel(),
                saveCountText = formatCount(item.savesCnt ?: 0),
            )
        }

/**
 * 서버 `CourseCategory` enum 이름을 화면 라벨로 옮긴다.
 * 모르는 값(서버에 카테고리가 추가된 경우)이나 null 은 빈 문자열 — 칩을 렌더하지 않는다.
 */
private fun String?.toCategoryLabel(): String =
    when (this) {
        "DATE" -> "데이트"
        "HEALING" -> "힐링"
        "FOOD" -> "맛집"
        "CAFETOUR" -> "카페투어"
        "CULTURE" -> "문화·전시"
        "NATURE" -> "자연"
        "NIGHTVIEW" -> "야경"
        "SHOPPING" -> "쇼핑"
        "TRADITION" -> "전통"
        "ACTIVITY" -> "액티비티"
        "FAMILY" -> "가족"
        "SOLO" -> "혼자"
        else -> ""
    }
