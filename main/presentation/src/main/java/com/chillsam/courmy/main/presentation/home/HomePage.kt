package com.chillsam.courmy.main.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.chillsam.courmy.common.domain.navigation.NavigationSource
import com.chillsam.courmy.common.domain.telemetry.buttonClick
import com.chillsam.courmy.common.entity.category.CourseTagVO
import com.chillsam.courmy.common.entity.category.orCategoryLabel
import com.chillsam.courmy.common.presentation.R
import com.chillsam.courmy.common.presentation.component.DsButton
import com.chillsam.courmy.common.presentation.component.DsButtonVariant
import com.chillsam.courmy.common.presentation.component.DsConfirmDialog
import com.chillsam.courmy.common.presentation.component.DsRatingStar
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.PlaceThumbnail
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.common.presentation.helper.RefreshOnResume
import com.chillsam.courmy.common.presentation.telemetry.LocalTelemetry
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.common.presentation.ui.token.ScreenHorizontalPadding
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.course.domain.CourseDetailPage
import com.chillsam.courmy.course.domain.CourseFollowPage
import com.chillsam.courmy.course.domain.DraftListPage
import com.chillsam.courmy.main.domain.aichat.AiCourseChatPage
import com.chillsam.courmy.main.domain.chat.ChatPage
import com.chillsam.courmy.main.domain.home.RecommendedCoursesPage
import com.chillsam.courmy.main.domain.map.MapPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.saved.SavedPage
import com.chillsam.courmy.main.domain.search.SearchPage
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import com.chillsam.courmy.main.entity.home.HomeInProgressCourseVO
import com.chillsam.courmy.main.entity.home.HomeNearbyPlaceVO
import com.chillsam.courmy.main.entity.my.MyProfileVO
import com.chillsam.courmy.main.presentation.FeatureFlags
import com.chillsam.courmy.main.presentation.component.BottomBarHeight
import com.chillsam.courmy.main.presentation.component.CourmyBottomBar
import com.chillsam.courmy.main.presentation.component.CourseLikeCount
import com.chillsam.courmy.main.presentation.component.MainTab
import com.chillsam.courmy.main.presentation.component.RecommendedCardsSkeleton
import com.chillsam.courmy.main.presentation.component.hasLocationPermission
import com.chillsam.courmy.main.presentation.component.themeImageRes
import com.chillsam.courmy.main.presentation.map.MapFocusRequestStore
import com.chillsam.courmy.main.presentation.my.MyProfileIntent
import com.chillsam.courmy.main.presentation.my.MyViewModel
import com.chillsam.courmy.main.presentation.notification.NotificationConsentSheet
import com.chillsam.courmy.main.presentation.notification.NotificationConsentViewModel
import com.chillsam.courmy.main.presentation.profile.ProfileAvatar
import com.google.android.gms.location.LocationServices

/** 추천 코스 미리보기 카드 수. 전체 목록은 "전체보기"([RecommendedCoursesPage])가 맡는다. */
private const val RECOMMENDED_PREVIEW_COUNT = 5

/**
 * 진행 중인 코스 종료(end)가 서버에 실제로 기록되면 저장함(코스 · 완주)으로 보낸다
 * — 다이얼로그 안내("저장함에서 볼 수 있어요")와 목적지를 맞춘다. 실패하면 이동하지 않는다.
 */
@Composable
private fun InProgressEndedEffect(
    ended: Boolean,
    onConsume: () -> Unit,
    onNavigate: () -> Unit,
) {
    LaunchedEffect(ended) {
        if (ended) {
            onConsume()
            onNavigate()
        }
    }
}

/** 위치 권한·GPS 가 모두 켜져 있으면 마지막 위치를 받아 [onLocation] 으로 넘긴다(근처 저장 장소 기준점). */
@Composable
private fun NearbyLocationEffect(
    hasPermission: Boolean,
    gpsEnabled: Boolean,
    context: Context,
    onLocation: (Double, Double) -> Unit,
) {
    LaunchedEffect(hasPermission, gpsEnabled) {
        if (hasPermission && gpsEnabled) {
            fetchLastLocation(context) { lat, lng -> onLocation(lat, lng) }
        }
    }
}

