package com.ssafy.designsystem.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Stable
class DialogState<T> {
    var isVisible by mutableStateOf(false)
        private set

    var parameter: T? by mutableStateOf(null)

    fun show(parameter: T? = null) {
        isVisible = true
        this.parameter = parameter
    }

    fun dismiss() {
        isVisible = false
        this.parameter = null
    }
}

@Composable
fun <T> rememberDialogState(): DialogState<T> {
    return remember { DialogState() }
}