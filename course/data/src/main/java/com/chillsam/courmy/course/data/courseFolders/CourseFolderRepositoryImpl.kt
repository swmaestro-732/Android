package com.chillsam.courmy.course.data.courseFolders

import com.chillsam.courmy.course.data.courseFolders.dto.CreateCourseFolderRequest
import com.chillsam.courmy.course.data.courseFolders.dto.toVOList
import com.chillsam.courmy.course.domain.CourseFolderRepository
import com.chillsam.courmy.course.entity.CourseFolderVO

class CourseFolderRepositoryImpl(
    private val dataSource: CourseFolderDataSource,
) : CourseFolderRepository {
    override suspend fun getFolders(): List<CourseFolderVO> {
        val envelope = dataSource.getFolders()
        val data =
            requireNotNull(envelope.data) {
                envelope.message ?: "폴더 목록 응답에 data 가 없습니다."
            }
        return data.toVOList()
    }

    override suspend fun createFolder(name: String): Long {
        val envelope = dataSource.createFolder(CreateCourseFolderRequest(name = name))
        return requireNotNull(envelope.data?.folderId) { "폴더 생성 응답에 folderId 가 없습니다." }
    }
}
