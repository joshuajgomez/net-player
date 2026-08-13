package com.joshgm3z.netplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.ClosedCaptionOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Text
import com.joshgm3z.netplayer.repository.VideoLink
import com.joshgm3z.netplayer.ui.theme.cardColor
import com.joshgm3z.netplayer.ui.theme.subTextColor
import com.joshgm3z.netplayer.ui.theme.textColor
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface
import com.joshgm3z.netplayer.util.relativeTime
import com.joshgm3z.netplayer.util.toTextTime

@Composable
fun VideoLinkItem(
    modifier: Modifier = Modifier,
    videoLink: VideoLink,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isLinkValid = videoLink.linkInvalid == null
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (isLinkValid) onClick()
                }
            )
            .border(
                width = 2.dp,
                color = if (isFocused) colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .background(color = cardColor())
            .width(450.dp)
            .padding(vertical = 13.dp, horizontal = 18.dp),
    ) {
        Icon(
            imageVector = if (isFocused && isLinkValid) Icons.Default.PlayArrow
            else Icons.Default.Link,
            tint = colorScheme.primary,
            contentDescription = null,
            modifier = Modifier
                .background(
                    color = if (isFocused) colorScheme.primaryContainer
                    else colorScheme.onBackground.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .padding(5.dp)
                .size(20.dp)
        )

        Column(
            modifier = Modifier.padding(start = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (!videoLink.title.isEmpty()) Text(
                text = videoLink.title,
                style = typography.titleMedium,
                color = if (isFocused) colorScheme.primary
                else textColor(),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
            )

            Text(
                text = videoLink.url,
                style = typography.bodySmall,
                color = textColor().copy(alpha = 0.5f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                textDecoration = if (!isLinkValid) TextDecoration.LineThrough
                else TextDecoration.None
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildAnnotatedString {
                        val dot = "  •  "
                        append(videoLink.added.relativeTime())
                        videoLink.totalDuration?.let {
                            withStyle(style = SpanStyle(color = colorScheme.primary)) {
                                append(dot)
                            }
                            append(it.toTextTime())
                        }
                    },
                    style = typography.labelMedium,
                    color = subTextColor(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Spacer(Modifier.size(6.dp))
                videoLink.subtitleUrl?.let {
                    Icon(
                        imageVector = Icons.Default.ClosedCaptionOff,
                        contentDescription = null,
                        tint = colorScheme.onBackground.copy(alpha = 0.4f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                if (videoLink.progress > 0 && isLinkValid) Row(modifier = Modifier.width(180.dp)) {
                    Text(
                        text = "${(videoLink.progress * 100).toInt()}%",
                        style = typography.labelMedium,
                        color = subTextColor(),
                    )
                    LinearProgressIndicator(
                        progress = { videoLink.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, start = 5.dp),
                        drawStopIndicator = {},
                        trackColor = colorScheme.onBackground.copy(alpha = 0.1f),
                        color = colorScheme.primaryContainer
                    )
                }
            }

            videoLink.linkInvalid?.let {
                ErrorText(it)
            }
        }
    }
}

@Composable
fun ErrorText(error: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(
                color = colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = colorScheme.onTertiaryContainer,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.size(5.dp))
        Text(
            text = error,
            color = colorScheme.onTertiaryContainer,
            style = typography.bodyMedium
        )
    }
}

@DarkPreview
@Composable
private fun PreviewVideoLink() {
    DarkSurface {
        VideoLinkItem(
            videoLink = VideoLink(
                title = "Sample Video Link",
                url = "https://example.com/withsomelongassnameandurl4223/fkfjjkcomingmorethanonce",
                added = System.currentTimeMillis() - 300000,
                totalDuration = 4980000L,
                playedDuration = 2700000L,
                subtitleUrl = "https://example.com/subtitles.srt"
            ).apply {
//                linkInvalid = "File format not supported"
            }
        )
    }
}