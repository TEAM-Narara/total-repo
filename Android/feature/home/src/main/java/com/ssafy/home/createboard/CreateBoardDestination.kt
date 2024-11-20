package com.ssafy.home.createboard

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.designsystem.values.backgroundColorList
import com.ssafy.designsystem.values.toColorString
import com.ssafy.model.background.Cover
import com.ssafy.model.with.CoverType
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
        val savedStateHandle = backStackEntry.savedStateHandle.get<String>(Cover.KEY)
        val createBoard: CreateBoard = backStackEntry.toRoute()
        val defaultCover = Cover(CoverType.COLOR, backgroundColorList[2].toColorString())
        val cover = createBoard.cover ?: if (savedStateHandle != null) {
            coverType.parseValue(savedStateHandle) ?: defaultCover
        } else {
            defaultCover
        }

        CreateBoardScreen(
            cover = cover,
            popBackToHome = popBackToHome,
            moveToSelectBackgroundScreen = moveToSelectBackgroundScreen
        )
    }
}
