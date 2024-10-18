package com.ssafy.login.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.designsystem.component.Comment
import com.ssafy.designsystem.component.EditText
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.component.List
import com.ssafy.designsystem.component.NegativeButton
import com.ssafy.designsystem.component.OutlineButton
import com.ssafy.designsystem.values.EMAIL
import com.ssafy.designsystem.values.EMAIL_HINT
import com.ssafy.designsystem.values.PaddingDefault
import java.util.Date

@Composable
fun LogInScreen(
    viewModel: LogInViewModel = hiltViewModel(),
    moveToSignUpScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(horizontal = PaddingDefault)) {
        Text(
            text = "나는야 로그인 페이지",
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { moveToSignUpScreen() }) {
            Text(text = "회원가입 페이지로 넘어가기")
        }

        Spacer(modifier = Modifier.height(20.dp))

//        Image(
//            painter = painterResource(id = R.drawable.logo),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxWidth()
//                .aspectRatio(1f)
//        )

        EditText(
            title = EMAIL,
            textHint = EMAIL_HINT,
            text = uiState.email,
            onTextChange = viewModel::updateEmail
        )

        FilledButton(text = "Test", onClick = ::println)
        NegativeButton(text = "Test", onClick = ::println)
        OutlineButton(text = "Test", onClick = ::println)
        Comment(
            iconUrl = "https://img-cdn.pixlr.com/image-generator/history/65bb506dcb310754719cf81f/ede935de-1138-4f66-8ed7-44bd16efc709/medium.webp",
            nickname = "Nickname",
            date = Date().time,
            content = "Comment",
            onMenuClick = ::println
        )
        List(
            title = "List",
            onTitleChange = ::println,
            addCard = ::println,
            addPhoto = ::println
        )

        Spacer(modifier = Modifier.padding(10.dp))

        List(
            title = "List",
            onTitleChange = ::println,
            isWatching = true,
            addCard = ::println,
            addPhoto = ::println
        )
    }
}
