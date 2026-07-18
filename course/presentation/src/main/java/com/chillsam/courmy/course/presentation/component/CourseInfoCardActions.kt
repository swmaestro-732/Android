package com.chillsam.courmy.course.presentation.component

/** [CourseInfoCard] 편집 콜백 묶음. */
data class CourseInfoCardActions(
    val onNameChange: (String) -> Unit,
    val onDescriptionChange: (String) -> Unit,
    val onRemoveTag: (String) -> Unit,
    val onAddTag: (String) -> Unit,
)
