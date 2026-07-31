package com.chillsam.courmy.main.entity.home

/**
 * 홈 화면(FS-09) 공개 코스 피드 카드 1건.
 *
 * - [id]              코스 식별자(상세 이동에 사용)
 * - [categoryLabel]   커버 좌상단 카테고리 칩(예: "성수 · 데이트")
 * - [title]           커버 위 코스 제목
 * - [coverImageUrl]   커버 이미지 URL(없으면 placeholder)
 * - [authorName]      작성자 이름(예: "지호님")
 * - [followText]      따라감 수 텍스트(예: "1.2k 따라감")
 * - [authorImageUrl]  작성자 아바타 URL(없으면 placeholder)
 * - [placePhotoUrls]  코스 속 장소 사진(썸네일 줄). 없으면 placeholder 로 채운다
 * - [placeNamesText]  장소명 요약(예: "어니언 성수 · 대림창고 · 센터커피 · 소금집 델리")
 * - [isSaved]         저장(북마크) 여부
 */
data class HomeCourseVO(
    val id: String,
    val categoryLabel: String,
    val title: String,
    val coverImageUrl: String = "",
    val authorName: String,
    val followText: String,
    val authorImageUrl: String = "",
    val placePhotoUrls: List<String> = emptyList(),
    val placeNamesText: String,
    val isSaved: Boolean = false,
) {
    companion object {
        /** 개발/프리뷰용 더미 공개 코스(백엔드 미연동 시 사용). */
        val sample: List<HomeCourseVO> =
            listOf(
                HomeCourseVO(
                    id = "1",
                    categoryLabel = "성수 · 데이트",
                    title = "비 오는 날 성수 감성 카페 코스",
                    authorName = "지호님",
                    followText = "1.2k 따라감",
                    placePhotoUrls = List(4) { "" },
                    placeNamesText = "어니언 성수 · 대림창고 · 센터커피 · 소금집 델리",
                    isSaved = true,
                ),
                HomeCourseVO(
                    id = "2",
                    categoryLabel = "연남 · 산책",
                    title = "연남동 골목 브런치 산책",
                    authorName = "소마님",
                    followText = "860 따라감",
                    placePhotoUrls = List(3) { "" },
                    placeNamesText = "테일러 · 오브넬생 · 더별다방",
                ),
                HomeCourseVO(
                    id = "3",
                    categoryLabel = "을지로 · 혼술",
                    title = "을지로 노포 감성 술집 코스",
                    authorName = "현우님",
                    followText = "2.1k 따라감",
                    placePhotoUrls = List(5) { "" },
                    placeNamesText = "을지로생맥주 · 기울맥주 · 혐가냈비어 외 2곳",
                ),
                HomeCourseVO(
                    id = "4",
                    categoryLabel = "한남 · 전시",
                    title = "한남동 갤러리 & 브런치 코스",
                    authorName = "유진님",
                    followText = "540 따라감",
                    placePhotoUrls = List(4) { "" },
                    placeNamesText = "리이스 갤러리 · 오토산 · 더컨테프 · 손맛당",
                ),
            )
    }
}
