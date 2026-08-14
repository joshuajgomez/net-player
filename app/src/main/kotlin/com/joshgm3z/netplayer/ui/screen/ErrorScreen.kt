package com.joshgm3z.netplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Text
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface

@Composable
fun ErrorScreen(
    message: String,
    summary: String?,
    onDismissClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // WCAG: Use standard surface for full-screen background
            .background(color = colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        // Centering content is often better for TV error states
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            // Semantic color for errors
            tint = colorScheme.error,
            modifier = Modifier.size(48.dp)
        )

        Spacer(Modifier.size(24.dp))

        Text(
            text = message,
            style = typography.headlineMedium,
            // WCAG: onSurface provides maximum contrast for titles
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        summary?.let {
            Spacer(Modifier.size(8.dp))
            Text(
                text = it,
                style = typography.bodyLarge,
                // WCAG: onSurfaceVariant is the standard for secondary/de-emphasized text
                color = colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(Modifier.size(40.dp))

        // CustomButton uses standard M3 Button tokens internally
        CustomButton(text = "Close") { onDismissClick() }
    }
}

@DarkPreview
@Composable
private fun PreviewErrorScreen() {
    DarkSurface {
        ErrorScreen(
            message = "Error playing video",
            summary = "Something wrong with the source. Please check your internet connection or the link validity."
        )
    }
}