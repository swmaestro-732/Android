package com.chillsam.courmy.main.entity.my

import kotlinx.serialization.Serializable

/**
 * 마이·프로필(FS-15) 표시 데이터(VO). `service/v1/my/profile` 응답을 data 레이어의
 * `toVO()`에서 변환한 결과이며, presentation 은 이 타입만 사용한다(DTO 미노출).
 */
@Serializable
data class MyProfileVO(
    val id: Long = 0L,
    val nickname: String,
    val handle: String,
    val bio: String,
    val profileImageUrl: String = "",
    val myCourseCount: Int,
    val followerCount: String,
    val followingCount: String,
    val myCourses: List<MyCourseVO>,
) {
    companion object {
        /** 개발/프리뷰용 더미 프로필(백엔드 미연동 시 MyViewModel.USE_SAMPLE 로 사용). */
        val sample: MyProfileVO =
            MyProfileVO(
                nickname = "홍지호",
                handle = "jiho_routes",
                bio = "성수동 구석구석 카페 탐험가 · 걷기 좋은 코스를 만들어 나눠요",
                profileImageUrl = "",
                myCourseCount = 8,
                followerCount = "1.4k",
                followingCount = "312",
                myCourses =
                    listOf(
                        MyCourseVO("1", "비 오는 날 성수 카페 코스", "1.2k 따라감", "4스팟"),
                        MyCourseVO("2", "연남 브런치 산책", "430 따라감", "3스팟"),
                        MyCourseVO("3", "한남 갤러리 도장깨기", "210 따라감", "3스팟"),
                        MyCourseVO("4", "서울숲 아침 산책", "96 따라감", "5스팟"),
                    ),
            )
    }
}

/** 마이·프로필의 "내 코스" 카드 1건(썸네일 + 따라감 배지 + 제목 + 스팟 수). */
@Serializable
data class MyCourseVO(
    val id: String,
    val title: String,
    val tracingLabel: String,
    val spotLabel: String,
    val thumbnailUrl: String = "",
)
