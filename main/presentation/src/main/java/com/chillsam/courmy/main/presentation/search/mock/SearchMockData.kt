package com.chillsam.courmy.main.presentation.search.mock

import com.chillsam.courmy.main.entity.map.MapPlaceVO
import com.chillsam.courmy.main.entity.search.RankChange
import com.chillsam.courmy.main.entity.search.SearchCourseVO
import com.chillsam.courmy.main.entity.search.TrendingKeywordVO

/**
 * 검색 화면(FS-17 / FS-18)이 API 연동 전까지 쓰는 임시 데이터. 값은 시안 그대로다.
 *
 * 장소는 지도 탭과 같은 [MapPlaceVO] 를 쓴다 — 검색 결과의 장소 줄과 지도 시트의 장소 줄이
 * 보여주는 값(이름·평점·카테고리·도보 시간·저장 여부)이 똑같아서, 같은 것을 두 벌로 두지 않는다.
 *
 * TODO(SCRUM-검색API): 통합 검색 API 가 붙으면 이 파일을 지우고 UseCase 로 교체한다.
 */
internal object SearchMockData {
    /** 검색 결과 화면이 처음 여는 검색어. 입력 없이 결과를 열어 볼 때 쓴다. */
    const val SAMPLE_QUERY = "성수 카페"

    /** 급상승 검색어 집계 시각 안내. */
    const val TRENDING_BASE_TIME = "오후 3시 기준"

    /** 검색어에 걸린 전체 장소 수. 목록에는 일부만 싣는다. */
    const val PLACE_TOTAL_COUNT = 18

    /** 검색어에 걸린 전체 코스 수. */
    const val COURSE_TOTAL_COUNT = 6

    val recentKeywords: List<String> =
        listOf("성수 카페", "어니언 성수", "연남 브런치", "을지로 전시", "루프탑 바")

    val trendingKeywords: List<TrendingKeywordVO> =
        listOf(
            TrendingKeywordVO(1, "망원 소품샵", RankChange.Up(2)),
            TrendingKeywordVO(2, "성수 루프탑", RankChange.Up(1)),
            TrendingKeywordVO(3, "삼청동 전시", RankChange.New),
            TrendingKeywordVO(4, "연남 브런치 카페", RankChange.Down(1)),
            TrendingKeywordVO(5, "한남동 와인바", RankChange.Same),
            TrendingKeywordVO(6, "을지로 노포 코스", RankChange.Up(4)),
        )

    /** 장소 탭의 결과. 전체 탭은 이 목록의 앞쪽 몇 건만 보여준다. */
    val places: List<MapPlaceVO> =
        listOf(
            MapPlaceVO(
                id = 1L,
                name = "어니언 성수",
                category = "카페",
                subCategory = "베이커리",
                rating = 4.8,
                walkingMinutes = 6,
                saved = true,
            ),
            MapPlaceVO(
                id = 3L,
                name = "대림창고 카페",
                category = "카페",
                subCategory = "전시",
                rating = 4.6,
                walkingMinutes = 9,
            ),
            MapPlaceVO(
                id = 9L,
                name = "센터커피 성수",
                category = "카페",
                subCategory = "로스터리",
                rating = 4.7,
                walkingMinutes = 12,
            ),
            MapPlaceVO(
                id = 4L,
                name = "아우어베이커리 성수",
                category = "카페",
                subCategory = "디저트",
                rating = 4.5,
                walkingMinutes = 8,
                saved = true,
            ),
            MapPlaceVO(
                id = 10L,
                name = "콤포트 성수",
                category = "카페",
                subCategory = "브런치",
                rating = 4.6,
                walkingMinutes = 5,
            ),
            MapPlaceVO(
                id = 11L,
                name = "대성정미소 카페",
                category = "카페",
                subCategory = "전시",
                rating = 4.4,
                walkingMinutes = 14,
            ),
        )

    /** 코스 탭의 결과. 전체 탭의 "관련 코스"는 이 목록의 앞쪽 몇 건만 보여준다. */
    val courses: List<SearchCourseVO> =
        listOf(
            SearchCourseVO(
                id = "mock-course-1",
                title = "비 오는 날 성수 감성 카페 코스",
                categoryLabel = "성수 · 데이트",
                spotCount = 4,
                durationHours = 3,
                authorName = "지호님",
                followCountText = "1.2k명",
            ),
            SearchCourseVO(
                id = "mock-course-2",
                title = "성수 카페 호핑 브런치 코스",
                categoryLabel = "성수 · 브런치",
                spotCount = 3,
                durationHours = 2,
                authorName = "나영님",
                followCountText = "860명",
            ),
            SearchCourseVO(
                id = "mock-course-3",
                title = "성수 디저트 카페 투어",
                categoryLabel = "성수 · 디저트",
                spotCount = 5,
                durationHours = 4,
                authorName = "성현님",
                followCountText = "640명",
            ),
            SearchCourseVO(
                id = "mock-course-4",
                title = "골목 사이 성수 로스터리 코스",
                categoryLabel = "성수 · 로스터리",
                spotCount = 4,
                durationHours = 3,
                authorName = "지호님",
                followCountText = "410명",
            ),
        )
}
