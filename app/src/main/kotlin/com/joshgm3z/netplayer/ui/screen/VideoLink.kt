package com.joshgm3z.netplayer.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun VideoLink(videoLink: VideoLink) {
    ConstraintLayout(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = cardColor())
            .padding(horizontal = 15.dp, vertical = 15.dp)
            .width(450.dp),
    ) {
        val (icon, title, url, new) = createRefs()
        Icon(
            imageVector = Icons.Default.Link,
            tint = colorScheme.primary,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(icon) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                .background(
                    color = colorScheme.primaryContainer,
                    shape = CircleShape
                )
                .padding(5.dp)
        )
        Text(
            text = videoLink.title,
            style = typography.titleMedium,
            color = textColor(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(icon.end, margin = 10.dp)
            },
        )
        Text(
            text = videoLink.url,
            style = typography.bodyLarge,
            color = subTextColor(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.constrainAs(url) {
                top.linkTo(title.bottom, margin = 5.dp)
                start.linkTo(title.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@DarkPreview
@Composable
private fun PreviewVideoLink() {
    DarkSurface {
        VideoLink(
            videoLink = VideoLink(
                title = "Sample Video Link",
                url = "https://example.com",
                added = System.currentTimeMillis() - 30000,
                totaDuration = 0,
                playedDuration = 0
            )
        )
    }
}