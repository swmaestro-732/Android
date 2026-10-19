package com.chillsam.courmy.course.domain

import com.chillsam.courmy.course.entity.CourseFolderVO

/**
 * 저장 폴더 계약. 폴더는 코스 저장(`SaveCourseRequest.folderId`)의 분류 단위라 course 도메인에 둔다.
 * 사용자는 서버가 JWT 로 식별한다(OpenAPI 의 `userId` 쿼리는 서버가 무시한다 — 코스 저장과 동일).
 */
interface CourseFolderRepository {
    /** 내 폴더 전체(`GET /api/v1/folders`). 폴더는 수가 적어 페이징이 없다. */
    suspend fun getFolders(): List<CourseFolderVO>

    /** 폴더를 만들고 생성된 id 를 돌려준다(`POST /api/v1/folders`). */
    suspend fun createFolder(name: String): Long
}
