package com.chillsam.courmy.course.data.placeSave

import com.chillsam.courmy.course.data.placeSave.dto.PlaceSaveEnvelope
import com.chillsam.courmy.course.data.placeSave.dto.SavePlaceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

/** 장소 저장(도메인 API). 코스 저장([com.chillsam.courmy.course.data.courseSave.CourseSaveApiService])과 대칭이다. */
interface PlaceSaveApiService {
    /** 장소 저장. 성공은 201(Created)이다. 대상 장소는 바디로 보낸다(코스 저장과 같은 계약 모양). */
    @POST("api/v1/saved-places")
    suspend fun save(
        @Body request: SavePlaceRequest,
    ): Response<PlaceSaveEnvelope>

    @DELETE("api/v1/saved-places/{placeId}")
    suspend fun unsave(
        @Path("placeId") placeId: Long,
    ): Response<PlaceSaveEnvelope>

    /** 저장한 장소 방문 처리. 저장 레코드가 없으면 404 다. */
    @PATCH("api/v1/saved-places/{placeId}")
    suspend fun visit(
        @Path("placeId") placeId: Long,
    ): Response<PlaceSaveEnvelope>
}
