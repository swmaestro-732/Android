package com.chillsam.courmy.main.presentation.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsRatingStar
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.course.domain.DraftListPage
import com.chillsam.courmy.main.domain.home.RecommendedCoursesPage
import com.chillsam.courmy.main.domain.map.MapPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.domain.search.SearchPage
import com.chillsam.courmy.main.domain.search.SearchResultPage
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.home.HomeNearbyPlaceVO
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.component.BottomBarHeight
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.CourseLikeCount
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.component.PlaceThumbnail
import com.chillsam.courmy.main.presentation.map.MapFocusRequestStore
import com.chillsam.courmy.main.presentation.my.MyProfileIntent
import com.chillsam.courmy.main.presentation.my.MyViewModel
import com.chillsam.courmy.main.presentation.profile.ProfileAvatar
import com.chillsam.courmy.main.presentation.R as MainR

/** 추천 코스 미리보기 카드 수. 전체 목록은 "전체보기"([RecommendedCoursesPage])가 맡는다. */
private const val RECOMMENDED_PREVIEW_COUNT = 5

/** 헤더 보조줄의 위치·날씨. 위치·날씨 연동 전까지 시안 고정값을 쓴다. */
private const val LOCATION_WEATHER_MOCK = "성수동 · 흐림 18°"

/** 테마별 둘러보기 타일 한 장: 라벨 + 테마를 대표하는 아이콘. */
private data class ThemeTileSpec(
    val label: String,
    @param:DrawableRes val iconRes: Int,
)

/** 테마별 둘러보기 타일. 탭하면 테마명을 검색어로 하는 검색 결과로 보낸다. */
private val themeTiles: List<ThemeTileSpec> =
    listOf(
        ThemeTileSpec("데이트 코스", R.drawable.ic_heart_24),
        ThemeTileSpec("혼술 · 회식", MainR.drawable.ic_place_bar_24),
        ThemeTileSpec("전시 · 산책", MainR.drawable.ic_place_culture_24),
        ThemeTileSpec("카페 투어", MainR.drawable.ic_place_cafe_24),
    )

/**
 * 앱의 기본 시작 화면(FS-09). 새 시안 기준으로 섹션형 홈으로 개편했다.
 *
 * 헤더(위치·날씨 mock + 인사) → 검색바 → 오늘의 추천 코스(가로 미리보기)
 * → 근처 저장 장소 → 테마별 둘러보기 순서다.
 *
 * 추천 코스·근처 저장 장소는 `GET /service/v2/home`(공개 엔드포인트) 한 번으로 받는다.
 * 세로 피드 전체는 [RecommendedCoursesPage] 로 옮겼다(`GET /service/v1/home` 페이지네이션 유지)
 * — 홈은 앞쪽 몇 건만 미리 보여준다.
 * "커미 AI" 배너·"진행 중인 코스" 띠는 이번 범위에서 제외했다. [wiki-needed]
 */
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    var menuExpanded by remember { mutableStateOf(false) }

    // 홈 화면 조합(GET /service/v2/home). 공개 엔드포인트라 게스트에서도 그대로 부른다.
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    // 헤더 인사말·아바타는 내 프로필에서 가져온다.
    val profile = if (session.isLoggedIn) myProfileForHeader() else null

    // 코스를 만들거나 장소를 저장하고 돌아오면 섹션이 바뀌어 있을 수 있어 다시 불러온다.
    RefreshOnResume { homeViewModel.onIntent(HomeIntent.Retry) }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(DesignSystemThemeImpl.designSystemColor.bgDefaultLevel0),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding =
                PaddingValues(
                    top = 12.dp,
                    bottom = BottomBarHeight + 40.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                HomeHeader(
                    profile = profile,
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                )
            }
            item {
                HomeSearchBar(
                    onClick = { navigationHelper.navigateTo(SearchPage) },
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                )
            }
            item {
                RecommendedCoursesSection(
                    homeState = homeState,
                    onSeeAll = { navigationHelper.navigateTo(RecommendedCoursesPage) },
                    onCourseClick = { id -> navigationHelper.navigateByRoute(CourseDetailPage.route(id)) },
                    onRetry = { homeViewModel.onIntent(HomeIntent.Retry) },
                )
            }
            // 비로그인이거나 근처에 저장한 곳이 없으면 섹션째 숨긴다(빈 헤더만 뜨지 않게).
            if (homeState.nearbyPlaces.isNotEmpty()) {
                item {
                    NearbySavedSection(
                        places = homeState.nearbyPlaces,
                        onSeeMap = { navigationHelper.navigateTo(MapPage) },
                        onPlaceClick = { placeId ->
                            // 지도 탭이 장소 목록을 받은 뒤 이 장소를 선택해(시트·카메라) 보여준다.
                            MapFocusRequestStore.request(placeId)
                            navigationHelper.navigateTo(MapPage)
                        },
                        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                    )
                }
            }
            item {
                ThemeBrowseSection(
                    onThemeClick = { theme ->
                        navigationHelper.navigateByRoute(SearchResultPage.route(theme))
                    },
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                )
            }
        }

        HomeBottomBar(
            loggedIn = session.isLoggedIn,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        // 코스 만들기 FAB 는 로그인 상태에서만 노출한다.
        if (session.isLoggedIn) {
            CreateCourseMenu(
                expanded = menuExpanded,
                onToggle = { menuExpanded = !menuExpanded },
                onNewCourse = {
                    menuExpanded = false
                    navigationHelper.navigateTo(CourseCreatePage)
                },
                onLoadDraft = {
                    menuExpanded = false
                    navigationHelper.navigateTo(DraftListPage)
                },
                onNewPlan = {
                    menuExpanded = false
                    navigationHelper.navigateByRoute(CourseCreatePage.planRoute())
                },
            )
        }
    }
}

