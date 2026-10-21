package com.chillsam.courmy.main.domain.auth

import com.chillsam.courmy.main.entity.auth.HandleCheckResult
import javax.inject.Inject

/**
 * 아이디(핸들) 사용 가능 여부 판정.
 *
 * 길이·형식은 서버 왕복 없이 먼저 거르고, 통과한 값만 `GET /api/v1/users/availability` 로 확인한다.
 * 네트워크 실패는 예외로 올리지 않고 [HandleCheckResult.ERROR] 로 바꿔, 화면이 "사용 가능"으로
 * 오해하지 않게 한다(data 는 throw, 처리는 domain 이라는 계층 규칙).
 */
class CheckHandleUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        suspend operator fun invoke(handle: String): HandleCheckResult =
            when {
                handle.length !in LENGTH_RANGE -> {
                    HandleCheckResult.INVALID_LENGTH
                }

                !HANDLE_REGEX.matches(handle) -> {
                    HandleCheckResult.INVALID_FORMAT
                }

                else -> {
                    runCatching { repository.isHandleAvailable(handle) }
                        .fold(
                            onSuccess = { available ->
                                if (available) HandleCheckResult.AVAILABLE else HandleCheckResult.TAKEN
                            },
                            onFailure = { HandleCheckResult.ERROR },
                        )
                }
            }

        companion object {
            val LENGTH_RANGE = 3..12
            private val HANDLE_REGEX = Regex("^[a-z0-9_]+$")
        }
    }
