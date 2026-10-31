package com.chillsam.courmy.course.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsChip
import com.chillsam.courmy.common.presentation.ui.modifier.cardShadow
import com.chillsam.courmy.course.entity.CourseCompanionVO

/**
 * ③ 코스 설정 — 동행 카드. 누구와 함께했는지(혼자·연인·가족·친구)를 칩으로 고른다.
 *
 * **선택 입력**이다 — 태그 카드와 공개 설정 사이에 놓이지만, 공개 설정과 달리 안 골라도 저장된다.
 * 선택된 칩을 다시 탭하면 해제되며([onToggle] 재전달), 그 규칙은 ViewModel 이 판단한다.
 */
@Composable
fun CourseCompanionCard(
    selected: CourseCompanionVO?,
    onToggle: (CourseCompanionVO) -> Unit,
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
        FieldLabel("누구와 함께했나요? (선택)")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CourseCompanionVO.entries.forEach { companion ->
                DsChip(
                    text = companion.label,
                    selected = companion == selected,
                    modifier = Modifier.semantics { this.selected = companion == selected },
                    onClick = { onToggle(companion) },
                )
            }
        }
    }
}
