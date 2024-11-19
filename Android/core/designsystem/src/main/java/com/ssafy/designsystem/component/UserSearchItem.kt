package com.ssafy.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.ssafy.designsystem.values.IconXLarge
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall
import com.ssafy.designsystem.values.White

@Composable
fun UserSearchItem(
    modifier: Modifier = Modifier,
    nickname: String,
    email: String,
    userAuth: String,
    onChangeUserAuth: (String) -> Unit,
    canChangeAuth: Boolean = true,
    clickAction: (() -> Unit)? = null,
    icon: @Composable () -> Unit
) {
    val (expanded, setExpanded) = remember { mutableStateOf(false) }
    val authOptions = listOf("ADMIN", "MEMBER")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(PaddingDefault)
            .then(
                if (clickAction != null) Modifier.clickable { clickAction() }
                else Modifier
            ),
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

        if (canChangeAuth) {
            DropDownMemberAuth(
                userAuth = userAuth,
                expanded = expanded,
                setExpanded = setExpanded,
                authOptions = authOptions,
                onChangeUserAuth = onChangeUserAuth
            )
        }
    }
}

@Composable
private fun DropDownMemberAuth(
    modifier: Modifier = Modifier,
    userAuth: String,
    expanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    authOptions: List<String>,
    onChangeUserAuth: (String) -> Unit
) {
    val (value, onValueChanged) = remember { mutableStateOf(userAuth) }

    Column(modifier = modifier) {
        Row(modifier = Modifier.clickable { setExpanded(true) }) {
            Text(text = value, fontSize = TextMedium)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "유저 권한 설정"
            )
        }

        DropdownMenu(
            containerColor = White,
            expanded = expanded,
            onDismissRequest = { setExpanded(false) }
        ) {
            authOptions.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            fontSize = TextMedium,
                            fontWeight = FontWeight.Normal,
                        )
                    },

                    onClick = {
                        onValueChanged(option)
                        onChangeUserAuth(option)
                        setExpanded(false)
                    }
                )
            }
        }
    }
}
