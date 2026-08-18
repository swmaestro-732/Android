package com.chillsam.courmy.course.data.courseDetail.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * [CourseScreenData.toVO] 매핑 검증 — 표시용 문자열 포맷팅·순서 정렬·null 처리.
 */
class CourseDetailDtoMappingTest {
    @Test
    fun `course 필드를 화면 표시용 VO 로 포맷팅한다`() {
        val vo = fullData().toVO(myUserId = null)

        assertEquals("비 오는 날 성수 감성 카페 코스", vo.title)
        assertEquals(listOf("성수", "데이트"), vo.themes)
        assertEquals("지호님", vo.authorName)
        assertEquals("@jiho_routes", vo.authorHandle)
        assertEquals("4곳", vo.placeCountText)
        assertEquals("도보 20분", vo.walkText)
        assertEquals("1.2k 따라감", vo.followerText)
        assertEquals("4.3", vo.rating)
        assertEquals("6개", vo.reviewCountText)
    }

    @Test
    fun `장소는 orderNo 순으로 정렬되고 order 는 1부터, 마지막 도보 안내는 null`() {
        val vo =
            fullData()
                .copy(
                    course =
                        fullData().course!!.copy(
                            places =
                                listOf(
                                    place(orderNo = 1, name = "둘째", walkToNext = null),
                                    place(orderNo = 0, name = "첫째", walkToNext = 6),
                                ),
                        ),
                ).toVO(myUserId = null)

        assertEquals(listOf("첫째", "둘째"), vo.places.map { it.name })
        assertEquals(listOf(1, 2), vo.places.map { it.order })
        assertEquals("도보 6분", vo.places[0].walkToNextText)
        assertNull(vo.places[1].walkToNextText)
        assertEquals("1/2", vo.places[0].photoCountText)
    }

    @Test
    fun `리뷰 작성자 null 이면 익명, photoUrls 는 사진 목록`() {
        val vo =
            fullData()
                .copy(
                    reviewSummary =
                        ReviewSummaryDTO(
                            averageRating = 5.0,
                            totalCount = 1,
                            previews =
                                listOf(
                                    ReviewPreviewDTO(
                                        author = null,
                                        rating = 5,
                                        content = "좋아요",
                                        relativeTime = "1일 전",
                                        photoUrls = listOf("a", "b"),
                                    ),
                                ),
                        ),
                ).toVO(myUserId = null)

        assertEquals("익명", vo.reviews[0].author)
        assertEquals(listOf("a", "b"), vo.reviews[0].photoUrls)
        assertEquals("1일 전", vo.reviews[0].dateText)
    }

    /** 서버가 isMine 을 주지 않아 작성자 id 와 세션 id 비교로 판정한다. 잘못되면 남의 코스에 삭제 버튼이 뜬다. */
    @Test
    fun `내 코스 여부는 작성자 id 와 내 id 비교로 정한다`() {
        fun voWith(
            authorId: Long?,
            myId: Long?,
        ) = fullData()
            .copy(course = fullData().course!!.copy(author = AuthorDTO(id = authorId, handle = "jiho")))
            .toVO(myUserId = myId)

        assertEquals(true, voWith(authorId = 7L, myId = 7L).isMine)
        assertEquals(false, voWith(authorId = 7L, myId = 9L).isMine)
        // 비로그인이거나 작성자 id 가 없으면 내 코스로 보지 않는다(삭제 버튼이 뜨면 안 된다).
        assertEquals(false, voWith(authorId = 7L, myId = null).isMine)
        assertEquals(false, voWith(authorId = null, myId = null).isMine)
    }

    /** 서버가 author.isFollowing 을 주는데 DTO 가 받지 않아 항상 "팔로우하기" 가 뜨던 문제를 고정한다. */
    @Test
    fun `작성자 팔로우 여부를 매핑한다`() {
        val following =
            fullData()
                .copy(course = fullData().course!!.copy(author = AuthorDTO(handle = "jiho", isFollowing = true)))
                .toVO(myUserId = null)
        val notFollowing =
            fullData()
                .copy(course = fullData().course!!.copy(author = AuthorDTO(handle = "jiho", isFollowing = false)))
                .toVO(myUserId = null)

        assertEquals(true, following.isFollowingAuthor)
        assertEquals(false, notFollowing.isFollowingAuthor)
    }

    /** 서버는 포맷된 문자열이 아니라 raw 숫자(tracingCount)를 준다. 이름·타입이 어긋나면 항상 0 이 된다. */
    @Test
    fun `따라감 수는 raw 숫자를 축약해 표시한다`() {
        fun voWith(count: Int) =
            fullData()
                .copy(course = fullData().course!!.copy(stats = CourseStatsDTO(tracingCount = count)))
                .toVO(myUserId = null)

        assertEquals("0 따라감", voWith(0).followerText)
        assertEquals("999 따라감", voWith(999).followerText)
        assertEquals("1k 따라감", voWith(1000).followerText)
        assertEquals("1.2k 따라감", voWith(1200).followerText)
    }

