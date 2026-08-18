package com.chillsam.courmy.main.data.follow.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * [FollowListDTO.toVOList] 매핑 검증 — 자기 자신 판정·id 없는 항목 제외.
 */
class FollowListDtoMappingTest {
    /**
     * 서버가 isMe 를 주지 않아 내 id 와 비교해 채운다. 잘못되면 목록에서 나를 눌렀을 때
     * 마이 화면 대신 타유저 프로필이 열린다.
     */
    @Test
    fun `내 id 와 같은 사용자만 isMe 로 표시한다`() {
        val dto = FollowListDTO(users = listOf(user(id = 7L), user(id = 9L)))

        val vos = dto.toVOList(myUserId = 7L)

        assertTrue(vos.first { it.id == 7L }.isMe)
        assertFalse(vos.first { it.id == 9L }.isMe)
    }

    /** 비로그인이거나 토큰에서 id 를 못 꺼낸 경우 아무도 내가 아니다. */
    @Test
    fun `내 id 가 없으면 아무도 isMe 가 아니다`() {
        val dto = FollowListDTO(users = listOf(user(id = 7L)))

        assertFalse(dto.toVOList(myUserId = null).single().isMe)
    }

    @Test
    fun `id 가 없는 사용자는 팔로우 요청을 걸 수 없어 목록에서 뺀다`() {
        val dto = FollowListDTO(users = listOf(user(id = null), user(id = 3L)))

        val vos = dto.toVOList(myUserId = null)

        assertEquals(listOf(3L), vos.map { it.id })
    }

    private fun user(id: Long?) =
        FollowUserDTO(
            id = id,
            nickname = "지호님",
            handle = "jiho_routes",
            profileImageUrl = "avatar.jpg",
            isFollowing = true,
        )
}
