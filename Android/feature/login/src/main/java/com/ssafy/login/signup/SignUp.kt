package com.ssafy.login.signup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.EditText
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.values.BorderDefault
import com.ssafy.designsystem.values.CornerSmall
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXLarge
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextMedium

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    moveToLogInScreen: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var authNumber by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf("5:00") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingDefault)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(
            PaddingXSmall
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.big_logo),
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingDefault, PaddingXLarge, PaddingDefault, PaddingDefault),
            contentDescription = "watch",
            contentScale = ContentScale.FillWidth
        )
        EditText(
            title = "이메일",
            text = email,
            onTextChange = { newText ->
                email = newText
            },
            modifier = Modifier.fillMaxWidth()
        )
        EditText(
            title = "비밀번호",
            text = password,
            onTextChange = { newText ->
                password = newText
            },
            modifier = Modifier.fillMaxWidth()
        )
        EditText(
            title = "닉네임",
            text = nickname,
            onTextChange = { newText ->
                nickname = newText
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    PaddingZero, PaddingZero, PaddingZero,
                    PaddingXLarge
                ),
            horizontalArrangement = Arrangement.spacedBy(
                PaddingSmall
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EditText(
                modifier = Modifier.weight(1f),
                title = "인증번호",
                text = authNumber,
                onTextChange = { newText ->
                    authNumber = newText
                }
            )
            Text(text = timer)
            OutlinedButton(
                modifier = Modifier.width(80.dp),
                onClick = { startTimer() },
                shape = RoundedCornerShape(CornerSmall),
                border = BorderStroke(width = BorderDefault, color = Primary)
            ) {
                Text(
                    text = "확인",
                    color = Primary,
                    fontSize = TextMedium,
                )
            }
        }
        FilledButton(onClick = { moveToLogInScreen() }, text = "로그인")
    }
}

fun startTimer() {

}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    SignUpScreen {}
}
