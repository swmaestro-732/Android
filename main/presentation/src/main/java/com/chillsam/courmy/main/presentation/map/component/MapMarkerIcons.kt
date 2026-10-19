package com.chillsam.courmy.main.presentation.map.component

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import com.naver.maps.map.overlay.OverlayImage

/*
 * 지도 핀 아이콘을 비트맵으로 그린다.
 *
 * 네이버 지도 SDK 의 마커는 Compose 를 못 받고 [OverlayImage] 만 받는다. 그래서 시안의 핀(원형 썸네일
 * + 아래쪽 꼬리)과 선택 시 뜨는 말풍선을 여기서 직접 그린다. 말풍선은 별도 오버레이로 띄우면 지도를
 * 움직일 때 핀과 따로 놀아서, 핀과 **한 장의 비트맵**으로 합쳐 그린다.
 *
 * 지도 핀은 카테고리 아이콘으로 장소 종류를 표시한다. 원격 사진은 카드에서 표시한다.
 */

/** 일반 핀의 원 지름(dp). */
private const val PIN_DIAMETER_DP = 40f

/** 선택된 핀의 원 지름(dp). 나머지보다 확실히 크게 잡아 눈에 띄게 한다. */
private const val SELECTED_PIN_DIAMETER_DP = 52f

/** 원 아래 붙는 삼각 꼬리의 높이(dp). 꼬리 끝이 실제 좌표를 가리킨다. */
private const val PIN_TAIL_HEIGHT_DP = 8f

/** 핀 테두리 두께(dp). */
private const val PIN_STROKE_DP = 3f

/** 묶음(클러스터) 핀의 지름(dp). */
private const val CLUSTER_DIAMETER_DP = 36f

private const val LABEL_TEXT_SP = 12f
private const val LABEL_PADDING_H_DP = 10f
private const val LABEL_PADDING_V_DP = 6f
private const val LABEL_GAP_DP = 6f
private const val LABEL_CORNER_DP = 12f

/** 비트맵에 그림자를 그릴 여백(dp). 이만큼 사방을 비워 두지 않으면 그림자가 잘린다. */
private const val SHADOW_PAD_DP = 4f

/**
 * 장소 핀. [selected] 면 크게 그리고, [label] 이 있으면 핀 위에 말풍선을 얹는다.
 *
 * 반환 비트맵의 **아래쪽 가운데**가 좌표를 가리키므로 호출부는 anchor 를 (0.5, 1.0) 으로 준다.
 */
internal fun placePinIcon(
    density: Float,
    style: PlacePinStyle,
    selected: Boolean,
    label: String? = null,
): OverlayImage {
    val diameter = (if (selected) SELECTED_PIN_DIAMETER_DP else PIN_DIAMETER_DP) * density
    val tail = PIN_TAIL_HEIGHT_DP * density
    val shadowPad = SHADOW_PAD_DP * density
    val labelBox = label?.let { LabelBox.measure(it, density, style.labelTextArgb) }

    val width = maxOf(diameter, labelBox?.width ?: 0f) + shadowPad * 2
    val height = (labelBox?.blockHeight(density) ?: 0f) + diameter + tail + shadowPad * 2
    val bitmap = createBitmap(width.toInt(), height.toInt())
    val canvas = Canvas(bitmap)
    val centerX = width / 2f

    labelBox?.draw(canvas, centerX, shadowPad, density, style.labelBackgroundArgb)
    canvas.drawPin(
        spec =
            PinSpec(
                centerX = centerX,
                top = shadowPad + (labelBox?.blockHeight(density) ?: 0f),
                diameter = diameter,
                tail = tail,
                shadowPad = shadowPad,
                strokeWidth = PIN_STROKE_DP * density,
            ),
        fillArgb = style.fillArgb,
        strokeArgb = style.strokeArgb,
    )
    val centerY = shadowPad + (labelBox?.blockHeight(density) ?: 0f) + diameter / 2f
    val halfIcon = diameter * 0.24f
    style.categoryIcon.setBounds(
        (centerX - halfIcon).toInt(),
        (centerY - halfIcon).toInt(),
        (centerX + halfIcon).toInt(),
        (centerY + halfIcon).toInt(),
    )
    style.categoryIcon.draw(canvas)
    return OverlayImage.fromBitmap(bitmap)
}

/** 카드와 같은 카테고리 아이콘 및 테마 색상. */
internal data class PlacePinStyle(
    val fillArgb: Int,
    val strokeArgb: Int,
    val labelBackgroundArgb: Int,
    val labelTextArgb: Int,
    val categoryIcon: Drawable,
)

private class PinSpec(
    val centerX: Float,
    val top: Float,
    val diameter: Float,
    val tail: Float,
    val shadowPad: Float,
    val strokeWidth: Float,
)

