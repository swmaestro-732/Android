package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/** 저장(북마크) 화면(FS-14). 로그인 상태면 저장한 코스 리스트, 비로그인이면 게스트 잠금 화면을 보여준다. */
object SavedPage : Page {
    const val PATH = "/saved"

    override fun toRoute(): NavRoute = NavRoute(PATH)
}
