package com.chillsam.courmy.main.data.profile

import com.chillsam.courmy.main.data.profile.dto.AccountProfileEnvelope
import com.chillsam.courmy.main.data.profile.dto.AvailabilityEnvelope
import com.chillsam.courmy.main.data.profile.dto.FollowEnvelope
import com.chillsam.courmy.main.data.profile.dto.MyPageEnvelope
import com.chillsam.courmy.main.data.profile.dto.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 마이페이지 화면 조합(BFF) + 팔로우 API.
 * 사용자 식별자는 모두 JWT 에서 나오므로 요청에 userId 를 싣지 않는다.
 * baseUrl 은 common:data 의 NetworkModule 이 제공한다.
 */
interface ProfileApiService {
    /** 내 마이페이지 — 내 프로필 + 내가 발행한 코스. */
    @GET("service/v1/mypage")
    suspend fun getMyPage(): Response<MyPageEnvelope>

    /** 타유저 마이페이지 — 대상 프로필 + 공개 코스. 팔로우 플래그는 JWT 의 뷰어 기준. */
    @GET("service/v1/mypage/{handle}")
    suspend fun getUserPage(
        @Path("handle") handle: String,
    ): Response<MyPageEnvelope>

    /**
     * 내 프로필 수정 — 넘긴 필드만 반영된다(부분 수정). 대상은 JWT 로 식별하므로 경로에 id 가 없다.
     *
     * 경로가 `/api/v1/my/profile` 이 아니라 컬렉션 경로 `/api/v1/users` 인 점에 주의한다
     * (서버 `UserController` 의 `@RequestMapping("/api/v1/users")` + `@PatchMapping`).
     */
    @PATCH("api/v1/users")
    suspend fun updateProfile(
        @Body body: UpdateProfileRequest,
    ): Response<AccountProfileEnvelope>

    /** 핸들(아이디) 사용 가능 여부. 공개 엔드포인트라 인증이 필요 없다. */
    @GET("api/v1/users/availability")
    suspend fun checkHandleAvailability(
        @Query("handle") handle: String,
    ): Response<AvailabilityEnvelope>

    /** 대상 사용자의 팔로워로 "나"를 추가(멱등이라 PUT). */
    @PUT("api/v1/users/followers/{userId}")
    suspend fun follow(
        @Path("userId") userId: Long,
    ): Response<FollowEnvelope>

    /** 대상 사용자의 팔로워에서 "나"를 제거. */
    @DELETE("api/v1/users/followers/{userId}")
    suspend fun unfollow(
        @Path("userId") userId: Long,
    ): Response<FollowEnvelope>
}
