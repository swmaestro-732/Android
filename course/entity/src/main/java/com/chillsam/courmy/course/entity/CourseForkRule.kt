package com.chillsam.courmy.course.entity

/**
 * 코스 포크의 **원본 장소 유지 규칙**.
 *
 * 서버(`POST /api/v1/courses/{courseId}/forks`)가 같은 규칙으로 검증하고, 어기면
 * `400 FORK_PLACES_NOT_KEPT(4004)` 를 돌려준다. 포크가 원본과 전혀 다른 코스가 되어 버리는 것을
 * 막는 규칙이라, 앱에서도 같은 값을 계산해 **삭제 자체를 선제 차단**한다 — 사용자가 장소를 다 지우고
 * 사진까지 올린 뒤 저장 직전에야 거절당하지 않게 하기 위해서다.
 *
 * 서버 규칙(`Course.requiredKeptPlaceCount`)을 옮겨 둔 것이므로, 서버가 규칙을 바꾸면 여기도 함께 바꾼다.
 */
object CourseForkRule {
    /** 원본 장소를 **전부** 유지해야 하는 원본 크기의 상한. 이보다 크면 절반 이상만 유지하면 된다. */
    private const val FULL_KEEP_LIMIT = 4

    /**
     * 포크한 코스가 원본에서 **그대로 담고 있어야 하는 최소 장소 수**.
     *
     * - 원본이 4곳 이하: 전부 유지(한 곳도 뺄 수 없다)
     * - 원본이 5곳 이상: 절반 이상 유지(올림 — 5곳이면 3곳, 6곳이면 3곳, 7곳이면 4곳)
     *
     * 제한하는 것은 **원본 장소를 빼는 것**뿐이고, 장소 추가는 언제나 자유롭다(코스 최대 10곳).
     */
    fun requiredKeptPlaceCount(originPlaceCount: Int): Int =
        if (originPlaceCount <= FULL_KEEP_LIMIT) {
            originPlaceCount
        } else {
            (originPlaceCount + 1) / 2
        }
}
