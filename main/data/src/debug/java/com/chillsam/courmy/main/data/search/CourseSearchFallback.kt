package com.chillsam.courmy.main.data.search

import android.util.Log
import com.chillsam.courmy.main.data.search.dto.CourseSearchDTO
import com.chillsam.courmy.main.data.search.dto.CourseSearchEnvelope
import com.chillsam.courmy.main.data.search.dto.CourseSearchItemDTO
import kotlinx.coroutines.CancellationException

/**
 * 코스 검색 예시 응답 — **디버그 빌드 전용**.
 *
 * dev 검색 인프라가 자주 비거나 내려가 있어 레이아웃을 눈으로 확인하기 어려우므로,
 * **요청이 실패했을 때만** 예시로 대신한다. 빈 목록(검색 결과 0건)은 정상 응답이라 덮지 않는다
 * ([com.chillsam.courmy.course.data.placeReviews] 의 Fallback 과 같은 규칙).
 *
 * 릴리스 변형은 `src/release` 의 no-op 을 컴파일하므로 이 예시가 릴리스 산출물에 **존재할 수 없다**.
 *
 * dev 서버에 시드 데이터가 들어오면 debug·release 양쪽 파일을 함께 지운다. [wiki-needed]
 */
internal object CourseSearchFallback {
    private const val TAG = "CourseSearch"

    /** 디버그 빌드에서도 예시 데이터를 보고 싶지 않으면 false 로 둔다. */
    private const val ENABLED = true

    /** [fetch] 가 **실패했을 때만** [sample] 로 대신한다. 코루틴 취소는 폴백으로 삼키지 않는다. */
    suspend fun resolve(fetch: suspend () -> CourseSearchEnvelope): CourseSearchEnvelope {
        if (!ENABLED) return fetch()
        return try {
            fetch()
        } catch (e: CancellationException) {
            throw e
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            Log.w(TAG, "코스 검색 실패 — 디버그 예시로 대체한다", e)
            sample()
        }
    }

    /** 검색 화면(FS-18) 목업과 같은 구성. 저장 수는 "1.2k명" 같은 축약 표기를 확인할 수 있는 값으로 둔다. */
    private fun sample() =
        CourseSearchEnvelope(
            code = 2000,
            data =
                CourseSearchDTO(
                    courses =
                        listOf(
                            CourseSearchItemDTO(
                                id = 1,
                                title = "비 오는 날 성수 감성 카페 코스",
                                theme = "culture",
                                area = "성수",
                                savesCnt = 1_200,
                            ),
                            CourseSearchItemDTO(
                                id = 2,
                                title = "성수 카페 호핑 브런치 코스",
                                theme = "cafe",
                                area = "성수",
                                savesCnt = 860,
                            ),
                            CourseSearchItemDTO(
                                id = 3,
                                title = "성수 디저트 카페 투어",
                                theme = "restaurant",
                                area = "성수",
                                savesCnt = 640,
                            ),
                            CourseSearchItemDTO(
                                id = 4,
                                title = "골목 사이 성수 로스터리 코스",
                                theme = "cafe",
                                area = "성수",
                                savesCnt = 410,
                            ),
                        ),
                    nextCursor = null,
                    hasNext = false,
                ),
        )
}
