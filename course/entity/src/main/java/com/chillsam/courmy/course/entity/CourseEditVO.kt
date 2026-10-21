package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 코스 편집으로 서버에 보낼 값.
 *
 * 편집할 수 있는 건 코스 정보(제목·설명)·태그·장소별 한마디뿐이다. 이미지와 장소 구성(추가·삭제·순서)은
 * 바꿀 수 없어 [places] 의 좌표·사진·순번은 불러온 값을 그대로 되돌려 보낸다
 * (서버가 places 를 통째로 치환하므로 빼면 사진이 지워진다).
 *
 * [thumbnailUrl]·[visibility]·[published] 는 화면에서 바꾸지 않지만 서버가 본문에 요구해서(빠지면 400
 * "요청 본문 형식이 올바르지 않습니다") 담는다.
 *
 * 이 중 [visibility] 만 **현재 값을 알 수 없다** — 코스 상세 응답에 `visibility` 가 없다. 몰래 덮어쓰지
 * 않도록 편집 화면에 공개 설정을 노출해 사용자가 직접 고르게 한다. 서버가 상세 응답에 필드를 넣어 주면
 * 그 값을 초기값으로 채우고 안내 문구를 지운다. [wiki-needed]
 */
@Serializable
data class CourseEditVO(
    val title: String,
    val description: String,
    val tags: List<String> = emptyList(),
    val places: List<CourseEditPlaceVO> = emptyList(),
    /** 커버 이미지. 편집 대상이 아니라 상세에서 읽은 값을 그대로 되돌려 보낸다. */
    val thumbnailUrl: String = "",
    val visibility: CourseVisibility = CourseVisibility.PUBLIC,
    /** 이미 발행된 코스를 편집하는 화면이라 항상 true. 임시저장 편집이 생기면 그때 갈린다. */
    val published: Boolean = true,
)

/**
 * 편집 대상 장소 1건.
 *
 * - [placeId]   서버 place id. 어떤 장소인지 식별한다
 * - [orderNo]   서버 기준 순번(0부터). 불러온 값을 그대로 돌려보내 순서를 유지한다
 * - [name]      장소명. 표시 전용이며 서버로 보내지 않는다
 * - [tip]       작성자 한마디. **편집 가능한 유일한 장소 값**이다
 * - [imageUrls] 장소 사진. 편집 불가이며 지워지지 않게 그대로 되돌려 보낸다
 * - [walkingMinutes] 다음 장소까지의 도보 분. 편집 불가이며, 빼고 보내면 서버가 places 를 통째로
 *                    치환하면서 기존 도보 시간이 null 로 지워진다
 */
@Serializable
data class CourseEditPlaceVO(
    val placeId: Long,
    val orderNo: Int,
    val name: String,
    val tip: String = "",
    val imageUrls: List<String> = emptyList(),
    val walkingMinutes: Int? = null,
)
