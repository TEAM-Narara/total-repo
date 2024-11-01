package com.ssafy.login.signup

import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class SignUpViewModel @Inject constructor() : BaseViewModel() {

    private val _timeFlow: MutableStateFlow<String> = MutableStateFlow("")
    val timeFlow: StateFlow<String> = _timeFlow.asStateFlow()

    private val _signState: MutableStateFlow<SignState> = MutableStateFlow(SignState.CHECK)
    val signState: StateFlow<SignState> = _signState.asStateFlow()

    private val timer = object : CountDownTimer(5.minutes.inWholeMilliseconds, 1000) {
        override fun onTick(time: Long) {
            val secondsRemaining = time / 1000
            val minutes = secondsRemaining / 60
            val seconds = secondsRemaining % 60
            val remainTime = String.format(Locale.KOREA, "%02d:%02d", minutes, seconds)
            _timeFlow.value = remainTime
        }

        override fun onFinish() {}
    }

    fun changeState(signInfo: SignInfo) {
        when (signState.value) {
            SignState.CHECK -> checkInfo(signInfo)
            SignState.AUTH -> emailAuth(signInfo)
        }
    }

    private fun checkInfo(signInfo: SignInfo) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {

        }.onSuccess {
            _signState.value = SignState.AUTH
            startTimer()
        }.onFailure {

        }
    }

    private fun emailAuth(signInfo: SignInfo) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {

        }.onSuccess {
            _signState.value = SignState.CHECK
            stopTimer()
        }.onFailure {

        }
    }

    private fun startTimer() = timer.start()
    private fun stopTimer() = timer.cancel()

}
