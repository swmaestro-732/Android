package com.chillsam.courmy.course.data

import com.chillsam.courmy.course.domain.CourseRepository
import com.chillsam.courmy.course.entity.CourseDraftVO
import com.chillsam.courmy.course.entity.CoursePlaceVO
import com.chillsam.courmy.course.entity.CourseVisibility

/**
 * 코스 작성 Repository 구현.
 *
 * TODO-API-SPEC: 현재는 UI 확인용 더미(Figma FS-34 기준값)를 반환한다.
 * 실제 임시저장/코스 조회 API 가 붙으면 DataSource·DTO 를 추가하고 교체한다.
 */
class CourseRepositoryImpl : CourseRepository {
    override suspend fun getCourseDraft(): CourseDraftVO = STUB_DRAFT

    private companion object {
        val STUB_DRAFT =
            CourseDraftVO(
                name = "",
                description = "비 오는 날에도 예쁜 성수 카페만 골라 도보로 이어지는 코스예요.",
                tags = listOf("성수", "데이트", "비 오는 날"),
                suggestedTags = listOf("감성카페", "통창뷰", "조용한", "웨이팅없음"),
                places =
                    listOf(
                        CoursePlaceVO(
                            id = "1",
                            name = "어니언 성수",
                            category = "카페 · 베이커리",
                            note = "통창 자리 꼭 앉으세요. 비 오는 날 뷰가 최고예요!",
                            photoUrls = listOf("", ""),
                            walkText = "도보 9분 · 경로 자동",
                        ),
                        CoursePlaceVO(
                            id = "2",
                            name = "대림창고 갤러리",
                            category = "전시 · 카페",
                            note = "",
                            photoUrls = emptyList(),
                            walkText = "",
                        ),
                    ),
                visibility = CourseVisibility.PUBLIC,
            )
    }
}
