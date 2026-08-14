package com.joshgm3z.netplayer.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.joshgm3z.netplayer.ui.screen.AppUpdateDialog
import com.joshgm3z.netplayer.ui.screen.ErrorScreen
import com.joshgm3z.netplayer.ui.screen.HomeScreen
import com.joshgm3z.netplayer.ui.screen.PlayerScreen
import com.joshgm3z.subtitletrack.view.TrackSelectorDialog
import kotlinx.serialization.Serializable

open class NavDest {
    @Serializable
    object Home : NavDest()

    @Serializable
    class Player(val url: String, val title: String? = null) : NavDest()

    @Serializable
    object AppUpdate : NavDest()

    @Serializable
    class SubtitleTrackSelector(val title: String) : NavDest()

    @Serializable
    class Error(val summary: String) : NavDest()
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
            PlayerScreen(
                onCaptionsClicked = {
                    navController.navigate(NavDest.SubtitleTrackSelector(it))
                },
                onError = {
                    navController.navigate(NavDest.Error(it))
                })
        }
        composable<NavDest.AppUpdate> {
            AppUpdateDialog(onBackPress = { navController.popBackStack() })
        }
        dialog<NavDest.SubtitleTrackSelector> {
            TrackSelectorDialog(goBack = { navController.popBackStack() })
        }
        dialog<NavDest.Error> {
            val route = it.toRoute<NavDest.Error>()
            ErrorScreen(
                summary = route.summary,
                message = "Error playing video",
                onDismissClick = {
                    navController.navigate(NavDest.Home) {
                        popUpTo<NavDest.Home> {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