    @Test
    fun `이미지 URL 을 매핑한다 - 커버_작성자_장소_리뷰`() {
        val data =
            fullData().copy(
                course =
                    fullData().course!!.copy(
                        coverImageUrl = "cover.jpg",
                        author = AuthorDTO(nickname = "지호님", handle = "jiho_routes", profileImageUrl = "avatar.jpg"),
                        places =
                            listOf(
                                CoursePlaceDTO(
                                    orderNo = 0,
                                    name = "어니언",
                                    images =
                                        listOf(
                                            CoursePlaceImageDTO("p2.jpg", 1),
                                            CoursePlaceImageDTO("p1.jpg", 0),
                                        ),
                                ),
                            ),
                    ),
                reviewSummary =
                    ReviewSummaryDTO(
                        averageRating = 5.0,
                        totalCount = 1,
                        previews =
                            listOf(
                                ReviewPreviewDTO(
                                    author = ReviewAuthorDTO("성수러버", "rv.jpg"),
                                    rating = 5,
                                    content = "좋아요",
                                    relativeTime = "1일 전",
                                    photoUrls = listOf("rp.jpg"),
                                ),
                            ),
                    ),
            )
        val vo = data.toVO(myUserId = null)

        assertEquals("cover.jpg", vo.coverImageUrl)
        assertEquals("avatar.jpg", vo.authorImageUrl)
        // orderNo 순으로 정렬되어야 한다
        assertEquals(listOf("p1.jpg", "p2.jpg"), vo.places[0].imageUrls)
        assertEquals("rv.jpg", vo.reviews[0].authorImageUrl)
        assertEquals(listOf("rp.jpg"), vo.reviews[0].photoUrls)
    }

    @Test
    fun `장소 위치의 위도와 경도를 지도 좌표로 매핑한다`() {
        val vo =
            fullData()
                .copy(
                    course =
                        fullData().course!!.copy(
                            places =
                                listOf(
                                    place(orderNo = 0, name = "어니언 성수", walkToNext = null).copy(
                                        location =
                                            CoursePlaceLocationDTO(
                                                latitude = 37.5445,
                                                longitude = 127.0575,
                                            ),
                                    ),
                                ),
                        ),
                ).toVO(myUserId = null)

        assertEquals(37.5445, vo.places.single().latitude!!, 0.0)
        assertEquals(127.0575, vo.places.single().longitude!!, 0.0)
    }

    @Test
    fun `누락 필드는 기본값으로 안전하게 매핑된다`() {
        val vo = CourseScreenData(course = CourseScreenDTO(), reviewSummary = null).toVO(myUserId = null)

        assertEquals("", vo.title)
        assertEquals(emptyList<String>(), vo.themes)
        assertEquals("0곳", vo.placeCountText)
        // 도보 합계가 없으면 "도보 0분" 대신 빈 값 — 화면이 항목째 뺀다.
        assertEquals("", vo.walkText)
        assertEquals("0 따라감", vo.followerText)
        assertEquals("0.0", vo.rating)
        assertEquals("0개", vo.reviewCountText)
        assertEquals(emptyList<Any>(), vo.places)
        assertEquals(emptyList<Any>(), vo.reviews)
    }

    /**
     * 걸어갈 수 없는 구간(-1)이 섞이면 합계가 실제 동선을 말해 주지 않는다.
     * 표시를 비워 호출부가 "도보" 항목을 아예 빼게 한다.
     */
    @Test
    fun `걸어갈 수 없는 구간이 하나라도 있으면 총 도보 표시를 비운다`() {
        fun voWith(
            total: Int,
            toNext: Int?,
        ) = fullData()
            .copy(
                course =
                    fullData().course!!.copy(
                        stats = CourseStatsDTO(walkingMinutes = total),
                        places = listOf(place(orderNo = 0, name = "첫째", walkToNext = toNext)),
                    ),
            ).toVO(myUserId = null)

        assertEquals("도보 20분", voWith(total = 20, toNext = 6).walkText)
        // 구간 하나가 -1 이면 합계를 믿을 수 없다
        assertEquals("", voWith(total = 20, toNext = -1).walkText)
        // 합계가 0 이하면 값이 없다는 뜻이라 "도보 0분" 을 띄우지 않는다
        assertEquals("", voWith(total = 0, toNext = 6).walkText)
        assertEquals("", voWith(total = -1, toNext = 6).walkText)
    }

    @Test
    fun `코스 태그는 원본을 남기고 표시용 라벨을 따로 만든다`() {
        val vo =
            fullData()
                .copy(course = fullData().course!!.copy(themes = listOf("CULTURE", "성수")))
                .toVO(myUserId = null)

        assertEquals(listOf("CULTURE", "성수"), vo.themes)
        // 마스터 코드는 한글 라벨로, 코드로 알아보지 못한 값은 그대로 남긴다.
        assertEquals(listOf("문화·전시", "성수"), vo.themeLabels)
    }

