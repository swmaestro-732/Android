package com.chillsam.courmy.course.presentation.component

import android.widget.Toast
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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.presentation.PlaceSearchIntent
import com.chillsam.courmy.course.presentation.PlaceSearchViewModel

/**
 * 장소 검색 · 담기 오버레이(Figma FS-34 "+ 장소 더 담기 → 장소 검색").
 * 검색어로 후보를 필터링해 다중 선택하고, "담기 완료" 로 [onConfirm] 에 선택 장소를 넘긴다.
 *
 * 후보는 서버 장소 검색(`GET /api/v1/places`)에서 가져오며, 결과의 id 가 서버 place id 라
 * 그대로 코스 생성 요청의 `placeId` 로 쓸 수 있다.
 */
@Composable
fun PlaceSearchOverlay(
    onDismiss: () -> Unit,
    onConfirm: (List<CoursePlaceVO>) -> Unit,
    maxPlaces: Int,
    existingCount: Int,
    modifier: Modifier = Modifier,
    viewModel: PlaceSearchViewModel = hiltViewModel(),
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query = uiState.query
    val selectedIds = remember { mutableStateListOf<String>() }
    // 담기 완료 시 후보 목록에서 다시 찾을 수 있도록, 탭한 장소를 따로 모아 둔다
    // (검색어를 바꾸면 결과 목록이 갈리므로 결과에서 되찾을 수 없다).
    val selectedPlaces = remember { mutableStateMapOf<String, CoursePlaceVO>() }
    // 이미 담긴 장소까지 합쳐 최대 [maxPlaces]곳. 이 오버레이에서 더 고를 수 있는 수.
    val remaining = (maxPlaces - existingCount).coerceAtLeast(0)
    val context = LocalContext.current
    val showLimitAlert = {
        Toast.makeText(context, "장소는 최대 ${maxPlaces}개만 담을 수 있습니다.", Toast.LENGTH_SHORT).show()
    }
    // 기본은 결과 0(아무것도 안 보임). 키워드를 입력해야 서버에서 후보를 받아 온다.
    val results = uiState.results
    // 오버레이가 열리면(장소 더 담기) 검색창에 자동 포커스 + 키보드.
    val searchFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { searchFocus.requestFocus() }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color.bgDefaultLevel0)
                .statusBarsPadding(),
    ) {
        SearchTopBar(
            query = query,
            onQueryChange = { viewModel.onIntent(PlaceSearchIntent.QueryChanged(it)) },
            onCancel = onDismiss,
            focusRequester = searchFocus,
        )
        if (query.isNotBlank()) {
            DsText(
                text = "검색 결과 ${results.size}",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
        if (results.isEmpty()) {
            SearchEmptyState(hasQuery = query.isNotBlank(), modifier = Modifier.weight(1f))
        } else {
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
                            when {
                                candidate.id in selectedIds -> {
                                    selectedIds.remove(candidate.id)
                                    selectedPlaces.remove(candidate.id)
                                }

                                // 남은 자리가 있을 때만 선택 추가(합계 최대 maxPlaces곳).
                                selectedIds.size < remaining -> {
                                    selectedIds.add(candidate.id)
                                    selectedPlaces[candidate.id] = candidate
                                }

                                // 상한 도달 시 선택되지 않고 알림만 띄운다.
                                else -> {
                                    showLimitAlert()
                                }
                            }
                        },
                    )
                }
            }
        }
        ConfirmBar(
            selectedCount = selectedIds.size,
            // 후보 목록 순서가 아니라 사용자가 탭한 순서 그대로 담기 순서로 넘긴다.
            onConfirm = { onConfirm(selectedIds.mapNotNull(selectedPlaces::get)) },
        )
    }
}

@Composable
private fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCancel: () -> Unit,
    focusRequester: FocusRequester,
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
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.bgDefaultLevel1)
                    .border(1.dp, color.borderAccent, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search_24),
                contentDescription = null,
                tint = color.contentDefaultLevel3,
                modifier = Modifier.size(20.dp),
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
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
            if (query.isNotEmpty()) {
                Box(
                    modifier =
                        Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(color.contentDefaultLevel3)
                            .clickable { onQueryChange("") },
                    contentAlignment = Alignment.Center,
                ) {
                    DsText(
                        text = "✕",
                        style = DesignSystemThemeImpl.typeScale.textExtraXS,
                        color = color.contentOnAccent,
                    )
                }
            }
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
                // 접근성: 선택 여부·선택 순서를 스크린리더에 노출.
                .semantics {
                    selected = orderNumber != null
                    if (orderNumber != null) stateDescription = "선택됨, ${orderNumber}번째"
                }.padding(vertical = 10.dp),
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

/** 검색 전(기본)·결과 없음 상태 안내. */
@Composable
private fun SearchEmptyState(
    hasQuery: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        DsText(
            text = if (hasQuery) "검색 결과가 없어요" else "장소나 주소를 검색해 보세요",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
        )
    }
}
