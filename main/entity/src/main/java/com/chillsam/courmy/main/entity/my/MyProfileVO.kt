package com.chillsam.courmy.main.entity.my

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.area.AreaVO
import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import kotlinx.serialization.Serializable

/**
 * 마이·프로필(FS-15) 표시 데이터(VO). `GET /service/v1/mypage` 응답을 data 레이어의
 * `toVO()`에서 변환한 결과이며, presentation 은 이 타입만 사용한다(DTO 미노출).
 *
 * TODO-API-SPEC: [interestThemes]·[interestRegions] 는 서버가 저장은 하지만
 * (`SignupRequest`·`UpdateProfileRequest` 의 `likeThemes`·`areaCodes`) 마이페이지 응답에는
 * 내려오지 않아, 조회는 기기 캐시(InterestPreferencesDataStore)로 대신한다.
 * 응답에 실리면 병합과 로컬 캐시를 함께 지운다. [wiki-needed]
 *
 * [interestThemes] 는 서버 코스 카테고리 **코드**(`CAFETOUR` …)다 — 표시할 때
 * `CourseCategoryVO.labelOf` 로 라벨을 얻는다.
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
