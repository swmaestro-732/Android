package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.media.MediaRepository
import com.chillsam.courmy.common.domain.media.UploadPurpose
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CreateCourseResultVO
import javax.inject.Inject

/**
 * 작성 중인 코스를 서버에 임시저장한다.
 *
 * 사진은 코스 저장과 같은 규칙으로 presign 업로드를 거쳐 공개 URL 로 바꾼다. 로컬 URI 를 그대로
 * 보내면 이어서 작성할 때 아무 데서도 열리지 않는 주소만 남는다.
 *
 * [courseId] 는 이 작성 세션이 이미 만들어 둔 초안의 id 다. 넘기면 그 초안을 갱신하고, null 이면
 * 새로 만든다 — 매번 새로 만들면 임시저장을 누른 횟수만큼 같은 코스가 목록에 쌓인다.
 */
class SaveDraftUseCase
    @Inject
    constructor(
        private val repository: CourseRepository,
        private val mediaRepository: MediaRepository,
    ) {
        suspend operator fun invoke(
            draft: CourseDraftVO,
            courseId: Long?,
        ): CreateCourseResultVO {
            val uploader = CourseImageUploader(mediaRepository)
            val places = draft.places.map { place -> uploader.uploadPhotos(place) }
            val thumbnailUrl =
                draft.thumbnailUrl
                    .takeIf { it.isNotBlank() }
                    ?.let { uploader.upload(it, UploadPurpose.COURSE) }
            val savedId =
                repository.saveDraft(
                    draft = draft.copy(places = places, thumbnailUrl = thumbnailUrl.orEmpty()),
                    courseId = courseId,
                )
            return CreateCourseResultVO(courseId = savedId, imagesUploaded = uploader.allSucceeded)
        }
    }
