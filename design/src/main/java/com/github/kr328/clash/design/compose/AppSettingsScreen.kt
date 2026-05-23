package com.github.kr328.clash.design.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.model.DarkMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    onBackClick: () -> Unit,
    autoRestart: Boolean,
    onAutoRestartChange: (Boolean) -> Unit,
    darkMode: DarkMode,
    onDarkModeChange: (DarkMode) -> Unit,
    dynamicNotification: Boolean,
    onDynamicNotificationChange: (Boolean) -> Unit,
    isRunning: Boolean,
    hideAppIcon: Boolean,
    onHideAppIconChange: (Boolean) -> Unit,
    hideFromRecents: Boolean,
    onHideFromRecentsChange: (Boolean) -> Unit,
) {
    var showDarkModeDialog by remember { mutableStateOf(false) }

    if (showDarkModeDialog) {
        DarkModeDialog(
            currentMode = darkMode,
            onDismiss = { showDarkModeDialog = false },
            onSelect = { mode ->
                showDarkModeDialog = false
                onDarkModeChange(mode)
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_arrow_back),
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingsCategoryHeader(stringResource(R.string.behavior))

            SwitchPreference(
                title = stringResource(R.string.auto_restart),
                summary = stringResource(R.string.allow_clash_auto_restart),
                icon = R.drawable.ic_baseline_restore,
                checked = autoRestart,
                onCheckedChange = onAutoRestartChange,
            )

            SettingsCategoryHeader(stringResource(R.string.interface_))

            val darkModeTexts = arrayOf(
                stringResource(R.string.follow_system_android_10),
                stringResource(R.string.always_light),
                stringResource(R.string.always_dark),
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.dark_mode)) },
                supportingContent = { Text(darkModeTexts[darkMode.ordinal]) },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_brightness_4),
                        contentDescription = null,
                    )
                },
                modifier = Modifier.clickable { showDarkModeDialog = true },
            )

            SettingsCategoryHeader(stringResource(R.string.service))

            SwitchPreference(
                title = stringResource(R.string.show_traffic),
                summary = stringResource(R.string.show_traffic_summary),
                icon = R.drawable.ic_baseline_domain,
                checked = dynamicNotification,
                onCheckedChange = onDynamicNotificationChange,
                enabled = !isRunning,
            )

            SwitchPreference(
                title = stringResource(R.string.hide_app_icon_title),
                summary = stringResource(R.string.hide_app_icon_desc),
                icon = R.drawable.ic_baseline_hide,
                checked = hideAppIcon,
                onCheckedChange = onHideAppIconChange,
            )

            SwitchPreference(
                title = stringResource(R.string.hide_from_recents_title),
                summary = stringResource(R.string.hide_from_recents_desc),
                icon = R.drawable.ic_baseline_stack,
                checked = hideFromRecents,
                onCheckedChange = onHideFromRecentsChange,
            )
        }
    }
}

@Composable
private fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun SwitchPreference(
    title: String,
    summary: String,
    icon: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(summary) },
        leadingContent = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        },
        modifier = Modifier.clickable(enabled = enabled) {
            onCheckedChange(!checked)
        },
    )
}

@Composable
private fun DarkModeDialog(
    currentMode: DarkMode,
    onDismiss: () -> Unit,
    onSelect: (DarkMode) -> Unit,
) {
    val options = DarkMode.entries
    val labels = arrayOf(
        stringResource(R.string.follow_system_android_10),
        stringResource(R.string.always_light),
        stringResource(R.string.always_dark),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dark_mode)) },
        text = {
            Column {
                options.forEachIndexed { index, mode ->
                    ListItem(
                        headlineContent = { Text(labels[index]) },
                        leadingContent = {
                            RadioButton(
                                selected = mode == currentMode,
                                onClick = { onSelect(mode) },
                            )
                        },
                        modifier = Modifier.clickable { onSelect(mode) },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}

