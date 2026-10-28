package com.chillsam.courmy.main.domain.search

import com.chillsam.courmy.common.entity.paging.CursorPageVO
import com.chillsam.courmy.main.entity.search.SearchCourseVO
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class SearchCoursesUseCaseTest {
    private class FakeRepository(
        val page: CursorPageVO<SearchCourseVO> = CursorPageVO(),
    ) : CourseSearchRepository {
        var requestedKeyword: String? = null
        var requestedSize: Int? = null
        var requestedCursor: String? = null

        override suspend fun searchCourses(
            keyword: String,
            size: Int,
            cursor: String?,
        ): CursorPageVO<SearchCourseVO> {
            requestedKeyword = keyword
            requestedSize = size
            requestedCursor = cursor
            return page
        }
    }

    @Test
    fun `검색어 양끝 공백을 제거하고 기본 크기로 첫 페이지를 조회한다`() =
        runTest {
            val repository = FakeRepository()

            SearchCoursesUseCase(repository)("  성수 산책 \n")

            assertEquals("성수 산책", repository.requestedKeyword)
            assertEquals(20, repository.requestedSize)
            assertNull(repository.requestedCursor)
        }

    @Test
    fun `빈 검색어는 저장소를 호출하지 않고 빈 페이지를 반환한다`() =
        runTest {
            val repository = FakeRepository()
            val useCase = SearchCoursesUseCase(repository)

            listOf("", " \t\n ").forEach { keyword ->
                assertEquals(CursorPageVO<SearchCourseVO>(), useCase(keyword, cursor = "next"))
            }

            assertNull(repository.requestedKeyword)
        }

    @Test
    fun `요청 크기를 서버 허용 범위로 제한한다`() =
        runTest {
            val repository = FakeRepository()
            val useCase = SearchCoursesUseCase(repository)

            mapOf(-1 to 1, 0 to 1, 1 to 1, 25 to 25, 50 to 50, 51 to 50).forEach { (size, expected) ->
                useCase("산책", size = size)
                assertEquals(expected, repository.requestedSize)
            }
        }

    @Test
    fun `불투명 커서를 그대로 전달하고 결과의 항목과 다음 페이지 정보를 보존한다`() =
        runTest {
            val page =
                CursorPageVO(
                    items = listOf(SearchCourseVO(id = "42", title = "성수 산책")),
                    nextCursor = "next+/=",
                    hasNext = true,
                )
            val repository = FakeRepository(page)

            val result = SearchCoursesUseCase(repository)("산책", cursor = "current+/=")

            assertEquals("current+/=", repository.requestedCursor)
            assertSame(page, result)
        }
}
