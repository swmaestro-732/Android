package com.chillsam.courmy.course.data.draft

import com.chillsam.courmy.course.data.draft.dto.DraftListEnvelope
import retrofit2.Response
import retrofit2.http.GET

/**
 * 임시저장 전용 엔드포인트는 목록 조회 하나뿐이다.
 *
 * 초안의 생성·갱신·삭제·조회는 발행 전 코스를 다루는 것과 같아서 코스 API 를 그대로 쓴다
 * (`CourseCreateApiService` 의 `POST`, `CourseManageApiService` 의 `PATCH`·`DELETE`·`GET`).
 */
interface DraftApiService {
    /** 내 임시저장 목록. 작성자는 JWT 로 식별되며, 커서 없이 전체를 배열로 준다. */
    @GET("api/v1/courses/drafts")
    suspend fun getDrafts(): Response<DraftListEnvelope>
}
