package com.chillsam.courmy.main.presentation.map.component

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.PlaceThumbnail
import com.chillsam.courmy.common.presentation.component.placeIcon
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemTheme
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 로그인·서버 없이 실제 핀 렌더러를 확인하는 debug 전용 화면. 운영 지도 데이터가 아니다. */
class PlacePinPreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 상세값 + 폴백 핀(null)을 10개씩 나눈 마지막 페이지까지 허용한다.
        val page = intent.getIntExtra("page", 0).coerceIn(0, PlaceDetailCategoryVO.entries.size / 10)
        setContent {
            DesignSystemTheme {
                PinPreviewPage(page)
            }
        }
    }
}

@Composable
private fun PinPreviewPage(page: Int) {
    val color = DesignSystemThemeImpl.designSystemColor
    val type = DesignSystemThemeImpl.typeScale
    val entries = PlaceDetailCategoryVO.entries.toList() + null
    Column(
        Modifier
            .fillMaxSize()
            .background(
                color.bgDefaultLevel1,
            ).windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(20.dp),
    ) {
        DsText(
            text = "장소 공통 아이콘 ${page + 1}/${PlaceDetailCategoryVO.entries.size / 10 + 1}",
            style = type.textStrongM,
            color = color.contentDefaultLevel0,
        )
        DsText(text = "예시 · 일반 / 선택 / 썸네일 · 2026-11-03", style = type.textRegularM, color = color.contentDefaultLevel0)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 16.dp),
        ) {
            items(entries.drop(page * 10).take(10)) { detail ->
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PinPreview(detail, selected = false)
                        PinPreview(detail, selected = true)
                        PlaceThumbnail(
                            url = null,
                            category = "ETC",
                            detailCategory = detail,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                    DsText(
                        text = detail?.label ?: "기본 핀",
                        style = type.textRegularM,
                        color = color.contentDefaultLevel0,
                    )
                }
            }
        }
    }
}

@Composable
private fun PinPreview(
    detail: PlaceDetailCategoryVO?,
    selected: Boolean,
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val color = DesignSystemThemeImpl.designSystemColor
    val bitmap =
        remember(detail, selected, density, color, context) {
            val icon = requireNotNull(ContextCompat.getDrawable(context, placeIcon("ETC", detail))).mutate()
            icon.setTint(color.contentAccent.toArgb())
            placePinBitmap(
                density,
                PlacePinStyle(
                    fillArgb = color.bgAccentSubtle.toArgb(),
                    strokeArgb = if (selected) color.contentAccent.toArgb() else color.bgDefaultLevel1.toArgb(),
                    labelBackgroundArgb = color.contentDefaultLevel0.toArgb(),
                    labelTextArgb = color.contentOnAccent.toArgb(),
                    categoryIcon = icon,
                ),
                selected,
            ).asImageBitmap()
        }
    Image(bitmap = bitmap, contentDescription = if (selected) "선택된 핀" else "일반 핀")
}
