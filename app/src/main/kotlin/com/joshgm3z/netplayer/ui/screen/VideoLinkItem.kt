package com.joshgm3z.netplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
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

    ConstraintLayout(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 2.dp,
                color = if (isFocused) colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(
                color = if (!isFocused) cardColor()
                else colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
            .padding(horizontal = 15.dp, vertical = 10.dp)
            .width(450.dp),
    ) {
        val (icon, title, url, metadata, progress, cc) = createRefs()
        Icon(
            imageVector = if (isFocused) Icons.Default.PlayArrow
            else Icons.Default.Link,
            tint = colorScheme.primary,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(icon) {
                    top.linkTo(parent.top, margin = 3.dp)
                    start.linkTo(parent.start)
                }
                .background(
                    color = if (isFocused) colorScheme.primaryContainer
                    else colorScheme.onBackground.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .padding(5.dp)
                .size(20.dp)
        )

        if (videoLink.playedDuration > 0) LinearProgressIndicator(
            progress = { videoLink.playedDuration.toFloat() / videoLink.totalDuration.toFloat() },
            modifier = Modifier
                .constrainAs(progress) {
                    top.linkTo(icon.bottom, margin = 14.dp)
                    start.linkTo(icon.start)
                    end.linkTo(icon.end)
                    width = Dimension.fillToConstraints
                }
                .width(100.dp),
            drawStopIndicator = {}
        )

        if (!videoLink.title.isEmpty()) Text(
            text = videoLink.title,
            style = typography.titleMedium,
            color = textColor(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(icon.end, margin = 10.dp)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 5.dp),
        )

        Text(
            text = videoLink.url,
            style = typography.bodySmall,
            color = textColor().copy(alpha = 0.5f),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.constrainAs(url) {
                top.linkTo(title.bottom)
                start.linkTo(metadata.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = buildAnnotatedString {
                val dot = "  •  "
                append(videoLink.added.relativeTime())
                if (videoLink.totalDuration > 0) {
                    withStyle(style = SpanStyle(color = colorScheme.primary)) {
                        append(dot)
                    }
                    append(videoLink.totalDuration.toTextTime())
                }
            },
            style = typography.bodySmall,
            color = subTextColor(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.constrainAs(metadata) {
                top.linkTo(url.bottom, margin = 5.dp)
                start.linkTo(icon.end, margin = 10.dp)
                width = Dimension.fillToConstraints
            }
        )
        videoLink.subtitleUrl?.let {
            Icon(
                imageVector = Icons.Default.ClosedCaption,
                contentDescription = null,
                tint = colorScheme.onBackground.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(18.dp)
                    .constrainAs(cc) {
                        top.linkTo(metadata.top)
                        bottom.linkTo(metadata.bottom)
                        start.linkTo(metadata.end, margin = 7.dp)
                    })
        }
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
                added = System.currentTimeMillis() - 30000,
                totalDuration = 4980000L,
                playedDuration = 2700000L,
                subtitleUrl = "https://example.com/subtitles.srt"
            )
        )
    }
}