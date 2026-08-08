package com.chillsam.courmy.main.domain.follow

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.my.FollowUserVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

        override suspend fun getMyFollowers(
            size: Int,
            cursor: String?,
        ): CursorPageVO<FollowUserVO> {
            followersCalled = true
            requestedSize = size
            requestedCursor = cursor
            return CursorPageVO()
        }

        override suspend fun getMyFollowings(
            size: Int,
            cursor: String?,
        ): CursorPageVO<FollowUserVO> {
            followingsCalled = true
            requestedSize = size
            requestedCursor = cursor
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
}
