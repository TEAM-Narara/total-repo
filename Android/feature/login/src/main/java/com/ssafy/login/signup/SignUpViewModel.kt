package com.ssafy.login.signup

import android.os.CountDownTimer
import androidx.lifecycle.viewModelScope
import com.ssafy.login.RegisterUseCase
import com.ssafy.login.SendEmailUseCase
import com.ssafy.login.VerifyEmailUseCase
import com.ssafy.model.user.signup.RegisterDTO
import com.ssafy.model.user.signup.SignState
import com.ssafy.socket.GetSocketStateUseCase
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val sendEmailUseCase: SendEmailUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val registerUseCase: RegisterUseCase,
    getSocketStateUseCase: GetSocketStateUseCase,
) : BaseViewModel(getSocketStateUseCase) {

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

    fun checkEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        sendEmailUseCase(email).withUiState().collect {
            _signState.update { SignState.VERIFY }
            startTimer()
        }
    }

    fun verifyEmail(email: String, code: String) = viewModelScope.launch(Dispatchers.IO) {
        verifyEmailUseCase(email, code).withUiState(
            onError = {
                _signState.update { SignState.CHECK }
                stopTimer()
            }
        ).collect {
            _signState.update { SignState.REGISTER }
            stopTimer()
        }
    }

    fun register(dto: RegisterDTO, moveToHome: () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        registerUseCase(dto).withUiState(
            onError = {
                _signState.update { SignState.CHECK }
                stopTimer()
            }
        ).collect { withMain { moveToHome() } }
    }

    private fun startTimer() = timer.start()
    private fun stopTimer() = timer.cancel()

}
