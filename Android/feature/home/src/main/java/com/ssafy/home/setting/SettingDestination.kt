package com.ssafy.home.setting

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val SETTING = "SETTING"

fun NavGraphBuilder.settingScreen(
    backHomeScreen: () -> Unit
) {
    composable(route = SETTING) {
        HomeSettingScreen(
            images = listOf(
                {
                    Image(
                        painter = painterResource(id = com.ssafy.designsystem.R.drawable.big_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
                {
                    Image(
                        painter = painterResource(id = com.ssafy.designsystem.R.drawable.logo_naver),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
                {
                    Image(
                        painter = painterResource(id = com.ssafy.designsystem.R.drawable.logo_github),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
                {
                    Image(
                        painter = painterResource(id = com.ssafy.designsystem.R.drawable.logo_naver),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
            ),
            backHome = backHomeScreen
        )
    }
}
