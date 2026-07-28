package com.chillsam.courmy.main.domain.login

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 약관 동의 화면(FS-04). 소셜 로그인 후 필수/선택 약관 동의. */
object TermsAgreementPage : Page {
    const val PATH = "/login/terms"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
