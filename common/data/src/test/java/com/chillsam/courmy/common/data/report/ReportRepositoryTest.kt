package com.chillsam.courmy.common.data.report

import com.chillsam.courmy.common.domain.report.ReportSubmissionException
import com.chillsam.courmy.common.entity.report.ReportReason
import com.chillsam.courmy.common.entity.report.ReportTargetType
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class ReportRepositoryTest {
    private class FakeApi(
        var response: Response<ReportEnvelope>,
    ) : ReportApiService {
        var request: ReportRequest? = null

        override suspend fun submit(request: ReportRequest): Response<ReportEnvelope> {
            this.request = request
            return response
        }
    }

    @Test
    fun `201과 성공 코드 및 양수 ID를 확인하고 enum 이름을 전송한다`() =
        runBlocking {
            val api = FakeApi(Response.success(201, ReportEnvelope(2000, "신고 접수 완료", ReportIdDTO(42))))
            val result =
                ReportRepositoryImpl(
                    ReportDataSource(api),
                ).submit(ReportTargetType.COURSE_COMMENT, 7, ReportReason.ABUSE, "설명")

            assertEquals(42L, result.id)
            assertEquals("신고 접수 완료", result.message)
            assertEquals(ReportRequest("COURSE_COMMENT", 7, "ABUSE", "설명"), api.request)
        }

    @Test
    fun `성공 HTTP여도 코드나 접수 ID가 없거나 잘못되면 실패한다`() =
        runBlocking {
            val invalid =
                listOf(
                    ReportEnvelope(2000),
                    ReportEnvelope(2000, data = ReportIdDTO()),
                    ReportEnvelope(2000, data = ReportIdDTO(0)),
                    ReportEnvelope(2000, data = ReportIdDTO(-1)),
                    ReportEnvelope(4000, data = ReportIdDTO(42)),
                    ReportEnvelope(data = ReportIdDTO(42)),
                )
            invalid.forEach { envelope ->
                val repository = ReportRepositoryImpl(ReportDataSource(FakeApi(Response.success(envelope))))
                assertTrue(
                    runCatching { repository.submit(ReportTargetType.USER, 1, ReportReason.OTHER, null) }.isFailure,
                )
            }
        }

    @Test
    fun `서버 거절을 성공 결과로 바꾸지 않는다`() =
        runBlocking {
            val api =
                FakeApi(Response.error(409, """{"code":4090,"message":"이미 신고한 대상입니다.","data":null}""".toResponseBody()))
            val repository = ReportRepositoryImpl(ReportDataSource(api))

            val failure =
                runCatching {
                    repository.submit(
                        ReportTargetType.USER,
                        1,
                        ReportReason.SPAM,
                        null,
                    )
                }.exceptionOrNull()

            assertTrue(failure is ReportSubmissionException)
            assertEquals("이미 신고한 대상입니다.", failure?.message)
        }

    @Test
    fun `원시 오류나 안내 없는 JSON은 공통 실패 문구로 변환한다`() =
        runBlocking {
            listOf(
                "<html>upstream error</html>",
                """{"code":5000,"debug":"internal failure"}""",
                """{"message":null}""",
                """{"message":" "}""",
            ).forEach { body ->
                val repository =
                    ReportRepositoryImpl(ReportDataSource(FakeApi(Response.error(500, body.toResponseBody()))))
                val failure =
                    runCatching {
                        repository.submit(
                            ReportTargetType.USER,
                            1,
                            ReportReason.SPAM,
                            null,
                        )
                    }.exceptionOrNull()

                assertTrue(failure is ReportSubmissionException)
                assertEquals("신고하지 못했어요. 다시 시도해 주세요.", failure?.message)
            }
        }
}
