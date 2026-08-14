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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Text
import com.joshgm3z.netplayer.BuildConfig
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
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = uiState.title,
                style = typography.headlineLarge,
                // WCAG: High contrast for main heading
                color = colorScheme.onSurface,
            )
            Spacer(Modifier.size(12.dp))
            Text(
                text = uiState.subtitle ?: "",
                style = typography.bodyLarge,
                // WCAG: Standard contrast for secondary text
                color = colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Current version ${BuildConfig.VERSION_NAME}",
                style = typography.labelMedium,
                // WCAG: Standard contrast for secondary text
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        }

        Spacer(Modifier.width(48.dp))

        if (uiState.enableButtons) {
            Column(Modifier.width(250.dp)) {
                val firstItemRequester = remember { FocusRequester() }
                LaunchedEffect(Unit) {
                    firstItemRequester.requestFocus()
                }
                Button(
                    onClick = onActionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(firstItemRequester),
                    colors = ButtonDefaults.colors(
                        containerColor = colorScheme.surface,
                    ),
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp))
                ) {
                    Text(
                        uiState.buttonAction.text,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.size(12.dp))
                Button(
                    onClick = onDismissClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.colors(
                        containerColor = colorScheme.surface,
                    ),
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp))
                ) {
                    Text(
                        "Dismiss",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Loading state uses primary color to indicate activity
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 10.dp),
                color = colorScheme.onSurfaceVariant
            )
        }
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
