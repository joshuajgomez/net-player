package com.joshgm3z.netplayer.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.joshgm3z.netplayer.ui.screen.AppUpdateDialog
import com.joshgm3z.netplayer.ui.screen.HomeScreen
import com.joshgm3z.netplayer.ui.screen.PlayerScreen
import kotlinx.serialization.Serializable

open class NavDest {
    @Serializable
    object Home : NavDest()

    @Serializable
    class Player(val url: String, val title: String? = null) : NavDest()

    @Serializable
    object AppUpdate : NavDest()
}

@Composable
fun TvNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavDest.Home
    ) {
        composable<NavDest.Home> {
            HomeScreen(navigate = { navController.navigate(it) })
        }
        composable<NavDest.Player> {
            PlayerScreen()
        }
        composable<NavDest.AppUpdate> {
            AppUpdateDialog(onBackPress = { navController.popBackStack() })
        }
    }
}
