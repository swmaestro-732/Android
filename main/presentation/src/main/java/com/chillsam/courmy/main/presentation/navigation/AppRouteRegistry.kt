package com.chillsam.courmy.main.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.course.domain.CourseCreatePage
import com.chillsam.courmy.course.presentation.CourseCreatePage
import com.chillsam.courmy.course.presentation.CourseCreateViewModel
import com.chillsam.courmy.main.domain.course.CourseCompletePage
import com.chillsam.courmy.main.domain.course.CourseDetailPage
import com.chillsam.courmy.main.domain.course.DraftListPage
import com.chillsam.courmy.main.domain.deeplink.RoutePattern
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.presentation.course.CourseCompletePage
import com.chillsam.courmy.main.presentation.course.CourseDetailPage
import com.chillsam.courmy.main.presentation.course.CourseSessionStore
import com.chillsam.courmy.main.presentation.course.DraftListPage
import com.chillsam.courmy.main.presentation.home.HomePage
import com.chillsam.courmy.main.presentation.my.MyPage

/**
 * 앱의 모든 페이지 메타데이터 + 렌더러 모음.
 * 새 화면 추가 시 본 리스트에 한 줄을 더한다.
 */
val appRoutes: List<AppRoute> =
    listOf(
        AppRoute(
            path = HomePage.PATH,
            render = { HomePage() },
        ),
        // 코스 만들기: course 모듈의 실제 MVI 화면(SCRUM-234).
        // 상단바·저장바 네비게이션은 대상 화면(홈/코스 완성)이 main 모듈이라 여기서 주입한다.
        AppRoute(
            path = CourseCreatePage.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                CourseCreatePage(
                    viewModel = hiltViewModel<CourseCreateViewModel>(),
                    onClose = { navigationHelper.navigateToBack() },
                    onSaveDraft = { navigationHelper.navigateToBack() },
                    onSaveCourse = { completed ->
                        CourseSessionStore.completeCourse(completed)
                        navigationHelper.navigateTo(CourseCompletePage)
                    },
                )
            },
        ),
        AppRoute(
            path = CourseCompletePage.PATH,
            render = { CourseCompletePage() },
        ),
        AppRoute(
            path = DraftListPage.PATH,
            render = { DraftListPage() },
        ),
        AppRoute(
            path = MyPage.PATH,
            render = { MyPage() },
        ),
        AppRoute(
            path = CourseDetailPage.PATH,
            render = { args ->
                CourseDetailPage(
                    title = args[CourseDetailPage.ARG_TITLE].orEmpty(),
                    createdAtMillis = args[CourseDetailPage.ARG_CREATED_AT]?.toLongOrNull() ?: 0L,
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
