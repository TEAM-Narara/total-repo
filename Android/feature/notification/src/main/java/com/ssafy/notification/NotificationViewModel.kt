package com.ssafy.notification

import com.ssafy.fcm.GeyMyAlarmListUseCase
import com.ssafy.notification.fcm.data.FcmMessage
import com.ssafy.notification.fcm.data.toFcmMessage
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getMyAlarmListUseCase: GeyMyAlarmListUseCase
) : BaseViewModel() {

    private val _notificationList = MutableStateFlow<List<FcmMessage>>(emptyList())
    val notificationList = _notificationList.asStateFlow()


    init {
        withIO {
            getMyAlarmListUseCase().withUiState().collect { alarmList ->
                val fcmMessageLIst = alarmList.map { it.toFcmMessage() }
                _notificationList.emit(fcmMessageLIst)
            }
        }
    }
}
