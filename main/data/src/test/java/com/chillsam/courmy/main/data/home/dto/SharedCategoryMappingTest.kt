package com.chillsam.courmy.main.data.home.dto

import com.chillsam.courmy.common.entity.category.CourseTagVO
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
    private val expectedPlaceCategories =
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

    private val expectedCourseTags =
        listOf(
            "date" to "데이트",
            "healing" to "힐링",
            "food" to "맛집",
            "cafetour" to "카페 투어",
            "culture" to "문화",
            "nature" to "자연",
            "nightview" to "야경",
            "shopping" to "쇼핑",
            "tradition" to "역사",
            "activity" to "액티비티",
            "family" to "가족 나들이",
            "solo" to "혼놀",
        )

    @Test
    fun `장소 카테고리 10종 코드가 같은 라벨로 표시된다`() {
        assertEquals(expectedPlaceCategories, PlaceCategoryVO.entries.map { it.code to it.label })
        expectedPlaceCategories.forEach { (code, label) ->
            assertEquals(code, PlaceCategoryVO.fromCode(" ${code.uppercase()} ")?.code)
            assertEquals(label, code.toPlaceCategoryLabel())
        }
    }

    @Test
    fun `코스 태그 12종이 홈 추천 검색 저장함 프로필 코스 카드에서 같은 라벨로 표시된다`() {
        assertEquals(expectedCourseTags, CourseTagVO.entries.map { it.code to it.label })
        expectedCourseTags.forEach { (code, label) ->
            // 서버 `theme` 응답은 CourseCategory enum 이름(대문자)이다. 소문자 코드도 같은 라벨로 읽힌다.
            listOf(code, code.uppercase()).forEach { theme ->
                assertEquals(label, HomeRecommendedCourseDTO(id = 1, theme = theme).toVO().categoryLabel)
                assertEquals(
                    label,
                    CourseFeedDTO(courses = listOf(CourseFeedItemDTO(id = 1, theme = theme)))
                        .toPageVO()
                        .items
                        .single()
                        .categoryLabel,
                )
                assertEquals(
                    label,
                    CourseSearchDTO(courses = listOf(CourseSearchItemDTO(id = 1, theme = theme)))
                        .toPageVO()
                        .items
                        .single()
                        .categoryLabel,
                )
                assertEquals(
                    label,
                    SavedCourseScreenDTO(
                        savedCourses =
                            listOf(SavedCourseItemDTO(courseId = 1, course = SavedCourseSummaryDTO(theme = theme))),
                    ).toPageVO().items.single().tagLabel,
                )
                val profile = MyPageScreenDTO(courses = listOf(MyPageCourseDTO(id = "1", theme = theme)))
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
}
