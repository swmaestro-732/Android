package com.chillsam.courmy.course.data.courseFolders

import com.chillsam.courmy.course.data.courseFolders.dto.CourseFolderListEnvelope
import com.chillsam.courmy.course.data.courseFolders.dto.CreateCourseFolderEnvelope
import com.chillsam.courmy.course.data.courseFolders.dto.CreateCourseFolderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * 저장 폴더(도메인 API). 코스 저장(`courseSave`)과 같은 서버 컨트롤러지만,
 * 그쪽 패키지는 저장/취소 전용이라 폴더는 형제 패키지로 나눠 둔다.
 */
interface CourseFolderApiService {
    @GET("api/v1/folders")
    suspend fun getFolders(): Response<CourseFolderListEnvelope>

    /** 폴더 생성. 성공은 201 이며 `data.folderId` 를 준다. 이름 제약(1~10자)을 어기면 400 이다. */
    @POST("api/v1/folders")
    suspend fun createFolder(
        @Body request: CreateCourseFolderRequest,
    ): Response<CreateCourseFolderEnvelope>
}
