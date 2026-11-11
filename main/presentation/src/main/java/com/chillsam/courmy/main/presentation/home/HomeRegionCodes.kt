package com.chillsam.courmy.main.presentation.home

// The bundled map combines Jeonnam/Gwangju under 12; the feed API separates them with 46/29.
private val gwangjuDistricts = setOf("12210", "12240", "12270", "12300", "12330")

internal fun homeRegionCode(tileCode: String): String =
    when (tileCode) {
        "12" -> "46"
        "47U" -> "47940"
        else -> tileCode
    }

internal fun isHomeSubRegion(
    region: String,
    district: String,
): Boolean =
    when (region) {
        "29" -> district in gwangjuDistricts
        "46" -> district.startsWith("12") && district !in gwangjuDistricts
        else -> district.startsWith(region)
    }
