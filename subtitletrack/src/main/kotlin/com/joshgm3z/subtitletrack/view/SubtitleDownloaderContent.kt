package com.joshgm3z.subtitletrack.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme.colorScheme
import androidx.tv.material3.MaterialTheme.typography
import androidx.tv.material3.Text
import com.joshgm3z.subtitletrack.repository.SubtitleData
import com.joshgm3z.subtitletrack.util.languageName

@Composable
fun SubtitleDownloaderContent(
    listState: ListState.OnlineSubtitleTracks,
    onClick: (SubtitleData) -> Unit = {},
    onLanguageClick: (String) -> Unit = {},
) {
    Column {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listSpacing(10.dp)
            items(listState.languages) {
                val chipShape = RoundedCornerShape(5.dp)
                val selected = listState.selectedLanguage == it
                Text(
                    text = it.languageName(),
                    color = colorScheme.onBackground,
                    style = typography.labelLarge,
                    modifier = Modifier
                        .clip(chipShape)
                        .clickable(!selected) {
                            onLanguageClick(it)
                        }
                        .background(
                            color = if (selected) colorScheme.onPrimary
                            else colorScheme.onBackground.copy(alpha = 0.1f)
                        )
                        .border(
                            width = if (selected) 1.dp else 0.dp,
                            color = if (selected) colorScheme.primary else Color.Transparent,
                            shape = chipShape
                        )
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
            listSpacing(10.dp)
        }
        Spacer(Modifier.size(15.dp))
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .clip(RoundedCornerShape(10.dp)),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(listState.list) {
                SubtitleResultItem(it) {
                    onClick(it)
                }
            }
            listSpacing(20.dp)
        }
    }
}

@Composable
private fun SubtitleResultItem(
    subtitleData: SubtitleData,
    onClick: () -> Unit = {}
) {
    CustomCard(onClick = onClick) {
        ConstraintLayout(
            modifier = Modifier
                .clickable(true) {
                    onClick()
                }
                .fillMaxWidth()
        ) {
            val (titleRef, languageRef, downloadCountRef, iconRef) = createRefs()
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier
                    .constrainAs(iconRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .background(color = colorScheme.onPrimary, shape = CircleShape)
                    .padding(5.dp)
                    .size(20.dp)
            )
            Text(
                text = subtitleData.title,
                maxLines = 1,
                style = typography.bodyMedium,
                color = colorScheme.onBackground,
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top)
                    start.linkTo(iconRef.end, margin = 15.dp)
                }
            )
            val subTextStyle = typography.bodyMedium
            Text(
                text = subtitleData.language.languageName(),
                color = colorScheme.primary,
                style = subTextStyle,
                modifier = Modifier
                    .constrainAs(languageRef) {
                        top.linkTo(titleRef.bottom, margin = 5.dp)
                        start.linkTo(titleRef.start)
                    }
                    .background(
                        color = colorScheme.primaryContainer,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            )
            Row(
                modifier = Modifier.constrainAs(downloadCountRef) {
                    top.linkTo(languageRef.top)
                    bottom.linkTo(languageRef.bottom)
                    start.linkTo(languageRef.end, margin = 5.dp)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = colorScheme.primary
                )
                Text(
                    text = subtitleData.downloadCount.toString(),
                    style = subTextStyle,
                    color = colorScheme.primary,
                )
            }
        }
    }
}

@DarkPreview
@Composable
private fun PreviewSubtitleDownloaderContent() {
    DarkSurface {
        SubtitleDownloaderContent(
            listState = ListState.OnlineSubtitleTracks(
                listOf(
                    SubtitleData(
                        title = "Wonder.Women.2024.HDRip.Xeno200",
                        language = "English",
                        fileId = 1234,
                        downloadCount = 300,
                    ),
                    SubtitleData(
                        title = "Wonder.Women.2024.HDRip.Xeno200",
                        language = "English",
                        fileId = 1234,
                        downloadCount = 300,
                    ),
                    SubtitleData(
                        title = "Wonder.Women.2024.HDRip.Xeno200",
                        language = "English",
                        fileId = 1234,
                        downloadCount = 300,
                    ),
                ),
                languages = listOf(
                    "English", " Spanish ", " French "
                ),
                selectedLanguage = null,
            ),
        )
    }
}