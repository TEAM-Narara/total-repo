package com.ssafy.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.designsystem.values.IconXXLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingMedium
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.SpacerMedium
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextXLarge
import com.ssafy.designsystem.values.White

@Composable
fun DrawerSheetHead(
    modifier: Modifier = Modifier,
    nickname: String,
    email: String,
    icon: @Composable () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Primary)
            .padding(PaddingDefault)
    ) {

        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                tint = White,
                contentDescription = "로그아웃"
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(SpacerMedium)) {

            Box(
                modifier = Modifier
                    .size(IconXXLarge)
                    .clip(CircleShape)
            ) {
                icon()
            }

            Text(
                text = nickname,
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = TextXLarge,
                modifier = Modifier.padding(top = PaddingMedium)
            )

            Text(
                text = email,
                color = White,
                fontSize = TextMedium
            )
        }
    }
}