/**
 * 타일 지도 코드 → 지역 코스 필터 코드. area_map 은 광주·전남을 코드 "12"로 두므로 상위 코드로 매핑한다.
 * 울릉("47U")은 경북("47")으로 보내면 경북 전체 구·군(포항시 남구 등)이 울릉 밑에 나오므로,
 * 울릉군 시군구 코드("47940")로 바로 좁힌다 — 칩·피드 필터 모두 prefix 비교라 울릉군만 남는다.
 */
private fun areaMapProvinceCode(tileCode: String): String =
    when (tileCode) {
        "29" -> "12"
        "47U" -> "47940"
        else -> tileCode
    }

/** 테마별 둘러보기 타일. 탭하면 추천 코스의 해당 코스 태그 필터를 연다(코스 문맥이라 12종). */
private val themeTiles = CourseTagVO.entries

/**
 * 앱의 기본 시작 화면(FS-09). 새 시안 기준으로 섹션형 홈으로 개편했다.
 *
 * 헤더(위치·날씨 mock + 인사) → 검색바 → 커미 AI 배너 → 오늘의 추천 코스(가로 미리보기)
 * → 근처 저장 장소 → 테마별 둘러보기 순서다.
 *
 * 추천 코스·근처 저장 장소는 `GET /service/v2/home`(공개 엔드포인트) 한 번으로 받는다.
 * 세로 피드 전체는 [RecommendedCoursesPage] 로 옮겼다(`GET /service/v1/home` 페이지네이션 유지)
 * — 홈은 앞쪽 몇 건만 미리 보여준다.
 * "커미 AI" 배너는 [CourmyAiCard] 로 추가했다(탭 → [AiCourseChatPage], SCRUM-745).
 * 추천 API 미연동(목업)이라 [FeatureFlags.AI_COURSE_CHAT_ENABLED] 로 감춰 두고, repo 기본값은 false 다.
 * "진행 중인 코스" 띠는 아직 범위에서 제외했다. [wiki-needed]
 */
