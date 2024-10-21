package com.ssafy.home.home

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    moveToCreateNewBoardScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        boards = uiState.boards,
        moveToBoardScreen = moveToBoardScreen,
        moveToCreateNewBoardScreen = moveToCreateNewBoardScreen
    )
}

@Composable
private fun HomeScreen(
    boards: List<Any>,
    moveToBoardScreen: (Long) -> Unit,
    moveToCreateNewBoardScreen: () -> Unit
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
                onMenuClick = { /*TODO*/ }
            )
        },
        floatingActionButton = {
            if (boards.isNotEmpty()) {
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
        if (boards.isNotEmpty()) {
            HomeBodyScreen(
                modifier = Modifier.padding(innerPadding),
                boards = boards,
                spanCount = spanCount,
                moveToBoardScreen = moveToBoardScreen
            )
        } else {
            HomeEmptyScreen(
                modifier = Modifier.padding(innerPadding),
                moveToCreateNewBoardScreen = moveToCreateNewBoardScreen
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    HomeScreen(
        boards = List(4) { Any() },
        moveToBoardScreen = {},
        moveToCreateNewBoardScreen = {}
    )
}
