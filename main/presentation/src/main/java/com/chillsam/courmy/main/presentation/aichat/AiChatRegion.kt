package com.chillsam.courmy.main.presentation.aichat

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.cos

/** 현재점과 약 1.5km 주변 네 지점에서 서로 다른 구·시·군를 최대 3곳 찾는다. */
internal suspend fun reverseGeocodeRegionNames(
    context: Context,
    latitude: Double,
    longitude: Double,
): List<String> =
    withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext emptyList()
        val geocoder = Geocoder(context, Locale.KOREA)
        nearbyAiChatRegionNames(latitude, longitude) { lat, lng ->
            @Suppress("DEPRECATION")
            geocoder.getFromLocation(lat, lng, 1)?.firstOrNull()?.toAiChatRegionName()
        }
    }

/** 구·군을 우선하고 없으면 시를 사용한다. 동·읍·면과 도로명은 제외한다. */
internal fun Address.toAiChatRegionName(): String? {
    val names =
        listOfNotNull(subLocality, subAdminArea, locality)
            .map(String::trim)
            .filter(String::isNotEmpty)
            .distinct()
    return names.firstOrNull { it.endsWith("구") || it.endsWith("군") }
        ?: names.firstOrNull { it.endsWith("시") }
}

internal suspend fun nearbyAiChatRegionNames(
    latitude: Double,
    longitude: Double,
    lookup: suspend (Double, Double) -> String?,
): List<String> {
    if (!latitude.isFinite() || !longitude.isFinite() || latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
        return emptyList()
    }
    // 한국의 동네 단위 탐색용 근사 오프셋. 행정 경계나 거리순 주변 지역 목록을 뜻하지 않는다.
    val latitudeOffset = 1.5 / 111.32
    val longitudeOffset = latitudeOffset / cos(Math.toRadians(latitude)).coerceAtLeast(0.01)
    val points =
        listOf(
            latitude to longitude,
            latitude + latitudeOffset to longitude,
            latitude to longitude + longitudeOffset,
            latitude - latitudeOffset to longitude,
            latitude to longitude - longitudeOffset,
        )
    val names = linkedSetOf<String>()
    for ((lat, lng) in points) {
        currentCoroutineContext().ensureActive()
        if (lat !in -90.0..90.0 || lng !in -180.0..180.0) continue
        val name =
            try {
                lookup(lat, lng)?.trim()?.takeIf(String::isNotEmpty)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                null
            }
        name?.let(names::add)
        if (names.size == 3) break
    }
    return names.toList()
}