/**
 * 홈 검색바(FS-09). 여기서 직접 입력받지 않고 검색 화면으로 넘긴다 —
 * 최근 검색어·급상승 검색어를 함께 보여줘야 해서 입력은 그쪽에서 받는다.
 */
@Composable
private fun HomeSearchBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(color.bgDefaultLevel1)
                .border(width = 1.dp, color = color.borderDefaultLevel0, shape = shape)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search_24),
            contentDescription = null,
            tint = color.contentDefaultLevel2,
            modifier = Modifier.size(18.dp),
        )
        DsText(
            text = "코스 검색",
            style = DesignSystemThemeImpl.typeScale.textRegularS,
            color = color.contentDefaultLevel3,
        )
    }
}

/** 홈의 하단 탭바. 홈은 지금 보고 있는 탭이라 다시 눌러도 아무 일도 하지 않는다. */
@Composable
private fun HomeBottomBar(
    loggedIn: Boolean,
    modifier: Modifier = Modifier,
) {
    val navigationHelper = LocalNavigationHelper.current
    CourmyBottomBar(
        selectedTab = MainTab.HOME,
        onTabSelected = { tab ->
            when (tab) {
                // 로그인 전이면 게스트 마이, 로그인 후면 마이·프로필로 분기.
                MainTab.MY -> navigationHelper.navigateTo(if (loggedIn) MyPage else GuestMyPage)

                MainTab.SAVED -> navigationHelper.navigateTo(SavedPage)

                MainTab.MAP -> navigationHelper.navigateTo(MapPage)

                MainTab.HOME -> Unit
            }
        },
        modifier = modifier,
    )
}

/**
 * 홈 헤더용 내 프로필.
 *
 * [MyViewModel] 은 생성 즉시 `GET /service/v1/mypage` 를 호출하므로 **로그인 상태에서만** 만든다.
 * 게스트도 보는 홈에서 무조건 만들면 JWT 없이 요청이 나가 401 로 실패하고,
 * refreshToken 도 없어 TokenAuthenticator 가 재시도를 포기한다.
 */
@Composable
private fun myProfileForHeader(): MyProfileVO? {
    val myViewModel: MyViewModel = hiltViewModel()
    val myState by myViewModel.uiState.collectAsStateWithLifecycle()
    // MyViewModel 은 init 에서 로드하지 않으므로 여기서도 호출해야 헤더가 채워진다.
    RefreshOnResume { myViewModel.onIntent(MyProfileIntent.Retry) }
    return myState.profile
}

/**
 * 홈 헤더: 위치·날씨 보조줄 → 인사 타이틀 + 우상단 아바타.
 *
 * 위치·날씨는 연동 전이라 시안 고정값([LOCATION_WEATHER_MOCK])을 쓴다.
 * 로그인했으면 닉네임을 넣어 부르고, 게스트는 닉네임 없는 기존 문구를 유지한다.
 */
