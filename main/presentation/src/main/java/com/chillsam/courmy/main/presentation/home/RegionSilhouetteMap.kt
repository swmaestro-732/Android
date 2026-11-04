package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.color.RegionColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.entity.area.AreaMapVO
import com.chillsam.courmy.main.presentation.my.RegionShape
import com.chillsam.courmy.main.presentation.my.mainMapRegion

/**
 * 홈 지역별 둘러보기의 **부드러운 실루엣 지도**.
 *
 * 앱에 포함된 실제 행정구역 경계([AreaMapVO], 시·도)를 권역 색으로 채우고, 지역 사이를 흰 선으로 떼어
 * 말랑한 타일처럼 보이게 한다. 도(道)는 이름 텍스트를, 광역시·특별시는 흰 알약을 중심에 얹는다.
 * 지역을 누르면 그 지역 코스 목록([RecommendedCoursesPage])으로 보낸다(코드 = 시·도 법정동 2자리).
 */
@Composable
internal fun RegionSilhouetteMap(
    regions: List<AreaMapVO>,
    onRegionClick: (code: String, name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val labelStyle = DesignSystemThemeImpl.typeScale.textStrongS.copy(color = color.contentDefaultLevel0)
    val pillStyle = DesignSystemThemeImpl.typeScale.textExtraXS.copy(color = color.contentDefaultLevel0)
    val textMeasurer = rememberTextMeasurer()

    // 시·도만, 섬(도서) 조각은 떼고 본토 폴리곤만 그린다.
    val shapes =
        remember(regions) {
            regions
                .filter { it.area.level == AreaLevel.SIDO }
                .mapNotNull { region -> region.mainMapRegion()?.let { RegionShape(it) } }
        }
    val bounds = remember(shapes) { shapes.map { it.bounds }.combine() }

    Canvas(
        modifier =
            modifier.pointerInput(shapes, bounds) {
                detectTapGestures { pos ->
                    val (scale, translation) =
                        fitTransform(size.width.toFloat(), size.height.toFloat(), bounds, 14.dp.toPx())
                    val projected = (pos - translation) / scale
                    shapes.lastOrNull { it.contains(projected) }?.let {
                        val code = it.region.area.code
                        onRegionClick(code, regionLabels[code.take(2)] ?: it.region.area.shortName)
                    }
                }
            },
    ) {
        val (scale, translation) = fitTransform(size.width, size.height, bounds, 14.dp.toPx())
        val separator = 2.5.dp.toPx()

        withTransform({
            translate(translation.x, translation.y)
            scale(scale, scale, Offset.Zero)
        }) {
            shapes.forEach { drawPath(it.path, regionFill(it.region.area.code)) }
            // 지역 사이를 흰 선으로 떼어 말랑한 타일 느낌을 준다(국경 바깥쪽은 밝은 배경이라 보이지 않음).
            shapes.forEach {
                drawPath(it.path, Color.White, style = Stroke(width = separator / scale, join = StrokeJoin.Round))
            }
        }

        // 라벨은 화면 좌표로 그린다. 도는 텍스트, 광역시·특별시는 흰 알약.
        // 중심이 겹치는 몇 곳은 지역 넓이 기준 미세 오프셋으로 떼어 읽히게 한다.
        val nudgeUnit = 1.dp.toPx()
        shapes.forEach { shape ->
            val code2 =
                shape.region.area.code
                    .take(2)
            val label = regionLabels[code2] ?: shape.region.area.shortName
            val nudge = labelNudges[code2] ?: Offset.Zero
            val center =
                Offset(
                    shape.bounds.center.x * scale + translation.x + nudge.x * nudgeUnit,
                    shape.bounds.center.y * scale + translation.y + nudge.y * nudgeUnit,
                )
            if (code2 in metropolitanCodes) {
                drawPill(textMeasurer, label, center, pillStyle, color.bgDefaultLevel1, color.borderDefaultLevel0)
            } else {
                drawCenteredText(textMeasurer, label, center, labelStyle)
            }
        }
    }
}

/** [bounds] 를 [padding] 여백을 두고 [width]×[height] 안에 맞추는 (확대율, 평행이동). */
private fun fitTransform(
    width: Float,
    height: Float,
    bounds: Rect,
    padding: Float,
): Pair<Float, Offset> {
    val scale =
        minOf(
            (width - padding * 2) / bounds.width,
            (height - padding * 2) / bounds.height,
        ).coerceAtLeast(0.01f)
    val translation =
        Offset(
            width / 2 - bounds.center.x * scale,
            height / 2 - bounds.center.y * scale,
        )
    return scale to translation
}

private fun DrawScope.drawCenteredText(
    measurer: TextMeasurer,
    text: String,
    center: Offset,
    style: TextStyle,
) {
    val measured = measurer.measure(text, style)
    drawText(measured, topLeft = Offset(center.x - measured.size.width / 2, center.y - measured.size.height / 2))
}

private fun DrawScope.drawPill(
    measurer: TextMeasurer,
    text: String,
    center: Offset,
    style: TextStyle,
    background: Color,
    border: Color,
) {
    val measured = measurer.measure(text, style)
    val padH = 8.dp.toPx()
    val padV = 4.dp.toPx()
    val w = measured.size.width + padH * 2
    val h = measured.size.height + padV * 2
    val topLeft = Offset(center.x - w / 2, center.y - h / 2)
    val radius = CornerRadius(h / 2)
    drawRoundRect(background, topLeft = topLeft, size = Size(w, h), cornerRadius = radius)
    drawRoundRect(border, topLeft = topLeft, size = Size(w, h), cornerRadius = radius, style = Stroke(1.dp.toPx()))
    drawText(measured, topLeft = Offset(center.x - measured.size.width / 2, center.y - measured.size.height / 2))
}

private fun List<Rect>.combine(): Rect =
    if (isEmpty()) {
        Rect(0f, 0f, 1f, 1f)
    } else {
        Rect(minOf { it.left }, minOf { it.top }, maxOf { it.right }, maxOf { it.bottom })
    }

/** 시·도 법정동 2자리 → 권역 색. 같은 권역이라도 몇 곳은 살짝 밝혀 옆 지역과 구분한다. */
private fun regionFill(code: String): Color {
    val base =
        when (code.take(2)) {
            "11", "28", "41" -> RegionColor.CAPITAL
            "51" -> RegionColor.GANGWON
            "43", "44", "30", "36" -> RegionColor.CHUNGCHEONG
            "52", "12" -> RegionColor.JEOLLA
            "47", "48", "26", "27", "31" -> RegionColor.GYEONGSANG
            "50" -> RegionColor.JEJU
            else -> RegionColor.CAPITAL
        }.color
    // 한 권역 안에서 이웃한 도를 살짝 밝혀 경계가 읽히게 한다.
    val lighten = if (code.take(2) in lightenedProvinces) 0.22f else 0f
    return lerp(base, Color.White, lighten)
}

/** 광역시·특별시·특별자치시(작아서 알약으로 표시). 도는 텍스트 라벨만 둔다. */
private val metropolitanCodes = setOf("11", "26", "27", "28", "30", "31", "36")

/** 같은 권역 안에서 밝혀 구분할 도. */
private val lightenedProvinces = setOf("44", "48", "52")

/**
 * 중심이 서로 겹치는 라벨을 떼기 위한 미세 오프셋(단위 dp, +x 오른쪽 / +y 아래).
 * 특히 광역시가 도 안쪽에 박혀 있어(서울⊂경기, 대전·세종⊂충청, 부산·울산⊂경남) 도 라벨을 밀어낸다.
 */
private val labelNudges =
    mapOf(
        "41" to Offset(10f, -26f), // 경기: 서울 위로
        "28" to Offset(-6f, 2f), // 인천: 왼쪽
        "44" to Offset(-10f, 0f), // 충남: 왼쪽
        "43" to Offset(14f, -2f), // 충북: 오른쪽
        "36" to Offset(-2f, -12f), // 세종: 위
        "30" to Offset(-6f, 12f), // 대전: 아래
        "48" to Offset(-14f, -8f), // 경남: 왼위
        "26" to Offset(10f, 14f), // 부산: 오른아래
        "31" to Offset(10f, -2f), // 울산: 오른쪽
        "27" to Offset(2f, -8f), // 대구: 위
    )

/** 시·도 2자리 → 짧은 표시 이름(area_map.json 커스텀 데이터 기준). */
private val regionLabels =
    mapOf(
        "11" to "서울",
        "12" to "전남·광주",
        "26" to "부산",
        "27" to "대구",
        "28" to "인천",
        "30" to "대전",
        "31" to "울산",
        "36" to "세종",
        "41" to "경기",
        "43" to "충북",
        "44" to "충남",
        "47" to "경북",
        "48" to "경남",
        "50" to "제주",
        "51" to "강원",
        "52" to "전북",
    )
