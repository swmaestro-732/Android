package com.chillsam.courmy.main.entity.my

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import kotlinx.serialization.Serializable

/**
 * 마이·프로필(FS-15) 표시 데이터(VO). `GET /service/v1/mypage` 응답을 data 레이어의
 * `toVO()`에서 변환한 결과이며, presentation 은 이 타입만 사용한다(DTO 미노출).
 *
 * 편집 필드와 [interestThemes]·[interestRegions]는 `GET /api/v1/users`에서 읽는다.
 * data 레이어에서 테마 코드를 정규화하고 법정동 목록의 완전한 상위 범위를 묶는다.
 * 코스·카운트는 마이페이지 BFF 값을 유지한다.
 *
 * [interestThemes] 는 코스 태그 **코드**(`date` …)다 — 표시할 때
 * `CourseTagVO.labelOf` 로 라벨을 얻는다.
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
    /**
     * 내 코스 **한 페이지**. 서버가 기본 10개씩 커서 페이징으로 준다.
     * 화면은 페이지를 이어 붙여 보여주므로 누적은 ViewModel 이 맡는다(홈 피드와 같은 방식).
     */
    val myCourses: CursorPageVO<ProfileCourseVO> = CursorPageVO(),
    val interestThemes: List<String> = emptyList(),
    val interestRegions: List<AreaVO> = emptyList(),
)
