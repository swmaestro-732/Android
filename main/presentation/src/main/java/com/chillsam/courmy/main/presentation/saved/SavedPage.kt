package com.chillsam.courmy.main.presentation.saved

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.LoadMoreOnScrollEnd
import com.chillsam.courmy.common.presentation.component.LoadingMoreFooter
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.login.LoginPage
import com.chillsam.courmy.main.domain.map.MapPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.entity.saved.SavedCourseVO
import com.chillsam.courmy.main.entity.saved.SavedPlaceVO
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.component.SavedCourseCard
import com.chillsam.courmy.main.presentation.component.SavedPlaceCard

/** 저장함의 콘텐츠 탭 인덱스. 화면 이탈 후 복귀에도 유지되도록 rememberSaveable 로 든다. */
internal const val SAVED_TAB_COURSES = 0
internal const val SAVED_TAB_PLACES = 1

/**
 * 저장(북마크) 화면(FS-14). 세션 로그인 여부에 따라
 * 저장한 코스/장소 리스트([SavedCourseList]·[SavedPlaceList]) / 게스트 잠금([GuestSaved]) 을 분기한다.
 */
@Composable
fun SavedPage(modifier: Modifier = Modifier) {
    val session = LocalSessionUiState.current
    if (session.isLoggedIn) {
        var selectedTab by rememberSaveable { mutableStateOf(SAVED_TAB_PLACES) }
        if (selectedTab == SAVED_TAB_COURSES) {
            SavedCourseList(modifier = modifier, onSelectTab = { selectedTab = it })
        } else {
            SavedPlaceList(modifier = modifier, onSelectTab = { selectedTab = it })
        }
    } else {
        GuestSaved(modifier = modifier)
    }
}

/** 로그인 상태 · 코스 탭: 저장한 코스 카드 리스트(`GET /service/v1/my/saved-courses`). */
@Composable
private fun SavedCourseList(
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedCoursesViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingRemoval by remember { mutableStateOf<SavedCourseVO?>(null) }

    // 다른 화면에서 저장한 코스가 반영되도록 다시 보일 때마다 새로 불러온다.
    RefreshOnResume { viewModel.onIntent(SavedCoursesIntent.Retry) }

    // 저장 취소 실패는 화면을 바꾸지 않고 토스트로만 알린다.
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(SavedCoursesIntent.ConsumeError)
        }
    }

    val scrollState = rememberScrollState()
    LoadMoreOnScrollEnd(scrollState) { viewModel.onIntent(SavedCoursesIntent.LoadMore) }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .statusBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding),
        ) {
            SavedHeader(selectedTab = SAVED_TAB_COURSES, onSelectTab = onSelectTab)
            SavedCourseFilters(state = uiState, onIntent = viewModel::onIntent)
            when {
                uiState.courses.isNotEmpty() -> {
                    uiState.courses.forEach { course ->
                        SavedCourseCard(
                            course = course,
                            onClick = { navigationHelper.navigateByRoute(CourseDetailPage.route(course.id)) },
                            onBookmarkClick = { pendingRemoval = course },
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }
                    if (uiState.isLoadingMore) {
                        LoadingMoreFooter()
                    }
                }

                uiState.isLoading -> {
                    SavedListLoading()
                }

                else -> {
                    SavedListMessage(
                        message = uiState.loadErrorMessage ?: "조건에 맞는 저장 코스가 없어요.",
                        onRetry =
                            uiState.loadErrorMessage?.let {
                                { viewModel.onIntent(SavedCoursesIntent.Retry) }
                            },
                    )
                }
            }
        }

        CourmyBottomBar(
            selectedTab = MainTab.SAVED,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.MAP -> navigationHelper.navigateTo(MapPage)
                    MainTab.MY -> navigationHelper.navigateTo(MyPage)
                    else -> Unit
                }
            },
        )
    }

    pendingRemoval?.let { course ->
        DsConfirmDialog(
            title = "저장을 취소할까요?",
            description = "저장을 취소하면 이 코스가 저장함에서 사라져요.",
            destructive = true,
            onConfirm = {
                viewModel.onIntent(SavedCoursesIntent.Unsave(course.id))
                pendingRemoval = null
            },
            onDismiss = { pendingRemoval = null },
        )
    }
}

/**
 * 로그인 상태 · 장소 탭: 저장한 장소 카드 리스트(`GET /service/v1/my/saved-places`).
 * 구조는 [SavedCourseList] 와 같다.
 *
 * 방문 여부와 카테고리 필터는 불러온 장소 목록에 적용한다.
 */
