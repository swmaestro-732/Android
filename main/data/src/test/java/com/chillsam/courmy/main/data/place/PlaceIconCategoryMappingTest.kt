package com.chillsam.courmy.main.data.place

import com.chillsam.courmy.common.entity.category.PlaceDetailCategoryVO
import com.chillsam.courmy.main.data.home.dto.HomeDTO
import com.chillsam.courmy.main.data.home.dto.toVO
import com.chillsam.courmy.main.data.saved.dto.SavedPlaceScreenDTO
import com.chillsam.courmy.main.data.saved.dto.toPageVO
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceIconCategoryMappingTest {
    @Test
    fun `홈과 저장함이 같은 상세 분류 및 누락 폴백을 전달한다`() {
        mapOf(
            "" to null,
            ",\"detailCategory\":null" to null,
            ",\"detailCategory\":\"FUTURE\"" to null,
            ",\"detailCategory\":\" bakery \"" to PlaceDetailCategoryVO.BAKERY,
        ).forEach { (field, expected) ->
            val home =
                Json
                    .decodeFromString<HomeDTO>(
                        """{"nearbySavedPlaces":[{"placeId":1,"category":"CAFE"$field}]}""",
                    ).toVO()
                    .nearbySavedPlaces
                    .single()
            val saved =
                Json
                    .decodeFromString<SavedPlaceScreenDTO>(
                        """{"savedPlaces":[{"placeId":1,"place":{"category":"CAFE"$field}}]}""",
                    ).toPageVO()
                    .items
                    .single()
            assertEquals(expected, home.detailCategory)
            assertEquals(expected, saved.detailCategory)
            assertEquals("카페·디저트", home.categoryLabel)
            assertEquals(home.categoryLabel, saved.categoryLabel)
        }
    }
}
