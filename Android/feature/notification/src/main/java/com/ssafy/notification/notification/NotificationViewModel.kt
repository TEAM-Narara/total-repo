package com.ssafy.notification.notification

import androidx.lifecycle.viewModelScope
import com.ssafy.notification.notification.data.NotificationData
import com.ssafy.socket.GetSocketStateUseCase
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    getSocketStateUseCase: GetSocketStateUseCase,
) : BaseViewModel(getSocketStateUseCase) {
    val notificationList: StateFlow<List<NotificationData>?> = flow {
        val data = (1..10).map { NotificationData() }
        emit(data)
    }.withUiState().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null,
    )

    fun checkAllNotification() {}
}