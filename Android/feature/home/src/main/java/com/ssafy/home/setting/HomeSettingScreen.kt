package com.ssafy.home.setting

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.ssafy.designsystem.component.EditableText
import com.ssafy.designsystem.component.FilledButton
import com.ssafy.designsystem.values.IconLarge
import com.ssafy.designsystem.values.PEOPLE_ICON
import com.ssafy.designsystem.values.PaddingDefault
import com.ssafy.designsystem.values.PaddingXSmall
import com.ssafy.designsystem.values.PaddingZero
import com.ssafy.designsystem.values.Primary
import com.ssafy.designsystem.values.ReversePrimary
import com.ssafy.designsystem.values.TextMedium
import com.ssafy.designsystem.values.TextXLarge
import com.ssafy.designsystem.values.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSettingScreen(
    modifier: Modifier = Modifier,
    workspaceId: Long,
    backHome: () -> Unit
) {
    val (name, onValueChange) = remember { mutableStateOf("워크스페이스") }
    val activity = LocalContext.current as? Activity
    activity?.let {
        WindowCompat.getInsetsController(it.window, it.window.decorView).apply {
            isAppearanceLightStatusBars = false
            it.window.statusBarColor = Primary.toArgb()
        }
    }
    Scaffold(
        containerColor = White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Primary),
                navigationIcon = {
                    IconButton(onClick = { backHome() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "탐색 창",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(text = "Workspace settings", fontSize = TextXLarge, color = Color.White)
                }
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(PaddingDefault),
            modifier = Modifier.padding(paddingValues)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(PaddingXSmall),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    top = PaddingDefault,
                    start = PaddingDefault,
                    end = PaddingDefault
                )
            ) {
                Text(text = "Name", fontSize = TextMedium, color = Primary)
                EditableText(
                    text = name,
                    onTextChanged = { newName: String -> onValueChange(newName) },
                    modifier = Modifier.weight(1f),
                    alignStyle = TextAlign.End
                )
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(PaddingDefault),
                modifier = Modifier.padding(PaddingDefault, PaddingZero)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = PEOPLE_ICON,
                    modifier = modifier.size(IconLarge)
                )
                Column(verticalArrangement = Arrangement.spacedBy(PaddingDefault)) {
                    Text(text = "Members", fontSize = TextXLarge)
                    LazyVerticalGrid(
                        modifier = modifier,
                        horizontalArrangement = Arrangement.spacedBy(PaddingXSmall),
                        columns = GridCells.Fixed(6),
                    ) {
                        // TODO: viewmodel에서 가져와서 붙이삼. 
                        val images: List<Any>? = null
                        images?.let {
                            items(it.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .aspectRatio(1f)
                                ) {
                                    it[index]
                                }
                            }
                        }
                    }
                    FilledButton(text = "Invite", onClick = { /*TODO*/ })
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = Color.LightGray
            )
            Spacer(modifier = modifier.weight(1f))
            FilledButton(
                text = "Delete", onClick = { /*TODO*/ }, modifier = Modifier.padding(
                    PaddingDefault, PaddingZero, PaddingDefault, PaddingDefault
                ), color = ReversePrimary
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
fun GreetingPreview() {
    HomeSettingScreen(
        workspaceId = 1, backHome = {}
    )
}
