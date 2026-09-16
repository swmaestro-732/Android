package com.chillsam.courmy.main.domain.follow

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.my.FollowUserVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** 팔로워/팔로잉은 엔드포인트가 다르다. 분기를 잘못 타면 반대 목록이 보인다. */
class GetFollowListUseCaseTest {
    private class FakeRepository : FollowListRepository {
        var followersCalled = false
            private set
        var followingsCalled = false
            private set
        var requestedSize: Int? = null
            private set

        var requestedCursor: String? = null
            private set

        var requestedUserId: Long? = null
            private set

        override suspend fun getFollowers(
            userId: Long?,
            size: Int,
            cursor: String?,
        ): CursorPageVO<FollowUserVO> {
            followersCalled = true
            requestedSize = size
            requestedCursor = cursor
            requestedUserId = userId
            return CursorPageVO()
        }

        override suspend fun getFollowings(
            userId: Long?,
            size: Int,
            cursor: String?,
        ): CursorPageVO<FollowUserVO> {
            followingsCalled = true
            requestedSize = size
            requestedCursor = cursor
            requestedUserId = userId
            return CursorPageVO()
        }
    }

    @Test
    fun `followers 가 true 면 팔로워 목록을 부른다`() =
        runTest {
            val repo = FakeRepository()

            GetFollowListUseCase(repo)(followers = true)

            assertEquals(true, repo.followersCalled)
            assertEquals(false, repo.followingsCalled)
        }

    @Test
    fun `followers 가 false 면 팔로잉 목록을 부른다`() =
        runTest {
            val repo = FakeRepository()

            GetFollowListUseCase(repo)(followers = false)

            assertEquals(true, repo.followingsCalled)
            assertEquals(false, repo.followersCalled)
        }

    @Test
    fun `범위를 벗어난 size 는 서버 한도로 맞춘다`() =
        runTest {
            val repo = FakeRepository()

            GetFollowListUseCase(repo)(followers = true, size = 999)

            assertEquals(50, repo.requestedSize)
        }

    /** 대상을 안 주면 내 목록이다 — data 레이어가 JWT 에서 id 를 꺼낸다. */
    @Test
    fun `userId 를 주지 않으면 null 로 전달해 내 목록을 본다`() =
        runTest {
            val repo = FakeRepository()

            GetFollowListUseCase(repo)(followers = true)

            assertNull(repo.requestedUserId)
        }

    @Test
    fun `userId 를 주면 그 사용자의 목록을 부른다`() =
        runTest {
            val repo = FakeRepository()

            GetFollowListUseCase(repo)(followers = false, userId = 7L)

            assertEquals(7L, repo.requestedUserId)
            assertEquals(true, repo.followingsCalled)
        }
}
