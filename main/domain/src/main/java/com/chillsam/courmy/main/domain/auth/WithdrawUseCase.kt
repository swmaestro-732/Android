package com.chillsam.courmy.main.domain.auth

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import javax.inject.Inject

/** 회원 탈퇴 UseCase. 서버 계정을 삭제하고 로컬 토큰을 제거한다. */
class WithdrawUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
        private val telemetry: Telemetry,
    ) {
        // 탈퇴는 이탈 지표라 성공·실패를 모두 남긴다. SNS 연결 해제(카카오 unlink 등)는
        // 호출부의 부가 정리라 집계에 포함하지 않는다 — 서버 계정 삭제가 탈퇴의 기준이다.
        suspend operator fun invoke() = telemetry.track(AppFlow.Withdraw) { repository.withdraw() }
    }
