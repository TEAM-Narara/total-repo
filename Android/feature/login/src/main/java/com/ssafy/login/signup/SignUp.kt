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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ssafy.ui.uistate.ErrorScreen
import com.ssafy.ui.uistate.LoadingScreen
import com.ssafy.ui.uistate.UiState

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = hiltViewModel(),
    moveToLogInScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.resetUiState() }

    val time by viewModel.timeFlow.collectAsStateWithLifecycle()
    val signState by viewModel.signState.collectAsStateWithLifecycle()

    SignUpScreen(
        moveToLogInScreen = moveToLogInScreen,
        time = time,
        signState = signState,
        changeState = viewModel::changeState
    )

    when (uiState) {
        is UiState.Loading -> LoadingScreen()
        is UiState.Error -> uiState.errorMessage?.let { ErrorScreen(errorMessage = it) }
        is UiState.Success -> {}
        is UiState.Idle -> {}
    }
}

@Composable
private fun SignUpScreen(
    moveToLogInScreen: () -> Unit,
    time: String,
    signState: SignState,
    changeState: (SignInfo) -> Unit
) {
    val (email, setEmail) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (nickname, setNickname) = remember { mutableStateOf("") }
    val (authNumber, setAuthNumber) = remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingDefault)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(PaddingXSmall)
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
            onTextChange = setEmail,
            modifier = Modifier.fillMaxWidth()
        )
        EditText(
            title = "비밀번호",
            text = password,
            onTextChange = setPassword,
            modifier = Modifier.fillMaxWidth()
        )
        EditText(
            title = "닉네임",
            text = nickname,
            onTextChange = setNickname,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingZero, PaddingZero, PaddingZero, PaddingXLarge),
            horizontalArrangement = Arrangement.spacedBy(PaddingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EditText(
                modifier = Modifier.weight(1f),
                title = "인증번호",
                text = authNumber,
                onTextChange = setAuthNumber
            )

            if (signState == SignState.AUTH) {
                Text(text = time)
            }

            val text = if (signState == SignState.CHECK) "확인" else "인증"

            OutlinedButton(
                modifier = Modifier.width(80.dp),
                onClick = {
                    val signInfo = SignInfo(email, password, nickname, authNumber)
                    changeState(signInfo)
                },
                shape = RoundedCornerShape(CornerSmall),
                border = BorderStroke(width = BorderDefault, color = Primary)
            ) {
                Text(
                    text = text,
                    color = Primary,
                    fontSize = TextMedium,
                )
            }
        }
        FilledButton(onClick = { moveToLogInScreen() }, text = "로그인")
    }
}


@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    SignUpScreen {}
}
