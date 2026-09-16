package com.chillsam.courmy.common.domain.network

import javax.inject.Inject

/**
 * 지금 인터넷에 닿을 수 있는지 묻는다. 시스템 상태를 읽을 뿐이라 suspend 가 아니다.
 *
 * 스플래시 진입과 "다시 시도"에서 호출한다.
 */
class IsOnlineUseCase
    @Inject
    constructor(
        private val connectivityRepository: ConnectivityRepository,
    ) {
        operator fun invoke(): Boolean = connectivityRepository.isOnline()
    }
