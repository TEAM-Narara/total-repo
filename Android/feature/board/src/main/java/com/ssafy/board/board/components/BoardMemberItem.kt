package com.ssafy.board.board.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.IconXLarge
import com.ssafy.designsystem.values.PEOPLE_ICON
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.TextXLarge
import com.ssafy.model.board.MemberResponseDTO

@Composable
fun BoardMemberItem(
    modifier: Modifier = Modifier,
    boardMember: List<MemberResponseDTO>,
    moveToBoardInviteMemberScreen: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(PaddingDefault),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Groups,
            contentDescription = PEOPLE_ICON,
            modifier = Modifier.size(IconLarge)
        )
        Column(verticalArrangement = Arrangement.spacedBy(PaddingDefault)) {
            Text(text = "Members", fontSize = TextXLarge)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
            ) {
                items(boardMember.size) { index ->
                    Box(
                        modifier = Modifier
                            .height(IconXLarge)
                            .clip(CircleShape)
                            .aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = boardMember[index].memberProfileImgUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth(),
                            error = rememberVectorPainter(image = Icons.Default.AccountCircle)
                        )
                    }
                }
            }
            FilledButton(text = "Invite", onClick = moveToBoardInviteMemberScreen)
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    BoardMemberItem(
        boardMember = emptyList(),
        moveToBoardInviteMemberScreen = {}
    )
}
