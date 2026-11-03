package com.chillsam.courmy.main.presentation.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.chillsam.courmy.common.presentation.component.DsSwitch
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl

/**
 * 알림 항목 한 줄(제목·설명 + 토글). 동의 팝업과 설정 > 알림이 같은 행을 쓴다.
 * [tag] 는 "(필수)/(선택)" 같은 구분 표기로, 라벨보다 한 단계 가볍게 붙는다.
 * [caption] 은 설명 아래 보조 상태 줄로, 서버가 확인한 동의 처리 시각 안내 등에 쓴다(없으면 표시 안 함).
 */
@Composable
internal fun NotificationToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String? = null,
    caption: String? = null,
    enabled: Boolean = true,
) {
    val color = DesignSystemThemeImpl.designSystemColor
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .toggleable(
                    value = checked,
                    enabled = enabled,
                    role = Role.Switch,
                    onValueChange = { onToggle() },
                ).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DsText(
                    text = title,
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel0,
                )
                if (tag != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    DsText(
                        text = tag,
                        style = DesignSystemThemeImpl.typeScale.textRegularXS,
                        color = color.contentDefaultLevel1,
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            DsText(
                text = description,
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel1,
            )
            if (caption != null) {
                Spacer(modifier = Modifier.height(2.dp))
                DsText(
                    text = caption,
                    style = DesignSystemThemeImpl.typeScale.textRegularXS,
                    color = color.contentDefaultLevel1,
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        // 행 전체가 토글이므로 스위치의 중복 접근성 노드는 지운다.
        DsSwitch(
            checked = checked,
            onCheckedChange = { onToggle() },
            enabled = enabled,
            modifier = Modifier.clearAndSetSemantics {},
        )
    }
}
