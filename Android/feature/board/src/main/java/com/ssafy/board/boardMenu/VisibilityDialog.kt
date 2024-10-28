package com.ssafy.board.boardMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.ssafy.designsystem.values.BoardHeight
import com.ssafy.designsystem.values.BorderFat
import com.ssafy.designsystem.values.LightGray
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.TextLarge
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextSmall

@Composable
fun VisibilityDialog(onDismiss: () -> Unit, visibility: String, setVisibility: (String) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(PaddingXSmall),
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(PaddingDefault),
            ) {
                Text(
                    text = "Visibility",
                    modifier = Modifier.padding(bottom = PaddingSmall),
                    fontSize = TextLarge,
                    fontWeight = FontWeight.Bold
                )
                Card(
                    modifier = Modifier
                        .clickable {
                            setVisibility("PRIVATE")
                            onDismiss()
                        }
                        .border(BorderFat, if (visibility == "PRIVATE") Primary else LightGray),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BoardHeight)
                            .background(Color.White)
                            .padding(PaddingDefault),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Private",
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            fontSize = TextMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "이 보드는 비공개입니다.\n추가된 사용자만이 이 보드를 보고 수정할 수 있습니다.",
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontSize = TextSmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(PaddingSmall))
                Card(
                    modifier = Modifier
                        .clickable {
                            setVisibility("WORKSPACE")
                            onDismiss()
                        }
                        .border(BorderFat, if (visibility == "WORKSPACE") Primary else LightGray),
                    shape = RoundedCornerShape(PaddingXSmall),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(BoardHeight)
                            .background(Color.White)
                            .padding(PaddingDefault),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "Workspace",
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            fontSize = TextMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "workspace의 사용자들이 이 보드를 볼 수 있습니다.",
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontSize = TextSmall
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun MinimalDialogPreview() {
    VisibilityDialog(onDismiss = {}, visibility = "WORKSPACE", {})
}