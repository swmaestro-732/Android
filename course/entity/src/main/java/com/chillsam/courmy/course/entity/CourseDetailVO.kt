package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 완성된 코스의 상세. Figma FS-11 CourseDetailActivity 한 화면의 전체 표시 데이터.
 *
 * - [title]          코스 제목 (예: "비 오는 날 성수 감성 카페 코스")
 * - [coverImageUrl]  히어로 커버 이미지 URL (없으면 빈 문자열)
 * - [themes]         코스 태그의 **서버 원본 값** (예: ["CULTURE", "성수"]). 코스 편집이 이 값을
 *                    그대로 `tags` 로 돌려보내므로 라벨로 바꾸지 않는다. 표시에는 [themeLabels] 를 쓴다
 * - [themeLabels]    화면에 그릴 태그 라벨 (예: ["문화·전시", "성수"]). 합치지 않고 목록 그대로 담아
 *                    화면에서 칩 하나씩 렌더한다. 비어 있으면 태그 줄을 그리지 않는다
 * - [authorId]       작성자 id. 팔로우 요청 대상이다(0 이면 서버가 주지 않은 것).
 * - [authorName]     작성자 이름 (예: "지호님")
 * - [authorHandle]   작성자 핸들 (예: "@jiho_routes")
 * - [authorImageUrl] 작성자 프로필 이미지 URL
 * - [isFollowingAuthor] 내가 이 작성자를 팔로우 중인지. true 면 버튼이 "팔로잉"(흐린 상태)으로 바뀐다.
 * - [placeCountText] 장소 수 요약 (예: "4곳")
 * - [walkText]       총 도보 요약 (예: "도보 20분")
 * - [followerText]   따라간 사람 수 (예: "1.2k 따라감")
 * - [description]    코스 소개 문단
 * - [places]         코스 속 장소 목록 (순서대로)
 * - [rating]         평균 별점 텍스트 (예: "4.8")
 * - [reviewCountText] 리뷰 수 (예: "128개")
 * - [reviews]        리뷰 목록
 */
@Serializable
data class CourseDetailVO(
    val title: String,
    val coverImageUrl: String = "",
    val themes: List<String> = emptyList(),
    val themeLabels: List<String> = emptyList(),
    val authorId: Long = 0L,
    val authorName: String,
    val authorHandle: String,
    val authorImageUrl: String = "",
    val isFollowingAuthor: Boolean = false,
    val placeCountText: String,
    val walkText: String,
    val followerText: String,
    val description: String,
    val places: List<CourseDetailPlaceVO>,
    val rating: String,
    val reviewCountText: String,
    val reviews: List<CourseReviewVO>,
    /** 내가 이 코스를 저장했는지(서버 viewer.hasSaved). 하단 저장 버튼 상태에 쓴다. */
    val isSaved: Boolean = false,
    /**
     * 내가 만든 코스인지. 서버가 내려주지 않아 작성자 id 와 세션 사용자 id 를 비교해 채운다.
     * true 면 저장 대신 편집·삭제를 노출하고 작성자 팔로우 버튼을 숨긴다.
     */
    val isMine: Boolean = false,
)

/**
 * 코스 상세의 장소 1건("코스 속 장소").
 *
 * - [placeId]        place 도메인 식별자. 장소 상세(`GET /service/v1/places/{placeId}`) 조회 키
 * - [order]          순번 (1부터)
 * - [name]           장소명
 * - [category]       카테고리 (예: "카페 · 베이커리")
 * - [photoCountText] 사진 인덱스 배지 (예: "1/3")
 * - [imageUrls]      장소 사진 URL 목록 (순서대로)
 * - [tip]            작성자 팁 ("지호님 팁" 본문)
 * - [walkToNextText] 다음 장소로의 도보 안내 (예: "도보 6분"). 마지막 장소는 null
 * - [latitude]       위도. 좌표가 없으면 null (지도에 핀을 찍지 않는다)
 * - [longitude]      경도. 좌표가 없으면 null
 */
@Serializable
data class CourseDetailPlaceVO(
    val placeId: Long = 0L,
    val order: Int,
    val name: String,
    val category: String,
    val photoCountText: String,
    val tip: String,
    val imageUrls: List<String> = emptyList(),
    val walkToNextText: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
)

/**
 * 코스 리뷰 1건.
 *
 * - [author]      리뷰어 이름
 * - [authorImageUrl] 리뷰어 프로필 이미지 URL
 * - [rating]      별점(1~5)
 * - [dateText]    상대 날짜 (예: "2일 전")
 * - [body]        리뷰 본문
 * - [photoUrls]   첨부 사진 URL 목록 (비면 미표시)
 */
@Serializable
data class CourseReviewVO(
    val author: String,
    val authorImageUrl: String = "",
    val rating: Int,
    val dateText: String,
    val body: String,
    val photoUrls: List<String> = emptyList(),
)
