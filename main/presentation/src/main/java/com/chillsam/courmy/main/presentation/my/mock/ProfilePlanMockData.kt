package com.chillsam.courmy.main.presentation.my.mock

import com.chillsam.courmy.main.entity.profile.ProfileCourseVO

/**
 * 프로필 "계획" 탭이 API 연동 전까지 쓰는 임시 데이터.
 *
 * 서버에 계획 목록을 돌려주는 엔드포인트가 아직 없다. 탭과 그리드가 실제로 도는 걸 보려면
 * 값이 있어야 해서 시안 톤에 맞춘 몇 건을 넣어 둔다.
 *
 * id 는 코스와 섞이지 않도록 접두사를 달았다. 서버에 없는 id 라 이 값으로는 코스 상세를 열 수
 * 없고, 그래서 화면도 계획 탭에서는 카드 클릭을 흘려보낸다([com.chillsam.courmy.main.presentation.my.MyPage]).
 *
 * TODO(SCRUM-계획API): 계획 목록 API 가 붙으면 이 파일을 지우고 UseCase 로 교체한다.
 */
internal object ProfilePlanMockData {
    val planCourses: List<ProfileCourseVO> =
        listOf(
            ProfileCourseVO(id = "mock-plan-1", title = "이번 주말 성수 카페"),
            ProfileCourseVO(id = "mock-plan-2", title = "연남 골목 산책"),
            ProfileCourseVO(id = "mock-plan-3", title = "한강 노을 보러"),
            ProfileCourseVO(id = "mock-plan-4", title = "을지로 전시 나들이"),
            ProfileCourseVO(id = "mock-plan-5", title = "망원 브런치 데이"),
        )
}
