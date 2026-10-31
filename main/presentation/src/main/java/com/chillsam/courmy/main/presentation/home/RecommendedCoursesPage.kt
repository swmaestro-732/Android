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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.chillsam.courmy.common.entity.category.CourseTagVO
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
import com.chillsam.courmy.main.presentation.component.CourseCardsSkeleton
import com.chillsam.courmy.main.presentation.component.HomeCourseCard

/**
 * 진입·복귀 시 1회성 효과 모음. 복귀 재컴포지션에서 다시 실행돼 무한 스크롤 위치·칩 선택을
 * 날리지 않도록 전부 rememberSaveable 가드를 둔다(SCRUM-631).
 *
 * - 초기 카테고리(홈 테마 타일 진입)는 최초 1회만 적용 — 재적용하면 사용자가 바꾼 칩이 되돌아간다.
 * - 목록 로드는 최초 1회만. 복귀 시 전체 재로드(Retry)는 쌓아둔 페이지와 스크롤 위치를 날리므로,
 *   상세에서 바뀔 수 있는 저장 상태만 동기화한다.
 * - 상단 이동은 칩이 실제로 바뀐 경우에만 — 키 기반 LaunchedEffect 를 그대로 쓰면 복귀에서도
 *   실행돼 저장된 스크롤 위치를 지워 버린다.
 */
@Composable
private fun FeedEntryEffects(
    feedState: HomeFeedUIState,
    listState: LazyListState,
    initialCategory: CourseTagVO?,
    onIntent: (HomeFeedIntent) -> Unit,
) {
    var initialCategoryApplied by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // SelectCategory 가 서버 재조회까지 겸하므로, 초기 카테고리를 적용한 진입에서는
        // 아래 Load 를 또 보내지 않는다(같은 요청 두 번 방지).
        if (!initialCategoryApplied) {
            initialCategoryApplied = true
            if (initialCategory != null) {
                onIntent(HomeFeedIntent.SelectCategory(initialCategory))
                return@LaunchedEffect
            }
        }
        if (feedState.courses.isEmpty()) onIntent(HomeFeedIntent.Load)
    }
    RefreshOnResume { onIntent(HomeFeedIntent.SyncSavedIds) }

    var scrolledForCategory by rememberSaveable { mutableStateOf(feedState.selectedCategory) }
    LaunchedEffect(feedState.selectedCategory) {
        if (scrolledForCategory != feedState.selectedCategory) {
            scrolledForCategory = feedState.selectedCategory
            listState.scrollToItem(0)
        }
    }
}

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

    val listState = rememberLazyListState()
    FeedEntryEffects(
        feedState = feedState,
        listState = listState,
        // 홈 테마 타일은 코드("date")를 싣지만, 과거 라우트가 라벨("데이트")을 싣던 시기가 있어 둘 다 받는다.
        initialCategory = CourseTagVO.fromCodeOrLabel(initialCategory),
        onIntent = feedViewModel::onIntent,
    )
    LoadMoreOnScrollEnd(listState) { feedViewModel.onIntent(HomeFeedIntent.LoadMore) }

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
            when {
                feedState.courses.isNotEmpty() -> {
                    items(feedState.courses, key = { it.id }) { course ->
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
                                        "해당 테마의 코스가 아직 없어요."
                                    },
                            onRetry = feedState.errorMessage?.let { { feedViewModel.onIntent(HomeFeedIntent.Retry) } },
                        )
                    }
                }
            }
            // 로딩 전후 아이템 수·높이를 유지해 실패 시 끝 감지가 자동 재시도를 반복하지 않게 한다.
            item(key = "paging-footer") {
                Box(Modifier.fillMaxWidth().height(54.dp)) {
                    if (feedState.isLoadingMore) LoadingMoreFooter()
                }
            }
        }
    }
}

// 코스 피드 필터이므로 코스 태그 12종을 쓴다. null 은 전체(추천순)이고 서버에는 category 를 빼고 보낸다.
private val recommendedCategories: List<Pair<CourseTagVO?, String>> =
    listOf<Pair<CourseTagVO?, String>>(null to "추천순") + CourseTagVO.entries.map { it to it.label }

@Composable
private fun RecommendedTopBar(
    selectedCategory: CourseTagVO?,
    onSelectCategory: (CourseTagVO?) -> Unit,
    onBack: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
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
            )
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
    CourseCardsSkeleton()
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
