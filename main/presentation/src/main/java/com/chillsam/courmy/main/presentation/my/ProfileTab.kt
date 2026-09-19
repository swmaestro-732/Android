package com.chillsam.courmy.main.presentation.my

/**
 * 프로필 코스 목록의 구분 탭(FS-15).
 *
 * 가 볼 곳을 모아 둔 계획과 다녀와서 완성한 코스는 성격이 달라 한 그리드에 섞으면 어느 쪽이
 * 아직 진행 중인지 알 수 없다. 목록을 나누는 대신 탭으로 갈라 둔다.
 *
 * 선언 순서가 곧 탭이 놓이는 순서다 — 화면은 [entries] 로 탭 줄을 그린다.
 */
enum class ProfileTab {
    /** 가 보고 싶은 곳을 미리 묶어 둔 계획. */
    PLAN,

    /** 내가 만들어 둔 코스. */
    COURSES,
}

/** 탭에 노출할 이름. 옆에 개수가 따로 붙으므로 이름만 갖는다. */
internal fun ProfileTab.label(): String =
    when (this) {
        ProfileTab.PLAN -> "계획"
        ProfileTab.COURSES -> "코스"
    }
