package com.chillsam.courmy.common.entity.time

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// 서버 ISO-8601 시각 문자열 → 화면 표시용 상대 시각("3분 전").
//
// 서버는 목록·상세 어디서도 상대 시각 문구를 주지 않고 createdAt(UTC Instant)만 준다.
// 같은 규칙을 코스 상세 미리보기와 후기 전체보기가 함께 써야 해서 한곳에 둔다.
//
// java.time 을 쓰지 않는 이유는 minSdk 가 24 이기 때문이다(java.time 은 API 26+ 이고
// 이 프로젝트는 core library desugaring 을 켜지 않았다 — DraftListDTO 도 같은 이유로
// SimpleDateFormat 을 쓴다).

/**
 * ISO-8601 UTC(`2026-08-16T04:20:00Z`, `2026-08-08T12:14:07.923816Z`) → epoch millis.
 * 읽을 수 없으면 null 이다. 호출부가 시각 표시만 포기하고 본문은 그대로 보여주게 한다.
 *
 * 소수 이하 초는 잘라내고 파싱한다. 서버가 마이크로초(6자리)를 주는데 [SimpleDateFormat] 의 `S` 는
 * 자릿수를 세지 않고 전부 밀리초로 읽어 923816ms(≈15분) 만큼 시각이 밀린다.
 */
fun parseIsoUtcMillis(raw: String?): Long? {
    val trimmed = raw?.takeIf { it.isNotBlank() } ?: return null
    val withoutFraction = trimmed.substringBefore('.').removeSuffix("Z")
    val format =
        SimpleDateFormat(ISO_PATTERN, Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
            isLenient = false
        }
    return runCatching { format.parse(withoutFraction)?.time }.getOrNull()
}

/**
 * epoch millis → "방금 전"·"3분 전"·"2일 전".
 *
 * [nowMillis] 를 인자로 받는 이유는 테스트가 고정 시각으로 검증하기 위해서다.
 * 서버 시계가 앞서 있어 미래로 계산되면 "방금 전" 으로 접는다 — "-1분 전" 을 보여주지 않는다.
 */
fun formatRelativeTime(
    epochMillis: Long,
    nowMillis: Long = System.currentTimeMillis(),
): String {
    val elapsed = nowMillis - epochMillis
    val minutes = elapsed / MINUTE
    val hours = elapsed / HOUR
    val days = elapsed / DAY
    return when {
        elapsed < MINUTE -> "방금 전"
        minutes < MINUTES_PER_HOUR -> "${minutes}분 전"
        hours < HOURS_PER_DAY -> "${hours}시간 전"
        days < DAYS_PER_WEEK -> "${days}일 전"
        days < DAYS_PER_MONTH -> "${days / DAYS_PER_WEEK}주 전"
        days < DAYS_PER_YEAR -> "${days / DAYS_PER_MONTH}개월 전"
        else -> "${days / DAYS_PER_YEAR}년 전"
    }
}

/**
 * ISO-8601 문자열 → 상대 시각 문구. 읽을 수 없으면 빈 문자열이라 화면이 날짜 줄을 비운다.
 * DTO → VO 변환에서 바로 쓰라고 확장으로 둔다.
 */
fun String?.toRelativeTimeText(nowMillis: Long = System.currentTimeMillis()): String =
    parseIsoUtcMillis(this)?.let { formatRelativeTime(it, nowMillis) }.orEmpty()

private const val ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

private const val MINUTE = 60_000L
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR

private const val MINUTES_PER_HOUR = 60
private const val HOURS_PER_DAY = 24
private const val DAYS_PER_WEEK = 7
private const val DAYS_PER_MONTH = 30
private const val DAYS_PER_YEAR = 365
