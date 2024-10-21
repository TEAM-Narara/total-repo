package com.ssafy.superboard

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
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ssafy.superboard.navigation.SuperBoardNavHost
import com.ssafy.superboard.ui.theme.SuperBoardTheme
import dagger.hilt.android.AndroidEntryPoint

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
    }

    @Composable
    fun SuperBoardApp(navController: NavHostController = rememberNavController()) {
        SuperBoardNavHost(navController = navController)
    }
}
