package com.chillsam.courmy.main.domain.my

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * [validateHandleFormat] 의 형식 규칙(3~12자, 영문 소문자·숫자·밑줄) 검증.
 *
 * 이 검증이 서버 왕복 전 게이트 역할을 하므로, 경계값(2·3·12·13자)을 골든 케이스로 고정한다.
 */
class HandleFormatTest {
    @Test
    fun `valid handle passes`() {
        assertNull(validateHandleFormat("jiho_routes"))
        assertNull(validateHandleFormat("a_1"))
        assertNull(validateHandleFormat("abcdefghijkl"))
    }

    @Test
    fun `length boundaries are 3 to 12`() {
        assertEquals(HandleFormatError.LENGTH, validateHandleFormat("ab"))
        assertNull(validateHandleFormat("abc"))
        assertNull(validateHandleFormat("abcdefghijkl"))
        assertEquals(HandleFormatError.LENGTH, validateHandleFormat("abcdefghijklm"))
    }

    @Test
    fun `empty handle is a length error`() {
        assertEquals(HandleFormatError.LENGTH, validateHandleFormat(""))
    }

    @Test
    fun `uppercase and symbols are rejected`() {
        assertEquals(HandleFormatError.CHARSET, validateHandleFormat("Jiho"))
        assertEquals(HandleFormatError.CHARSET, validateHandleFormat("ji-ho"))
        assertEquals(HandleFormatError.CHARSET, validateHandleFormat("ji.ho"))
        assertEquals(HandleFormatError.CHARSET, validateHandleFormat("지호님"))
    }

    /** 길이와 형식이 모두 틀리면 길이 오류가 먼저 보고된다(화면 문구가 하나만 뜨므로 순서를 고정). */
    @Test
    fun `length is reported before charset`() {
        assertEquals(HandleFormatError.LENGTH, validateHandleFormat("AB"))
    }
}
