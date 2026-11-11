package com.chillsam.courmy.common.presentation.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode

/** Gate user-initiated writes before making a request. Login never replays a pending write. */
@Composable
fun rememberLoginGate(): (() -> Unit) -> Unit {
    if (LocalInspectionMode.current) return { action -> action() }
    val session = LocalSessionUiState.current
    return remember(session) { { action -> session.requireLogin(action) } }
}
