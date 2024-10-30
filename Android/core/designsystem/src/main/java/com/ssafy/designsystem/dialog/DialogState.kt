package com.ssafy.designsystem.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class DialogState<T>(private val onConfirm: (T) -> Unit = {}) {
    var isVisible by mutableStateOf(false)
        private set

    var property: T? by mutableStateOf(null)

    fun show(property: T? = null) {
        isVisible = true
        this.property = property
    }

    fun dismiss() {
        isVisible = false
        this.property = null
    }

    fun confirm() {
        property?.let { onConfirm(it) }
        dismiss()
    }
}

@Composable
fun rememberDialogState(): DialogState<Unit> {
    return remember { DialogState() }
}

@Composable
fun <T> rememberDialogState(onConfirm: (T) -> Unit = {}): DialogState<T> {
    return remember { DialogState(onConfirm) }
}