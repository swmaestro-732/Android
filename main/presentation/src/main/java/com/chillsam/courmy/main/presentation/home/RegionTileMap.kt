package com.chillsam.courmy.main.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chillsam.courmy.common.presentation.translation.catalogLabel
import com.chillsam.courmy.common.presentation.ui.color.RegionTileColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import kotlin.math.hypot

private const val GRID_COLS = 16

/**
 * 홈 지역별 둘러보기 **타일 지도**(디자인 목업과 동일한 그리드 타일 시안).
 *
 * 각 시·도를 20px 격자 셀의 합집합으로 두고, 셀 합집합 외곽선을 둥글려 말랑한 블록으로 그린다.
 * 지역 사이는 카드 배경색 선으로 떼어 타일처럼 보이게 한다. 도·광역시 모두 이름 텍스트를 같은 크기로
 * 중심에 얹는다(알약 배경 없음). 지역을 누르면 [onRegionClick] 으로 코드(시·도 법정동 2자리)와 이름을 넘긴다.
 */
@Composable
internal fun RegionTileMap(
    onRegionClick: (code: String, name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    // 도(경기 등)·광역시(서울 등) 라벨은 같은 크기로 통일한다(서울 기준 textExtraXS, SCRUM-791).
    // 자간은 지도 위 두 글자가 "서 울"처럼 벌어져 보이지 않게 0 으로 덮는다.
    val labelStyle =
        DesignSystemThemeImpl.typeScale.textExtraXS.copy(color = color.contentDefaultLevel0, letterSpacing = 0.sp)
    val measurer = rememberTextMeasurer()
    val cardColor = color.bgDefaultLevel1
    // 외곽선 둥글림·분리선은 remember 로 셀→외곽선 계산을 캐시한다.
    val labels = regionTiles.associate { it.code to catalogLabel(it.label) }
    val outlines = remember { regionTiles.associateWith { outlineLoops(it.cells) } }

    Canvas(
        modifier =
            modifier.pointerInput(Unit) {
                detectTapGestures { pos ->
                    val cell = size.width / GRID_COLS.toFloat()
                    val c = (pos.x / cell).toInt()
                    val r = (pos.y / cell).toInt()
                    val key = TileRegion.packCell(c, r)
                    regionTiles.firstOrNull { it.cells.contains(key) }?.let { onRegionClick(it.code, it.label) }
                }
            },
    ) {
        val cell = size.width / GRID_COLS
        val radius = cell * 0.46f
        val gap = cell * 0.16f

        regionTiles.forEach { region ->
            val path = roundedPath(outlines.getValue(region), cell, radius)
            drawPath(path, RegionTileColor.of(region.code))
            // 카드색 선으로 이웃 지역과 틈을 만든다(바깥 경계는 카드 위라 보이지 않음).
            drawPath(path, cardColor, style = Stroke(width = gap, join = StrokeJoin.Round))
        }

        // 광역시(서울 등)도 알약 배경 없이 도 라벨과 같은 방식으로 글자만 올린다(SCRUM-791).
        regionTiles.forEach { region ->
            val center = Offset(region.labelCol * cell, region.labelRow * cell)
            drawCenteredText(measurer, labels.getValue(region.code), center, labelStyle, region.labelWidth() * cell)
        }
    }
}

/** 셀 합집합의 외곽선 루프들(시계방향 외곽 + 반시계 구멍). 좌표는 격자 단위. */
private fun outlineLoops(cells: Set<Long>): List<List<Offset>> {
    fun has(
        c: Int,
        r: Int,
    ) = cells.contains(TileRegion.packCell(c, r))

    // 경계 방향간선: 지역을 오른쪽에 두고 도는 half-edge (y 아래 방향, 시계방향).
    val next = HashMap<Long, Long>()

    fun pt(
        x: Int,
        y: Int,
    ): Long = (x.toLong() shl 32) or (y.toLong() and 0xffffffffL)

    fun add(
        ax: Int,
        ay: Int,
        bx: Int,
        by: Int,
    ) {
        next[pt(ax, ay)] = pt(bx, by)
    }
    cells.forEach { packed ->
        val c = (packed shr 32).toInt()
        val r = (packed and 0xffffffffL).toInt()
        if (!has(c, r - 1)) add(c, r, c + 1, r) // 위: 좌→우
        if (!has(c + 1, r)) add(c + 1, r, c + 1, r + 1) // 우: 상→하
        if (!has(c, r + 1)) add(c + 1, r + 1, c, r + 1) // 아래: 우→좌
        if (!has(c - 1, r)) add(c, r + 1, c, r) // 좌: 하→상
    }
    val loops = mutableListOf<List<Offset>>()
    while (next.isNotEmpty()) {
        val start = next.keys.first()
        val loop = mutableListOf<Offset>()
        var cur = start
        do {
            val x = (cur shr 32).toInt()
            val y = (cur and 0xffffffffL).toInt()
            loop.add(Offset(x.toFloat(), y.toFloat()))
            val nxt = next.remove(cur) ?: break
            cur = nxt
        } while (cur != start)
        // 꼭짓점만 남기도록 일직선 점 제거.
        loops.add(simplifyCollinear(loop))
    }
    return loops
}

/** 같은 방향으로 이어지는 중간 점을 제거해 꼭짓점만 남긴다. */
private fun simplifyCollinear(loop: List<Offset>): List<Offset> {
    if (loop.size < 3) return loop
    val out = mutableListOf<Offset>()
    for (i in loop.indices) {
        val prev = loop[(i - 1 + loop.size) % loop.size]
        val cur = loop[i]
        val nextP = loop[(i + 1) % loop.size]
        val d1x = cur.x - prev.x
        val d1y = cur.y - prev.y
        val d2x = nextP.x - cur.x
        val d2y = nextP.y - cur.y
        if (d1x * d2y - d1y * d2x != 0f || d1x * d2x + d1y * d2y < 0f) out.add(cur)
    }
    return out
}

/** 격자 단위 루프들을 px 로 바꾸고 각 꼭짓점을 [radius] 로 둥글린 EvenOdd 경로. */
private fun roundedPath(
    loops: List<List<Offset>>,
    cell: Float,
    radius: Float,
): Path =
    Path().apply {
        fillType = PathFillType.EvenOdd
        loops.forEach { grid ->
            addRoundedLoop(grid.map { Offset(it.x * cell, it.y * cell) }, radius)
        }
    }

/** 닫힌 다각형 [pts] 의 각 꼭짓점을 [radius] 로 둥글려 경로에 더한다. */
private fun Path.addRoundedLoop(
    pts: List<Offset>,
    radius: Float,
) {
    val n = pts.size
    if (n < 3) return
    for (i in 0 until n) {
        val v = pts[i]
        val prev = pts[(i - 1 + n) % n]
        val nxt = pts[(i + 1) % n]
        val a = towards(v, prev, minOf(radius, dist(prev, v) / 2f))
        val b = towards(v, nxt, minOf(radius, dist(v, nxt) / 2f))
        if (i == 0) moveTo(a.x, a.y) else lineTo(a.x, a.y)
        quadraticTo(v.x, v.y, b.x, b.y)
    }
    close()
}

private fun dist(
    a: Offset,
    b: Offset,
): Float = hypot(a.x - b.x, a.y - b.y)

/** [from] 에서 [to] 방향으로 [d] 만큼 이동한 점. */
private fun towards(
    from: Offset,
    to: Offset,
    d: Float,
): Offset {
    val len = dist(from, to)
    if (len == 0f) return from
    return Offset(from.x + (to.x - from.x) / len * d, from.y + (to.y - from.y) / len * d)
}

private fun DrawScope.drawCenteredText(
    measurer: TextMeasurer,
    text: String,
    center: Offset,
    style: TextStyle,
    maxWidth: Float,
) {
    val natural = measurer.measure(text, style)
    val scale = (maxWidth / natural.size.width.coerceAtLeast(1)).coerceAtMost(1f)
    val measured = if (scale < 1f) measurer.measure(text, style.copy(fontSize = style.fontSize * scale)) else natural
    drawText(measured, topLeft = Offset(center.x - measured.size.width / 2, center.y - measured.size.height / 2))
}

/** 라벨 중심을 포함하는 가로 셀 구간 안에 긴 번역도 들어가도록 폭을 제한한다. */
private fun TileRegion.labelWidth(): Float {
    val row = labelRow.toInt()
    var left = labelCol.toInt()
    var right = left + 1
    while (cells.contains(TileRegion.packCell(left - 1, row))) left--
    while (cells.contains(TileRegion.packCell(right, row))) right++
    return (2f * minOf(labelCol - left, right - labelCol) - 0.4f).coerceAtLeast(1f)
}

/**
 * 16×25 격자 위의 시·도 타일(디자인 목업에서 추출). cells 는 (col,row) 평탄 배열.
 * labelCol·labelRow 는 라벨 중심(격자 단위). 울릉("47U")은 경북 소속 섬이라 따로 둔다.
 */
private val regionTiles =
    listOf(
        TileRegion.of(
            "41",
            "경기",
            false,
            4.5f,
            3.4f,
            intArrayOf(
                2,
                3,
                2,
                4,
                2,
                7,
                3,
                2,
                3,
                3,
                3,
                6,
                3,
                7,
                4,
                2,
                4,
                3,
                4,
                6,
                4,
                7,
                5,
                2,
                5,
                3,
                5,
                6,
                5,
                7,
                6,
                3,
                6,
                4,
                6,
                5,
                6,
                6,
                7,
                4,
                7,
                5,
                7,
                6,
            ),
        ),
        TileRegion.of("11", "서울", true, 4.5f, 5.0f, intArrayOf(3, 4, 3, 5, 4, 4, 4, 5, 5, 4, 5, 5)),
        TileRegion.of("28", "인천", true, 1.9f, 5.7f, intArrayOf(1, 4, 1, 5, 1, 6, 2, 5, 2, 6)),
        TileRegion.of(
            "51",
            "강원",
            false,
            10.0f,
            4.8f,
            intArrayOf(
                6,
                1,
                6,
                2,
                7,
                1,
                7,
                2,
                7,
                3,
                8,
                1,
                8,
                2,
                8,
                3,
                8,
                4,
                8,
                5,
                8,
                6,
                9,
                0,
                9,
                1,
                9,
                2,
                9,
                3,
                9,
                4,
                9,
                5,
                9,
                6,
                9,
                7,
                10,
                2,
                10,
                3,
                10,
                4,
                10,
                5,
                10,
                6,
                10,
                7,
                11,
                3,
                11,
                4,
                11,
                5,
                11,
                6,
                11,
                7,
                12,
                5,
                12,
                6,
                12,
                7,
                13,
                7,
            ),
        ),
        TileRegion.of(
            "43",
            "충북",
            false,
            7.0f,
            8.8f,
            intArrayOf(
                5,
                8,
                6,
                7,
                6,
                8,
                6,
                9,
                6,
                10,
                6,
                11,
                6,
                12,
                7,
                7,
                7,
                8,
                7,
                9,
                7,
                10,
                7,
                11,
                7,
                12,
                8,
                7,
                8,
                8,
                9,
                8,
            ),
        ),
        TileRegion.of(
            "44",
            "충남",
            false,
            2.5f,
            9.8f,
            intArrayOf(0, 9, 1, 8, 1, 9, 1, 10, 2, 8, 2, 9, 2, 10, 2, 11, 2, 12, 3, 8, 3, 9, 3, 10, 3, 11, 3, 12, 4, 8),
        ),
        TileRegion.of("36", "세종", true, 5.0f, 10.0f, intArrayOf(4, 9, 4, 10, 5, 9, 5, 10)),
        TileRegion.of("30", "대전", true, 5.0f, 12.0f, intArrayOf(4, 11, 4, 12, 5, 11, 5, 12)),
        TileRegion.of(
            "47",
            "경북",
            false,
            11.5f,
            10.8f,
            intArrayOf(
                8,
                9,
                8,
                10,
                8,
                11,
                8,
                12,
                8,
                13,
                9,
                9,
                9,
                10,
                9,
                11,
                9,
                12,
                10,
                8,
                10,
                9,
                10,
                10,
                10,
                11,
                10,
                12,
                11,
                8,
                11,
                9,
                11,
                10,
                11,
                11,
                11,
                12,
                11,
                13,
                11,
                14,
                12,
                8,
                12,
                9,
                12,
                10,
                12,
                11,
                12,
                12,
                12,
                13,
                13,
                8,
                13,
                9,
                13,
                10,
                13,
                11,
                13,
                12,
                13,
                13,
                14,
                12,
            ),
        ),
        TileRegion.of("47U", "울릉", true, 15.0f, 5.0f, intArrayOf(14, 4, 14, 5, 15, 4, 15, 5)),
        TileRegion.of("27", "대구", true, 10.0f, 14.0f, intArrayOf(9, 13, 9, 14, 10, 13, 10, 14)),
        TileRegion.of("31", "울산", true, 13.0f, 15.0f, intArrayOf(12, 14, 12, 15, 13, 14, 13, 15)),
        TileRegion.of("26", "부산", true, 13.0f, 17.5f, intArrayOf(12, 16, 12, 17, 12, 18, 13, 16, 13, 17, 13, 18)),
        TileRegion.of(
            "52",
            "전북",
            false,
            4.5f,
            14.8f,
            intArrayOf(
                2,
                13,
                2,
                14,
                2,
                15,
                3,
                13,
                3,
                14,
                3,
                15,
                4,
                13,
                4,
                14,
                4,
                15,
                5,
                13,
                5,
                14,
                5,
                15,
                6,
                13,
                6,
                14,
                6,
                15,
                7,
                13,
            ),
        ),
        TileRegion.of(
            "12",
            "전남",
            false,
            3.0f,
            19.8f,
            intArrayOf(
                0,
                18,
                1,
                17,
                1,
                18,
                1,
                19,
                1,
                20,
                2,
                16,
                2,
                17,
                2,
                18,
                2,
                19,
                2,
                20,
                2,
                21,
                3,
                16,
                3,
                19,
                3,
                20,
                3,
                21,
                4,
                16,
                4,
                19,
                5,
                16,
                5,
                17,
                5,
                18,
                5,
                19,
                5,
                20,
                6,
                17,
                6,
                18,
                6,
                19,
                6,
                20,
            ),
        ),
        TileRegion.of("29", "광주", true, 4.0f, 18.0f, intArrayOf(3, 17, 3, 18, 4, 17, 4, 18)),
        TileRegion.of(
            "48",
            "경남",
            false,
            9.5f,
            17.8f,
            intArrayOf(
                6,
                16,
                7,
                14,
                7,
                15,
                7,
                16,
                7,
                17,
                7,
                18,
                7,
                19,
                8,
                14,
                8,
                15,
                8,
                16,
                8,
                17,
                8,
                18,
                8,
                19,
                9,
                15,
                9,
                16,
                9,
                17,
                9,
                18,
                9,
                19,
                10,
                15,
                10,
                16,
                10,
                17,
                10,
                18,
                10,
                19,
                11,
                15,
                11,
                16,
                11,
                17,
                11,
                18,
            ),
        ),
        TileRegion.of("50", "제주", false, 2.2f, 24.2f, intArrayOf(0, 23, 1, 23, 1, 24, 2, 23, 2, 24, 3, 23, 3, 24)),
    )

/** 그리드 타일 한 지역. [cells] 는 (col,row) 쌍이 평탄하게 들어간다. 라벨은 [labelCol]·[labelRow] 중심에 둔다. */
internal class TileRegion(
    val code: String,
    val label: String,
    val isMetro: Boolean,
    val labelCol: Float,
    val labelRow: Float,
    val cells: Set<Long>,
) {
    companion object {
        fun packCell(
            col: Int,
            row: Int,
        ): Long = (col.toLong() shl 32) or (row.toLong() and 0xffffffffL)

        fun of(
            code: String,
            label: String,
            isMetro: Boolean,
            labelCol: Float,
            labelRow: Float,
            cells: IntArray,
        ): TileRegion {
            val set = HashSet<Long>(cells.size)
            var i = 0
            while (i < cells.size) {
                set.add(packCell(cells[i], cells[i + 1]))
                i += 2
            }
            return TileRegion(code, label, isMetro, labelCol, labelRow, set)
        }
    }
}
