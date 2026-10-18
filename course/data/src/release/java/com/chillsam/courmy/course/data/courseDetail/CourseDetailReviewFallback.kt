package com.chillsam.courmy.course.data.courseDetail

import com.chillsam.courmy.course.data.courseDetail.dto.CourseDetailEnvelope

/**
 * 릴리스용 no-op — 응답을 손대지 않는다.
 *
 * 디버그 빌드에는 리뷰 자리를 예시로 채우는 구현이 있다(`src/debug` 의 같은 이름 파일에 배경 설명).
 * 예시 데이터가 릴리스 산출물에 **컴파일조차 되지 않게** 하려고 소스셋으로 갈라 뒀다.
 * 백엔드가 `reviewSummary` 를 내려주기 시작하면 이 파일도 debug 쪽과 함께 지운다.
 */
internal object CourseDetailReviewFallback {
    fun applyTo(envelope: CourseDetailEnvelope): CourseDetailEnvelope = envelope
}
