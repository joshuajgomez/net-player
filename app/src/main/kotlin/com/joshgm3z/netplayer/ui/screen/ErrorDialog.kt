package com.joshgm3z.netplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.Text
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface
import com.joshgm3z.subtitletrack.view.theme.subTextColor
import com.joshgm3z.subtitletrack.view.theme.textColor

@Composable
fun ErrorDialog(
    message: String,
    summary: String?,
    onDismissClick: () -> Unit = {},
) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .background(color = colorScheme.background)
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = colorScheme.error
            )
            Spacer(Modifier.size(15.dp))
            Text(
                text = message,
                style = typography.titleMedium,
                color = textColor()
            )
            summary?.let {
                Text(
                    text = it,
                    style = typography.bodyMedium,
                    color = subTextColor(),
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(Modifier.size(30.dp))
            CustomButton(text = "Close") { onDismissClick() }
        }
    }
}

@DarkPreview
@Composable
private fun PreviewErrorDialog() {
    DarkSurface {
        ErrorDialog(
            message = "Error playing video",
            summary = "Something wrong with the source"
        )
    }
}