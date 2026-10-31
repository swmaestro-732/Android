package com.chillsam.courmy.common.entity.format

/**
 * 통계 숫자 표시 포맷(Figma FS-15: 1400 → "1.4k", 312 → "312").
 * 소수점 첫째 자리에서 버리며, 정확한 값보다 자릿수 안정성을 우선한다.
 */
fun formatCount(value: Int): String =
    when {
        value >= 1_000_000 -> "${trimTrailingZero(value / 1_000_000.0)}m"
        value >= 1_000 -> "${trimTrailingZero(value / 1_000.0)}k"
        else -> value.toString()
    }

private fun trimTrailingZero(value: Double): String {
    val truncated = (value * 10).toInt() / 10.0
    return if (truncated % 1.0 == 0.0) truncated.toInt().toString() else truncated.toString()
}
