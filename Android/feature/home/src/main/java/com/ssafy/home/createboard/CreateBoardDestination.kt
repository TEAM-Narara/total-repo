package com.ssafy.home.createboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.designsystem.values.backgroundColorList
import com.ssafy.designsystem.values.toColorString
import com.ssafy.model.background.Cover
import com.ssafy.model.background.CoverType
import com.ssafy.ui.safetype.coverType
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

@Serializable
data class CreateBoard(val cover: Cover? = null)

fun NavGraphBuilder.createBoardScreen(
    popBackToHome: () -> Unit,
    moveToSelectBackgroundScreen: (Cover?) -> Unit
) {
    composable<CreateBoard>(
        mapOf(typeOf<Cover?>() to coverType)
    ) { backStackEntry ->
        val createBoard: CreateBoard = backStackEntry.toRoute()
        val cover = createBoard.cover ?: Cover(
            CoverType.COLOR,
            backgroundColorList.first().toColorString()
        )

        CreateBoardScreen(
            cover = cover,
            popBackToHome = popBackToHome,
            moveToSelectBackgroundScreen = moveToSelectBackgroundScreen
        )
    }
}
