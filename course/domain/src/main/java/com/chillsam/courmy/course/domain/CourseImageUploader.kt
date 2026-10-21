package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.common.domain.media.UploadPurpose
import com.chillsam.courmy.course.entity.CoursePlaceVO
import kotlinx.coroutines.CancellationException

/**
 * 코스 사진(장소 사진·대표 사진)을 presign 업로드해 공개 URL 로 바꿔 주는 헬퍼.
 *
 * 코스 저장([CreateCourseUseCase])과 임시저장([SaveDraftUseCase])이 같은 규칙을 써야 해서 한곳에 둔다
 * — 한쪽만 업로드를 빼먹으면 로컬 URI(content://)가 그대로 서버에 저장돼 아무 데서도 안 보이는 사진이 된다.
 *
 * 업로드 실패는 예외로 올리지 않고 [allSucceeded] 로 알린다. 업로드는 서버 S3 설정 등 외부 사정으로
 * 통째로 막힐 수 있는데, 그때 저장까지 못 하게 하면 사용자가 작성한 내용을 아예 남길 수 없기 때문이다.
 */
internal class CourseImageUploader(
    private val mediaRepository: MediaRepository,
) {
    var allSucceeded: Boolean = true
        private set

    /**
     * 이미 http URL 이면 업로드하지 않고 그대로 쓴다(이어서 작성·편집 재저장 대비). 실패하면 null.
     *
     * 실패 원인을 여기서 따로 남기지 않는 이유: 요청/응답은 data 레이어의 API 로깅에 이미
     * 남고(`API` 태그), domain 은 순수 Kotlin 이라 안드로이드 로거를 쓸 수 없다.
     */
    suspend fun upload(
        uri: String,
        purpose: UploadPurpose,
    ): String? =
        if (uri.startsWith("http")) {
            uri
        } else {
            runCatching { mediaRepository.uploadImage(uri, purpose) }
                .getOrElse { e ->
                    if (e is CancellationException) throw e
                    allSucceeded = false
                    null
                }
        }

    /** 장소에 담긴 사진을 모두 올린 사본을 돌려준다. 올리지 못한 사진은 빠진다. */
    suspend fun uploadPhotos(place: CoursePlaceVO): CoursePlaceVO =
        place.copy(photoUrls = place.photoUrls.mapNotNull { upload(it, UploadPurpose.PLACE) })
}
