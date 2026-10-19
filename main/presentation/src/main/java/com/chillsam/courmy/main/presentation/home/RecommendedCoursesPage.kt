package com.chillsam.courmy.main.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.entity.category.PlaceCategoryVO
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.presentation.component.HomeCourseCard

/**
 * 오늘의 추천 코스 전체 목록. 홈(FS-09)에 있던 공개 코스 피드 리스트를 그대로 옮겨 온 화면이다.
 *
 * 홈이 가로 미리보기 카드만 남기도록 개편되면서 세로 피드 전체는 이 화면이 맡는다.
 * 데이터는 홈 미리보기와 같은 [HomeFeedViewModel](`GET /service/v1/courses`)을 쓴다 —
 * 라우트가 달라 ViewModel 인스턴스는 분리되지만, 같은 공개 엔드포인트라 상태를 공유할 필요가 없다.
 */
@Composable
fun RecommendedCoursesPage(
    modifier: Modifier = Modifier,
    initialCategory: String? = null,
) {
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    val navigationHelper = LocalNavigationHelper.current
    val context = LocalContext.current
    val feedViewModel: HomeFeedViewModel = hiltViewModel()
    val feedState by feedViewModel.uiState.collectAsStateWithLifecycle()

    // 홈 테마 타일로 들어오면 그 카테고리가 선택된 채 시작한다.
    // 최초 1회만 적용한다 — 이후에는 사용자가 고른 칩이 기준이다.
    LaunchedEffect(Unit) { initialCategory?.let { feedViewModel.onIntent(HomeFeedIntent.SelectCategory(it)) } }

    // 코스를 만들거나 저장한 뒤 돌아오면 목록이 바뀌어 있을 수 있어 다시 불러온다(홈과 동일).
    RefreshOnResume { feedViewModel.onIntent(HomeFeedIntent.Retry) }

    val listState = rememberLazyListState()
    LoadMoreOnScrollEnd(listState) { feedViewModel.onIntent(HomeFeedIntent.LoadMore) }

    LaunchedEffect(feedState.selectedCategory) { listState.scrollToItem(0) }

    // 저장 실패는 화면을 바꾸지 않고 토스트로만 알린다.
    LaunchedEffect(feedState.actionErrorMessage) {
        feedState.actionErrorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            feedViewModel.onIntent(HomeFeedIntent.ConsumeError)
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
                .statusBarsPadding()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0),
    ) {
        RecommendedTopBar(
            selectedCategory = feedState.selectedCategory,
            onSelectCategory = { feedViewModel.onIntent(HomeFeedIntent.SelectCategory(it)) },
            onBack = { navigationHelper.navigateToBack() },
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = ScreenHorizontalPadding,
                    end = ScreenHorizontalPadding,
                    top = 16.dp,
                    bottom = 40.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    DsText(
                        text = "불러온 ${feedState.selectedCategory ?: "추천"} 코스",
                        style = DesignSystemThemeImpl.typeScale.textRegularS,
                        color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel3,
                    )
                    if (!feedState.isLoading) {
                        DsText(
                            text = "${feedState.visibleCourses.size}",
                            style = DesignSystemThemeImpl.typeScale.textStrongS,
                            color = DesignSystemThemeImpl.designSystemColor.contentDefaultLevel1,
                        )
                    }
                }
            }
            when {
                feedState.visibleCourses.isNotEmpty() -> {
                    items(feedState.visibleCourses, key = { it.id }) { course ->
                        HomeCourseCard(
                            course = course,
                            isSaved = course.id in feedState.savedCourseIds,
                            onClick = { navigationHelper.navigateByRoute(CourseDetailPage.route(course.id)) },
                            onBookmarkClick = { feedViewModel.onIntent(HomeFeedIntent.ToggleSave(course.id)) },
                        )
                    }
                }

                feedState.isLoading -> {
                    item { FeedLoading() }
                }

                else -> {
                    item {
                        FeedMessage(
                            message =
                                feedState.errorMessage
                                    ?: if (feedState.selectedCategory == null) {
                                        "아직 공개된 코스가 없어요."
                                    } else {
                                        "불러온 코스에 해당 테마가 없어요."
                                    },
                            onRetry = feedState.errorMessage?.let { { feedViewModel.onIntent(HomeFeedIntent.Retry) } },
                        )
                    }
                }
            }
            if (feedState.isLoadingMore) {
                item { LoadingMoreFooter() }
            } else if (feedState.hasNext && !feedState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = { feedViewModel.onIntent(HomeFeedIntent.LoadMore) }) {
                            DsText(text = "코스 더 불러오기", style = DesignSystemThemeImpl.typeScale.textRegularS)
                        }
                    }
                }
            }
        }
    }
}

private val recommendedCategories =
    listOf(null to "추천순") + PlaceCategoryVO.entries.map { it.label to it.label }

@Composable
private fun RecommendedTopBar(
    selectedCategory: String?,
    onSelectCategory: (String?) -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var filterExpanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_left_24),
                    contentDescription = "뒤로가기",
                    tint = color.contentDefaultLevel0,
                )
            }
            DsText(
                text = "오늘의 추천 코스",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                modifier = Modifier.weight(1f),
            )
            Box {
                IconButton(onClick = { filterExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_filter_list_24),
                        contentDescription = "테마 필터",
                        tint = color.contentDefaultLevel0,
                    )
                }
                DropdownMenu(expanded = filterExpanded, onDismissRequest = { filterExpanded = false }) {
                    recommendedCategories.forEach { (category, label) ->
                        DropdownMenuItem(
                            text = { DsText(text = label, style = DesignSystemThemeImpl.typeScale.textRegularS) },
                            modifier = Modifier.semantics { selected = selectedCategory == category },
                            onClick = {
                                onSelectCategory(category)
                                filterExpanded = false
                            },
                        )
                    }
                }
            }
        }
        Row(
            modifier =
                Modifier
                    .horizontalScroll(
                        rememberScrollState(),
                    ).padding(horizontal = ScreenHorizontalPadding, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            recommendedCategories.forEach { (category, label) ->
                DsChip(
                    text = label,
                    selected = selectedCategory == category,
                    modifier = Modifier.semantics { selected = selectedCategory == category },
                    onClick = { onSelectCategory(category) },
                )
            }
        }
        HorizontalDivider(color = color.borderDefaultLevel0)
    }
}

/** 피드 로딩 자리. 홈 미리보기·전체 목록이 함께 쓴다. */
@Composable
internal fun FeedLoading() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = DesignSystemThemeImpl.designSystemColor.contentAccent)
    }
}

/** 피드가 비었거나 실패했을 때의 안내. [onRetry] 가 있으면 재시도를 노출한다. */
@Composable
internal fun FeedMessage(
    message: String,
    onRetry: (() -> Unit)?,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DsText(
            text = message,
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel2,
            maxLines = Int.MAX_VALUE,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
        if (onRetry != null) {
            DsText(
                text = "다시 시도",
                style = DesignSystemThemeImpl.typeScale.textRegularM,
                color = color.contentAccent,
                modifier = Modifier.clickable(onClick = onRetry),
            )
        }
    }
}
