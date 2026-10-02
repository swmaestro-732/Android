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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
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
fun RecommendedCoursesPage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val context = LocalContext.current
    val feedViewModel: HomeFeedViewModel = hiltViewModel()
    val feedState by feedViewModel.uiState.collectAsStateWithLifecycle()

    // 코스를 만들거나 저장한 뒤 돌아오면 목록이 바뀌어 있을 수 있어 다시 불러온다(홈과 동일).
    RefreshOnResume { feedViewModel.onIntent(HomeFeedIntent.Retry) }

    val listState = rememberLazyListState()
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
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0)
                .statusBarsPadding(),
    ) {
        RecommendedTopBar(onBack = { navigationHelper.navigateToBack() })
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = ScreenHorizontalPadding,
                    end = ScreenHorizontalPadding,
                    top = 8.dp,
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
                    if (feedState.isLoadingMore) {
                        item { LoadingMoreFooter() }
                    }
                }

                feedState.isLoading -> {
                    item { FeedLoading() }
                }

                else -> {
                    item {
                        FeedMessage(
                            message = feedState.errorMessage ?: "아직 공개된 코스가 없어요.",
                            onRetry = feedState.errorMessage?.let { { feedViewModel.onIntent(HomeFeedIntent.Retry) } },
                        )
                    }
                }
            }
        }
    }
}

/** 뒤로가기 + 화면 제목. 홈에서 "전체보기"로 들어오는 화면이라 홈과 같은 섹션명을 쓴다. */
@Composable
private fun RecommendedTopBar(onBack: () -> Unit) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left_24),
                contentDescription = "뒤로가기",
                tint = color.contentDefaultLevel0,
                modifier = Modifier.size(24.dp),
            )
        }
        DsText(
            text = "오늘의 추천 코스",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
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
