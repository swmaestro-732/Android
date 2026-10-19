package com.chillsam.courmy.course.data.courseLike

import com.chillsam.courmy.course.data.courseLike.dto.toVO
import com.chillsam.courmy.course.domain.CourseLikeRepository
import com.chillsam.courmy.course.entity.CourseLikeVO

class CourseLikeRepositoryImpl(
    private val dataSource: CourseLikeDataSource,
) : CourseLikeRepository {
    override suspend fun setLiked(
        courseId: Long,
        liked: Boolean,
    ): CourseLikeVO? {
        val envelope = if (liked) dataSource.like(courseId) else dataSource.unlike(courseId)
        // data 가 없으면 보정값 없이 성공만 알린다(계약 KDoc 참고). 실패는 이미 예외로 올라갔다.
        return envelope?.data?.toVO(requestedLiked = liked)
    }
}
