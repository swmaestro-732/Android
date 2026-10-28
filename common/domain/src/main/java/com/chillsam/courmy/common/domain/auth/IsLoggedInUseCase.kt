package com.chillsam.courmy.common.domain.auth

import javax.inject.Inject

/**
 * 지금 로그인 상태인지 묻는다. 인메모리 캐시를 읽을 뿐이라 suspend 가 아니다.
 *
 * 로그인이 필요한 동작(팔로우 등)을 시작하기 직전에 호출한다.
 */
class IsLoggedInUseCase
    @Inject
    constructor(
        private val sessionRepository: SessionRepository,
    ) {
        operator fun invoke(): Boolean = sessionRepository.isLoggedIn
    }
