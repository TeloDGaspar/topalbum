package com.telogaspar.itunestopalbums.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.telogaspar.albums.presentation.AlbumDetailRoute
import com.telogaspar.albums.presentation.AlbumDetailScreen
import com.telogaspar.albums.presentation.AlbumListRoute
import com.telogaspar.albums.presentation.AlbumListScreen
import kotlinx.serialization.Serializable


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AlbumListRoute,
    ) {
        composable<AlbumListRoute> {
            AlbumListScreen(
                onAlbumClick = { albumId ->
                    navController.navigate(
                        AlbumDetailRoute(albumId)
                    )
                },
            )
        }

        composable<AlbumDetailRoute> {
            AlbumDetailScreen(
                onBackClick = navController::popBackStack,
            )
        }
    }
}