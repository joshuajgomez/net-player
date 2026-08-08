package com.joshgm3z.netplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.tv.material3.MaterialTheme.colorScheme
import com.joshgm3z.netplayer.ui.TvNavHost
import com.joshgm3z.netplayer.ui.theme.NetPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NetPlayerTheme {
                Surface(color = colorScheme.background) {
                    TvNavHost()
                }
            }
        }
    }
}
