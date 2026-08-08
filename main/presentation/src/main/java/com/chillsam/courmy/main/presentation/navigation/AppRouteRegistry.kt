package com.chillsam.courmy.main.presentation.navigation

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.chillsam.courmy.common.presentation.helper.LocalNavigationHelper
import com.chillsam.courmy.common.presentation.helper.LocalSessionUiState
import com.chillsam.courmy.course.presentation.CourseCompletePage
import com.chillsam.courmy.course.presentation.CourseCompleteViewModel
import com.chillsam.courmy.course.presentation.CourseCreateIntent
import com.chillsam.courmy.course.presentation.CourseCreatePage
import com.chillsam.courmy.course.presentation.CourseCreateViewModel
import com.chillsam.courmy.course.presentation.CourseDetailPage
import com.chillsam.courmy.course.presentation.CourseDetailViewModel
import com.chillsam.courmy.course.presentation.CourseEditPage
import com.chillsam.courmy.course.presentation.CourseEditViewModel
import com.chillsam.courmy.course.presentation.DraftListPage
import com.chillsam.courmy.course.presentation.DraftListViewModel
import com.chillsam.courmy.main.domain.deeplink.RoutePattern
import com.chillsam.courmy.main.presentation.home.HomePage
import com.chillsam.courmy.main.presentation.login.LoginPage
import com.chillsam.courmy.main.presentation.login.OnboardingCompletePage
import com.chillsam.courmy.main.presentation.login.ProfileSetupPage
import com.chillsam.courmy.main.presentation.login.SignupSelectionStore
import com.chillsam.courmy.main.presentation.login.TermsAgreementPage
import com.chillsam.courmy.main.presentation.my.FollowListPage
import com.chillsam.courmy.main.presentation.my.FollowTab
import com.chillsam.courmy.main.presentation.my.GuestMyPage
import com.chillsam.courmy.main.presentation.my.InterestRegionPage
import com.chillsam.courmy.main.presentation.my.InterestThemePage
import com.chillsam.courmy.main.presentation.my.MyPage
import com.chillsam.courmy.main.presentation.my.ProfileEditPage
import com.chillsam.courmy.main.presentation.onboarding.OnboardingPage
import com.chillsam.courmy.main.presentation.onboarding.OnboardingViewModel
import com.chillsam.courmy.main.presentation.onboarding.SplashPage
import com.chillsam.courmy.main.presentation.saved.SavedPage
import com.chillsam.courmy.main.presentation.settings.AccountManagePage
import com.chillsam.courmy.main.presentation.settings.SettingsPage
import com.chillsam.courmy.main.presentation.user.UserProfilePage
import com.chillsam.courmy.course.domain.CourseCompletePage as CourseCompleteRoute
import com.chillsam.courmy.course.domain.CourseCreatePage as CourseCreateRoute
import com.chillsam.courmy.course.domain.CourseDetailPage as CourseDetailRoute
import com.chillsam.courmy.course.domain.CourseEditPage as CourseEditRoute
import com.chillsam.courmy.course.domain.DraftListPage as DraftListRoute
import com.chillsam.courmy.main.domain.home.HomePage as HomeRoute
import com.chillsam.courmy.main.domain.login.LoginPage as LoginRoute
import com.chillsam.courmy.main.domain.login.OnboardingCompletePage as CompleteRoute
import com.chillsam.courmy.main.domain.login.ProfileSetupPage as ProfileSetupRoute
import com.chillsam.courmy.main.domain.login.SignupRegionPage as SignupRegionRoute
import com.chillsam.courmy.main.domain.login.SignupThemePage as SignupThemeRoute
import com.chillsam.courmy.main.domain.login.TermsAgreementPage as TermsRoute
import com.chillsam.courmy.main.domain.my.FollowListPage as FollowListRoute
import com.chillsam.courmy.main.domain.my.GuestMyPage as GuestMyRoute
import com.chillsam.courmy.main.domain.my.InterestRegionPage as InterestRegionRoute
import com.chillsam.courmy.main.domain.my.InterestThemePage as InterestThemeRoute
import com.chillsam.courmy.main.domain.my.MyPage as MyRoute
import com.chillsam.courmy.main.domain.my.ProfileEditPage as ProfileEditRoute
import com.chillsam.courmy.main.domain.onboarding.OnboardingPage as OnboardingRoute
import com.chillsam.courmy.main.domain.onboarding.SplashPage as SplashRoute
import com.chillsam.courmy.main.domain.saved.SavedPage as SavedRoute
import com.chillsam.courmy.main.domain.settings.AccountManagePage as AccountManageRoute
import com.chillsam.courmy.main.domain.settings.SettingsPage as SettingsRoute
import com.chillsam.courmy.main.domain.user.UserProfilePage as UserProfileRoute

