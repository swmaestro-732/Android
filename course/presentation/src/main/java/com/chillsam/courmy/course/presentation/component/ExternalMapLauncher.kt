package com.chillsam.courmy.course.presentation.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri

private const val TAG = "ExternalMap"

/**
 * 외부 지도로 도보 길안내를 넘긴다. 따라가기 경로를 앱에서 못 그릴 때(Tmap 503·도보 불가)의 폴백이다.
 *
 * 네이버 지도 앱이 있으면 도보 길찾기(`nmap://route/walk`)로 열고, 없으면 구글 지도 웹 도보 길찾기로 연다.
 * 좌표만으로 안내하므로 서버 경로가 없어도 동작한다.
 */
fun Context.openExternalWalkingRoute(
    fromLatitude: Double,
    fromLongitude: Double,
    fromName: String,
    toLatitude: Double,
    toLongitude: Double,
    toName: String,
) {
    val appName = packageName
    val naver =
        (
            "nmap://route/walk?slat=$fromLatitude&slng=$fromLongitude&sname=${Uri.encode(fromName)}" +
                "&dlat=$toLatitude&dlng=$toLongitude&dname=${Uri.encode(toName)}&appname=$appName"
        ).toUri()
    if (startExternal(naver)) return

    // 네이버 지도 미설치 → 구글 지도 웹 도보 길찾기(브라우저·지도 앱 모두 연다).
    val google =
        (
            "https://www.google.com/maps/dir/?api=1" +
                "&origin=$fromLatitude,$fromLongitude&destination=$toLatitude,$toLongitude&travelmode=walking"
        ).toUri()
    if (!startExternal(google)) {
        Log.w(TAG, "외부 지도 길안내를 열 수 있는 앱이 없습니다.")
    }
}

private fun Context.startExternal(uri: Uri): Boolean =
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (e: ActivityNotFoundException) {
        Log.d(TAG, "길안내를 처리할 앱 없음: $uri", e)
        false
    }
