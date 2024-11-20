package com.ssafy.card.card.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.ssafy.designsystem.component.Comment
import com.ssafy.designsystem.values.PaddingSmall
import com.ssafy.model.card.CommentDTO

fun LazyListScope.cardComment(
    modifier: Modifier = Modifier,
    comments: List<CommentDTO>,
    userId: Long,
    setCommitContent: (CommentDTO, String) -> Unit,
    deleteComment: (CommentDTO) -> Unit,
    focusedComment: CommentDTO? = null,
    setFocusedComment: (CommentDTO?) -> Unit
) {
    item {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Icon(imageVector = BrowseActivity, contentDescription = "댓글")
            Text(
                text = "댓글", modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = PaddingSmall)
            )
        }
    }

    items(
        count = comments.size,
        key = { index -> comments[index].commentId + focusedComment.hashCode() }
    ) { index ->
        val comment = comments[index]
        Comment(
            modifier = modifier.padding(vertical = PaddingSmall),
            icon = {
                AsyncImage(
                    model = comment.profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(image = Icons.Default.AccountCircle)
                )
            },
            nickname = comment.nickname,
            date = comment.createDate,
            content = comment.content,
            setContent = { content -> setCommitContent(comment, content) },
            isFocus = comment == focusedComment,
            setFocus = { isFocus ->
                if (isFocus) setFocusedComment(comment)
            },
            hasAuth = comment.userId == userId,
            deleteComment = { deleteComment(comment) },
        )
    }
}
