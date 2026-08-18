package com.chillsam.courmy.main.entity.user

/**
 * 뷰어(나)와 대상 유저의 팔로우 관계.
 *
 * 서버 `GET /api/v1/users/{userId}` 응답의 `following`(내가 상대를 팔로우) ·
 * `follower`(상대가 나를 팔로우) 두 boolean 조합이며,
 * 타유저 프로필(FS-15)의 팔로우 버튼 4가지 상태와 1:1 대응한다.
 */
enum class FollowRelation {
    /** 서로 팔로우하지 않음 → "팔로우하기". */
    NONE,

    /** 상대만 나를 팔로우 중 → "나도 팔로우하기". */
    FOLLOWS_ME,

    /** 나만 상대를 팔로우 중 → "나만 팔로우 중". */
    ONLY_I_FOLLOW,

    /** 서로 팔로우 중 → "서로 팔로우 중". */
    MUTUAL,
    ;

    /** 내가 상대를 팔로우 중인지. true 면 버튼을 누를 때 언팔로우한다. */
    val isFollowing: Boolean
        get() = this == ONLY_I_FOLLOW || this == MUTUAL

    /** 상대가 나를 팔로우 중인지. 내 팔로우 요청으로는 바뀌지 않는다. */
    val isFollower: Boolean
        get() = this == FOLLOWS_ME || this == MUTUAL

    /** 팔로우/언팔로우 후의 관계(상대가 나를 팔로우하는지는 그대로 유지된다). */
    fun toggled(): FollowRelation = of(isFollowing = !isFollowing, isFollower = isFollower)

    companion object {
        fun of(
            isFollowing: Boolean,
            isFollower: Boolean,
        ): FollowRelation =
            when {
                isFollowing && isFollower -> MUTUAL
                isFollowing -> ONLY_I_FOLLOW
                isFollower -> FOLLOWS_ME
                else -> NONE
            }
    }
}
