package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 완성된 코스의 상세. Figma FS-11 CourseDetailActivity 한 화면의 전체 표시 데이터.
 *
 * - [title]          코스 제목 (예: "비 오는 날 성수 감성 카페 코스")
 * - [coverImageUrl]  히어로 커버 이미지 URL (없으면 빈 문자열)
 * - [category]       상단 카테고리 (예: "성수 · 데이트")
 * - [authorName]     작성자 이름 (예: "지호님")
 * - [authorHandle]   작성자 핸들 (예: "@jiho_routes")
 * - [authorImageUrl] 작성자 프로필 이미지 URL
 * - [isFollowingAuthor] 내가 이 작성자를 팔로우 중인지. true 면 팔로우 버튼을 숨긴다.
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
    val category: String,
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
) {
    companion object {
        /**
         * 개발/프리뷰용 더미(성수동 좌표 포함). 백엔드에 장소 좌표가 아직 없어
         * "코스 경로" 지도가 뜨지 않으므로, 좌표가 없는 응답은 이 더미로 대체해 화면을 확인한다.
         */
        val sample: CourseDetailVO =
            CourseDetailVO(
                title = "비 오는 날 성수 감성 카페 코스",
                coverImageUrl = "",
                category = "성수 · 데이트",
                authorName = "지호님",
                authorHandle = "@jiho_routes",
                authorImageUrl = "",
                placeCountText = "4곳",
                walkText = "도보 20분",
                followerText = "1.2k 따라감",
                description = "비가 오면 더 예쁜 성수 카페만 골라 담았어요. 전부 도보로 이어지고, 장소마다 제 팁을 남겨뒀으니 참고하세요 🌧",
                places =
                    listOf(
                        CourseDetailPlaceVO(
                            order = 1,
                            name = "어니언 성수",
                            category = "카페 · 베이커리",
                            photoCountText = "1/3",
                            tip = "통창 자리 꼭 앉으세요. 비 오는 날 이 뷰가 진짜 최고예요. 팡도르는 나오자마자!",
                            walkToNextText = "도보 6분",
                            latitude = 37.5447,
                            longitude = 127.0561,
                        ),
                        CourseDetailPlaceVO(
                            order = 2,
                            name = "대림창고 갤러리",
                            category = "전시 · 카페",
                            photoCountText = "1/2",
                            tip = "천장 높은 공간이라 사진이 잘 나와요. 안쪽 전시도 꼭 보세요.",
                            walkToNextText = "도보 5분",
                            latitude = 37.5410,
                            longitude = 127.0552,
                        ),
                        CourseDetailPlaceVO(
                            order = 3,
                            name = "센터커피 로스터스",
                            category = "카페 · 디저트",
                            photoCountText = "1/2",
                            tip = "로스팅 향이 진해요. 핸드드립 한 잔 시켜서 잠깐 앉았다 가기 좋아요.",
                            walkToNextText = "도보 4분",
                            latitude = 37.5433,
                            longitude = 127.0575,
                        ),
                        CourseDetailPlaceVO(
                            order = 4,
                            name = "소금집 델리",
                            category = "와인 · 안주",
                            photoCountText = "1/1",
                            tip = "마무리로 와인 한 잔. 안주는 관자 카르파초 강추예요.",
                            walkToNextText = null,
                            latitude = 37.5428,
                            longitude = 127.0532,
                        ),
                    ),
                rating = "4.8",
                reviewCountText = "128개",
                reviews = emptyList(),
            )
    }
}

/**
 * 코스 상세의 장소 1건("코스 속 장소").
 *
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
