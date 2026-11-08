package com.chillsam.courmy.main.presentation.aichat

import android.content.Context
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

/** 앱이 그리는 REGION 지역 칩에서 "내 근처"는 항상 맨 앞에 둔다(CHIP NEAR_ME). */
internal const val NEAR_ME_LABEL = "내 근처"

/** "내 근처" 칩이 서버로 보낼 값(명세 고정). */
internal const val NEAR_ME_VALUE = "NEAR_ME"

/**
 * 현재 좌표를 역지오코딩해 근처 지역 이름(동·구)을 뽑는다. 프론트에서 가볍게 쓰는 [Geocoder] 기반이라
 * 기기·네트워크 상황에 따라 비어 올 수 있고, 그때는 "내 근처" 칩만 두고 직접 입력으로 유도한다.
 *
 * 동(subLocality)과 구/시(subAdminArea·locality)를 최대 2개까지 돌려준다.
 */
internal suspend fun reverseGeocodeRegionNames(
    context: Context,
    latitude: Double,
    longitude: Double,
): List<String> =
    withContext(Dispatchers.IO) {
        if (!Geocoder.isPresent()) return@withContext emptyList()
        runCatching {
            @Suppress("DEPRECATION")
            val address =
                Geocoder(context, Locale.KOREA)
                    .getFromLocation(latitude, longitude, 1)
                    ?.firstOrNull()
                    ?: return@runCatching emptyList<String>()
            listOfNotNull(
                address.subLocality?.takeIf { it.isNotBlank() },
                (address.subAdminArea ?: address.locality)?.takeIf { it.isNotBlank() },
            ).distinct()
        }.getOrDefault(emptyList())
    }
