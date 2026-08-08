package com.chillsam.courmy.common.presentation.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/** 목록 끝에서 이만큼 남았을 때 다음 페이지를 미리 당긴다. */
private const val LOAD_MORE_THRESHOLD = 3

/**
 * `LazyColumn` 끝이 보이면 [onLoadMore] 를 부른다.
 *
 * 마지막 항목이 화면에 들어오기 [LOAD_MORE_THRESHOLD] 개 전부터 당겨, 바닥에서 멈칫하지 않게 한다.
 * 스크롤이 흔들릴 때마다 여러 번 불릴 수 있으므로, **중복 요청은 받는 쪽(ViewModel)에서 막는다**.
 */
@Composable
fun LoadMoreOnScrollEnd(
    listState: LazyListState,
    onLoadMore: () -> Unit,
) {
    // 끝에 닿았는지(Boolean)만 보면, 새 페이지를 붙인 뒤에도 여전히 끝 근처일 때 값이 true 로 유지돼
    // LaunchedEffect 가 다시 돌지 않는다(한 페이지 받고 멈춘다). 그래서 "몇 개일 때 끝이었는지"를 키로 쓴다.
    val loadMoreAt by remember(listState) {
        derivedStateOf {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: return@derivedStateOf null
            if (last >= info.totalItemsCount - LOAD_MORE_THRESHOLD) info.totalItemsCount else null
        }
    }
    LaunchedEffect(loadMoreAt) {
        if (loadMoreAt != null) onLoadMore()
    }
}

/**
 * `Column(verticalScroll)` 바닥에 닿으면 [onLoadMore] 를 부른다.
 *
 * Lazy 목록이 아니라 항목별 인덱스를 알 수 없어, 스크롤 값이 끝에서 [SCROLL_END_SLOP_PX] 안쪽에
 * 들어왔는지로 판단한다. 내용이 화면보다 짧으면 maxValue 가 0 이라 처음부터 끝으로 보이므로,
 * 스크롤할 게 있을 때만 발화시킨다.
 */
@Composable
fun LoadMoreOnScrollEnd(
    scrollState: ScrollState,
    onLoadMore: () -> Unit,
) {
    // Lazy 목록과 같은 이유로, 끝에 닿았을 때의 maxValue 를 키로 쓴다.
    // 새 항목이 붙으면 maxValue 가 커지므로 여전히 끝 근처여도 다시 발화한다.
    val loadMoreAt by remember(scrollState) {
        derivedStateOf {
            val max = scrollState.maxValue
            if (max > 0 && scrollState.value >= max - SCROLL_END_SLOP_PX) max else null
        }
    }
    LaunchedEffect(loadMoreAt) {
        if (loadMoreAt != null) onLoadMore()
    }
}

/** 바닥으로 인정할 여유(px). 한 화면 높이의 대략 절반쯤 남았을 때 당긴다. */
private const val SCROLL_END_SLOP_PX = 600

/** 다음 페이지를 받는 중 목록 맨 아래에 붙는 표시. 첫 로딩보다 얕게 잡는다. */
@Composable
fun LoadingMoreFooter(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = DesignSystemThemeImpl.designSystemColor.contentAccent,
            strokeWidth = 2.dp,
            modifier = Modifier.size(22.dp),
        )
    }
}