@Composable
private fun SavedPlaceList(
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedPlacesViewModel = hiltViewModel(),
) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingRemoval by remember { mutableStateOf<SavedPlaceVO?>(null) }

    // 다른 화면(장소 시트)에서 저장한 장소가 반영되도록 다시 보일 때마다 새로 불러온다.
    RefreshOnResume { viewModel.onIntent(SavedPlacesIntent.Retry) }

    // 저장 취소 실패는 화면을 바꾸지 않고 토스트로만 알린다.
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(SavedPlacesIntent.ConsumeError)
        }
    }

    val scrollState = rememberScrollState()
    LoadMoreOnScrollEnd(scrollState) { viewModel.onIntent(SavedPlacesIntent.LoadMore) }

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .statusBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding),
        ) {
            SavedHeader(selectedTab = SAVED_TAB_PLACES, onSelectTab = onSelectTab)
            SavedPlaceFilters(state = uiState, onIntent = viewModel::onIntent)
            when {
                uiState.visiblePlaces.isNotEmpty() -> {
                    uiState.visiblePlaces.forEach { place ->
                        SavedPlaceCard(
                            place = place,
                            onBookmarkClick = { pendingRemoval = place },
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }
                }

                uiState.isLoading -> {
                    SavedListLoading()
                }

                else -> {
                    SavedListMessage(
                        message = uiState.loadErrorMessage ?: "조건에 맞는 저장 장소가 없어요.",
                        onRetry =
                            uiState.loadErrorMessage?.let {
                                { viewModel.onIntent(SavedPlacesIntent.Retry) }
                            },
                    )
                }
            }
            SavedPlacesFooter(state = uiState, onLoadMore = { viewModel.onIntent(SavedPlacesIntent.LoadMore) })
        }

        CourmyBottomBar(
            selectedTab = MainTab.SAVED,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.MAP -> navigationHelper.navigateTo(MapPage)
                    MainTab.MY -> navigationHelper.navigateTo(MyPage)
                    else -> Unit
                }
            },
        )
    }

    pendingRemoval?.let { place ->
        DsConfirmDialog(
            title = "저장을 취소할까요?",
            description = "저장을 취소하면 이 장소가 저장함에서 사라져요.",
            destructive = true,
            onConfirm = {
                viewModel.onIntent(SavedPlacesIntent.Unsave(place.placeId))
                pendingRemoval = null
            },
            onDismiss = { pendingRemoval = null },
        )
    }
}

@Composable
private fun SavedPlacesFooter(
    state: SavedPlacesUIState,
    onLoadMore: () -> Unit,
) {
    if (state.isLoadingMore) {
        LoadingMoreFooter()
    } else if (state.hasNext && !state.isLoading) {
        androidx.compose.material3.TextButton(onClick = onLoadMore, modifier = Modifier.fillMaxWidth()) {
            DsText(text = "장소 더 불러오기", style = DesignSystemThemeImpl.typeScale.textRegularS)
        }
    }
}

/** 저장 목록 로딩 자리. */
@Composable
private fun SavedListLoading() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = DesignSystemThemeImpl.designSystemColor.contentAccent)
    }
}

/** 저장 목록이 비었거나 로드에 실패했을 때의 안내. [onRetry] 가 있으면 재시도를 노출한다. */
@Composable
private fun SavedListMessage(
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

/** 비로그인 상태: 저장 잠금 안내 + 로그인 유도. */
@Composable
private fun GuestSaved(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val color = DesignSystemThemeImpl.designSystemColor

    Column(modifier = modifier.fillMaxSize().background(color.bgDefaultLevel0)) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = ScreenHorizontalPadding),
        ) {
            DsText(
                text = "저장함",
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(width = 96.dp, height = 110.dp)
                            .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp))
                            .clip(RoundedCornerShape(24.dp))
                            .background(color.bgDefaultLevel1),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_bookmark_filled_24),
                        contentDescription = null,
                        tint = color.contentAccent,
                        modifier = Modifier.size(38.dp),
                    )
                }
                DsText(
                    text = "마음에 든 장소를\n여기에 모아보세요",
                    style = DesignSystemThemeImpl.typeScale.textStrongM,
                    color = color.contentDefaultLevel0,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 20.dp),
                )
                DsText(
                    text = "로그인하면 저장한 장소가 지도 위에 모이고,\n바로 나만의 코스로 이어집니다.",
                    style = DesignSystemThemeImpl.typeScale.textRegularS,
                    color = color.contentDefaultLevel2,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 10.dp),
                )
                DsButton(
                    text = "로그인 / 회원가입",
                    onClick = { navigationHelper.navigateTo(LoginPage) },
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                )
            }
        }

        CourmyBottomBar(
            selectedTab = MainTab.SAVED,
            onTabSelected = { tab ->
                when (tab) {
                    MainTab.HOME -> navigationHelper.navigateTo(HomePage)
                    MainTab.MAP -> navigationHelper.navigateTo(MapPage)
                    MainTab.MY -> navigationHelper.navigateTo(GuestMyPage)
                    else -> Unit
                }
            },
        )
    }
}
