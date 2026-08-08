package com.joshgm3z.netplayer.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.tv.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import com.joshgm3z.netplayer.repository.VideoLink
import com.joshgm3z.netplayer.ui.util.DarkPreview
import com.joshgm3z.netplayer.ui.util.DarkSurface
import com.joshgm3z.netplayer.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(80.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val uiState by viewModel.uiState.collectAsState()
        VideoLinks(uiState.videoLinks)
        QrCode(uiState.qrCode)
    }
}

@Composable
fun textColor() = colorScheme.onSurface

@Composable
fun subTextColor() = colorScheme.onSurface.copy(alpha = 0.8f)

@Composable
fun cardColor() = colorScheme.onBackground.copy(alpha = 0.1f)

@Composable
fun QrCode(qrCode: Bitmap?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(color = colorScheme.primary)
        ) {
            qrCode?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Text(
            text = "Scan the QR code to add new urls here",
            color = textColor(),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(20.dp)
                .width(200.dp)
        )
    }
}

@Composable
fun VideoLinks(videoLinks: List<VideoLink>) {
    Column {
        Text(
            text = "Video links",
            style = typography.headlineLarge,
            color = textColor(),
        )
        Spacer(Modifier.size(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(videoLinks) {
                VideoLink(it)
            }
        }
    }
}

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
private fun PreviewHomeScreen() {
    DarkSurface {
        HomeScreen()
    }
}
