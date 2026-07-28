package com.chillsam.courmy.main.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.course.presentation.CourseCompletePage
import com.chillsam.courmy.course.presentation.CourseCompleteViewModel
import com.chillsam.courmy.course.presentation.CourseCreateIntent
import com.chillsam.courmy.course.presentation.CourseCreatePage
import com.chillsam.courmy.course.presentation.CourseCreateViewModel
import com.chillsam.courmy.course.presentation.CourseDetailPage
import com.chillsam.courmy.course.presentation.CourseDetailViewModel
import com.chillsam.courmy.course.presentation.DraftListPage
import com.chillsam.courmy.course.presentation.DraftListViewModel
import com.chillsam.courmy.main.domain.deeplink.RoutePattern
import com.chillsam.courmy.main.presentation.home.HomePage
import com.chillsam.courmy.main.presentation.my.GuestMyPage
import com.chillsam.courmy.main.presentation.my.InterestRegionPage
import com.chillsam.courmy.main.presentation.my.InterestThemePage
import com.chillsam.courmy.main.presentation.my.MyPage
import com.chillsam.courmy.main.presentation.my.ProfileEditPage
import com.chillsam.courmy.main.presentation.settings.SettingsPage
import com.chillsam.courmy.course.domain.CourseCompletePage as CourseCompleteRoute
import com.chillsam.courmy.course.domain.CourseCreatePage as CourseCreateRoute
import com.chillsam.courmy.course.domain.CourseDetailPage as CourseDetailRoute
import com.chillsam.courmy.course.domain.DraftListPage as DraftListRoute
import com.chillsam.courmy.main.domain.home.HomePage as HomeRoute
import com.chillsam.courmy.main.domain.my.GuestMyPage as GuestMyRoute
import com.chillsam.courmy.main.domain.my.InterestRegionPage as InterestRegionRoute
import com.chillsam.courmy.main.domain.my.InterestThemePage as InterestThemeRoute
import com.chillsam.courmy.main.domain.my.MyPage as MyRoute
import com.chillsam.courmy.main.domain.my.ProfileEditPage as ProfileEditRoute
import com.chillsam.courmy.main.domain.settings.SettingsPage as SettingsRoute

/**
 * 앱의 모든 페이지 메타데이터 + 렌더러 모음.
 * 새 화면 추가 시 본 리스트에 한 줄을 더한다.
 */
val appRoutes: List<AppRoute> =
    listOf(
        AppRoute(
            path = HomeRoute.PATH,
            render = { HomePage() },
        ),
        // 코스 만들기: course 모듈의 실제 MVI 화면(SCRUM-234).
        // 상단바·저장바 네비게이션은 대상 화면(홈/코스 완성)이 main 모듈이라 여기서 주입한다.
        AppRoute(
            path = CourseCreateRoute.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                val viewModel = hiltViewModel<CourseCreateViewModel>()
                CourseCreatePage(
                    viewModel = viewModel,
                    onClose = { navigationHelper.navigateToBack() },
                    onSaveDraft = {
                        // 임시저장하면 세션에 저장하고 홈으로 나간다.
                        viewModel.onIntent(CourseCreateIntent.SaveDraft)
                        navigationHelper.navigateTo(HomeRoute)
                    },
                    onSaveCourse = { completed ->
                        viewModel.onIntent(CourseCreateIntent.CompleteCourse(completed))
                        navigationHelper.navigateTo(CourseCompleteRoute)
                    },
                )
            },
        ),
        AppRoute(
            path = CourseCompleteRoute.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                CourseCompletePage(
                    viewModel = hiltViewModel<CourseCompleteViewModel>(),
                    onClose = { navigationHelper.navigateTo(HomeRoute) },
                    onViewMyCourses = { navigationHelper.navigateTo(MyRoute) },
                )
            },
        ),
        AppRoute(
            path = DraftListRoute.PATH,
            render = { DraftListPage(viewModel = hiltViewModel<DraftListViewModel>()) },
        ),
        AppRoute(
            path = MyRoute.PATH,
            render = { MyPage() },
        ),
        AppRoute(
            path = GuestMyRoute.PATH,
            render = { GuestMyPage() },
        ),
        AppRoute(
            path = ProfileEditRoute.PATH,
            render = { ProfileEditPage() },
        ),
        AppRoute(
            path = InterestThemeRoute.PATH,
            render = { InterestThemePage() },
        ),
        AppRoute(
            path = InterestRegionRoute.PATH,
            render = { InterestRegionPage() },
        ),
        AppRoute(
            path = SettingsRoute.PATH,
            render = { SettingsPage() },
        ),
        AppRoute(
            path = CourseDetailRoute.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                CourseDetailPage(
                    viewModel = hiltViewModel<CourseDetailViewModel>(),
                    onBack = { navigationHelper.navigateToBack() },
                    onFollowAuthor = { },
                    onFollowCourse = { },
                )
            },
        ),
    )

val appRouteByPath: Map<String, AppRoute> = appRoutes.associateBy { it.path }

val bottomTabRoutes: List<AppRoute> = appRoutes.filter { it.isBottomTab }

/**
 * 동적 구간(`{param}`)을 가진 계층형 라우트의 (패턴, 라우트) 목록.
 *
 * 정적 path 는 [appRouteByPath] 가 O(1) 로 처리하므로, 여기에는 다중 세그먼트 템플릿
 * (예: "/articleList/articlePage/{articleId}")만 보관한다. deep-link URI 해석 시
 * exact 매칭이 실패한 경우에만 이 목록을 순차 매칭한다.
 */
val appRoutePatterns: List<Pair<RoutePattern, AppRoute>> =
    appRoutes
        .map { route -> RoutePattern(route.path) to route }
        .filter { (pattern, _) -> pattern.hasParams }
