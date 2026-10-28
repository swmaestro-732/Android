package com.chillsam.courmy.main.presentation.component

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

/**
 * 위치 권한(정밀 또는 대략)이 허용돼 있는지(SCRUM-634).
 *
 * 지도 추적 모드 초기값·홈 근처 저장 장소 안내처럼 "권한 유무로 첫 상태를 가르는" 곳이 공유한다.
 * 권한 요청 자체는 각 화면의 런처(FusedLocationSource 포함)가 맡고, 여기서는 상태만 읽는다.
 */
internal fun Context.hasLocationPermission(): Boolean =
    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
