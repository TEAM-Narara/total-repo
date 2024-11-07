package com.ssafy.home.createboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.designsystem.values.backgroundColorList
import com.ssafy.designsystem.values.toColorString
import com.ssafy.model.background.Background
import com.ssafy.model.background.BackgroundType
import com.ssafy.ui.safetype.backgroundType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class CreateBoard(val background: Background? = null)

fun NavGraphBuilder.createBoardScreen(
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Background?) -> Unit
) {
    composable<CreateBoard>(
        mapOf(typeOf<Background?>() to backgroundType)
    ) { backStackEntry ->
        val createBoard: CreateBoard = backStackEntry.toRoute()
        val background = createBoard.background ?: Background(
            BackgroundType.COLOR,
            backgroundColorList.first().toColorString()
        )

        CreateBoardScreen(
            background = background,
            popBackToHome = popBackToHome,
            moveToSelectBackgroundScreen = moveToSelectBackgroundScreen
        )
    }
}
