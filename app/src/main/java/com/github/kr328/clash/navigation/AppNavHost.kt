package com.github.kr328.clash.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.github.kr328.clash.AppViewModel
import com.github.kr328.clash.LocalNavController
import com.github.kr328.clash.design.compose.*
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.navigation.destinations.*

private const val TRANSITION_DURATION = 300

@Composable
fun AppNavHost(
    navController: NavHostController,
    appViewModel: AppViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Main,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(TRANSITION_DURATION),
            ) + fadeIn(tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(TRANSITION_DURATION),
            ) + fadeOut(tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(TRANSITION_DURATION),
            ) + fadeIn(tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(TRANSITION_DURATION),
            ) + fadeOut(tween(TRANSITION_DURATION))
        },
    ) {
        // Main screen - hub
        composable<Main> {
            MainDestination(navController = navController, appViewModel = appViewModel)
        }

        // Settings hub
        composable<Settings> {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onAppClick = { navController.navigate(AppSettings) },
                onNetworkClick = { navController.navigate(NetworkSettings) },
                onOverrideClick = { navController.navigate(OverrideSettings) },
                onMetaFeatureClick = { navController.navigate(MetaFeatureSettings) },
            )
        }

        // Help
        composable<Help> {
            HelpDestination(navController = navController)
        }

        // Profiles
        composable<Profiles> {
            ProfilesDestination(navController = navController, appViewModel = appViewModel)
        }

        // Properties (uuid arg)
        composable<Properties> { backStackEntry ->
            val route = backStackEntry.toRoute<Properties>()
            PropertiesDestination(navController = navController, uuid = route.uuid)
        }

        // Files (uuid arg)
        composable<Files> { backStackEntry ->
            val route = backStackEntry.toRoute<Files>()
            FilesDestination(navController = navController, uuid = route.uuid)
        }

        // New Profile
        composable<NewProfile> {
            NewProfileDestination(navController = navController)
        }

        // Logs
        composable<Logs> {
            LogsDestination(navController = navController)
        }

        // Logcat (optional fileName arg)
        composable<Logcat> { backStackEntry ->
            val route = backStackEntry.toRoute<Logcat>()
            LogcatDestination(navController = navController, fileName = route.fileName)
        }

        // Proxy
        composable<Proxy> {
            ProxyDestination(navController = navController, appViewModel = appViewModel)
        }

        // Providers
        composable<Providers> {
            ProvidersDestination(navController = navController, appViewModel = appViewModel)
        }

        // App Settings
        composable<AppSettings> {
            AppSettingsDestination(navController = navController, appViewModel = appViewModel)
        }

        // Network Settings
        composable<NetworkSettings> {
            NetworkSettingsDestination(navController = navController, appViewModel = appViewModel)
        }

        // Access Control
        composable<AccessControl> {
            AccessControlDestination(navController = navController, appViewModel = appViewModel)
        }

        // Override Settings
        composable<OverrideSettings> {
            OverrideSettingsDestination(navController = navController)
        }

        // Meta Feature Settings
        composable<MetaFeatureSettings> {
            MetaFeatureSettingsDestination(navController = navController)
        }
    }
}
