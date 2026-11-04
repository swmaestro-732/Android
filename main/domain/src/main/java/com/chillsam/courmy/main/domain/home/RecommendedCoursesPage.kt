package com.chillsam.courmy.main.domain.home

import com.chillsam.courmy.common.domain.navigation.NavRoute
import com.chillsam.courmy.common.domain.navigation.Page

/**
 * 오늘의 추천 코스 전체 목록 화면 식별자.
 *
 * 홈(FS-09)이 가로 미리보기 카드 몇 장만 남기도록 개편되면서,
 * 기존 홈의 공개 코스 피드 리스트 전체는 이 화면으로 옮겼다.
 * 홈의 "전체보기"에서 진입한다.
 */
object RecommendedCoursesPage : Page {
    const val PATH = "/home/recommended"

    /** 진입 시 선택해 둘 코스 태그 코드(예: "date"). 없으면 전체. 과거 라벨("데이트")도 수신 측이 허용한다. */
    const val ARG_CATEGORY = "category"

    /** 지역별 둘러보기에서 진입할 때 좁힐 시·군·구 법정동코드. 없으면 지역 제한 없음. */
    const val ARG_SIGUNGU = "sigungu"

    /** 지역 이름(상단 제목 표시용). 코드만으로는 이름을 복원할 수 없어 함께 싣는다. */
    const val ARG_REGION_NAME = "regionName"

    override fun toRoute(): NavRoute = NavRoute(PATH)

    /** 홈 테마 타일에서 특정 카테고리가 선택된 상태로 진입할 때 쓴다. [category] 는 코스 태그 코드. */
    fun route(category: String): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_CATEGORY to category),
        )

    /** 지역별 둘러보기에서 특정 시·군·구로 진입할 때 쓴다. [sigungu] 는 법정동코드, [regionName] 은 표시용 이름. */
    fun regionRoute(
        sigungu: String,
        regionName: String,
    ): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_SIGUNGU to sigungu, ARG_REGION_NAME to regionName),
        )

    /**
     * 홈 지역별 둘러보기의 권역(수도권·강원 등) 블롭에서 진입할 때 쓴다. [regionName] 은 권역 이름.
     *
     * 권역은 여러 시·도를 묶은 것이라 단일 법정동코드가 없다. [wiki-needed] 백엔드 선행: 권역 필터 계약이
     * 없어 지금은 제목만 "{권역} 코스"로 바꾸고 목록은 전국을 그대로 받는다(sigungu 는 싣지 않는다).
     */
    fun regionNameRoute(regionName: String): NavRoute =
        NavRoute(
            path = PATH,
            args = mapOf(ARG_REGION_NAME to regionName),
        )
}
