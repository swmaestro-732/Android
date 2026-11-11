package com.chillsam.courmy.course.entity

import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import kotlinx.serialization.Serializable

/**
 * 완성된 코스의 상세. Figma FS-11 CourseDetailActivity 한 화면의 전체 표시 데이터.
 *
 * - [title]          코스 제목 (예: "비 오는 날 성수 감성 카페 코스")
 * - [coverImageUrl]  히어로 커버 이미지 URL (없으면 빈 문자열)
 * - [themes]         서버가 장소 구성에서 **파생**한 카테고리 코드 (예: ["CULTURE"]). 읽기 전용이라
 *                    요청으로 설정할 수 없다. 표시에는 [themeLabels] 를 쓴다
 * - [themeLabels] [themes] 를 화면 라벨로 바꾼 값 (예: ["문화·전시"]). 합치지 않고 목록 그대로 담아
 *                    화면에서 칩 하나씩 렌더한다. 비어 있으면 태그 줄을 그리지 않는다
 * - [tags]           작성자가 직접 단 해시태그 (예: ["감성카페", "비오는날"]). [themes] 와 별개 필드이며
 *                    **편집이 되돌려 보내야 하는 값**이다. [themes] 를 대신 보내면 사용자 태그가 지워진다
 * - [authorId]       작성자 id. 팔로우 요청 대상이다(0 이면 서버가 주지 않은 것).
 * - [authorName]     작성자 이름 (예: "지호님")
 * - [authorHandle]   작성자 핸들 (예: "@jiho_routes")
 * - [authorImageUrl] 작성자 프로필 이미지 URL
 * - [isFollowingAuthor] 내가 이 작성자를 팔로우 중인지. true 면 버튼이 "팔로잉"(흐린 상태)으로 바뀐다.
 * - [placeCount] 장소 수. 표시 문구는 presentation에서 로컬라이즈한다.
 * - [walkingMinutes] 총 도보 분. 유효한 합계가 없으면 null.
 * - [tracingCount] 따라간 사람 수.
 * - [description]    코스 소개 문단
 * - [places]         코스 속 장소 목록 (순서대로)
 * - [rating]         평균 별점 텍스트 (예: "4.8")
 * - [reviewCount] 리뷰 수.
 * - [reviews]        리뷰 목록
 */
@Serializable
data class CourseDetailVO(
    val title: String,
    val coverImageUrl: String = "",
    val themes: List<String> = emptyList(),
    val themeLabels: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val authorId: Long = 0L,
    val authorName: String,
    val authorHandle: String,
    val authorImageUrl: String = "",
    val isFollowingAuthor: Boolean = false,
    val placeCount: Int,
    val walkingMinutes: Int?,
    val tracingCount: Int,
    val description: String,
    val places: List<CourseDetailPlaceVO>,
    val rating: String,
    val reviewCount: Int,
    val reviews: List<CourseReviewVO>,
    /** 내가 이 코스를 저장했는지(서버 viewer.hasSaved). 하단 저장 버튼 상태에 쓴다. */
    val isSaved: Boolean = false,
    /** 내가 좋아요를 눌렀는지(서버 viewer.hasLiked). 진입 시 좋아요 토글의 초기 상태로 시딩한다. */
    val isLiked: Boolean = false,
    /**
     * 내가 만든 코스인지. 서버가 내려주지 않아 작성자 id 와 세션 사용자 id 를 비교해 채운다.
     * true 면 저장 대신 편집·삭제를 노출하고 작성자 팔로우 버튼을 숨긴다.
     */
    val isMine: Boolean = false,
    val forkedFromId: Long? = null,
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
 * - [walkingMinutesToNext] 다음 장소까지 도보 분. 편집이 이 값을 그대로 되돌려 보내야
 *                    기존 도보 시간이 유지된다(서버가 places 를 통째로 치환한다). 마지막 장소는 null
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
    val walkingMinutesToNext: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val detailCategory: PlaceDetailCategoryVO? = null,
)

/**
 * 코스 리뷰 1건.
 *
 * - [id]          리뷰 식별자. 삭제(`DELETE .../reviews/{reviewId}`) 요청 키다. 0 이면 서버가 주지 않은 것.
 * - [authorId]    리뷰어 id. [isMine] 판정 근거다.
 * - [author]      리뷰어 이름
 * - [authorImageUrl] 리뷰어 프로필 이미지 URL
 * - [rating]      별점(1~5)
 * - [createdAtMillis] UTC epoch millis. 누락/파싱 실패는 null이며 상대 시각은 presentation에서 표시한다.
 * - [body]        리뷰 본문. 별점만 남긴 리뷰는 빈 문자열이다(서버 content 가 nullable).
 * - [photoUrls]   첨부 사진 URL 목록 (비면 미표시)
 * - [tags]        리뷰어가 고른 태그. 상세 미리보기 카드(FS-11)는 감추고 후기 목록(FS-37)만 보여준다.
 * - [isMine]      내가 쓴 리뷰인지. 서버가 주지 않아 [authorId] 와 세션 사용자 id 를 비교해 채운다.
 *                 true 일 때만 삭제 메뉴를 노출한다.
 */
@Serializable
data class CourseReviewVO(
    val id: Long = 0L,
    val authorId: Long = 0L,
    val author: String,
    val authorImageUrl: String = "",
    val rating: Int,
    val createdAtMillis: Long?,
    val body: String,
    val photoUrls: List<String> = emptyList(),
    val tags: List<CourseReviewTagVO> = emptyList(),
    val isMine: Boolean = false,
)

/**
 * 리뷰 태그 1개. 서버가 코드(`packed`)와 표기(문구·이모지)를 함께 내려준다.
 *
 * - [code]  서버 `CourseReviewTag` enum 이름의 소문자 (예: "packed"). 작성 요청의 `tagCodes` 에 싣는 값이라
 *           화면에 그리지 않더라도 버리지 않는다.
 * - [label] 태그 문구 (예: "구성이 알차요")
 * - [icon]  앞에 붙는 이모지. 서버가 안 주면 빈 문자열이고, 그때는 문구만 그린다.
 */
@Serializable
data class CourseReviewTagVO(
    val code: String = "",
    val label: String,
    val icon: String = "",
)
