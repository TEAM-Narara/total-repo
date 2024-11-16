package com.ssafy.notification

import com.ssafy.fcm.GeyMyAlarmListUseCase
import com.ssafy.notification.fcm.data.FcmMessage
import com.ssafy.notification.fcm.data.toFcmMessage
import com.ssafy.ui.uistate.UiState
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getMyAlarmListUseCase: GeyMyAlarmListUseCase
) : BaseViewModel() {

    private val _notificationList = MutableStateFlow<List<FcmMessage>>(emptyList())
    val notificationList = _notificationList.asStateFlow()

    fun getNotificationList() = withIO {
        getMyAlarmListUseCase().catch { e ->
            if (e is RuntimeException) {
                _uiState.emit(UiState.Error("오프라인 상태에서는 알람을 받아올 수 없습니다."))
            } else {
                _uiState.emit(UiState.Error("알람을 받아오는데 실패했습니다."))
            }
        }.collect { alarmList ->
            val fcmMessageLIst = alarmList.map { it.toFcmMessage() }
            _notificationList.emit(fcmMessageLIst)
        }
    }
}
