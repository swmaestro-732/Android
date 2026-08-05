package com.chillsam.courmy.main.domain.saved

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

        override suspend fun getSavedCourses(size: Int): List<SavedCourseVO> {
            requestedSize = size
            return emptyList()
        }

        override suspend fun getSavedCourseIds(size: Int): Set<String> {
            requestedIdsSize = size
            return emptySet()
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

    /** 홈 카드 대조는 한 페이지만 보므로 최대치를 요청해 정확도를 높인다. */
    @Test
    fun `저장 id 조회는 서버 최대치를 요청한다`() =
        runTest {
            val repo = FakeRepository()

            GetSavedCourseIdsUseCase(repo)()

            assertEquals(GetSavedCourseIdsUseCase.MAX_SIZE, repo.requestedIdsSize)
        }
}
