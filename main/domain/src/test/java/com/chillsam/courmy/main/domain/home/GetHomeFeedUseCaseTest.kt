package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.home.HomeCourseVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/** 서버가 size 를 1~50 으로 제한하므로, 범위를 벗어난 값이 그대로 나가면 400 이 된다. */
class GetHomeFeedUseCaseTest {
    private class FakeRepository : HomeFeedRepository {
        var requestedSize: Int? = null
            private set

        var requestedCursor: String? = null
            private set

        override suspend fun getCourseFeed(
            size: Int,
            cursor: String?,
        ): CursorPageVO<HomeCourseVO> {
            requestedSize = size
            requestedCursor = cursor
            return CursorPageVO()
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

    @Test
    fun `커서는 손대지 않고 그대로 넘긴다`() =
        runTest {
            val first = FakeRepository()
            val next = FakeRepository()

            GetHomeFeedUseCase(first)()
            GetHomeFeedUseCase(next)(cursor = "MjoxNzg1OTE5MTE3OjQzMDgwMzAwMDo0")

            // 첫 페이지는 커서 없이 나가야 한다(있으면 서버가 중간부터 준다).
            assertEquals(null, first.requestedCursor)
            assertEquals("MjoxNzg1OTE5MTE3OjQzMDgwMzAwMDo0", next.requestedCursor)
        }
}
