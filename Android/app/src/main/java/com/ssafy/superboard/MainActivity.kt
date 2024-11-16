package com.ssafy.superboard

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.ssafy.splash.SplashViewModel
import com.ssafy.superboard.navigation.SuperBoardNavHost
import com.ssafy.superboard.ui.theme.SuperBoardTheme
import com.ssafy.network.networkstate.NetworkState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.update

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val splashViewModel: SplashViewModel by viewModels()
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, NO_ALARM_PERMISSION, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        requestPermission()

        installSplashScreen().setKeepOnScreenCondition {
            splashViewModel.isLoading
        }

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
                com.ssafy.network.networkstate.NetworkState.isConnected.update { true }
            }

            override fun onLost(network: Network) {
                com.ssafy.network.networkstate.NetworkState.isConnected.update { false }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Composable
    fun SuperBoardApp(
        navController: NavHostController = rememberNavController(),
        viewModel: MainViewModel = hiltViewModel()
    ) {
        val direction by splashViewModel.direction.collectAsStateWithLifecycle()

        direction?.let {
            SuperBoardNavHost(
                navController = navController,
                viewModel = viewModel,
                direction = it,
            )
        }
    }

    companion object {
        const val NO_ALARM_PERMISSION = "권한을 허용하지 않으면 알람을 받을 수 없습니다."
    }
}