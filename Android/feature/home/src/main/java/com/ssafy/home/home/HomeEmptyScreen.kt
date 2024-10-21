package com.ssafy.home.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.designsystem.R
import com.ssafy.designsystem.values.EXPLAIN_SUPER_BOARD
import com.ssafy.designsystem.values.LineTitle
import com.ssafy.designsystem.values.MascotDefault
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingLarge
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.START
import com.ssafy.designsystem.values.START_WITH_SUPER_BOARD
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextTitle
import com.ssafy.designsystem.values.TextXLarge
import com.ssafy.designsystem.values.White

@Composable
fun HomeEmptyScreen(
    modifier: Modifier = Modifier,
    moveToCreateNewWorkSpaceScreen: () -> Unit
) {
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingDefault)
                .weight(1f)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(PaddingDefault)) {

                Image(
                    painter = painterResource(id = R.drawable.mascot),
                    contentDescription = "마스코트",
                    modifier = Modifier
                        .size(MascotDefault)
                        .align(Alignment.CenterVertically),
                )

                Text(
                    text = START_WITH_SUPER_BOARD,
                    color = Primary,
                    fontSize = TextTitle,
                    lineHeight = LineTitle,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = EXPLAIN_SUPER_BOARD,
                color = Primary,
                fontSize = TextXLarge,
                modifier = Modifier.padding(top = PaddingLarge)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = START,
                    color = Primary,
                    fontSize = TextXLarge,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(PaddingDefault)
                        .clickable(
                            onClick = moveToCreateNewWorkSpaceScreen,
                            interactionSource = null,
                            indication = null,
                        )
                )
            }
        }

        if (isPortrait) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Primary)
                    .weight(1f)
            ) {
                // TODO : 여기에는 캡쳐한 사진들을 넣습니다.
                Text(
                    text = "여기에는 캡쳐한 사진들을 넣습니다.",
                    color = White,
                    fontSize = TextMedium,
                    modifier = Modifier.padding(PaddingDefault)
                )
            }
        }
    }
}
