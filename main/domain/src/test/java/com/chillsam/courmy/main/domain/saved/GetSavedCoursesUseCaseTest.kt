package com.chillsam.courmy.main.domain.saved

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.saved.SavedCourseVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetSavedCoursesUseCaseTest {
    private class FakeRepository : SavedCourseRepository {
        var requestedSize: Int? = null
            private set
        var requestedIdsSize: Int? = null
            private set

        /** id 조회는 커서를 따라 끝까지 도는지 보려고 호출 횟수를 센다. */
        var idsCallCount = 0
            private set

        override suspend fun getSavedCourses(
            size: Int,
            cursor: String?,
        ): CursorPageVO<SavedCourseVO> {
            requestedSize = size
            return CursorPageVO()
        }

        override suspend fun getSavedCourseIds(
            size: Int,
            cursor: String?,
        ): CursorPageVO<String> {
            requestedIdsSize = size
            idsCallCount += 1
            // 첫 페이지만 다음 커서를 주고, 두 번째에서 끝낸다.
            return if (cursor == null) {
                CursorPageVO(items = listOf("1"), nextCursor = "c1", hasNext = true)
            } else {
                CursorPageVO(items = listOf("2"))
            }
        }
    }

    @Test
    fun `범위를 벗어난 size 는 서버 한도로 맞춘다`() =
        runTest {
            val tooSmall = FakeRepository()
            val tooLarge = FakeRepository()

            GetSavedCoursesUseCase(tooSmall)(size = 0)
            GetSavedCoursesUseCase(tooLarge)(size = 999)

            assertEquals(1, tooSmall.requestedSize)
            assertEquals(50, tooLarge.requestedSize)
        }

    /** 페이지를 적게 돌수록 홈 진입이 빨라지므로 매번 서버 최대치를 요청한다. */
    @Test
    fun `저장 id 조회는 서버 최대치를 요청한다`() =
        runTest {
            val repo = FakeRepository()

            GetSavedCourseIdsUseCase(repo)()

            assertEquals(GetSavedCourseIdsUseCase.MAX_SIZE, repo.requestedIdsSize)
        }

    /** 한 페이지만 보면 그 밖의 코스가 저장 안 된 것처럼 보인다. 커서를 따라 끝까지 모아야 한다. */
    @Test
    fun `저장 id 조회는 커서를 따라 끝까지 모은다`() =
        runTest {
            val repo = FakeRepository()

            val ids = GetSavedCourseIdsUseCase(repo)()

            assertEquals(setOf("1", "2"), ids)
            assertEquals(2, repo.idsCallCount)
        }
}
