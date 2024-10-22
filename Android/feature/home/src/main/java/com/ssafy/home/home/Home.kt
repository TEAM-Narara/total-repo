package com.ssafy.home.home

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.designsystem.values.CornerLarge
import com.ssafy.designsystem.values.ElevationMedium
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: () -> Unit,
    moveToSettingScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity
    activity?.let {
        WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
            isAppearanceLightStatusBars = true
            it.window.statusBarColor = Color.White.toArgb()
        }
    }
    HomeScreen(
        workSpace = uiState.workSpace,
        moveToBoardScreen = moveToBoardScreen,
        moveToCreateNewBoardScreen = moveToCreateNewBoardScreen,
        moveToCreateNewWorkSpaceScreen = { /*TODO 새 워크 스페이스 만들기 */ },
        moveToSettingScreen = moveToSettingScreen
    )
}

@Composable
private fun HomeScreen(
    workSpace: Any?,
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: () -> Unit,
    moveToCreateNewWorkSpaceScreen: () -> Unit,
    moveToSettingScreen: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val spanCount = if (isPortrait) 2 else 4

    Scaffold(
        containerColor = White,
        topBar = {
            MainTopBar(
                onDrawerClick = { /*TODO*/ },
                onSearchClick = { /*TODO*/ },
                onAlarmClick = { /*TODO*/ },
                onMenuClick = { moveToSettingScreen() }
            )
        },
        floatingActionButton = {
            if (workSpace != null) {
                ExtendedFloatingActionButton(
                    containerColor = Primary,
                    contentColor = White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = ElevationMedium
                    ),
                    shape = RoundedCornerShape(CornerLarge),
                    onClick = moveToCreateNewBoardScreen,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create new board"
                        )
                    },

                    text = {
                        Text(
                            text = "Create Board",
                            fontSize = TextMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                )
            }
        }
    ) { innerPadding ->
        if (workSpace != null) {
            HomeBodyScreen(
                modifier = Modifier.padding(innerPadding),
                // TODO : Board에 대한 정보를 전달합니다.
                boards = List(4) { Any() },
                spanCount = spanCount,
                moveToBoardScreen = moveToBoardScreen
            )
        } else {
            HomeEmptyScreen(
                modifier = Modifier.padding(innerPadding),
                moveToCreateNewWorkSpaceScreen = moveToCreateNewWorkSpaceScreen
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    HomeScreen(
        workSpace = Any(),
        moveToBoardScreen = {},
        moveToCreateNewBoardScreen = {},
        moveToCreateNewWorkSpaceScreen = {},
        moveToSettingScreen = {}
    )
}
