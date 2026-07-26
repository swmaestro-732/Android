package com.chillsam.courmy.main.presentation.my

/**
 * 마이·프로필(FS-15) 표시 상태.
 *
 * TODO-API-SPEC: 현재는 UI 확인용 더미다. 프로필/내 코스 API 가 붙으면 교체한다.
 */
data class MyProfileUiState(
    val nickname: String,
    val handle: String,
    val bio: String,
    val savedCount: String,
    val myCourseCount: Int,
    val followerCount: String,
    val myCourses: List<MyCourseCardUi>,
) {
    companion object {
        /** FS-15 디자인 확인용 예시 프로필. */
        val sample: MyProfileUiState =
            MyProfileUiState(
                nickname = "홍지호",
                handle = "jiho_routes",
                bio = "성수동 구석구석 카페 탐험가 · 걷기 좋은 코스를 만들어 나눠요",
                savedCount = "312",
                myCourseCount = 8,
                followerCount = "1.4k",
                myCourses =
                    listOf(
                        MyCourseCardUi("1", "비 오는 날 성수 카페 코스", "1.2k 따라감", "4스팟"),
                        MyCourseCardUi("2", "연남 브런치 산책", "430 따라감", "3스팟"),
                        MyCourseCardUi("3", "한남 갤러리 도장깨기", "210 따라감", "3스팟"),
                        MyCourseCardUi("4", "서울숲 아침 산책", "96 따라감", "5스팟"),
                    ),
            )
    }
}

/** 마이·프로필의 "내 코스" 카드 1건(썸네일 + 따라감 배지 + 제목 + 스팟 수). */
data class MyCourseCardUi(
    val id: String,
    val title: String,
    val tracingLabel: String,
    val spotLabel: String,
    val thumbnailUrl: String = "",
)
