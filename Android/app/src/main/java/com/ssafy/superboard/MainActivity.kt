package com.ssafy.superboard

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ssafy.superboard.navigation.SuperBoardNavHost
import com.ssafy.superboard.ui.theme.SuperBoardTheme
import com.ssafy.ui.networkstate.NetworkState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperBoardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    SuperBoardApp()
                }
            }
        }

        window.apply {
            statusBarColor = Color.White.toArgb()
            navigationBarColor = Color.White.toArgb()
            WindowInsetsControllerCompat(this, decorView).isAppearanceLightStatusBars = true
            WindowInsetsControllerCompat(this, decorView).isAppearanceLightNavigationBars = true
        }

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                NetworkState.isConnected.update { true }
            }

            override fun onLost(network: Network) {
                NetworkState.isConnected.update { false }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    @Composable
    fun SuperBoardApp(
        navController: NavHostController = rememberNavController(),
        viewModel: MainViewModel = hiltViewModel()
    ) {
        SuperBoardNavHost(navController = navController, viewModel = viewModel)
    }
}