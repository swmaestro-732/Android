package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.course.entity.CourseVisibility

/**
 * ③ 코스 설정 — 공개 설정 카드. [CourseVisibilitySegment] 를 태그·동행 카드와 같은
 * 흰 카드(그림자·17dp 패딩·필드 라벨)로 감싸 설정 단계의 항목 생김새를 통일한다.
 *
 * 편집 화면(CourseEditScreen)은 자체 레이아웃이 따로 있어 세그먼트를 직접 쓴다.
 */
@Composable
fun CourseVisibilityCard(
    selected: CourseVisibility,
    onSelect: (CourseVisibility) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .cardShadow(RoundedCornerShape(18.dp))
                .padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FieldLabel("공개 설정")
        CourseVisibilitySegment(selected = selected, onSelect = onSelect)
    }
}
