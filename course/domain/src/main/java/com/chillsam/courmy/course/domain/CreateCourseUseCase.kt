package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.common.domain.media.UploadPurpose
import com.chillsam.courmy.common.domain.telemetry.AppFailure
import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CreateCourseResultVO
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
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke(
            draft: CourseDraftVO,
            thumbnailUris: List<String> = emptyList(),
            published: Boolean = true,
        ): CreateCourseResultVO =
            telemetry.track(AppFlow.CourseCreate) {
                val uploader = CourseImageUploader(mediaRepository)
                val places = draft.places.map { place -> uploader.uploadPhotos(place) }
                val thumbnailUrl = thumbnailUris.firstOrNull()?.let { uploader.upload(it, UploadPurpose.COURSE) }
                val courseId =
                    repository.createCourse(
                        draft = draft.copy(places = places),
                        thumbnailUrl = thumbnailUrl,
                        published = published,
                    )
                // 코스는 만들어졌지만 사진이 빠진 상태. 화면에는 안내가 나가도 운영에서는 성공으로만 보이므로
                // 따로 남긴다 — 업로드가 통째로 막힌 상황을 이걸로 알아챌 수 있다.
                if (!uploader.allSucceeded) {
                    telemetry.recordFailure(
                        AppFailure(area = AppFlow.CourseCreate.eventName, kind = "image_upload"),
                    )
                }
                CreateCourseResultVO(courseId = courseId, imagesUploaded = uploader.allSucceeded)
            }
    }
