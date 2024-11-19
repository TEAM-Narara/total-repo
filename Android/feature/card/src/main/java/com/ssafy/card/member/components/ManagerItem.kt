package com.ssafy.card.member.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.ssafy.card.member.data.ManagerData
import com.ssafy.designsystem.R
import com.ssafy.designsystem.values.IconXLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.designsystem.values.TextSmall

@Composable
fun ManagerItem(
    modifier: Modifier = Modifier,
    managerData: ManagerData,
    onIsManagerChanged: (Long, Boolean) -> Unit
) = with(managerData) {
    ManagerItem(
        modifier = modifier,
        nickname = nickname,
        email = email,
        manager = isManager,
        onClick = { onIsManagerChanged(id, it) }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = profileUrl,
            contentDescription = "profile image",
            contentScale = ContentScale.Crop,
            error = rememberVectorPainter(Icons.Default.AccountCircle),
        )
    }
}

@Composable
fun ManagerItem(
    modifier: Modifier = Modifier,
    nickname: String,
    email: String,
    manager: Boolean = false,
    onClick: (Boolean) -> Unit = {},
    profileImage: @Composable () -> Unit,
) {
    var isManager by remember { mutableStateOf(manager) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                isManager = !isManager
                onClick(isManager)
            }
            .padding(vertical = PaddingDefault),
    ) {
        Box(
            modifier = Modifier
                .size(IconXLarge)
                .clip(CircleShape)
        ) {
            profileImage()
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

        if (isManager) Icon(imageVector = Icons.Default.Check, contentDescription = "담당자")
    }
}

@Preview
@Composable
private fun ManagerItemPrev() {
    ManagerItem(nickname = "nickname", email = "email@example.com", onClick = {}) {
        Image(
            painter = painterResource(id = R.drawable.logo_github),
            contentDescription = ""
        )
    }
}