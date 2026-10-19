package com.chillsam.courmy.main.data.home.dto

import com.chillsam.courmy.common.entity.category.PlaceCategoryVO
import com.chillsam.courmy.common.entity.category.toPlaceCategoryLabel
import com.chillsam.courmy.main.data.profile.dto.MyPageCourseDTO
import com.chillsam.courmy.main.data.profile.dto.MyPageScreenDTO
import com.chillsam.courmy.main.data.profile.dto.toMyProfileVO
import com.chillsam.courmy.main.data.profile.dto.toUserProfileVO
import com.chillsam.courmy.main.data.saved.dto.SavedCourseItemDTO
import com.chillsam.courmy.main.data.saved.dto.SavedCourseScreenDTO
import com.chillsam.courmy.main.data.saved.dto.SavedCourseSummaryDTO
import com.chillsam.courmy.main.data.saved.dto.toPageVO
import com.chillsam.courmy.main.data.search.dto.CourseSearchDTO
import com.chillsam.courmy.main.data.search.dto.CourseSearchItemDTO
import com.chillsam.courmy.main.data.search.dto.toPageVO
import org.junit.Assert.assertEquals
import org.junit.Test

class SharedCategoryMappingTest {
    private val expected =
        listOf(
            "cafe" to "카페·디저트",
            "restaurant" to "음식점",
            "bar" to "술집·바",
            "shopping" to "쇼핑·상점",
            "culture" to "문화·전시",
            "experience" to "체험·클래스",
            "nature" to "자연·아웃도어",
            "landmark" to "역사·명소",
            "entertainment" to "여가·놀거리",
            "wellness" to "웰니스·힐링",
        )

    @Test
    fun `10종 코드가 홈 추천 검색 저장함 프로필 코스 카드에서 같은 라벨로 표시된다`() {
        assertEquals(expected, PlaceCategoryVO.entries.map { it.code to it.label })
        expected.forEach { (code, label) ->
            assertEquals(code, PlaceCategoryVO.fromCode(" ${code.uppercase()} ")?.code)
            assertEquals(label, code.toPlaceCategoryLabel())
            assertEquals(label, HomeRecommendedCourseDTO(id = 1, theme = code).toVO().categoryLabel)
            assertEquals(
                label,
                CourseFeedDTO(courses = listOf(CourseFeedItemDTO(id = 1, theme = code)))
                    .toPageVO()
                    .items
                    .single()
                    .categoryLabel,
            )
            assertEquals(
                label,
                CourseSearchDTO(courses = listOf(CourseSearchItemDTO(id = 1, theme = code)))
                    .toPageVO()
                    .items
                    .single()
                    .categoryLabel,
            )
            assertEquals(
                label,
                SavedCourseScreenDTO(
                    savedCourses =
                        listOf(SavedCourseItemDTO(courseId = 1, course = SavedCourseSummaryDTO(theme = code))),
                ).toPageVO().items.single().tagLabel,
            )
            val profile = MyPageScreenDTO(courses = listOf(MyPageCourseDTO(id = "1", theme = code)))
            assertEquals(
                label,
                profile
                    .toMyProfileVO()
                    .myCourses.items
                    .single()
                    .categoryLabel,
            )
            assertEquals(
                label,
                profile
                    .toUserProfileVO(myUserId = null)
                    .courses.items
                    .single()
                    .categoryLabel,
            )
        }
    }
}
