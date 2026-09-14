package com.chillsam.courmy.main.presentation.profile

/**
 * [ProfileTabRow] 의 탭 한 칸. 이름과 개수를 함께 그린다("내 코스 8").
 *
 * 어떤 탭이 있는지(내 코스·포크)는 화면이 정하는 일이라 여기서는 표시할 값만 받는다.
 */
data class ProfileTabItem(
    val label: String,
    val count: Int,
)
