package com.chillsam.courmy.main.presentation.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.content.getSystemService
import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.main.domain.deeplink.AppLink

/**
 * [route] 가 가리키는 화면의 공유 링크를 클립보드에 복사한다.
 *
 * Android 13(TIRAMISU) 부터는 시스템이 "복사됨" 미리보기를 직접 띄우므로 토스트를 더하면
 * 안내가 두 번 뜬다. 그 아래 버전에서만 토스트로 알린다.
 */
fun Context.copyShareLink(route: NavRoute) {
    val clipboard = getSystemService<ClipboardManager>()
    if (clipboard == null) {
        Toast.makeText(this, "링크를 복사하지 못했어요", Toast.LENGTH_SHORT).show()
        return
    }
    clipboard.setPrimaryClip(ClipData.newPlainText(CLIP_LABEL, AppLink.of(route)))
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Toast.makeText(this, "링크를 복사했어요", Toast.LENGTH_SHORT).show()
    }
}

/** 클립보드 항목에 붙는 이름. 기기 클립보드 기록에서 무엇인지 알아볼 수 있게 앱 이름을 넣는다. */
private const val CLIP_LABEL = "Courmy 링크"
