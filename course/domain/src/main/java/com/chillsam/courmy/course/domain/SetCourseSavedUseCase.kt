package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import javax.inject.Inject

/**
 * 코스 저장/취소 토글 UseCase.
 *
 * 폴더 지정 저장("폴더 선택 시트")은 아직 화면이 없어 미분류로 저장한다
 * (서버가 folderId=null 을 허용한다). 폴더 UI 가 생기면 인자를 추가한다. [wiki-needed]
 */
class SetCourseSavedUseCase
    @Inject
    constructor(
        private val repository: CourseSaveRepository,
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke(
            courseId: Long,
            saved: Boolean,
        ) = telemetry.track(AppFlow.CourseSave) { repository.setSaved(courseId, saved) }
    }
