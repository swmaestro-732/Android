package com.chillsam.courmy.main.presentation.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.chillsam.courmy.common.presentation.component.DsText
import com.chillsam.courmy.common.presentation.component.DsTextField
import com.chillsam.courmy.common.presentation.ui.theme.DesignSystemThemeImpl
import com.chillsam.courmy.course.domain.CreatePlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 계획 생성 다이얼로그 상태. 입력이 "제목 하나 + 만들기"뿐이라 MVI 대신 단순 홀더로 둔다
 * (장소 상세 시트의 [com.chillsam.courmy.course.presentation.PlaceDetailViewModel] 과 같은 판단).
 */
data class PlanCreateUiState(
    val isSubmitting: Boolean = false,
    /** 생성이 끝난 계획 id. 받아 간 쪽이 토스트를 띄우고 다이얼로그를 닫은 뒤 지운다. */
    val createdPlanId: Long? = null,
    val errorMessage: String? = null,
)

/** 홈 FAB "계획하기" ViewModel. `POST /api/v1/plans` 로 제목만 실은 빈 계획을 만든다. */
@HiltViewModel
class PlanCreateViewModel
    @Inject
    constructor(
        private val createPlanUseCase: CreatePlanUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(PlanCreateUiState())
        val uiState: StateFlow<PlanCreateUiState> = _uiState

        fun create(title: String) {
            if (_uiState.value.isSubmitting) return
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            viewModelScope.launch {
                runCatching { createPlanUseCase(title) }
                    .onSuccess { planId ->
                        _uiState.update { it.copy(isSubmitting = false, createdPlanId = planId) }
                    }.onFailure { e ->
                        if (e is CancellationException) throw e
                        Log.w(TAG, "계획 생성 실패", e)
                        _uiState.update { it.copy(isSubmitting = false, errorMessage = "계획을 만들지 못했어요.") }
                    }
            }
        }

        fun consumeResult() {
            _uiState.update { it.copy(createdPlanId = null, errorMessage = null) }
        }

        private companion object {
            const val TAG = "PlanCreate"
        }
    }

/**
 * 계획 제목을 받아 빈 계획을 만드는 다이얼로그. 홈 FAB 의 "계획하기"로 연다.
 *
 * 장소·날짜는 여기서 받지 않는다 — 서버가 제목만으로 생성을 허용하고(places minItems 0),
 * FAB 에서는 "일단 이름부터 적어 두는" 가장 짧은 길이 맞다. 나머지는 계획 상세 화면의 몫이다.
 * 만든 계획은 마이 "계획" 탭에서 확인한다.
 */
@Composable
fun PlanCreateDialog(
    onDismiss: () -> Unit,
    viewModel: PlanCreateViewModel = hiltViewModel(),
) {
    val color = DesignSystemThemeImpl.designSystemColor
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf("") }

    LaunchedEffect(uiState.createdPlanId) {
        if (uiState.createdPlanId != null) {
            Toast.makeText(context, "계획을 만들었어요. 마이 > 계획에서 볼 수 있어요.", Toast.LENGTH_SHORT).show()
            viewModel.consumeResult()
            onDismiss()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.consumeResult()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color.bgDefaultLevel1)
                    .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            DsText(
                text = "계획하기",
                style = DesignSystemThemeImpl.typeScale.textStrongM,
                color = color.contentDefaultLevel0,
            )
            DsText(
                text = "가 보고 싶은 곳을 모아 둘 계획 이름을 지어 주세요.",
                style = DesignSystemThemeImpl.typeScale.textRegularXS,
                color = color.contentDefaultLevel3,
                maxLines = 2,
            )
            DsTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "예) 이번 주말 성수 나들이",
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                DsText(
                    text = "취소",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = color.contentDefaultLevel2,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                )
                Spacer(Modifier.width(4.dp))
                val canSubmit = title.isNotBlank() && !uiState.isSubmitting
                DsText(
                    text = if (uiState.isSubmitting) "만드는 중…" else "만들기",
                    style = DesignSystemThemeImpl.typeScale.textStrongS,
                    color = if (canSubmit) color.contentOnAccent else color.contentDefaultLevel3,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (canSubmit) color.bgAccent else color.borderDefaultLevel0)
                            .clickable(enabled = canSubmit) { viewModel.create(title) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
    }
}