@Composable
private fun HomeHeader(
    profile: MyProfileVO?,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val greeting =
        if (profile != null) {
            "${profile.nickname}님, 오늘은\n어디로 떠나볼까요?"
        } else {
            "어디로 떠나볼까요?"
        }
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = LOCATION_WEATHER_MOCK,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
            )
            DsText(
                text = greeting,
                modifier = Modifier.padding(top = 6.dp),
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                maxLines = 2,
            )
        }
        ProfileAvatar(
            imageUrl = profile?.profileImageUrl.orEmpty(),
            size = 44.dp,
            modifier = Modifier.padding(start = 12.dp),
        )
    }
}

/** 섹션 공통 헤더: 좌측 제목 + 우측 액션 링크. */
@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(
            text = title,
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
            modifier = Modifier.weight(1f),
        )
        DsText(
            text = actionLabel,
            style = DesignSystemThemeImpl.typeScale.textRegularXS,
            color = color.contentAccent,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .clickable(onClick = onAction)
                    .padding(horizontal = 4.dp, vertical = 4.dp),
        )
    }
}

/**
 * "오늘의 추천 코스" 섹션: 피드 앞쪽 [RECOMMENDED_PREVIEW_COUNT] 건을 가로 카드로 미리 보여준다.
 *
 * 가로 스크롤 카드가 화면 가장자리까지 흐르도록 섹션 자체는 수평 패딩 없이 두고,
 * 헤더·카드 목록의 contentPadding 으로만 여백을 준다.
 */
@Composable
private fun RecommendedCoursesSection(
    homeState: HomeUIState,
    onSeeAll: () -> Unit,
    onCourseClick: (String) -> Unit,
    onRetry: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "오늘의 추천 코스",
            actionLabel = "전체보기",
            onAction = onSeeAll,
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
        )
        // 헤더·다른 섹션은 늘 보여야 하므로, 로딩·에러는 카드 자리에서만 분기한다.
        when {
            homeState.courses.isNotEmpty() -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = ScreenHorizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        homeState.courses.take(RECOMMENDED_PREVIEW_COUNT),
                        key = { it.id },
                    ) { course ->
                        RecommendedCourseCard(
                            course = course,
                            onClick = { onCourseClick(course.id) },
                        )
                    }
                }
            }

            homeState.isLoading -> {
                FeedLoading()
            }

            else -> {
                FeedMessage(
                    message = homeState.errorMessage ?: "아직 공개된 코스가 없어요.",
                    onRetry = homeState.errorMessage?.let { { onRetry() } },
                )
            }
        }
    }
}

/**
 * 추천 코스 가로 카드: 상단 썸네일(카테고리 칩) + 하단 흰 배경의 제목·메타.
 *
 * TODO-API-SPEC: 시안의 메타는 "4 스팟 · 3시간"(스팟 수·소요 시간)이지만
 * 피드 응답(`CourseFeedResponse.Item`)에 해당 필드가 없어 저장 수로 대신한다.
 * 서버가 필드를 내려주면 교체한다. [wiki-needed]
 */
