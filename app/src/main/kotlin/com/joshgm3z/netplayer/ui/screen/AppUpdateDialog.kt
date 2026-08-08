package com.joshgm3z.netplayer.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Text
import com.joshgm3z.netplayer.ui.theme.subTextColor
import com.joshgm3z.netplayer.ui.theme.textColor
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface
import com.joshgm3z.netplayer.viewmodel.ButtonAction
import com.joshgm3z.netplayer.viewmodel.SelfUpdateUiState
import com.joshgm3z.netplayer.viewmodel.SelfUpdateViewModel

@Composable
fun AppUpdateDialog(
    onBackPress: () -> Unit,
    viewModel: SelfUpdateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AppUpdateDialogContent(
        uiState = uiState,
        onDismissClick = onBackPress,
        onActionClick = { viewModel.onButtonClick() }
    )
}

@Composable
fun AppUpdateDialogContent(
    uiState: SelfUpdateUiState,
    onDismissClick: () -> Unit = {},
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(100.dp)
            .fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = uiState.title,
                style = typography.headlineLarge,
                color = textColor(),
            )
            Spacer(Modifier.size(10.dp))
            Text(
                text = uiState.subtitle ?: "",
                style = typography.bodyLarge,
                color = subTextColor(),
            )
        }
        if (uiState.enableButtons) Column {
            val firstItemRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                firstItemRequester.requestFocus()
            }
            Button(
                onClick = onActionClick,
                modifier = Modifier
                    .width(200.dp)
                    .focusRequester(firstItemRequester)
            ) {
                Text(
                    uiState.buttonAction.text,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.size(10.dp))
            Button(
                onClick = onDismissClick,
                modifier = Modifier.width(200.dp)
            ) {
                Text(
                    "Dismiss",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        } else CircularProgressIndicator(modifier = Modifier.padding(top = 10.dp))
    }
}

@DarkPreview
@Composable
private fun PreviewAppUpdateScreen_loading() {
    DarkSurface {
        AppUpdateDialogContent(
            SelfUpdateUiState(
                title = "Update available",
                subtitle = "New version 1.0.0 is available for download",
                enableButtons = false,
            )
        )
    }
}

@DarkPreview
@Composable
private fun PreviewAppUpdateScreen() {
    DarkSurface {
        AppUpdateDialogContent(
            SelfUpdateUiState(
                title = "Update available",
                subtitle = "New version 1.0.0 is available for download",
                enableButtons = true,
                buttonAction = ButtonAction.UpdateNow
            )
        )
    }
}