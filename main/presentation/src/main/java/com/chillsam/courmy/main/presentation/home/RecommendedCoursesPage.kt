package com.chillsam.courmy.main.presentation.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.buttonClick
import com.chillsam.courmy.common.domain.telemetry.selectionChange
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.helper.StatusBarColor
import com.chillsam.courmy.common.presentation.telemetry.LocalTelemetry
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.entity.area.AreaLevel
import com.chillsam.courmy.main.presentation.component.CourseCardsSkeleton
import com.chillsam.courmy.main.presentation.component.HomeCourseCard
import com.chillsam.courmy.main.presentation.my.RegionMapIntent
import com.chillsam.courmy.main.presentation.my.RegionMapViewModel

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
    initialSigungu: String?,
    onIntent: (HomeFeedIntent) -> Unit,
) {
    var initialFilterApplied by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // SelectCategory·SetRegion 이 서버 재조회까지 겸하므로, 초기 필터를 적용한 진입에서는
        // 아래 Load 를 또 보내지 않는다(같은 요청 두 번 방지). 지역·테마는 진입 방식이 달라 동시 진입은 없다.
        if (!initialFilterApplied) {
            initialFilterApplied = true
            if (initialSigungu != null) {
                onIntent(HomeFeedIntent.SetRegion(initialSigungu))
                return@LaunchedEffect
            }
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
    sidoCode: String? = null,
    regionName: String? = null,
) {
    StatusBarColor(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel1)
    val navigationHelper = LocalNavigationHelper.current
    val telemetry = LocalTelemetry.current
    val context = LocalContext.current
    val feedViewModel: HomeFeedViewModel = hiltViewModel()
    val feedState by feedViewModel.uiState.collectAsStateWithLifecycle()

    // 지역 진입이면 그 시·도의 세부 지역(구·군) 칩을 지도 데이터에서 뽑는다. "전체"는 시·도 전체.
    val subRegions = rememberSubRegions(sidoCode)
    var selectedSub by rememberSaveable { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()
    FeedEntryEffects(
        feedState = feedState,
        listState = listState,
        // 홈 테마 타일은 코드("date")를 싣지만, 과거 라우트가 라벨("데이트")을 싣던 시기가 있어 둘 다 받는다.
        initialCategory = CourseTagVO.fromCodeOrLabel(initialCategory),
        initialSigungu = sidoCode,
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
            title = regionName?.let { "$it 코스" } ?: "오늘의 추천 코스",
            selectedCategory = feedState.selectedCategory,
            onSelectCategory = {
                telemetry.selectionChange("/home/recommended", "theme", it?.code)
                feedViewModel.onIntent(HomeFeedIntent.SelectCategory(it))
            },
            onBack = { navigationHelper.navigateToBack() },
            // 지역 진입이면 세부 지역 필터를, 아니면 null(= 테마 칩)을 넘긴다.
            regionFilter =
                sidoCode?.let {
                    RegionFilter(
                        subRegions = subRegions,
                        selected = selectedSub,
                        onSelect = { code ->
                            telemetry.selectionChange("/home/recommended", "region", code ?: "all")
                            selectedSub = code
                            // 선택한 세부 지역(없으면 시·도 전체)으로 피드를 좁힌다. [wiki-needed] 백엔드 선행:
                            // 서버가 아직 지역 필터를 안 받아 지금은 목록이 바뀌지 않는다(선택만 반영).
                            feedViewModel.onIntent(HomeFeedIntent.SetRegion(code ?: sidoCode))
                        },
                    )
                },
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
                            onClick = {
                                navigationHelper.navigateByRoute(
                                    CourseDetailPage.route(course.id),
                                    source = NavigationSource("/home/recommended", "course"),
                                )
                            },
                            onBookmarkClick = {
                                telemetry.buttonClick("/home/recommended", "bookmark", mapOf("course_id" to course.id))
                                feedViewModel.onIntent(HomeFeedIntent.ToggleSave(course.id))
                            },
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
    title: String,
    selectedCategory: CourseTagVO?,
    onSelectCategory: (CourseTagVO?) -> Unit,
    onBack: () -> Unit,
    regionFilter: RegionFilter?,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(Modifier.fillMaxWidth().background(color.bgDefaultLevel1)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
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
                text = title,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
        }
        // 선택된 칩이 오른쪽에 있어도 진입하자마자 보이도록 LazyRow 로 선택 위치까지 스크롤한다(SCRUM-793).
        val chipListState = rememberLazyListState()
        val selectedIndex =
            if (regionFilter != null) {
                regionFilter.subRegions.indexOfFirst { it.code == regionFilter.selected } + 1
            } else {
                recommendedCategories.indexOfFirst { (category, _) -> category == selectedCategory }
            }
        LaunchedEffect(selectedIndex) {
            if (selectedIndex > 0) chipListState.animateScrollToItem(selectedIndex)
        }
        LazyRow(
            state = chipListState,
            modifier = Modifier.padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (regionFilter != null) {
                // 지역 진입: 세부 지역(구·군) 칩만. 첫 칩 "추천순"이 그 시·도 전체다(카테고리 칩은 두지 않는다).
                item {
                    DsChip(
                        text = "추천순",
                        selected = regionFilter.selected == null,
                        onClick = { regionFilter.onSelect(null) },
                    )
                }
                items(regionFilter.subRegions) { sub ->
                    DsChip(
                        text = sub.name,
                        selected = regionFilter.selected == sub.code,
                        onClick = { regionFilter.onSelect(sub.code) },
                    )
                }
            } else {
                // 일반 "오늘의 추천 코스": 코스 태그(테마) 칩.
                items(recommendedCategories) { (category, label) ->
                    DsChip(
                        text = label,
                        selected = selectedCategory == category,
                        modifier = Modifier.semantics { selected = selectedCategory == category },
                        onClick = { onSelectCategory(category) },
                    )
                }
            }
        }
        HorizontalDivider(color = color.borderDefaultLevel0)
    }
}

/** 지역 진입 화면의 세부 지역 칩 하나(구·군). [code] 는 법정동 5자리, [name] 은 짧은 이름. */
internal data class SubRegion(
    val code: String,
    val name: String,
)

/** 지역 진입 시 상단 바에 넘기는 세부 지역 필터 묶음. [selected] 가 null 이면 "추천순"(시·도 전체)이다. */
private class RegionFilter(
    val subRegions: List<SubRegion>,
    val selected: String?,
    val onSelect: (String?) -> Unit,
)

/** [sidoCode] 시·도의 세부 지역(구·군) 목록을 지도 데이터에서 뽑는다. 지역 진입이 아니면 빈 목록. */
@Composable
private fun rememberSubRegions(sidoCode: String?): List<SubRegion> {
    if (sidoCode == null) return emptyList()
    val viewModel: RegionMapViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.onIntent(RegionMapIntent.Initialize(editing = false, regions = emptyList()))
    }
    return remember(state.regions, sidoCode) {
        state.regions
            .filter { it.area.level == AreaLevel.SIGUNGU && it.area.code.startsWith(sidoCode) }
            .map { SubRegion(it.area.code, it.area.shortName) }
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
