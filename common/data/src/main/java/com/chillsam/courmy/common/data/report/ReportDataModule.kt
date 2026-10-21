package com.chillsam.courmy.common.data.report

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.common.domain.error.HttpResponseException
import com.chillsam.courmy.common.domain.report.ReportRepository
import com.chillsam.courmy.common.domain.report.ReportSubmissionException
import com.chillsam.courmy.common.entity.report.ReportReason
import com.chillsam.courmy.common.entity.report.ReportResultVO
import com.chillsam.courmy.common.entity.report.ReportTargetType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Singleton

@Serializable
data class ReportRequest(
    val targetType: String,
    val targetId: Long,
    val reason: String,
    val description: String?,
)

@Serializable
data class ReportIdDTO(
    val reportId: Long? = null,
)

@Serializable
data class ReportEnvelope(
    val code: Int? = null,
    val message: String? = null,
    val data: ReportIdDTO? = null,
)

interface ReportApiService {
    @POST("api/v1/reports")
    suspend fun submit(
        @Body request: ReportRequest,
    ): Response<ReportEnvelope>
}

class ReportDataSource(
    private val api: ReportApiService,
) : BaseRemoteDataSource() {
    suspend fun submit(request: ReportRequest): ReportEnvelope =
        try {
            checkResponse(api.submit(request))
        } catch (error: HttpResponseException) {
            val message =
                runCatching {
                    Json
                        .parseToJsonElement(
                            error.cause?.message.orEmpty(),
                        ).jsonObject["message"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                }.getOrNull()?.takeIf { it.isNotBlank() }
            throw ReportSubmissionException(message ?: "신고하지 못했어요. 다시 시도해 주세요.", error)
        }
}

class ReportRepositoryImpl(
    private val source: ReportDataSource,
) : ReportRepository {
    override suspend fun submit(
        targetType: ReportTargetType,
        targetId: Long,
        reason: ReportReason,
        description: String?,
    ): ReportResultVO {
        val response = source.submit(ReportRequest(targetType.name, targetId, reason.name, description))
        val id = response.data?.reportId
        check(response.code == SUCCESS && id != null && id > 0) { response.message ?: "신고 접수를 확인하지 못했어요." }
        return ReportResultVO(id, response.message ?: "신고가 접수되었습니다.")
    }

    private companion object {
        const val SUCCESS = 2000
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ReportDataModule {
    @Provides @Singleton
    fun repository(retrofit: Retrofit): ReportRepository =
        ReportRepositoryImpl(ReportDataSource(retrofit.create<ReportApiService>()))
}
