package com.github.kr328.clash.navigation.destinations

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.AppViewModel
import com.github.kr328.clash.RestartReceiver
import com.github.kr328.clash.common.util.componentName
import com.github.kr328.clash.design.compose.AppSettingsScreen
import com.github.kr328.clash.design.model.DarkMode
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.store.UiStore.Companion.mainActivityAlias
import com.github.kr328.clash.service.store.ServiceStore
import com.github.kr328.clash.util.ApplicationObserver

@Composable
fun AppSettingsDestination(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val uiStore = remember { UiStore(context) }
    val srvStore = remember { ServiceStore(context) }
    val isRunning by appViewModel.clashRunning.collectAsState()

    var autoRestart by remember {
        mutableStateOf(
            context.packageManager.getComponentEnabledSetting(
                RestartReceiver::class.componentName,
            ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        )
    }
    var darkMode by remember { mutableStateOf(uiStore.darkMode) }
    var dynamicNotification by remember { mutableStateOf(srvStore.dynamicNotification) }
    var hideAppIcon by remember { mutableStateOf(uiStore.hideAppIcon) }
    var hideFromRecents by remember { mutableStateOf(uiStore.hideFromRecents) }

    AppSettingsScreen(
        onBackClick = { navController.popBackStack() },
        autoRestart = autoRestart,
        onAutoRestartChange = { value ->
            autoRestart = value
            val status = if (value) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }
            context.packageManager.setComponentEnabledSetting(
                RestartReceiver::class.componentName,
                status,
                PackageManager.DONT_KILL_APP,
            )
        },
        darkMode = darkMode,
        onDarkModeChange = { mode ->
            darkMode = mode
            uiStore.darkMode = mode
            ApplicationObserver.createdActivities.forEach { it.recreate() }
        },
        dynamicNotification = dynamicNotification,
        onDynamicNotificationChange = { value ->
            dynamicNotification = value
            srvStore.dynamicNotification = value
        },
        isRunning = isRunning,
        hideAppIcon = hideAppIcon,
        onHideAppIconChange = { value ->
            hideAppIcon = value
            uiStore.hideAppIcon = value
            val newState = if (value) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }
            context.packageManager.setComponentEnabledSetting(
                context.mainActivityAlias,
                newState,
                PackageManager.DONT_KILL_APP,
            )
        },
        hideFromRecents = hideFromRecents,
        onHideFromRecentsChange = { value ->
            hideFromRecents = value
            uiStore.hideFromRecents = value
            ApplicationObserver.createdActivities.forEach { it.recreate() }
        },
    )
}
