package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CreateCourseResultVO
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

/**
 * 작성 중인 초안을 서버에 코스로 생성한다.
 *
 * 사용자가 고른 사진은 로컬 URI(content://)라 그대로 보낼 수 없어, 프로필 이미지와 같은 방식으로
 * presign 업로드를 거쳐 공개 URL 로 바꾼 뒤 요청에 싣는다.
 *
 * 업로드가 실패해도 코스 생성은 계속한다. 업로드는 서버 S3 설정 등 외부 사정으로 통째로 막힐 수 있는데,
 * 그때 코스 생성까지 못 하게 하면 사용자가 작성한 내용을 아예 저장할 수 없기 때문이다.
 * 대신 [CreateCourseResultVO.imagesUploaded] 로 알려, 화면이 "사진은 빠졌다"고 안내하게 한다.
 */
class CreateCourseUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
        private val mediaRepository: MediaRepository,
    ) {
        /**
         * @param fallbackImageUrl 업로드 실패 시 대신 쓸 이미지 URL. null 이면 해당 사진을 뺀다.
         *   발행 코스는 서버가 커버 이미지와 장소별 사진 1장 이상을 요구하므로, 업로드가 막힌 동안
         *   생성 경로를 확인하려면 대체 URL 이 필요하다(디버그 빌드에서만 넘긴다).
         */
        suspend operator fun invoke(
            draft: CourseDraftVO,
            thumbnailUris: List<String> = emptyList(),
            published: Boolean = true,
            fallbackImageUrl: String? = null,
        ): CreateCourseResultVO {
            val uploader = ImageUploader(fallbackImageUrl)
            val places = draft.places.map { place -> place.uploadPhotos(uploader, fallbackImageUrl) }
            val thumbnailUrl = thumbnailUris.firstOrNull()?.let { uploader.upload(it) } ?: fallbackImageUrl
            val courseId =
                repository.createCourse(
                    draft = draft.copy(places = places),
                    thumbnailUrl = thumbnailUrl,
                    published = published,
                )
            return CreateCourseResultVO(courseId = courseId, imagesUploaded = uploader.allSucceeded)
        }

        /**
         * 사진을 한 장도 고르지 않은 장소는 업로드할 게 없어 빈 목록으로 남는데, 발행 코스는 장소별 사진을
         * 요구하므로 [fallbackImageUrl] 이 있으면 그 자리도 더미로 채운다(디버그 전용 우회).
         */
        private suspend fun CoursePlaceVO.uploadPhotos(
            uploader: ImageUploader,
            fallbackImageUrl: String?,
        ): CoursePlaceVO {
            val uploaded = photoUrls.mapNotNull { uploader.upload(it) }
            val filled = uploaded.ifEmpty { listOfNotNull(fallbackImageUrl) }
            return copy(photoUrls = filled)
        }

        /** 업로드 결과를 모아 두는 헬퍼. 하나라도 실패하면 [allSucceeded] 가 false 가 된다. */
        private inner class ImageUploader(
            private val fallbackImageUrl: String?,
        ) {
            var allSucceeded: Boolean = true
                private set

            /**
             * 이미 http URL 이면 업로드하지 않고 그대로 쓴다(편집 재저장 대비). 실패하면 null.
             *
             * 실패 원인을 여기서 따로 남기지 않는 이유: 요청/응답은 data 레이어의 API 로깅에 이미
             * 남고(`API` 태그), domain 은 순수 Kotlin 이라 안드로이드 로거를 쓸 수 없다.
             */
            suspend fun upload(uri: String): String? =
                if (uri.startsWith("http")) {
                    uri
                } else {
                    runCatching { mediaRepository.uploadImage(uri) }
                        .getOrElse { e ->
                            if (e is CancellationException) throw e
                            allSucceeded = false
                            fallbackImageUrl
                        }
                }
        }
    }