@Composable
private fun RecommendedCourseCard(
    course: HomeCourseVO,
    onClick: () -> Unit,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(
        modifier =
            Modifier
                .width(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1)
                .border(width = 1.dp, color = color.borderDefaultLevel0, shape = RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(color.imagePlaceholder),
        ) {
            if (course.coverImageUrl.isNotBlank()) {
                AsyncImage(
                    model = course.coverImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            // 카테고리가 없는 코스(theme 미선택)는 빈 알약이 뜨지 않도록 칩 자체를 생략한다.
            if (course.categoryLabel.isNotBlank()) {
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(color.bgDefaultLevel1)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    DsText(
                        text = course.categoryLabel,
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel1,
                    )
                }
            }
        }
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            DsText(
                text = course.title,
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
                maxLines = 2,
                // 제목이 한 줄이어도 두 줄 높이를 확보한다 — 가로 카드끼리 높이가 들쭉날쭉하지 않게.
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CourseLikeCount(count = course.likeCountText)
                if (course.saveCountText.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_tab_bookmark_24),
                            contentDescription = "저장",
                            tint = color.contentDefaultLevel3,
                            modifier = Modifier.size(16.dp),
                        )
                        DsText(
                            text = course.saveCountText,
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = color.contentDefaultLevel2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

/** "근처 저장 장소" 섹션. `GET /service/v2/home` 의 `nearbySavedPlaces` 를 그대로 그린다. */
@Composable
private fun NearbySavedSection(
    places: List<HomeNearbyPlaceVO>,
    onSeeMap: () -> Unit,
    onPlaceClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "근처 저장 장소",
            actionLabel = "지도에서 보기",
            onAction = onSeeMap,
        )
        places.forEach { place ->
            NearbyPlaceCard(
                place = place,
                onClick = { onPlaceClick(place.id) },
            )
        }
    }
}

/** 근처 저장 장소 카드: 썸네일 + 이름·평점·카테고리·도보 시간 + 방문 여부 칩. 탭하면 지도에서 그 장소를 보여준다. */
@Composable
private fun NearbyPlaceCard(
    place: HomeNearbyPlaceVO,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color.bgDefaultLevel1)
                .border(width = 1.dp, color = color.borderDefaultLevel0, shape = RoundedCornerShape(16.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PlaceThumbnail(
            url = place.thumbnailUrl,
            category = place.categoryLabel,
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = place.name,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                // 평가가 없는 장소는 별 아이콘째 숨긴다("0.0"이 뜨지 않게).
                if (place.ratingText.isNotBlank()) {
                    DsRatingStar(starSize = 12.dp)
                }
                DsText(
                    text = nearbyPlaceMeta(place),
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Box(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(color.bgDefaultLevel0)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            DsText(
                text = if (place.visited) "방문" else "미방문",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel2,
            )
        }
    }
}

/** 평점·카테고리·도보 시간을 " · "로 이은 메타 한 줄(예: "4.8 · 카페·디저트 · 도보 6분"). */
private fun nearbyPlaceMeta(place: HomeNearbyPlaceVO): String =
    buildList {
        if (place.ratingText.isNotBlank()) add(place.ratingText)
        if (place.categoryLabel.isNotBlank()) add(place.categoryLabel)
        place.walkingMinutes?.let { add("도보 ${it}분") }
    }.joinToString(" · ")

/** "테마별 둘러보기" 섹션: 2열 그리드 타일 4개. 탭하면 테마명 검색 결과로 보낸다. */
@Composable
private fun ThemeBrowseSection(
    onThemeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        DsText(
            text = "테마별 둘러보기",
            style = DesignSystemThemeImpl.typeScale.textStrongM,
            color = color.contentDefaultLevel0,
        )
        // 항목이 4개로 고정이라 LazyVerticalGrid 대신 Row 두 줄로 그린다(LazyColumn 중첩 제약도 피한다).
        themeTiles.chunked(2).forEach { rowTiles ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowTiles.forEach { theme ->
                    ThemeTile(
                        spec = theme,
                        onClick = { onThemeClick(theme.label) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

/**
 * 테마 타일. 대표 이미지 없이 아이콘으로 테마를 표현한다 —
 * 장소 썸네일 placeholder([com.chillsam.courmy.main.presentation.component.PlaceThumbnail])와
 * 같은 모티프(밝은 강조색 그라데이션 + 아이콘)라 앱 안에서 톤이 이어진다.
 * 우상단에는 큰 워터마크 아이콘, 좌하단에는 라벨 옆 작은 아이콘 배지를 둔다(raw hex 금지, 토큰 알파 조합만).
 */
@Composable
private fun ThemeTile(
    spec: ThemeTileSpec,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .height(96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        listOf(color.bgAccentSubtle, color.bgDefaultLevel1),
                    ),
                ).border(width = 1.dp, color = color.borderDefaultLevel0, shape = RoundedCornerShape(16.dp))
                .clickable(onClick = onClick),
    ) {
        // 우상단 워터마크: 테마 아이콘을 크게, 은은하게 깔아 빈 느낌을 지운다.
        Icon(
            painter = painterResource(spec.iconRes),
            contentDescription = null,
            tint = color.contentAccent.copy(alpha = 0.14f),
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp)
                    .size(56.dp),
        )
        Row(
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .background(color.bgDefaultLevel1.copy(alpha = 0.65f), CircleShape)
                        .padding(5.dp),
            ) {
                Icon(
                    painter = painterResource(spec.iconRes),
                    contentDescription = null,
                    tint = color.contentAccent,
                    modifier = Modifier.size(16.dp),
                )
            }
            DsText(
                text = spec.label,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentDefaultLevel0,
            )
        }
    }
}
