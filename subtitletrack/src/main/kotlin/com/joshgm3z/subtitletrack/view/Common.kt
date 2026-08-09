package com.joshgm3z.subtitletrack.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun LazyListScope.listSpacing(size: Dp = 50.dp) = item {
    Spacer(Modifier.size(size))
}

@Composable
fun CustomHorizontalDivider(index: Int, size: Int) {
    if (index < size - 1) HorizontalDivider(modifier = Modifier.alpha(0.5f))
}