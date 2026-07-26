package com.chillsam.courmy.common.data

import kotlinx.serialization.Serializable

/**
 * Courmy API 공통 응답 봉투: `{ code, message, data }`.
 * 성공/실패는 HTTP 상태로 1차 판단하고([BaseRemoteDataSource]), 화면별 세부 정책은 [code] 로 본다.
 * [data] 는 성공 시에만 존재할 수 있어 nullable 이다.
 */
@Serializable
data class ApiResponse<T>(
    val code: Int = 0,
    val message: String? = null,
    val data: T? = null,
)
