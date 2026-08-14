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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaptionOff
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NotInterested
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

    val titleColor = if (isFocused) colorScheme.primary else colorScheme.onSurface
    val subTextColor = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { if (isLinkValid) onClick() }
            )
            .border(
                width = 2.dp,
                color = if (isFocused) colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .background(color = colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(vertical = 16.dp, horizontal = 18.dp),
    ) {
        Row {
            Icon(
                imageVector = when {
                    isFocused && isLinkValid -> Icons.Default.PlayArrow
                    else -> Icons.Default.Link
                },
                tint = if (isFocused) colorScheme.onPrimaryContainer else titleColor,
                contentDescription = null,
                modifier = Modifier
                    .background(
                        color = if (isFocused) colorScheme.primaryContainer
                        else colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
                    .padding(6.dp)
                    .size(20.dp)
            )

            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (videoLink.title.isNotEmpty()) {
                    Text(
                        text = videoLink.title,
                        style = typography.titleMedium,
                        color = titleColor,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2,
                    )
                }

                Text(
                    text = videoLink.url,
                    style = typography.bodySmall,
                    color = subTextColor,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            val dot = "  •  "
                            append(videoLink.added.relativeTime())
                            videoLink.totalDuration?.let {
                                withStyle(style = SpanStyle(color = colorScheme.secondary)) {
                                    append(dot)
                                }
                                append(it.toTextTime())
                            }
                        },
                        style = typography.labelMedium,
                        color = subTextColor,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )

                    videoLink.subtitleUrl?.let {
                        Spacer(Modifier.size(8.dp))
                        Icon(
                            imageVector = Icons.Default.ClosedCaptionOff,
                            contentDescription = "Subtitles available",
                            tint = subTextColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    if (videoLink.progress > 0 && isLinkValid) {
                        Row(
                            modifier = Modifier.width(160.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${(videoLink.progress * 100).toInt().coerceAtLeast(1)}%",
                                style = typography.labelSmall,
                                color = colorScheme.primary,
                            )
                            LinearProgressIndicator(
                                progress = { videoLink.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp)
                                    .height(4.dp)
                                    .clip(CircleShape),
                                trackColor = colorScheme.surfaceVariant,
                                color = colorScheme.primary,
                                drawStopIndicator = {}
                            )
                        }
                    }
                }
            }
        }
        videoLink.linkInvalid?.let {
            ErrorText(it)
        }
    }
}

@Composable
fun ErrorText(error: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.NotInterested,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = colorScheme.primary
        )
        Text(
            text = error,
            color = colorScheme.primary,
            style = typography.labelMedium,
            modifier = Modifier.padding(start = 5.dp)
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
                playedDuration = 2980000L,
                subtitleUrl = "https://example.com/subtitles.srt"
            )
        )
    }
}

@DarkPreview
@Composable
private fun PreviewVideoLink_Error() {
    DarkSurface {
        VideoLinkItem(
            videoLink = VideoLink(
                title = "Sample Video Link",
                url = "https://example.com/withsomelongassnameandurl4223/fkfjjkcomingmorethanonce",
                added = System.currentTimeMillis() - 300000,
            ).apply {
                linkInvalid = "File format not supported"
            }
        )
    }
}
