package com.chillsam.courmy.main.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 회원가입 플로우에서 고른 관심 테마·지역을 완료 화면(FS-08)까지 전달하는 임시 인메모리 홀더.
 *
 * 테마→지역→완료가 서로 다른 라우트(=별개 컴포저블 스코프)라 선택값을 직접 넘길 수 없어,
 * 각 단계의 "다음"에서 이 홀더에 담고 완료 화면이 읽는다. 실 회원가입 API·프로필 저장이
 * 붙기 전까지의 시연용이다.
 */
object SignupSelectionStore {
    var themes by mutableStateOf<List<String>>(emptyList())
    var regions by mutableStateOf<List<String>>(emptyList())
}
