package com.github.kr328.clash

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.kr328.clash.common.util.componentName
import com.github.kr328.clash.design.compose.AppSettingsScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.model.DarkMode
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.store.UiStore.Companion.mainActivityAlias
import com.github.kr328.clash.remote.Remote
import com.github.kr328.clash.service.store.ServiceStore
import com.github.kr328.clash.util.ApplicationObserver

class AppSettingsActivity : ComponentActivity() {
    private lateinit var uiStore: UiStore
    private lateinit var srvStore: ServiceStore

    private var autoRestart by mutableStateOf(false)
    private var darkMode by mutableStateOf(DarkMode.Auto)
    private var dynamicNotification by mutableStateOf(false)
    private var hideAppIcon by mutableStateOf(false)
    private var hideFromRecents by mutableStateOf(false)
    private var isRunning by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        uiStore = UiStore(this)
        srvStore = ServiceStore(this)
        isRunning = Remote.broadcasts.clashRunning

        loadState()

        setContent {
            CoKlashTheme {
                AppSettingsScreen(
                    onBackClick = { finish() },
                    autoRestart = autoRestart,
                    onAutoRestartChange = ::onAutoRestartChange,
                    darkMode = darkMode,
                    onDarkModeChange = ::onDarkModeChange,
                    dynamicNotification = dynamicNotification,
                    onDynamicNotificationChange = ::onDynamicNotificationChange,
                    isRunning = isRunning,
                    hideAppIcon = hideAppIcon,
                    onHideAppIconChange = ::onHideAppIconChange,
                    hideFromRecents = hideFromRecents,
                    onHideFromRecentsChange = ::onHideFromRecentsChange,
                )
            }
        }
    }

    private fun loadState() {
        autoRestart = packageManager.getComponentEnabledSetting(
            RestartReceiver::class.componentName,
        ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        darkMode = uiStore.darkMode
        dynamicNotification = srvStore.dynamicNotification
        hideAppIcon = uiStore.hideAppIcon
        hideFromRecents = uiStore.hideFromRecents
    }

    private fun onAutoRestartChange(value: Boolean) {
        autoRestart = value
        val status = if (value) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        packageManager.setComponentEnabledSetting(
            RestartReceiver::class.componentName,
            status,
            PackageManager.DONT_KILL_APP,
        )
    }

    private fun onDarkModeChange(mode: DarkMode) {
        darkMode = mode
        uiStore.darkMode = mode
        ApplicationObserver.createdActivities.forEach { it.recreate() }
    }

    private fun onDynamicNotificationChange(value: Boolean) {
        dynamicNotification = value
        srvStore.dynamicNotification = value
    }

    private fun onHideAppIconChange(value: Boolean) {
        hideAppIcon = value
        uiStore.hideAppIcon = value
        val newState = if (value) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        }
        packageManager.setComponentEnabledSetting(
            mainActivityAlias,
            newState,
            PackageManager.DONT_KILL_APP,
        )
    }

    private fun onHideFromRecentsChange(value: Boolean) {
        hideFromRecents = value
        uiStore.hideFromRecents = value
        ApplicationObserver.createdActivities.forEach { it.recreate() }
    }
}
