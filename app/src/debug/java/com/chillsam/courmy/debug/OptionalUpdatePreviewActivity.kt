package com.chillsam.courmy.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.main.presentation.update.OptionalAppUpdateDialog

/** 디버그 APK에서 기존 화면 위에 실제 선택 업데이트 모달만 표시한다. */
class OptionalUpdatePreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DesignSystemTheme {
                OptionalAppUpdateDialog(onAccept = { finish() }, onDismiss = { finish() })
            }
        }
    }
}
