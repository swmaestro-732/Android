package com.chillsam.courmy.course.entity

import kotlinx.serialization.Serializable

/**
 * 코스를 누구와 함께했는지. 코스 만들기 "코스 설정" 단계의 선택 입력이다.
 *
 * 코스 태그 12종(`CourseTagVO`)과는 **별개 체계**다 — 태그는 코스 성격(데이트·맛집 …)이고
 * 이것은 동행 구성이다. 태그에 SOLO·FAMILY 가 겹쳐 보여도 서로 변환하지 않는다.
 *
 * 서버 생성 요청 `companion` 필드에는 enum 이름(대문자, [name])을 보내고,
 * UI 선택·캐시는 소문자 [code] 를 쓴다(다른 enum 계약과 동일).
 */
@Serializable
enum class CourseCompanionVO(
    val code: String,
    val label: String,
) {
    SOLO("solo", "혼자"),
    COUPLE("couple", "연인"),
    FAMILY("family", "가족"),
    FRIEND("friend", "친구"),
    ;

    companion object {
        /** 소문자 코드와 서버의 대문자 enum 이름을 모두 읽는다. 모르는 값은 null(선택 안 함)로 둔다. */
        fun fromCode(code: String?): CourseCompanionVO? =
            entries.firstOrNull { it.code.equals(code?.trim(), ignoreCase = true) }
    }
}
