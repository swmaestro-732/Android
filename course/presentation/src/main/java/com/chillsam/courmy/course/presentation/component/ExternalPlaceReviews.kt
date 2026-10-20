package com.chillsam.courmy.course.presentation.component

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CourseReviewListVO

/** 실제 집계가 확인된 소수 후기 장소만 외부 검색을 제안한다. */
@Composable
internal fun ExternalPlaceReviews(
    name: String,
    address: String,
    reviews: CourseReviewListVO?,
) {
    if (reviews?.shouldSuggestExternalReviews != true || name.isBlank()) return
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().background(color.bgAccentSubtle, RoundedCornerShape(16.dp)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DsText(
            text = "다른 방문자의 후기도 궁금하다면",
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
        )
        DsText(
            text = "네이버 지도에서 장소를 검색하고 후기를 확인해 보세요.",
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
            maxLines = Int.MAX_VALUE,
        )
        DsButton(
            text = "네이버 지도에서 찾아보기",
            variant = DsButtonVariant.Secondary,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val query = "$name $address".trim()
                val appUri =
                    Uri
                        .parse("nmap://search")
                        .buildUpon()
                        .appendQueryParameter("query", query)
                        .appendQueryParameter("appname", context.packageName)
                        .build()
                val webUri =
                    Uri
                        .parse("https://map.naver.com/p/search")
                        .buildUpon()
                        .appendPath(query)
                        .build()
                if (!context.openReviewSearch(appUri) && !context.openReviewSearch(webUri)) {
                    Toast.makeText(context, "네이버 지도를 열 수 없어요", Toast.LENGTH_SHORT).show()
                }
            },
        )
    }
}

private fun Context.openReviewSearch(uri: Uri): Boolean =
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (_: ActivityNotFoundException) {
        false
    }
