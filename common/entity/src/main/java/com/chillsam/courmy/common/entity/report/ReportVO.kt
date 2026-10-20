package com.chillsam.courmy.common.entity.report

enum class ReportTargetType { COURSE, USER, COURSE_COMMENT }

enum class ReportReason(
    val label: String,
) {
    SPAM("스팸·광고"),
    ABUSE("욕설·비방"),
    OBSCENE("음란한 내용"),
    PRIVACY("개인정보 침해"),
    OTHER("기타"),
}

data class ReportResultVO(
    val id: Long,
    val message: String,
)
