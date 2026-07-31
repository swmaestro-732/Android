package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CoursePlaceVO

/**
 * 장소 검색 · 담기 오버레이(Figma FS-34 "+ 장소 더 담기 → 장소 검색").
 * 검색어로 후보를 필터링해 다중 선택하고, "담기 완료" 로 [onConfirm] 에 선택 장소를 넘긴다.
 *
 * TODO-API-SPEC: 후보는 현재 정적 스텁이다. 실제 장소 검색 API 가 붙으면 UseCase 로 교체한다.
 */
@Composable
fun PlaceSearchOverlay(
    onDismiss: () -> Unit,
    onConfirm: (List<CoursePlaceVO>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var query by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<String>() }
    val results =
        remember(query) {
            if (query.isBlank()) {
                PLACE_CANDIDATES
            } else {
                PLACE_CANDIDATES.filter { it.name.contains(query.trim(), ignoreCase = true) }
            }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0)
                .statusBarsPadding(),
    ) {
        SearchTopBar(
            query = query,
            onQueryChange = { query = it },
            onCancel = onDismiss,
        )
        DsText(
            text = "검색 결과 ${results.size}",
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentDefaultLevel2,
        )
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(results) { candidate ->
                val order = selectedIds.indexOf(candidate.id)
                SearchResultRow(
                    candidate = candidate,
                    orderNumber = if (order >= 0) order + 1 else null,
                    onToggle = {
                        if (candidate.id in selectedIds) {
                            selectedIds.remove(candidate.id)
                        } else {
                            selectedIds.add(candidate.id)
                        }
                    },
                )
            }
        }
        ConfirmBar(
            selectedCount = selectedIds.size,
            // 후보 목록 순서가 아니라 사용자가 탭한 순서 그대로 담기 순서로 넘긴다.
            onConfirm = { onConfirm(selectedIds.mapNotNull { id -> PLACE_CANDIDATES.find { it.id == id } }) },
        )
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCancel: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.bgDefaultLevel1)
                    .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DsText(
                text = "🔍",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle =
                    DesignSystemThemeImpl.typeScale.textRegularS
                        .copy(color = color.contentDefaultLevel0),
                cursorBrush = SolidColor(color.contentAccent),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        DsText(
                            text = "장소 · 주소 검색",
                            style = DesignSystemThemeImpl.typeScale.textRegularS,
                            color = color.contentDefaultLevel3,
                        )
                    }
                    innerTextField()
                },
            )
        }
        DsText(
            text = "취소",
            modifier = Modifier.clickable(onClick = onCancel),
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentAccent,
        )
    }
}

@Composable
private fun SearchResultRow(
    candidate: CoursePlaceVO,
    orderNumber: Int?,
    onToggle: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onToggle)
                .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.imagePlaceholder),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            DsText(
                text = candidate.name,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = candidate.category,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        if (orderNumber != null) {
            // 선택 순서를 번호로 표시(담기 순서와 동일).
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(color.bgAccent),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = orderNumber.toString(),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentOnAccent,
                )
            }
        } else {
            Box(
                modifier =
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .border(1.dp, color.borderDefaultLevel0, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                DsText(
                    text = "＋",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel2,
                )
            }
        }
    }
}

@Composable
private fun ConfirmBar(
    selectedCount: Int,
    onConfirm: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color.bgDefaultLevel1)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = "${selectedCount}곳 선택됨",
            modifier = Modifier.weight(1f),
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel1,
        )
        val enabled = selectedCount > 0
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (enabled) color.bgAccent else color.borderDefaultLevel0)
                    .clickable(enabled = enabled, onClick = onConfirm)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            DsText(
                text = "담기 완료",
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = if (enabled) color.contentOnAccent else color.contentDefaultLevel2,
            )
        }
    }
}

// 정적 장소 후보(스텁). id 는 담긴 장소 중복 판별에 쓰인다.
private val PLACE_CANDIDATES: List<CoursePlaceVO> =
    listOf(
        CoursePlaceVO(id = "p_center_coffee", name = "센터커피 로스터리", category = "카페 · 성수동"),
        CoursePlaceVO(id = "p_centerfield", name = "센터필드 베이커리", category = "베이커리 · 성수동"),
        CoursePlaceVO(id = "p_center_lounge", name = "센터 라운지", category = "라운지바 · 성수동"),
        CoursePlaceVO(id = "p_onion", name = "어니언 성수", category = "카페 · 베이커리"),
        CoursePlaceVO(id = "p_daelim", name = "대림창고 갤러리", category = "전시 · 카페"),
        CoursePlaceVO(id = "p_seoulforest", name = "서울숲 산책로", category = "공원 · 산책"),
        CoursePlaceVO(id = "p_glow", name = "글로우 서울", category = "디저트 · 포토존"),
        CoursePlaceVO(id = "p_sogeumjip", name = "소금집 델리", category = "와인 · 안주"),
    )