@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val telemetry = LocalTelemetry.current
    val navigationHelper = LocalNavigationHelper.current
    val session = LocalSessionUiState.current
    var menuExpanded by remember { mutableStateOf(false) }

    // 홈 화면 조합(GET /service/v2/home). 공개 엔드포인트라 게스트에서도 그대로 부른다.
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    // 헤더 인사말·아바타는 내 프로필에서 가져온다.
    val profile = if (session.isLoggedIn) myProfileForHeader() else null

    InProgressEndedEffect(
        ended = homeState.inProgressEnded,
        onConsume = { homeViewModel.onIntent(HomeIntent.ConsumeInProgressEnded) },
        onNavigate = {
            navigationHelper.navigateByRoute(
                SavedPage.completedCourses(),
                source = NavigationSource("/home", "complete_course_success", trigger = "result"),
            )
        },
    )

    // 근처 저장 장소의 거리 기준점(SCRUM-634). 권한·GPS 상태는 설정에 다녀오면 바뀔 수 있어
    // 복귀할 때마다 다시 평가한다(locationTick).
    val context = LocalContext.current
    var locationTick by remember { mutableIntStateOf(0) }
    val hasLocationPermission = remember(locationTick) { context.hasLocationPermission() }
    val gpsEnabled = remember(locationTick) { context.isLocationEnabled() }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            locationTick++
        }
    NearbyLocationEffect(hasLocationPermission, gpsEnabled, context) { lat, lng ->
        homeViewModel.onIntent(HomeIntent.UpdateLocation(lat, lng))
    }

    // 코스를 만들거나 장소를 저장하고 돌아오면 섹션이 바뀌어 있을 수 있어 다시 불러온다.
    RefreshOnResume {
        homeViewModel.onIntent(HomeIntent.Retry)
        locationTick++
    }

    // 홈 최초 진입 시 알림 수신 동의를 받는다(SCRUM-725). 기기 단위라 게스트에게도 띄운다.
    HomeNotificationConsent()

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
                    // 바텀바는 BottomBarHeight 위에 navigationBarsPadding 을 더해 그려진다. 콘텐츠도
                    // 같은 네비게이션 바 인셋을 빼줘야 3버튼 네비 기기에서 마지막 줄이 바텀바에 가리지 않는다.
                    bottom =
                        BottomBarHeight + 40.dp +
                            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            item {
                HomeHeader(
                    profile = profile,
                    onProfileClick = {
                        telemetry.buttonClick("/home", "profile")
                        navigationHelper.navigateTo(
                            myPageDestination(session.isLoggedIn),
                            source = NavigationSource("/home", "profile"),
                        )
                    },
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                )
            }
            item {
                HomeSearchBar(
                    onClick = {
                        telemetry.buttonClick("/home", "search")
                        navigationHelper.navigateTo(SearchPage, source = NavigationSource("/home", "search"))
                    },
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                )
            }

            // 진행 중이던(따라가는) 코스가 있으면 추천보다 위에 띄워 이어가기를 먼저 유도한다. 없으면(null) 숨긴다.
            inProgressCourseItem(
                course = homeState.inProgressCourse,
                onResume = { course ->
                    // 현재 진행도(traceId·방문 수·시작 시각)를 실어 미리보기 대신 이어가기로 연다.
                    navigationHelper.navigateByRoute(
                        CourseFollowPage.route(
                            courseId = course.courseId,
                            traceId = course.traceId,
                            visitedPlaceCount = course.visitedPlaceCount ?: 0,
                            startedAt = course.startedAt,
                        ),
                        source = NavigationSource("/home", "in_progress_course"),
                    )
                },
                onDismiss = { homeViewModel.onIntent(HomeIntent.DismissInProgressCourse) },
                modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
            )

            // 커미 AI 코스 추천 배너(FS-09). 게스트에서도 둘러보는 진입점이라 세션 분기 없이 노출한다.
            // 추천 API 미연동(목업)이라 FeatureFlags 로 감춘다 — 라우트 등록도 같은 플래그로 묶는다.
            if (FeatureFlags.AI_COURSE_CHAT_ENABLED) {
                item {
                    CourmyAiCard(
                        onClick = { navigationHelper.navigateTo(AiCourseChatPage) },
                        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                    )
                }
            }

            item {
                RecommendedCoursesSection(
                    homeState = homeState,
                    onSeeAll = {
                        navigationHelper.navigateTo(
                            RecommendedCoursesPage,
                            source = NavigationSource("/home", "recommended_all"),
                        )
                    },
                    onCourseClick = { id ->
                        telemetry.buttonClick("/home", "course", mapOf("course_id" to id))
                        navigationHelper.navigateByRoute(
                            CourseDetailPage.route(id),
                            source = NavigationSource("/home", "course"),
                        )
                    },
                    onRetry = { homeViewModel.onIntent(HomeIntent.Retry) },
                )
            }
            // 위치가 없으면 BFF 가 기본 좌표(성수) 기준으로 응답해 도보 시간이 틀어진다.
            // 그래서 안내가 데이터보다 먼저고, 조회 성공 후 빈 응답이면 지도 탐색 안내를 표시한다.
            if (session.isLoggedIn && (!hasLocationPermission || !gpsEnabled)) {
                // 섹션이 비는 이유(위치 없음)를 그 자리에서 안내한다 — 상단 띠는 과해서 뺐다(사용자 결정).
                item {
                    NearbyLocationPrompt(
                        message =
                            if (!hasLocationPermission) {
                                "위치를 허용하면 근처에 저장한 장소가 여기에 보여요"
                            } else {
                                "기기 위치(GPS)가 꺼져 있어 근처 장소를 찾을 수 없어요"
                            },
                        actionLabel = if (!hasLocationPermission) "위치 허용" else "위치 켜기",
                        onAction = {
                            if (!hasLocationPermission) {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                    ),
                                )
                            } else {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            }
                        },
                        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                    )
                }
            } else if (homeState.nearbyPlaces.isNotEmpty()) {
                item {
                    NearbySavedSection(
                        places = homeState.nearbyPlaces,
                        onSeeMap = {
                            navigationHelper.navigateTo(
                                MapPage,
                                source = NavigationSource("/home", "nearby_map"),
                            )
                        },
                        onPlaceClick = { placeId ->
                            // 지도 탭이 장소 목록을 받은 뒤 이 장소를 선택해(시트·카메라) 보여준다.
                            MapFocusRequestStore.request(placeId)
                            navigationHelper.navigateTo(MapPage, source = NavigationSource("/home", "nearby_place"))
                        },
                        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                    )
                }
            } else if (session.isLoggedIn && homeState.showNearbyEmptyState) {
                item {
                    NearbyLocationPrompt(
                        message = "근처에 저장한 장소가 없어요",
                        actionLabel = "지도에서 장소 찾기",
                        onAction = {
                            navigationHelper.navigateTo(
                                MapPage,
                                source = NavigationSource("/home", "empty_nearby_map"),
                            )
                        },
                        modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                    )
                }
            }
            item {
                ThemeBrowseSection(
                    onThemeClick = { theme ->
                        telemetry.buttonClick("/home", "theme", mapOf("theme" to theme))
                        navigationHelper.navigateByRoute(
                            RecommendedCoursesPage.route(theme),
                            source = NavigationSource("/home", "theme"),
                        )
                    },
                    modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
                )
            }
            if (FeatureFlags.REGION_BROWSE_ENABLED) {
                item {
                    RegionBrowseSection(
                        // 지역을 누르면 그 시·도의 "{지역} 코스" 목록으로 가서 세부 지역(구·군) 칩으로 더 좁힌다.
                        onRegionClick = { code, name ->
                            telemetry.buttonClick("/home", "region", mapOf("region" to code))
                            navigationHelper.navigateByRoute(
                                RecommendedCoursesPage.regionRoute(sido = areaMapProvinceCode(code), regionName = name),
                                source = NavigationSource("/home", "region"),
                            )
                        },
                        modifier = Modifier.padding(top = 8.dp).padding(horizontal = ScreenHorizontalPadding),
                    )
                }
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
                    telemetry.buttonClick("/home", "create_course")
                    menuExpanded = false
                    navigationHelper.navigateTo(CourseCreatePage, source = NavigationSource("/home", "create_course"))
                },
                onLoadDraft = {
                    telemetry.buttonClick("/home", "load_draft")
                    menuExpanded = false
                    navigationHelper.navigateTo(DraftListPage, source = NavigationSource("/home", "load_draft"))
                },
                onNewPlan = {
                    telemetry.buttonClick("/home", "create_plan")
                    menuExpanded = false
                    navigationHelper.navigateByRoute(
                        CourseCreatePage.planRoute(),
                        source = NavigationSource("/home", "create_plan"),
                    )
                },
            )
        }
    }
}

