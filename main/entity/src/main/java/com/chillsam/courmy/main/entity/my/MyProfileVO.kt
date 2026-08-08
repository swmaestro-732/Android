package com.chillsam.courmy.main.entity.my

import com.chillsam.courmy.main.entity.profile.ProfileCourseVO
import kotlinx.serialization.Serializable

/**
 * 마이·프로필(FS-15) 표시 데이터(VO). `GET /service/v1/mypage` 응답을 data 레이어의
 * `toVO()`에서 변환한 결과이며, presentation 은 이 타입만 사용한다(DTO 미노출).
 *
 * TODO-API-SPEC: [bio] 는 서버 응답(`MyPageProfileResponse`)에 필드가 없어, 응답이 아니라
 * **기기에 저장된 값**(BioPreferencesDataStore)을 ProfileRepositoryImpl 에서 합쳐 채운다.
 * 그래서 소개는 이 기기에서만 보이고 다른 사용자에게는 보이지 않는다.
 * 서버에 bio 가 추가되면 그 병합과 로컬 저장을 지우고 응답 값을 그대로 매핑한다. [wiki-needed]
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
)
