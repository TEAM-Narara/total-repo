package com.ssafy.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.designsystem.values.BoardHeight
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.RadiusDefault
import com.ssafy.designsystem.values.White
import com.ssafy.designsystem.values.Yellow

@Composable
fun Board(
    title: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Yellow,
    onBoardClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        shape = RoundedCornerShape(RadiusDefault),
        modifier = modifier
            .height(BoardHeight)
            .fillMaxWidth()
            .clickable { onBoardClick() }
    ) {

        Column {

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "더보기 메뉴"
                    )
                }
            }

            Spacer(modifier = modifier.weight(1f))

            Surface(
                color = White,
                shape = RoundedCornerShape(RadiusDefault),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(PaddingSmall)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(
                        horizontal = PaddingDefault,
                        vertical = PaddingXSmall
                    )
                )
            }
        }
    }
}
