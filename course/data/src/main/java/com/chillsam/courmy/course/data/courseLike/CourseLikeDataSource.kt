package com.chillsam.courmy.course.data.courseLike

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseLike.dto.CourseLikeEnvelope
import retrofit2.Response

class CourseLikeDataSource(
    private val apiService: CourseLikeApiService,
) : BaseRemoteDataSource() {
    /** 쓰기 요청이라 폴백 없이 실패를 그대로 올린다(가짜 성공 금지 — 코스 후기 쓰기와 같은 규칙). */
    suspend fun like(courseId: Long): CourseLikeEnvelope? = bodyOrNull(apiService.like(courseId))

    suspend fun unlike(courseId: Long): CourseLikeEnvelope? = bodyOrNull(apiService.unlike(courseId))

    /**
     * 상태 코드만 검증하고 바디는 있으면 쓴다.
     *
     * [checkResponse] 는 2xx 에 바디가 없으면 예외라, 서버가 바디 없이 성공을 주는 순간
     * **실제로 반영된 쓰기가 실패로 둔갑해 화면이 되돌아간다**. 좋아요는 바디가 집계 보정용
     * 부가 정보일 뿐이므로 저장 토글([checkSuccess] 사용)처럼 성공 판정과 분리한다.
     */
    private fun bodyOrNull(response: Response<CourseLikeEnvelope>): CourseLikeEnvelope? {
        checkSuccess(response)
        return response.body()
    }
}
