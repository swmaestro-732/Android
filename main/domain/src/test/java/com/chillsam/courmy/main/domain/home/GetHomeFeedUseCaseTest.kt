package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.main.entity.home.HomeCourseVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** 서버가 size 를 1~50 으로 제한하므로, 범위를 벗어난 값이 그대로 나가면 400 이 된다. */
class GetHomeFeedUseCaseTest {
    private class FakeRepository : HomeFeedRepository {
        var requestedSize: Int? = null
            private set

        override suspend fun getCourseFeed(size: Int): List<HomeCourseVO> {
            requestedSize = size
            return emptyList()
        }
    }

    @Test
    fun `기본값을 그대로 넘긴다`() =
        runTest {
            val repo = FakeRepository()

            GetHomeFeedUseCase(repo)()

            assertEquals(GetHomeFeedUseCase.DEFAULT_SIZE, repo.requestedSize)
        }

    @Test
    fun `범위를 벗어난 size 는 서버 한도로 맞춘다`() =
        runTest {
            val tooSmall = FakeRepository()
            val tooLarge = FakeRepository()

            GetHomeFeedUseCase(tooSmall)(size = 0)
            GetHomeFeedUseCase(tooLarge)(size = 999)

            assertEquals(1, tooSmall.requestedSize)
            assertEquals(50, tooLarge.requestedSize)
        }
}
