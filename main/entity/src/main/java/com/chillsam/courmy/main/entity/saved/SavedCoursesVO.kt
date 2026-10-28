package com.chillsam.courmy.main.entity.saved

import com.chillsam.courmy.common.entity.paging.CursorPageVO

data class SavedCoursesVO(
    val page: CursorPageVO<SavedCourseVO>,
    val totalCount: Int = 0,
    val uncompletedCount: Int = 0,
    val completedCount: Int = 0,
    val folders: List<SavedCourseFolderVO> = emptyList(),
)

data class SavedCourseFolderVO(
    val id: Long,
    val name: String,
    val count: Int,
)
