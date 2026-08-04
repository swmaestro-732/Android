package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.data.courseCreate.CourseCreateApiService
import com.chillsam.courmy.course.data.courseCreate.CourseCreateDataSource
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseEnvelope
import com.chillsam.courmy.course.data.courseCreate.dto.CreateCourseRequest
import com.chillsam.courmy.course.data.courseDetail.CourseDetailApiService
import com.chillsam.courmy.course.data.courseDetail.CourseDetailDataSource
import com.chillsam.courmy.course.data.courseDetail.dto.CourseDetailEnvelope
import com.chillsam.courmy.course.entity.CourseDraftVO
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

/**
 * 임시저장 초안의 저장/이어서 편집 동작 검증. 핵심은 "제목을 바꿔 저장해도 중복이 생기지 않는다".
 * 초안 로직은 network 를 쓰지 않으므로 원격 DataSource 는 호출되지 않는 fake 로 채운다.
 */
class CourseRepositoryImplDraftTest {
    private fun newRepository() =
        CourseRepositoryImpl(
            CourseDetailDataSource(UnusedApiService),
            CourseCreateDataSource(UnusedCreateApiService),
        )

    @Test
    fun `새 코스를 임시저장하면 목록에 1건 생긴다`() =
        runBlocking {
            val repo = newRepository()

            repo.getCourseDraft() // 새 편집 세션 시작
            repo.saveDraft(CourseDraftVO(name = "성수 카페"))

            assertEquals(1, repo.drafts.value.size)
            assertEquals(
                "성수 카페",
                repo.drafts.value
                    .first()
                    .title,
            )
        }

    @Test
    fun `이어서 편집해 제목을 바꿔 저장해도 초안이 중복되지 않는다`() =
        runBlocking {
            val repo = newRepository()

            // 1) 새 초안 "성수 카페" 저장
            repo.getCourseDraft()
            repo.saveDraft(CourseDraftVO(name = "성수 카페"))
            val savedId =
                repo.drafts.value
                    .single()
                    .id

            // 2) 그 초안을 이어서 편집 → 제목만 "연남 데이트"로 바꿔 다시 저장
            repo.beginEditDraft(savedId)
            repo.getCourseDraft()
            repo.saveDraft(CourseDraftVO(name = "연남 데이트"))

            // 같은 초안(id)이 갱신될 뿐, 목록은 여전히 1건이어야 한다.
            assertEquals(1, repo.drafts.value.size)
            assertEquals(
                savedId,
                repo.drafts.value
                    .single()
                    .id,
            )
            assertEquals(
                "연남 데이트",
                repo.drafts.value
                    .single()
                    .title,
            )
        }

    @Test
    fun `서로 다른 새 코스는 각각 저장된다`() =
        runBlocking {
            val repo = newRepository()

            repo.getCourseDraft()
            repo.saveDraft(CourseDraftVO(name = "코스 A"))
            repo.getCourseDraft()
            repo.saveDraft(CourseDraftVO(name = "코스 B"))

            assertEquals(2, repo.drafts.value.size)
            assertEquals(listOf("코스 A", "코스 B"), repo.drafts.value.map { it.title })
        }

    @Test
    fun `같은 세션에서 제목을 바꿔 다시 저장하면 같은 초안을 덮어쓴다`() =
        runBlocking {
            val repo = newRepository()

            repo.getCourseDraft()
            repo.saveDraft(CourseDraftVO(name = "코스 A"))
            val savedId =
                repo.drafts.value
                    .single()
                    .id
            // getCourseDraft() 없이(같은 편집 세션) 제목만 바꿔 다시 저장 → 같은 초안 갱신.
            repo.saveDraft(CourseDraftVO(name = "코스 B"))

            assertEquals(1, repo.drafts.value.size)
            assertEquals(
                savedId,
                repo.drafts.value
                    .single()
                    .id,
            )
            assertEquals(
                "코스 B",
                repo.drafts.value
                    .single()
                    .title,
            )
        }

    private object UnusedApiService : CourseDetailApiService {
        override suspend fun getCourseDetail(
            courseId: Long,
            mock: Boolean,
        ): Response<CourseDetailEnvelope> = error("draft 테스트에서는 호출되지 않아야 한다")
    }

    private object UnusedCreateApiService : CourseCreateApiService {
        override suspend fun createCourse(request: CreateCourseRequest): Response<CreateCourseEnvelope> =
            error("draft 테스트에서는 호출되지 않아야 한다")
    }
}
