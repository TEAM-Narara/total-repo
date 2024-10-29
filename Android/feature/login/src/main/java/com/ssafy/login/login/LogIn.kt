package com.ssafy.login.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.designsystem.R
import com.ssafy.designsystem.component.EditText
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.component.LoginButton
import com.ssafy.designsystem.component.OutlineButton
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSemiLarge
import com.ssafy.designsystem.values.PaddingXLarge
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.PaddingZero

@Composable
fun LogInScreen(
    viewModel: LogInViewModel = hiltViewModel(),
    moveToSignUpScreen: () -> Unit,
    moveToHomeScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    PaddingZero, PaddingZero, PaddingZero,
                    PaddingSemiLarge
                )
        )
        FilledButton(onClick = { moveToHomeScreen() }, text = "로그인")
        OutlineButton(text = "회원가입", onClick = { moveToSignUpScreen() })

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 10.dp),
            thickness = 1.dp,
            color = Color.Gray
        )

        LoginButton(
            onClick = { moveToSignUpScreen() },
            icon = painterResource(id = R.drawable.logo_naver),
            content = "네이버 로그인",
            backColor = Color(0xFF03C75A),
            textColor = Color.White
        )
        LoginButton(
            onClick = { moveToSignUpScreen() },
            icon = painterResource(id = R.drawable.logo_github),
            content = "깃허브 로그인",
            backColor = Color.Black,
            textColor = Color.White
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    LogInScreen(
        moveToSignUpScreen = {},
        moveToHomeScreen = {}
    )
}