package com.chillsam.courmy.intro.data

import com.chillsam.courmy.intro.data.dto.IntroDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface IntroApiService {
    @GET("/intro")
    suspend fun getIntro(): Response<IntroDTO>
}
