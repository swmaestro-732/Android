package com.chillsam.courmy.course.domain

import com.chillsam.courmy.common.domain.telemetry.AppFlow
import com.chillsam.courmy.common.domain.telemetry.Telemetry
import com.chillsam.courmy.common.domain.telemetry.track
import javax.inject.Inject

/**
 * 장소 저장/취소 UseCase. 장소 상세 시트·저장함 장소 탭이 함께 쓴다.
 *
 * [SetCourseSavedUseCase] 와 같은 pass-through 다 — 성공/실패만 가르면 되고(응답 data 없음),
 * 낙관적 토글·되돌리기는 화면 상태의 몫이라 여기서 다루지 않는다.
 *
 * 저장(saved=true)만 [AppFlow.PlaceSave] 로 집계한다 — 해제는 참여 전환이 아니라 기록하지 않는다.
 */
class SetPlaceSavedUseCase
    @Inject
    constructor(
        private val repository: PlaceSaveRepository,
        private val telemetry: Telemetry,
    ) {
        suspend operator fun invoke(
            placeId: Long,
            saved: Boolean,
        ) {
            require(placeId > 0) { "저장할 장소 id 가 없습니다." }
            if (saved) {
                telemetry.track(AppFlow.PlaceSave) { repository.setSaved(placeId, saved) }
            } else {
                repository.setSaved(placeId, saved)
            }
        }
    }
