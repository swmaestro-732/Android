package com.chillsam.courmy.course.data.courseFolders

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.courseFolders.dto.CourseFolderListEnvelope
import com.chillsam.courmy.course.data.courseFolders.dto.CreateCourseFolderEnvelope
import com.chillsam.courmy.course.data.courseFolders.dto.CreateCourseFolderRequest

/** 폴더는 아직 연결된 화면이 없어 폴백 없이 예외를 그대로 올린다(쓰기 포함). */
class CourseFolderDataSource(
    private val apiService: CourseFolderApiService,
) : BaseRemoteDataSource() {
    suspend fun getFolders(): CourseFolderListEnvelope = checkResponse(apiService.getFolders())

    suspend fun createFolder(request: CreateCourseFolderRequest): CreateCourseFolderEnvelope =
        checkResponse(apiService.createFolder(request))
}
