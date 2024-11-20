package com.ssafy.card.card.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.ssafy.designsystem.component.BasicTransparentTextField
import com.ssafy.designsystem.values.BorderDefault
import com.ssafy.designsystem.values.DarkGray
import com.ssafy.designsystem.values.LabelBlue
import com.ssafy.designsystem.values.LabelRed
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.designsystem.values.RadiusDefault
import com.ssafy.designsystem.values.White
import com.ssafy.model.card.CommentDTO

@Composable
fun CardBottomBar(
    isTitleFocus: Boolean,
    setTitleFocus: (Boolean) -> Unit,
    isContentFocus: Boolean,
    setContentFocus: (Boolean) -> Unit,
    focusedComment: CommentDTO?,
    setFocusedComment: (CommentDTO?) -> Unit,
    saveCardTitle: () -> Unit,
    saveCardContent: () -> Unit,
    resetCardTitle: () -> Unit,
    resetCardContent: () -> Unit,
    saveCommitContent: () -> Unit,
    resetCommitContent: () -> Unit,
    addComment: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .navigationBarsPadding()
            .background(color = White)
    ) {
        if (isTitleFocus || isContentFocus || focusedComment != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    if (isTitleFocus) {
                        saveCardTitle()
                        setTitleFocus(false)
                    } else if (isContentFocus) {
                        saveCardContent()
                        setContentFocus(false)
                    } else {
                        saveCommitContent()
                        setFocusedComment(null)
                    }

                }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "확인",
                        tint = LabelBlue
                    )
                }

                IconButton(onClick = {
                    if(isTitleFocus) {
                        resetCardTitle()
                        setTitleFocus(false)
                    } else if (isContentFocus) {
                        resetCardContent()
                        setContentFocus(false)
                    } else {
                        resetCommitContent()
                        setFocusedComment(null)
                    }

                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "취소",
                        tint = LabelRed
                    )
                }
            }
        } else {

            val (text, setText) = remember { mutableStateOf("") }
            val keyboardController = LocalSoftwareKeyboardController.current
            val focusManager = LocalFocusManager.current

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingDefault)
                    .border(  // clip 전에 border를 적용
                        width = BorderDefault,
                        color = DarkGray,
                        shape = RoundedCornerShape(RadiusDefault)
                    )
                    .clip(RoundedCornerShape(RadiusDefault))
            ) {

                BasicTransparentTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(PaddingSmall),
                    value = text,
                    onValueChange = setText,
                )

                IconButton(
                    onClick = {
                        addComment(text)
                        setText("")
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = "댓글 추가"
                    )
                }
            }
        }
    }
}
