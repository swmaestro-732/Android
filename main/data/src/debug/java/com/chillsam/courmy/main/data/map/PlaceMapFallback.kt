package com.chillsam.courmy.main.data.map

import android.util.Log
import com.chillsam.courmy.main.data.map.dto.PlaceMapClusterDTO
import com.chillsam.courmy.main.data.map.dto.PlaceMapDTO
import com.chillsam.courmy.main.data.map.dto.PlaceMapEnvelope
import com.chillsam.courmy.main.data.map.dto.PlaceMapMarkerDTO
import kotlinx.coroutines.CancellationException

/**
 * 지도 장소 예시 응답 — **디버그 빌드 전용**.
 *
 * dev 장소 검색 인프라(Elasticsearch)가 자주 503 으로 내려가 있어 지도를 눈으로 확인하기 어려우므로,
 * **요청이 실패했을 때만** 예시로 대신한다. 빈 결과(영역 안에 장소 0곳)는 정상 응답이라 덮지 않는다
 * ([com.chillsam.courmy.course.data.placeReviews] 의 Fallback 과 같은 규칙).
 *
 * 릴리스 변형은 `src/release` 의 no-op 을 컴파일하므로 이 예시가 릴리스 산출물에 **존재할 수 없다**.
 *
 * dev 검색 인프라가 안정되면 debug·release 양쪽 파일을 함께 지운다. [wiki-needed]
 */
internal object PlaceMapFallback {
    private const val TAG = "PlaceMap"

    /** 디버그 빌드에서도 예시 데이터를 보고 싶지 않으면 false 로 둔다. */
    private const val ENABLED = true

    /** [fetch] 가 **실패했을 때만** [sample] 로 대신한다. 코루틴 취소는 폴백으로 삼키지 않는다. */
    suspend fun resolve(fetch: suspend () -> PlaceMapEnvelope): PlaceMapEnvelope {
        if (!ENABLED) return fetch()
        return try {
            fetch()
        } catch (e: CancellationException) {
            throw e
        } catch (
            @Suppress("TooGenericExceptionCaught") e: Exception,
        ) {
            Log.w(TAG, "지도 장소 조회 실패 — 디버그 예시로 대체한다", e)
            sample()
        }
    }

    /**
     * Figma FS-10 목업(성수동 일대)과 같은 구성. 좌표는 실제 위치를 대략 맞춘 값이라 핀이 흩어져 보인다.
     *
     * category 는 서버 enum 이름 대신 화면 라벨을 그대로 싣는다 — DTO 변환이 모르는 값을 통과시키므로
     * 서버에 없는 "디저트" 칩의 클라이언트 필터까지 디버그에서 확인할 수 있다.
     */
    private fun sample() =
        PlaceMapEnvelope(
            code = 2000,
            data =
                PlaceMapDTO(
                    totalCount = 28,
                    places =
                        listOf(
                            PlaceMapMarkerDTO(1, "어니언 성수", "카페", 37.5445, 127.0559),
                            PlaceMapMarkerDTO(2, "센터커피 로스터스", "카페", 37.5451, 127.0534),
                            PlaceMapMarkerDTO(3, "대림창고 카페", "카페", 37.5424, 127.0552),
                            PlaceMapMarkerDTO(4, "아우어베이커리 성수", "디저트", 37.5439, 127.0578),
                            PlaceMapMarkerDTO(5, "성수동 소금집", "맛집", 37.5462, 127.0571),
                            PlaceMapMarkerDTO(6, "언더스탠드에비뉴", "편집숍", 37.5416, 127.0525),
                            PlaceMapMarkerDTO(7, "에스팩토리 갤러리", "전시·갤러리", 37.5432, 127.0596),
                            PlaceMapMarkerDTO(8, "성수 바 웨이브", "술집·바", 37.5471, 127.0548),
                        ),
                    clusters =
                        listOf(
                            PlaceMapClusterDTO(key = "cluster-1", latitude = 37.5479, longitude = 127.0592, count = 9),
                        ),
                ),
        )
}
