package com.chillsam.courmy.common.entity.time

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

// 서버 시각은 언어와 무관한 epoch millis·상대 단위로 전달하고 화면에서 문구를 정한다.

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

/** 언어와 무관한 상대 시각 단위. 문구와 복수형은 presentation 리소스가 정한다. */
enum class RelativeTimeUnit { JUST_NOW, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR }

data class RelativeTime(
    val unit: RelativeTimeUnit,
    val count: Long,
)

/** 미래 시각은 방금 전으로 처리하고 기존 7일·30일·365일 경계를 유지한다. */
fun relativeTime(
    epochMillis: Long,
    nowMillis: Long = System.currentTimeMillis(),
): RelativeTime {
    val elapsed = (nowMillis - epochMillis).coerceAtLeast(0)
    val minutes = elapsed / MINUTE
    val hours = elapsed / HOUR
    val days = elapsed / DAY
    return when {
        elapsed < MINUTE -> RelativeTime(RelativeTimeUnit.JUST_NOW, 0)
        minutes < MINUTES_PER_HOUR -> RelativeTime(RelativeTimeUnit.MINUTE, minutes)
        hours < HOURS_PER_DAY -> RelativeTime(RelativeTimeUnit.HOUR, hours)
        days < DAYS_PER_WEEK -> RelativeTime(RelativeTimeUnit.DAY, days)
        days < DAYS_PER_MONTH -> RelativeTime(RelativeTimeUnit.WEEK, days / DAYS_PER_WEEK)
        days < DAYS_PER_YEAR -> RelativeTime(RelativeTimeUnit.MONTH, days / DAYS_PER_MONTH)
        else -> RelativeTime(RelativeTimeUnit.YEAR, days / DAYS_PER_YEAR)
    }
}

private const val ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

private const val MINUTE = 60_000L
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR

private const val MINUTES_PER_HOUR = 60
private const val HOURS_PER_DAY = 24
private const val DAYS_PER_WEEK = 7
private const val DAYS_PER_MONTH = 30
private const val DAYS_PER_YEAR = 365
