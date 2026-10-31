package com.chillsam.courmy.course.data.placeSave

import com.chillsam.courmy.common.data.BaseRemoteDataSource
import com.chillsam.courmy.course.data.placeSave.dto.SavePlaceRequest

/** 쓰기 전용이라 폴백이 없다 — 실패를 가짜 성공으로 만들지 않고 예외로 올린다(코스 저장과 동일). */
class PlaceSaveDataSource(
    private val apiService: PlaceSaveApiService,
) : BaseRemoteDataSource() {
    suspend fun save(placeId: Long) {
        // 저장은 201(Created)이고 응답 바디가 비어 있을 수 있어 상태 코드만 확인한다.
        checkSuccess(apiService.save(SavePlaceRequest(placeId = placeId)))
    }

    suspend fun unsave(placeId: Long) {
        checkSuccess(apiService.unsave(placeId))
    }

    suspend fun visit(placeId: Long) {
        checkSuccess(apiService.visit(placeId))
    }
}
