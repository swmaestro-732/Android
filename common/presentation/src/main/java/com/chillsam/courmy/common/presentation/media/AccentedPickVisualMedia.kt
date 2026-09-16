package com.chillsam.courmy.common.presentation.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 시스템 사진 선택 화면을 앱 강조색으로 물들여 여는 계약.
 *
 * 사진 선택 화면은 MediaProvider 가 **다른 프로세스에서 그리는 시스템 UI** 라 앱 테마·Compose 색이
 * 닿지 않는다. 앱이 건드릴 수 있는 건 강조색 하나뿐이며(선택 체크·확인 버튼 등),
 * 배경의 밝기/어둡기는 앱이 아니라 기기의 다크 모드 설정을 따른다.
 *
 * 강조색 지정은 Android 15(API 35)에서 들어왔고, Android 11+ 기기라도 MediaProvider 가
 * R 확장 12 이상이면 쓸 수 있다. 그보다 낮으면 아무 일도 하지 않고 기본 색으로 열린다.
 */
class AccentedPickVisualMedia(
    private val accent: Color,
) : ActivityResultContracts.PickVisualMedia() {
    override fun createIntent(
        context: Context,
        input: PickVisualMediaRequest,
    ): Intent = super.createIntent(context, input).withAccentColor(accent)
}

/** 여러 장 고르는 쪽도 같은 강조색으로 연다. 한 장짜리와 색이 갈리면 더 어색하다. */
class AccentedPickMultipleVisualMedia(
    maxItems: Int,
    private val accent: Color,
) : ActivityResultContracts.PickMultipleVisualMedia(maxItems) {
    override fun createIntent(
        context: Context,
        input: PickVisualMediaRequest,
    ): Intent = super.createIntent(context, input).withAccentColor(accent)
}

/** `EXTRA_PICK_IMAGES_ACCENT_COLOR` 가 들어온 MediaProvider 확장 버전. */
private const val REQUIRED_R_EXTENSION = 12

private fun Intent.withAccentColor(accent: Color): Intent =
    apply {
        // 지원하지 않는 기기에 실어도 무시될 뿐이지만, 의도를 분명히 하려고 걸러 넣는다.
        if (isAccentColorSupported()) {
            putExtra(MediaStore.EXTRA_PICK_IMAGES_ACCENT_COLOR, accent.toArgb().toLong())
        }
    }

private fun isAccentColorSupported(): Boolean =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM -> {
            true
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= REQUIRED_R_EXTENSION
        }

        else -> {
            false
        }
    }

/** 사진 한 장 고르는 런처. 앱 강조색은 여기서 읽어 넣으므로 호출부는 테마를 몰라도 된다. */
@Composable
fun rememberAccentedImagePicker(onPicked: (Uri?) -> Unit): ActivityResultLauncher<PickVisualMediaRequest> {
    val accent = DesignSystemThemeImpl.designSystemColor.bgAccent
    // 재구성마다 새 계약을 만들면 런처가 다시 등록되므로 색이 바뀔 때만 새로 만든다.
    val contract = remember(accent) { AccentedPickVisualMedia(accent) }
    return rememberLauncherForActivityResult(contract, onPicked)
}

/** 사진 여러 장 고르는 런처. [maxItems] 는 2 이상이어야 한다(시스템 제약). */
@Composable
fun rememberAccentedImagesPicker(
    maxItems: Int,
    onPicked: (List<Uri>) -> Unit,
): ActivityResultLauncher<PickVisualMediaRequest> {
    val accent = DesignSystemThemeImpl.designSystemColor.bgAccent
    val contract = remember(accent, maxItems) { AccentedPickMultipleVisualMedia(maxItems, accent) }
    return rememberLauncherForActivityResult(contract, onPicked)
}
