package com.ssafy.home.home

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.designsystem.values.CornerLarge
import com.ssafy.designsystem.values.ElevationMedium
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.White

@Composable
fun AddNewBoardFloatingButton(
    modifier: Modifier = Modifier,
    moveToCreateNewBoardScreen: () -> Unit
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
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
