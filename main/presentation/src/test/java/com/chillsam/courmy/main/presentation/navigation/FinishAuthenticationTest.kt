package com.chillsam.courmy.main.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.chillsam.courmy.main.domain.home.HomePage
import com.chillsam.courmy.main.domain.login.LoginPage
import com.chillsam.courmy.main.domain.login.OnboardingCompletePage
import com.chillsam.courmy.main.domain.login.ProfileSetupPage
import com.chillsam.courmy.main.domain.login.SignupRegionPage
import com.chillsam.courmy.main.domain.login.SignupThemePage
import com.chillsam.courmy.main.domain.login.TermsAgreementPage
import com.chillsam.courmy.main.domain.my.GuestMyPage
import com.chillsam.courmy.main.domain.my.MyPage
import com.chillsam.courmy.main.domain.onboarding.OnboardingPage
import com.chillsam.courmy.main.domain.onboarding.SplashPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class FinishAuthenticationTest {
    @Test
    fun `로그인 완료는 원래 화면과 인자와 엔트리 객체를 보존한다`() {
        val home = GenericNavKey(HomePage.PATH)
        val search = GenericNavKey("/search/result", mapOf("query" to "성수 카페"))
        val detail = GenericNavKey("/course/detail", mapOf("courseId" to "169"))
        val stack = mutableListOf<NavKey>(home, search, detail, GenericNavKey(LoginPage.PATH))

        finishAuthentication(stack)

        assertEquals(listOf(home, search, detail), stack)
        assertSame(home, stack[0])
        assertSame(search, stack[1])
        assertSame(detail, stack[2])
    }

    @Test
    fun `회원가입 완료는 로그인부터 모든 가입 단계를 제거한다`() {
        val origin = GenericNavKey("/saved")
        val stack =
            mutableListOf<NavKey>(
                origin,
                GenericNavKey(LoginPage.PATH),
                GenericNavKey(TermsAgreementPage.PATH),
                GenericNavKey(ProfileSetupPage.PATH),
                GenericNavKey(SignupThemePage.PATH),
                GenericNavKey(SignupRegionPage.PATH),
                GenericNavKey(OnboardingCompletePage.PATH),
            )

        finishAuthentication(stack)

        assertEquals(listOf(origin), stack)
        assertSame(origin, stack.single())
    }

    @Test
    fun `로그인 단독 진입과 온보딩 진입은 홈 하나로 종료한다`() {
        val origins =
            listOf(
                emptyList(),
                listOf(GenericNavKey(OnboardingPage.PATH)),
                listOf(GenericNavKey(SplashPage.PATH)),
                listOf(GenericNavKey(SplashPage.PATH), GenericNavKey(OnboardingPage.PATH)),
            )
        origins.forEach { origin ->
            val stack = origin.toMutableList<NavKey>()
            stack.add(GenericNavKey(LoginPage.PATH))
            stack.add(GenericNavKey(OnboardingCompletePage.PATH))

            finishAuthentication(stack)

            assertEquals(listOf(GenericNavKey(HomePage.PATH)), stack)
        }
    }

    @Test
    fun `게스트 마이에서 가입하면 기존 마이와 중복 없이 마이로 복귀한다`() {
        val home = GenericNavKey(HomePage.PATH)
        val detail = GenericNavKey("/course/detail", mapOf("courseId" to "169"))
        val stack =
            mutableListOf<NavKey>(
                home,
                GenericNavKey(MyPage.PATH),
                detail,
                GenericNavKey(GuestMyPage.PATH),
                GenericNavKey(LoginPage.PATH),
                GenericNavKey(OnboardingCompletePage.PATH),
            )

        finishAuthentication(stack)

        assertEquals(listOf(home, detail, GenericNavKey(MyPage.PATH)), stack)
        assertSame(home, stack[0])
        assertSame(detail, stack[1])
    }

    @Test
    fun `중복 성공 신호는 복귀 후 새로 연 화면을 제거하지 않는다`() {
        val origin = GenericNavKey("/course/detail", mapOf("courseId" to "169"))
        val stack = mutableListOf<NavKey>(origin, GenericNavKey(LoginPage.PATH))
        finishAuthentication(stack)
        val next = GenericNavKey("/course/detail", mapOf("courseId" to "170"))
        stack.add(next)

        finishAuthentication(stack)

        assertEquals(listOf(origin, next), stack)
        assertSame(origin, stack[0])
        assertSame(next, stack[1])
    }

    @Test
    fun `로그인 경로가 없는 스택은 비어 있어도 변경하지 않는다`() {
        val stack = mutableListOf<NavKey>()

        finishAuthentication(stack)

        assertEquals(emptyList<NavKey>(), stack)
    }
}
