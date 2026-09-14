package com.chillsam.courmy.main.presentation.my.mock

import com.chillsam.courmy.main.entity.profile.ProfileCourseVO

/**
 * 프로필 "포크" 탭이 API 연동 전까지 쓰는 임시 데이터.
 *
 * 서버에 포크한 코스를 돌려주는 엔드포인트가 아직 없다. 탭과 그리드가 실제로 도는 걸 보려면
 * 값이 있어야 해서 시안 톤에 맞춘 코스 몇 건을 넣어 둔다.
 *
 * id 는 내 코스와 섞이지 않도록 접두사를 달았다. 이 값으로 코스 상세를 열면 서버에 없는 코스라
 * 실패하는데, 지금 단계에서는 탭 전환·그리드 렌더를 보는 게 목적이라 그대로 둔다.
 *
 * TODO(SCRUM-포크API): 포크 목록 API 가 붙으면 이 파일을 지우고 UseCase 로 교체한다.
 */
internal object ProfileForkMockData {
    val forkedCourses: List<ProfileCourseVO> =
        listOf(
            ProfileCourseVO(id = "mock-fork-1", title = "성수 커피 투어"),
            ProfileCourseVO(id = "mock-fork-2", title = "연남 골목 산책"),
            ProfileCourseVO(id = "mock-fork-3", title = "한강 노을 코스"),
            ProfileCourseVO(id = "mock-fork-4", title = "을지로 전시 나들이"),
            ProfileCourseVO(id = "mock-fork-5", title = "망원 브런치 데이"),
        )
}