/** 말풍선 한 덩이. 크기를 재는 일과 그리는 일이 같은 값(글자 Paint)을 써야 해서 묶어 둔다. */
private class LabelBox(
    val text: String,
    val paint: Paint,
    val width: Float,
    val height: Float,
) {
    /** 말풍선이 핀 위에서 차지하는 총 높이(말풍선 + 핀과의 간격). */
    fun blockHeight(density: Float): Float = height + LABEL_GAP_DP * density

    fun draw(
        canvas: Canvas,
        centerX: Float,
        top: Float,
        density: Float,
        backgroundArgb: Int,
    ) {
        val rect = RectF(centerX - width / 2f, top, centerX + width / 2f, top + height)
        val corner = LABEL_CORNER_DP * density
        canvas.drawRoundRect(
            rect,
            corner,
            corner,
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = backgroundArgb },
        )
        // 기준선(baseline)은 사각형 중앙에서 글자 높이의 절반만큼 내린 위치다.
        val baseline = rect.centerY() - (paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f
        canvas.drawText(text, centerX, baseline, paint)
    }

    companion object {
        fun measure(
            text: String,
            density: Float,
            textArgb: Int,
        ): LabelBox {
            val paint = labelTextPaint(density, textArgb)
            return LabelBox(
                text = text,
                paint = paint,
                width = paint.measureText(text) + LABEL_PADDING_H_DP * density * 2,
                height = paint.textHeight() + LABEL_PADDING_V_DP * density * 2,
            )
        }
    }
}

/** 원 + 아래 꼬리로 이뤄진 핀 몸통을 [PinSpec.top] 부터 그린다. */
private fun Canvas.drawPin(
    spec: PinSpec,
    fillArgb: Int,
    strokeArgb: Int,
) {
    val radius = spec.diameter / 2f
    val centerY = spec.top + radius
    val bottom = centerY + radius - spec.strokeWidth

    // 꼬리를 먼저 그려야 원의 테두리가 꼬리 윗변을 덮어 이음매가 안 보인다.
    val tailPath =
        Path().apply {
            moveTo(spec.centerX - spec.tail * 0.7f, bottom)
            lineTo(spec.centerX + spec.tail * 0.7f, bottom)
            lineTo(spec.centerX, centerY + radius + spec.tail)
            close()
        }
    drawPath(tailPath, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = strokeArgb })
    drawCircle(
        spec.centerX,
        centerY,
        radius - spec.strokeWidth / 2f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = fillArgb
            setShadowLayer(spec.shadowPad, 0f, spec.shadowPad / 2f, SHADOW_ARGB)
        },
    )
    drawCircle(
        spec.centerX,
        centerY,
        radius - spec.strokeWidth / 2f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = strokeArgb
            style = Paint.Style.STROKE
            strokeWidth = spec.strokeWidth
        },
    )
}

/** 여러 장소가 뭉친 묶음 핀(`+9`). 개수만 적힌 원이라 꼬리를 달지 않는다. */
internal fun clusterPinIcon(
    density: Float,
    count: Int,
    fillArgb: Int,
    textArgb: Int,
): OverlayImage {
    val diameter = CLUSTER_DIAMETER_DP * density
    val shadowPad = SHADOW_PAD_DP * density
    val size = (diameter + shadowPad * 2).toInt()
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    val center = size / 2f

    canvas.drawCircle(
        center,
        center,
        diameter / 2f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = fillArgb
            setShadowLayer(shadowPad, 0f, shadowPad / 2f, SHADOW_ARGB)
        },
    )
    val textPaint = labelTextPaint(density, textArgb)
    val baseline = center - (textPaint.fontMetrics.ascent + textPaint.fontMetrics.descent) / 2f
    canvas.drawText("+$count", center, baseline, textPaint)
    return OverlayImage.fromBitmap(bitmap)
}

/** 내 위치를 가리키는 점(흰 테두리 + 파란 점). SDK 의 위치 오버레이 대신 mock 좌표에 직접 찍는다. */
internal fun myLocationIcon(
    density: Float,
    dotArgb: Int,
    ringArgb: Int,
): OverlayImage {
    val diameter = MY_LOCATION_DIAMETER_DP * density
    val shadowPad = SHADOW_PAD_DP * density
    val size = (diameter + shadowPad * 2).toInt()
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    val center = size / 2f

    canvas.drawCircle(
        center,
        center,
        diameter / 2f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ringArgb
            setShadowLayer(shadowPad, 0f, shadowPad / 2f, SHADOW_ARGB)
        },
    )
    canvas.drawCircle(
        center,
        center,
        diameter / 2f - MY_LOCATION_RING_DP * density,
        Paint(Paint.ANTI_ALIAS_FLAG).apply { color = dotArgb },
    )
    return OverlayImage.fromBitmap(bitmap)
}

private const val MY_LOCATION_DIAMETER_DP = 18f
private const val MY_LOCATION_RING_DP = 3f

private const val SHADOW_ARGB = 0x33000000

private fun labelTextPaint(
    density: Float,
    argb: Int,
): Paint =
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = argb
        textAlign = Paint.Align.CENTER
        textSize = LABEL_TEXT_SP * density
        typeface = Typeface.DEFAULT_BOLD
    }

private fun Paint.textHeight(): Float = fontMetrics.descent - fontMetrics.ascent