    /**
     * 서버의 `themes`(장소 구성에서 파생한 읽기 전용 카테고리)와 `tags`(작성자가 단 해시태그)는
     * 별개 필드다. 편집이 `tags` 자리에 `themes` 를 돌려보내면 사용자 태그가 영구 소실되므로,
     * 상세 매핑이 둘을 섞지 않는다는 것을 고정한다.
     */
    @Test
    fun `작성자 해시태그는 파생 카테고리와 별개로 매핑된다`() {
        val vo =
            fullData()
                .copy(
                    course =
                        fullData().course!!.copy(
                            themes = listOf("CULTURE"),
                            tags = listOf("감성카페", "비오는날", "성수동"),
                        ),
                ).toVO(myUserId = null)

        assertEquals(listOf("감성카페", "비오는날", "성수동"), vo.tags)
        assertEquals(listOf("CULTURE"), vo.themes)
    }

    /** 서버가 tags 를 안 주면 편집이 빈 목록을 보내 태그를 지우지 않도록 빈 값으로 둔다. */
    @Test
    fun `tags 가 없으면 빈 목록이 된다`() {
        assertEquals(emptyList<String>(), CourseScreenData(course = CourseScreenDTO()).toVO(null).tags)
    }

    /**
     * 서버는 도보 시간을 스스로 계산하지 않고 편집 요청에 실린 값을 저장한다. 상세가 숫자를 버리면
     * 편집 저장 때 되돌려 보낼 값이 없어 기존 도보 시간이 지워진다.
     */
    @Test
    fun `장소별 도보 분은 표시 문구와 원본 숫자를 함께 담는다`() {
        val vo =
            fullData()
                .copy(
                    course =
                        fullData().course!!.copy(
                            places =
                                listOf(
                                    place(orderNo = 0, name = "첫째", walkToNext = 6),
                                    place(orderNo = 1, name = "둘째", walkToNext = null),
                                ),
                        ),
                ).toVO(myUserId = null)

        assertEquals(6, vo.places[0].walkingMinutesToNext)
        assertEquals("도보 6분", vo.places[0].walkToNextText)
        assertNull(vo.places[1].walkingMinutesToNext)
    }

    /** 서버는 걸어갈 수 없는 구간을 -1 로 준다. 그대로 포맷하면 "도보 -1분" 이 노출된다. */
    @Test
    fun `걸어갈 수 없는 구간은 분으로 환산하지 않는다`() {
        val vo =
            fullData()
                .copy(
                    course =
                        fullData().course!!.copy(
                            places = listOf(place(orderNo = 0, name = "첫째", walkToNext = -1)),
                        ),
                ).toVO(myUserId = null)

        assertEquals("걸어갈 수 없는 거리", vo.places.single().walkToNextText)
        // 숫자는 편집 왕복을 위해 그대로 보존한다.
        assertEquals(-1, vo.places.single().walkingMinutesToNext)
    }

    @Test
    fun `장소 카테고리 코드는 한글 라벨로 바뀌고 UNKNOWN 은 빠진다`() {
        val vo =
            fullData()
                .copy(
                    course =
                        fullData().course!!.copy(
                            places =
                                listOf(
                                    place(orderNo = 0, name = "첫째", walkToNext = null)
                                        .copy(categories = listOf("RESTAURANT", "UNKNOWN", "LANDMARK")),
                                ),
                        ),
                ).toVO(myUserId = null)

        assertEquals("음식점 · 역사·명소", vo.places.single().category)
    }

    private fun place(
        orderNo: Int,
        name: String,
        walkToNext: Int?,
    ) = CoursePlaceDTO(
        orderNo = orderNo,
        name = name,
        caption = "팁",
        walkingMinutesToNext = walkToNext,
        categories = listOf("카페", "베이커리"),
        images = listOf(CoursePlaceImageDTO("u1", 0), CoursePlaceImageDTO("u2", 1)),
    )

    private fun fullData() =
        CourseScreenData(
            course =
                CourseScreenDTO(
                    title = "비 오는 날 성수 감성 카페 코스",
                    themes = listOf("성수", "데이트"),
                    description = "소개",
                    stats = CourseStatsDTO(placeCount = 4, walkingMinutes = 20, tracingCount = 1200),
                    author = AuthorDTO(nickname = "지호님", handle = "jiho_routes"),
                    places = listOf(place(orderNo = 0, name = "어니언 성수", walkToNext = 6)),
                ),
            reviewSummary =
                ReviewSummaryDTO(
                    averageRating = 4.3,
                    totalCount = 6,
                    previews = emptyList(),
                ),
        )
}
