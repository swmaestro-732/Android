package com.chillsam.courmy.course.data.courseFolders.dto

import com.chillsam.courmy.course.entity.CourseFolderVO
import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/folders` 응답 DTO. 봉투는 공통 계약 `{ code, message, data }`.
 * 응답의 `folderCount` 는 `folders.size` 와 같은 값이라 받지 않는다(`ignoreUnknownKeys = true`).
 */
@Serializable
data class CourseFolderListEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CourseFolderListDTO? = null,
)

@Serializable
data class CourseFolderListDTO(
    val folders: List<CourseFolderItemDTO>? = null,
)

@Serializable
data class CourseFolderItemDTO(
    val id: Long? = null,
    val name: String? = null,
)

/** `POST /api/v1/folders` 요청. 생성자는 JWT 로 식별한다(OpenAPI 의 `userId` 쿼리는 서버가 무시). */
@Serializable
data class CreateCourseFolderRequest(
    val name: String,
)

@Serializable
data class CreateCourseFolderEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: CreateCourseFolderData? = null,
)

@Serializable
data class CreateCourseFolderData(
    val folderId: Long? = null,
)

/** id 가 없는 폴더는 코스 저장에 지정할 수 없어 버린다. */
fun CourseFolderListDTO.toVOList(): List<CourseFolderVO> =
    folders
        .orEmpty()
        .filter { it.id != null }
        .map { CourseFolderVO(id = requireNotNull(it.id), name = it.name.orEmpty()) }
