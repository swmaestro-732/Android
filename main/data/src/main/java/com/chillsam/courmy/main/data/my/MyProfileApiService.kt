package com.chillsam.courmy.main.data.my

import com.chillsam.courmy.main.data.my.dto.MyProfileEnvelope
import retrofit2.Response
import retrofit2.http.GET

interface MyProfileApiService {
    /** 마이·프로필 화면 조합(BFF). baseUrl 은 common:data 의 NetworkModule 이 제공. */
    @GET("service/v1/my/profile")
    suspend fun getMyProfile(): Response<MyProfileEnvelope>
}