/**
 * 앱의 모든 페이지 메타데이터 + 렌더러 모음.
 * 새 화면 추가 시 본 리스트에 한 줄을 더한다.
 */
val appRoutes: List<AppRoute> =
    listOf(
        AppRoute(
            path = SplashRoute.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                val session = LocalSessionUiState.current
                SplashPage(
                    onFinished = { onboarded, loggedIn ->
                        // 저장된 세션이 복원되면(자동 로그인) UI 세션도 로그인 상태로 맞추고 홈으로.
                        if (loggedIn) session.login()
                        navigationHelper.navigateReplace(if (onboarded || loggedIn) HomeRoute else OnboardingRoute)
                    },
                )
            },
        ),
        AppRoute(
            path = OnboardingRoute.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                val viewModel = hiltViewModel<OnboardingViewModel>()
                OnboardingPage(
                    // 로그인 하기 → 온보딩 완료 처리 후 로그인 플로우(FS-03)로 진입.
                    onLogin = { viewModel.complete { navigationHelper.navigateTo(LoginRoute) } },
                    onBrowse = { viewModel.complete { navigationHelper.navigateReplace(HomeRoute) } },
                    onSkip = { viewModel.complete { navigationHelper.navigateReplace(HomeRoute) } },
                )
            },
        ),
        AppRoute(
            path = LoginRoute.PATH,
            render = { LoginPage() },
        ),
        AppRoute(
            path = TermsRoute.PATH,
            render = { TermsAgreementPage() },
        ),
        AppRoute(
            path = ProfileSetupRoute.PATH,
            render = { ProfileSetupPage() },
        ),
        // 회원가입 플로우용: 편집용 관심 테마/지역 화면을 "다음" 흐름으로 재사용.
        // 각 단계 선택값은 완료 화면 안내 문구에 쓰려고 SignupSelectionStore 에 담는다.
        AppRoute(
            path = SignupThemeRoute.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                InterestThemePage(
                    onNext = { themes ->
                        SignupSelectionStore.themes = themes
                        navigationHelper.navigateTo(SignupRegionRoute)
                    },
                    progressStep = 2,
                )
            },
        ),
        AppRoute(
            path = SignupRegionRoute.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                InterestRegionPage(
                    onNext = { regions ->
                        SignupSelectionStore.regions = regions
                        navigationHelper.navigateTo(CompleteRoute)
                    },
                    progressStep = 3,
                )
            },
        ),
        AppRoute(
            path = CompleteRoute.PATH,
            render = {
                OnboardingCompletePage()
            },
        ),
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
                        // 임시저장하고 작성 화면을 빠져나온다(들어온 곳으로 되돌아간다).
                        viewModel.onIntent(CourseCreateIntent.SaveDraft)
                        navigationHelper.navigateToBack()
                    },
                    // 서버 저장 성공 후 CourseCreatePage 가 호출한다(저장 인텐트는 Page 내부에서 발행).
                    //
                    // **작성 화면 엔트리를 먼저 걷어내고** 완성 화면으로 간다. 백스택에 남겨 두면
                    // 그 엔트리의 ViewModel(=이번 코스 입력값·저장 결과)이 계속 살아 있어서,
                    // 다음에 "코스 만들기"를 눌렀을 때 새 화면이 아니라 이전 입력이 채워진 화면이
                    // 되살아나고 저장 신호까지 남아 곧장 완성 화면으로 튄다.
                    // 완성 화면이 보여줄 요약은 저장 성공 시 CompleteCourseUseCase 로 이미 보관해
                    // 둔 값이라 작성 화면이 사라져도 문제없고, courseId 도 쓰지 않는다.
                    onSaveCourse = {
                        navigationHelper.navigateToBack()
                        navigationHelper.navigateTo(CourseCompleteRoute)
                    },
                )
            },
        ),
        AppRoute(
            path = CourseCompleteRoute.PATH,
            render = {
                val navigationHelper = LocalNavigationHelper.current
                // 완성 화면은 한 번 보고 끝나는 화면이라, 나갈 때 자기 엔트리를 먼저 걷어낸다.
                // 남겨 두면 홈에서 뒤로가기를 눌렀을 때 다 끝난 완성 화면이 다시 튀어나온다.
                CourseCompletePage(
                    viewModel = hiltViewModel<CourseCompleteViewModel>(),
                    onClose = {
                        navigationHelper.navigateToBack()
                        navigationHelper.navigateTo(HomeRoute)
                    },
                    onViewMyCourses = {
                        navigationHelper.navigateToBack()
                        navigationHelper.navigateTo(MyRoute)
                    },
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
            path = FollowListRoute.PATH,
            render = { args ->
                val tab =
                    if (args[FollowListRoute.ARG_TAB] == FollowListRoute.TAB_FOLLOWING) {
                        FollowTab.FOLLOWING
                    } else {
                        FollowTab.FOLLOWER
                    }
                FollowListPage(initialTab = tab)
            },
        ),
        AppRoute(
            path = UserProfileRoute.PATH,
            render = { args ->
                UserProfilePage(handle = args[UserProfileRoute.ARG_HANDLE].orEmpty())
            },
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
            path = AccountManageRoute.PATH,
            render = { AccountManagePage() },
        ),
        AppRoute(
            path = SavedRoute.PATH,
            render = { SavedPage() },
        ),
        AppRoute(
            path = CourseDetailRoute.PATH,
            render = { args ->
                val navigationHelper = LocalNavigationHelper.current
                val context = LocalContext.current
                // 팔로우·공유·저장 플로우는 아직 미구현이라, 무반응 대신 "준비 중" 안내를 띄운다.
                val notReady = { Toast.makeText(context, "준비 중이에요", Toast.LENGTH_SHORT).show() }
                CourseDetailPage(
                    viewModel = hiltViewModel<CourseDetailViewModel>(),
                    courseId = args[CourseDetailRoute.ARG_COURSE_ID]?.toLongOrNull() ?: 0L,
                    onBack = { navigationHelper.navigateToBack() },
                    onAuthorClick = { handle ->
                        navigationHelper.navigateByRoute(UserProfileRoute.route(handle))
                    },
                    onShare = { notReady() },
                    onEditCourse = {
                        val courseId = args[CourseDetailRoute.ARG_COURSE_ID].orEmpty()
                        navigationHelper.navigateByRoute(CourseEditRoute.route(courseId))
                    },
                )
            },
        ),
        AppRoute(
            path = CourseEditRoute.PATH,
            render = { args ->
                val navigationHelper = LocalNavigationHelper.current
                CourseEditPage(
                    viewModel = hiltViewModel<CourseEditViewModel>(),
                    courseId = args[CourseEditRoute.ARG_COURSE_ID]?.toLongOrNull() ?: 0L,
                    onClose = { navigationHelper.navigateToBack() },
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