/**
 * 홈 최초 진입 시 알림 수신 동의 시트(SCRUM-725). 기기 단위 1회 노출이라 게스트에게도 띄운다.
 * 동의 상태·노출 여부는 [NotificationConsentViewModel] 이 가지므로 홈 본문과 분리해 둔다.
 */
@Composable
private fun HomeNotificationConsent() {
    val consentViewModel: NotificationConsentViewModel = hiltViewModel()
    val consentState by consentViewModel.uiState.collectAsStateWithLifecycle()
    if (consentState.visible) {
        NotificationConsentSheet(state = consentState, onIntent = consentViewModel::onIntent)
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
                MainTab.MY -> {
                    navigationHelper.navigateTo(
                        myPageDestination(loggedIn),
                        source = NavigationSource("/home", "bottom_tab_my"),
                    )
                }

                MainTab.SAVED -> {
                    navigationHelper.navigateTo(
                        SavedPage,
                        source = NavigationSource("/home", "bottom_tab_saved"),
                    )
                }

                MainTab.MAP -> {
                    navigationHelper.navigateTo(
                        MapPage,
                        source = NavigationSource("/home", "bottom_tab_map"),
                    )
                }

                MainTab.CHAT -> {
                    navigationHelper.navigateTo(
                        ChatPage,
                        source = NavigationSource("/home", "bottom_tab_chat"),
                    )
                }

                MainTab.HOME -> {
                    Unit
                }
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
 * 홈 헤더: 인사 타이틀 + 우상단 아바타.
 *
 * 위치·날씨 보조줄은 실데이터 소스가 없어 고정값만 보여 혼란을 줘 제거했다(사용자 결정, 2026-10-19).
 * 로그인했으면 닉네임을 넣어 부르고, 게스트는 닉네임 없는 기존 문구를 유지한다.
 */
@Composable
private fun HomeHeader(
    profile: MyProfileVO?,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val greeting =
        if (profile != null) {
            "${profile.nickname}님, 오늘은\n어디로 떠나볼까요?"
        } else {
            "어디로 떠나볼까요?"
        }
    // 인사말이 2줄(titleExtraL 24sp × 2 ≈ 62dp)이라, 아바타를 그 높이에 맞춰 크게 둔다.
    Row(modifier = modifier, verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            DsText(
                text = greeting,
                style = DesignSystemThemeImpl.typeScale.titleExtraL,
                color = color.contentDefaultLevel0,
                maxLines = 2,
            )
        }
        // 밝은 홈 배경에 아바타가 묻히지 않도록 흰 바탕·테두리·옅은 그림자로 배경과 분리한다.
        Box(
            modifier =
                Modifier
                    .padding(start = 12.dp)
                    .size(62.dp)
                    .shadow(3.dp, CircleShape)
                    .clip(CircleShape)
                    .background(color.bgDefaultLevel1)
                    .border(1.dp, color.borderDefaultLevel0, CircleShape)
                    .clickable(onClickLabel = "마이로 이동", role = Role.Button, onClick = onProfileClick)
                    .semantics { contentDescription = "내 프로필" },
            contentAlignment = Alignment.Center,
        ) {
            ProfileAvatar(imageUrl = profile?.profileImageUrl.orEmpty(), size = 56.dp)
        }
    }
}

/** 섹션 공통 헤더: 좌측 제목 + (있으면) 우측 액션 링크. */
@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
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
        if (actionLabel != null && onAction != null) {
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
}

/**
 * 지역별 둘러보기 섹션. 테마별 둘러보기와 짝을 이루는 또 하나의 둘러보기 축으로, 그 바로 아래에 둔다.
 *
 * 디자인 목업과 동일한 **그리드 타일 지도**([RegionTileMap])를 흰 카드 위에 띄운다. 지역을 누르면
 * 그 지역의 "{지역} 코스" 목록으로 가서 세부 지역(구·군)을 칩으로 더 좁힌다([onRegionClick]).
 */
@Composable
private fun RegionBrowseSection(
    onRegionClick: (code: String, name: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(title = "지역별 둘러보기")
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.bgDefaultLevel1)
                    .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(20.dp))
                    .padding(8.dp),
        ) {
            RegionTileMap(
                onRegionClick = onRegionClick,
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 25f),
            )
        }
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
                RecommendedCardsSkeleton()
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

