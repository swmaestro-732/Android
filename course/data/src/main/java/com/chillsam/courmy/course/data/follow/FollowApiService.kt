package com.chillsam.courmy.course.data.follow

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * 코스 상세의 작성자 팔로우. 마이페이지 쪽과 같은 엔드포인트다.
 *
 * course 모듈은 main 에 의존할 수 없어(의존 방향 main → course) 여기서 직접 호출한다.
 * 계약이 바뀌면 `main:data` 의 ProfileApiService 와 함께 고쳐야 한다.
 */
interface FollowApiService {
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

@Serializable
data class FollowEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: FollowDataDTO? = null,
)

/**
 * 팔로우/언팔로우 응답 `{ isFollowing, followersCnt }`.
 *
 * 서버가 Kotlin `val isFollowing` 으로 선언해도 Jackson 의 boolean getter 규칙상 `is` 가 떨어져
 * `following` 으로 나갈 수 있다. `ignoreUnknownKeys = true` 라 키가 어긋나면 조용히 false 가 되므로
 * 두 표기를 모두 받는다.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class FollowDataDTO(
    @JsonNames("following")
    val isFollowing: Boolean = false,
    val followersCnt: Int? = null,
)
