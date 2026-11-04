package com.chillsam.courmy.main.data.profile.dto

import com.chillsam.courmy.main.entity.area.AreaVO
import kotlinx.serialization.Serializable

/** GET /api/v1/users 의 편집용 프로필. 관심사는 마이페이지 BFF가 아닌 이 응답을 따른다. */
@Serializable
data class AccountProfileEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: AccountProfileDTO? = null,
)

@Serializable
data class AccountProfileDTO(
    val id: Long? = null,
    val nickname: String? = null,
    val handle: String? = null,
    val profileImageUrl: String? = null,
    val bio: String? = null,
    val likeThemes: List<String>? = null,
    val areas: List<UserAreaDTO>? = null,
)

@Serializable
data class UserAreaDTO(
    val code: String? = null,
    val name: String? = null,
) {
    fun toVO(): AreaVO {
        val areaCode = requireNotNull(code) { "관심 지역 응답에 코드가 없습니다." }
        require(areaCode.length in listOf(2, 5, 10) && areaCode.all(Char::isDigit)) { "잘못된 관심 지역 코드" }
        val label = name?.takeIf { it.isNotBlank() } ?: areaCode
        return AreaVO(areaCode, label, label)
    }
}
