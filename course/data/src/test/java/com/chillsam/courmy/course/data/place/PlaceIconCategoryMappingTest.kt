package com.chillsam.courmy.course.data.place

import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import com.chillsam.courmy.course.data.courseDetail.dto.CourseScreenData
import com.chillsam.courmy.course.data.courseDetail.dto.toVO
import com.chillsam.courmy.course.data.draft.dto.toDraftVO
import com.chillsam.courmy.course.data.place.dto.ExternalPlaceSearchDataDTO
import com.chillsam.courmy.course.data.place.dto.PlaceDTO
import com.chillsam.courmy.course.data.place.dto.toVO
import com.chillsam.courmy.course.data.place.dto.toVOList
import com.chillsam.courmy.course.data.placeDetail.dto.PlaceDetailScreenDTO
import com.chillsam.courmy.course.data.placeDetail.dto.toVO
import com.chillsam.courmy.course.data.plans.dto.PlanDetailDTO
import com.chillsam.courmy.course.data.plans.dto.toVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceIconCategoryMappingTest {
    @Test
    fun `검색 상세 계획 코스 초안은 상세 분류와 누락 폴백을 함께 유지한다`() {
        mapOf(
            "" to null,
            ",\"detailCategory\":null" to null,
            ",\"detailCategory\":\"FUTURE\"" to null,
            ",\"detailCategory\":\"BAKERY\"" to PlaceDetailCategoryVO.BAKERY,
        ).forEach { (field, expected) ->
            val search = Json.decodeFromString<PlaceDTO>("""{"id":1$field}""").toVO()
            val external =
                Json
                    .decodeFromString<ExternalPlaceSearchDataDTO>(
                        """{"places":[{"id":1,"address":"bakery"$field}]}""",
                    ).toVOList()
                    .single()
            val detail =
                Json
                    .decodeFromString<PlaceDetailScreenDTO>("""{"place":{"id":1$field}}""")
                    .toVO()
            val plan =
                Json
                    .decodeFromString<PlanDetailDTO>(
                        """{"planId":1,"places":[{"placeId":1$field}]}""",
                    ).toVO()
                    .places
                    .single()
            val course =
                Json.decodeFromString<CourseScreenData>(
                    """{"course":{"places":[{"placeId":1$field}]}}""",
                )
            val categories =
                listOf(
                    search.detailCategory,
                    external.detailCategory,
                    detail.detailCategory,
                    plan.detailCategory,
                    course
                        .toVO(null)
                        .places
                        .single()
                        .detailCategory,
                    course
                        .toDraftVO(null)
                        .places
                        .single()
                        .detailCategory,
                )
            categories.forEach { assertEquals(expected, it) }
            // 코스 작성 상태 저장/복원 시에도 상세 아이콘 정보가 유실되지 않는다.
            assertEquals(search, Json.decodeFromString<CoursePlaceVO>(Json.encodeToString(search)))
        }
    }
}
