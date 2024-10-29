package com.ssafy.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.designsystem.R
import com.ssafy.designsystem.values.CornerSmall
import com.ssafy.designsystem.values.IconXLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingLarge
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.TextXSmall
import com.ssafy.designsystem.values.White

@Composable
fun UserInviteItem(
    modifier: Modifier = Modifier,
    nickname: String,
    email: String,
    isInvited: Boolean = false,
    onInvite: () -> Unit = {},
    icon: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingDefault),
    ) {
        Box(
            modifier = Modifier
                .size(IconXLarge)
                .clip(CircleShape)
        ) {
            icon()
        }

        Column(
            modifier = Modifier
                .padding(horizontal = PaddingDefault)
                .weight(1f)
        ) {
            Text(
                text = nickname,
                fontSize = TextLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = email,
                fontSize = TextSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isInvited) {
            Button(
                onClick = onInvite,
                shape = RoundedCornerShape(CornerSmall),
                border = BorderStroke(CornerSmall, Primary),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text(
                    text = "초대하기",
                    color = White,
                    fontSize = TextXSmall
                )
            }
        } else {
            Text(
                modifier = Modifier
                    .border(
                        shape = RoundedCornerShape(CornerSmall),
                        border = BorderStroke(width = 1.dp, color = Primary)
                    )
                    .padding(horizontal = PaddingLarge, vertical = PaddingSmall),
                text = "초대됨",
                color = Primary,
                fontSize = TextXSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun UserInviteItemPreviewIsInvited() {
    UserInviteItem(nickname = "nickname", email = "email@example.com", isInvited = true) {
        Image(
            painter = painterResource(id = R.drawable.logo_github),
            contentDescription = ""
        )
    }
}

@Preview
@Composable
private fun UserInviteItemPreviewIsNotInvited() {
    UserInviteItem(nickname = "nickname", email = "email@example.com", isInvited = false) {
        Image(
            painter = painterResource(id = R.drawable.logo_github),
            contentDescription = ""
        )
    }
}