/**
 * 근처 저장 장소 카드: 썸네일 + [이름·카테고리 / 평점] + 방문 여부 칩. 탭하면 지도에서 그 장소를 보여준다.
 *
 * 카테고리는 이름 줄에 인라인으로 올리고, 둘째 줄엔 평점만 둔다(도보 시간은 표시하지 않는다).
 */
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
            detailCategory = place.detailCategory,
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            val hasAddress = place.address.isNotBlank()
            val hasCategory = place.categoryLabel.isNotBlank()
            val hasRating = place.ratingText.isNotBlank()
            // 1줄: 장소명. 주소가 있으면 카테고리를 이름 옆에 인라인으로 붙여 아래에 주소 줄을 둔다.
            // 주소가 없으면 카테고리를 1줄에 붙이지 않고 아랫줄로 내려 2줄(이름 / 카테고리)로 보여 준다.
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DsText(
                    text = place.name,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alignByBaseline().weight(1f, fill = false),
                )
                if (hasAddress && hasCategory) {
                    DsText(
                        text = place.detailCategory.orCategoryLabel(place.categoryLabel),
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.alignByBaseline(),
                    )
                }
            }
            // 2줄: 주소가 없으면 카테고리를 여기에, 그리고 평점(있으면). 평가 없는 장소는 별 아이콘째 숨긴다.
            val showCategoryBelow = !hasAddress && hasCategory
            if (showCategoryBelow || hasRating) {
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    if (showCategoryBelow) {
                        DsText(
                            text = place.detailCategory.orCategoryLabel(place.categoryLabel),
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = color.contentDefaultLevel2,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (showCategoryBelow && hasRating) {
                        DsText(
                            text = "·",
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = color.borderDefaultLevel1,
                        )
                    }
                    if (hasRating) {
                        DsRatingStar(starSize = 13.dp)
                        DsText(
                            text = place.ratingText,
                            style = DesignSystemThemeImpl.typeScale.textRegularXS,
                            color = color.contentDefaultLevel3,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            // 3줄: 상세 주소(있으면). 서버가 주지 않으면 숨겨져 위의 2줄 구성으로 줄어든다.
            if (hasAddress) {
                DsText(
                    text = place.address,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp),
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

/** "테마별 둘러보기" 섹션: 3열 그리드 타일 10개. 탭하면 추천 코스의 해당 카테고리 필터를 연다. */
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
        themeTiles.chunked(3).forEach { rowTiles ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowTiles.forEach { theme ->
                    ThemeTile(
                        category = theme,
                        // 라우트 인자는 taxonomy 컨벤션대로 소문자 코드("date")를 싣는다. 라벨은 표시 전용.
                        onClick = { onThemeClick(theme.code) },
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(3 - rowTiles.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

/** 흰 카드 위 좌상단 라벨과 우하단 파스텔 벡터 일러스트. */
@Composable
private fun ThemeTile(
    category: CourseTagVO,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .height(100.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(color.bgDefaultLevel1)
                .border(1.dp, color.borderDefaultLevel0, RoundedCornerShape(18.dp))
                .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(category.themeImageRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize(),
        )
        DsText(
            text = category.label,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = DesignSystemThemeImpl.typeScale.textStrongS,
            color = color.contentDefaultLevel0,
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
        )
    }
}

/** 기기 위치 서비스(GPS·네트워크)가 켜져 있는지. */
private fun Context.isLocationEnabled(): Boolean {
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
        manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

/** 마지막 위치를 한 번 읽는다. 호출부가 권한을 확인한 뒤에만 부른다. */
@SuppressLint("MissingPermission")
private fun fetchLastLocation(
    context: Context,
    onLocation: (Double, Double) -> Unit,
) {
    LocationServices
        .getFusedLocationProviderClient(context)
        .lastLocation
        .addOnSuccessListener { location ->
            location?.let { onLocation(it.latitude, it.longitude) }
        }
}

/**
 * "근처 저장 장소" 섹션 자리에 띄우는 안내. 위치·빈 결과의 이유를 그 맥락에서 설명하고
 * 허용·설정·지도 탐색으로 이어 준다. 버튼은 탭하기 쉽게 카드 전체 폭을 쓴다.
 */
@Composable
private fun NearbyLocationPrompt(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(title = "근처 저장 장소")
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.bgDefaultLevel1)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DsText(
                text = message,
                style = DesignSystemThemeImpl.typeScale.textRegularS,
                color = color.contentDefaultLevel1,
            )
            DsButton(
                text = actionLabel,
                variant = DsButtonVariant.Secondary,
                onClick = onAction,
            )
        }
    }
}

private fun myPageDestination(loggedIn: Boolean) = if (loggedIn) MyPage else GuestMyPage

/** 진행 중이던 코스가 있으면(=[course] 가 null 이 아니면) 추천 위에 카드 아이템을 넣는다. null 이면 넣지 않는다. */
private fun LazyListScope.inProgressCourseItem(
    course: HomeInProgressCourseVO?,
    onResume: (HomeInProgressCourseVO) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    course ?: return
    item {
        InProgressCourseSection(course = course, onResume = onResume, onDismiss = onDismiss, modifier = modifier)
    }
}

/**
 * 홈 "진행 중인 코스" 카드. 따라가는 중 세션이 있을 때만(=[course] 가 있을 때만) 노출한다.
 * 카드를 누르면 이어서 따라가기로 간다. 우측 상단 X 를 누르면 확인 다이얼로그를 거쳐
 * 이 카드를 홈에서 지운다([onDismiss] — 서버 세션 종료가 아니라 홈 로컬 숨김).
 * 코스를 더 볼 수 없으면([HomeInProgressCourseVO.isDeleted]) "비공개된 코스"로 표시하되 따라가기는 계속한다.
 *
 * 어두운 면이라 테마에 on-dark 슬롯이 없어 기존 토큰 값을 그대로 재사용한다(새 토큰 추가 없음):
 * 배경 [contentDefaultLevel0](Black) · 흰 글자 [contentOnAccent](White) · 보조 글자/막대/아이콘 [contentDefaultLevel3].
 */
@Composable
private fun InProgressCourseSection(
    course: HomeInProgressCourseVO,
    onResume: (HomeInProgressCourseVO) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    var showDismissDialog by remember { mutableStateOf(false) }
    if (showDismissDialog) {
        DsConfirmDialog(
            title = "진행 중인 코스를 종료할까요?",
            description = "종료된 코스는 완주되어 저장함에서 볼 수 있어요.",
            onConfirm = {
                showDismissDialog = false
                onDismiss()
            },
            onDismiss = { showDismissDialog = false },
            destructive = true,
        )
    }
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color.contentDefaultLevel0)
                .clickable { onResume(course) }
                .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = course.coverImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.imagePlaceholder),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // 좌측 라벨, 우측 끝에 홈에서 지우는 X.
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                DsText(
                    text = if (course.isDeleted) "비공개된 코스" else "진행 중인 코스",
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    painter = painterResource(R.drawable.close_small_24),
                    contentDescription = "진행 중인 코스 지우기",
                    tint = color.contentDefaultLevel3,
                    modifier = Modifier.clip(CircleShape).clickable { showDismissDialog = true }.size(22.dp),
                )
            }
            DsText(
                text = course.title,
                style = DesignSystemThemeImpl.typeScale.textStrongS,
                color = color.contentOnAccent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // 진행 현황: 막대 + "N / M곳 방문"을 한 줄에(줄바꿈 없이). 진행도 정보가 없으면 지역·장소 수 요약으로 폴백.
            if (course.hasProgress) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    InProgressBar(progress = course.progress, modifier = Modifier.weight(1f))
                    DsText(
                        text = course.progressText,
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel3,
                        maxLines = 1,
                    )
                }
            } else if (course.summaryText.isNotBlank()) {
                DsText(
                    text = course.summaryText,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel3,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/** 진행도 막대(방문/전체). 어두운 카드 위: 트랙 [contentDefaultLevel2], 채움 [contentOnAccent]. */
@Composable
private fun InProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Box(
        modifier =
            modifier
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color.contentDefaultLevel2),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(color.contentOnAccent),
        )
    }
}
