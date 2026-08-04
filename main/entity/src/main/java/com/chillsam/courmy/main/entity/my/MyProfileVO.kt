package com.chillsam.courmy.main.entity.my

import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import kotlinx.serialization.Serializable

/**
 * 마이·프로필(FS-15) 표시 데이터(VO). `GET /service/v1/mypage` 응답을 data 레이어의
 * `toVO()`에서 변환한 결과이며, presentation 은 이 타입만 사용한다(DTO 미노출).
 *
 * TODO-API-SPEC: [bio] 는 서버 응답(`MyPageProfileResponse`)에 필드가 없어 항상 빈 문자열이며,
 * 화면에서는 비어 있으면 렌더하지 않는다. 프로필 편집 화면이 입력 UI 를 갖고 있으므로 필드는 남겨 둔다.
 * 서버에 bio 가 추가되면 [toVO] 매핑과 화면 렌더를 함께 되살린다. [wiki-needed]
 */
@Serializable
data class MyProfileVO(
    val id: Long = 0L,
    val nickname: String,
    val handle: String,
    val bio: String = "",
    val profileImageUrl: String = "",
    val myCourseCount: Int,
    val followerCount: String,
    val followingCount: String,
    val myCourses: List<ProfileCourseVO>,
) {
    companion object {
        /** 개발/프리뷰용 더미 프로필. */
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
                        ProfileCourseVO("1", "비 오는 날 성수 카페 코스"),
                        ProfileCourseVO("2", "연남 브런치 산책"),
                        ProfileCourseVO("3", "한남 갤러리 도장깨기"),
                        ProfileCourseVO("4", "서울숲 아침 산책"),
                    ),
            )
    }
}
