package com.chillsam.courmy.main.presentation.my

/**
 * 프로필 코스 목록의 구분 탭(FS-15).
 *
 * 내가 만든 코스와 남의 코스를 떠 온(포크) 코스는 성격이 달라 한 그리드에 섞으면 어느 쪽이
 * 내 작업인지 알 수 없다. 목록을 나누는 대신 탭으로 갈라 둔다.
 */
enum class ProfileTab {
    /** 내가 직접 만든 코스. */
    MY_COURSES,

    /** 남의 코스를 떠 와 내 것으로 담은 코스. */
    FORKED,
}

/** 탭에 노출할 이름. 옆에 개수가 따로 붙으므로 이름만 갖는다. */
internal fun ProfileTab.label(): String =
    when (this) {
        ProfileTab.MY_COURSES -> "내 코스"
        ProfileTab.FORKED -> "포크"
    }
