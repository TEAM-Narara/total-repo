package com.ssafy.splash

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.login.AutoLogInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val autoLogInUseCase: AutoLogInUseCase,
) : ViewModel() {

    var isLoading = true

    private val _direction = MutableStateFlow<StartDirection?>(null)
    val direction: StateFlow<StartDirection?> = _direction.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val isSuccess = autoLogInUseCase(isOnline())
            if (isSuccess) _direction.update { StartDirection.HOME }
            else _direction.update { StartDirection.LOGIN }
            isLoading = false
        }
    }

    private fun isOnline(): Boolean {
        val connectService = Context.CONNECTIVITY_SERVICE
        val internet = NetworkCapabilities.NET_CAPABILITY_INTERNET
        val connectManger = context.getSystemService(connectService) as ConnectivityManager
        val network = connectManger.activeNetwork
        val capabilities = connectManger.getNetworkCapabilities(network)
        return capabilities?.hasCapability(internet) ?: false
    }
}
