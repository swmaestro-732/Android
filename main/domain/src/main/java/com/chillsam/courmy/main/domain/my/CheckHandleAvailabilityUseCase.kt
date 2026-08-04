package com.chillsam.courmy.main.domain.my

import com.chillsam.courmy.main.domain.profile.ProfileRepository
import javax.inject.Inject

/**
 * 아이디(핸들) 사용 가능 여부 확인 UseCase(`GET /api/v1/users/availability`).
 *
 * 길이·형식 검증은 서버 왕복 없이 [validateHandleFormat] 으로 먼저 거른다.
 * 중복 여부만 서버가 판정한다(예약어 포함).
 */
class CheckHandleAvailabilityUseCase
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) {
        suspend operator fun invoke(handle: String): Boolean = repository.isHandleAvailable(handle)
    }

/** 아이디 형식 규칙(FS-26·회원가입 공통): 3~12자, 영문 소문자·숫자·밑줄만. */
fun validateHandleFormat(handle: String): HandleFormatError? =
    when {
        handle.length !in HANDLE_LENGTH_RANGE -> HandleFormatError.LENGTH
        !HANDLE_REGEX.matches(handle) -> HandleFormatError.CHARSET
        else -> null
    }

enum class HandleFormatError {
    LENGTH,
    CHARSET,
}

private val HANDLE_LENGTH_RANGE = 3..12
private val HANDLE_REGEX = Regex("^[a-z0-9_]+$")
