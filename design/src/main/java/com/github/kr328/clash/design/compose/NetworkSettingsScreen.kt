package com.github.kr328.clash.design.compose

import android.os.Build
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.github.kr328.clash.service.model.AccessControlMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkSettingsScreen(
    onBackClick: () -> Unit,
    isRunning: Boolean,
    enableVpn: Boolean,
    onEnableVpnChange: (Boolean) -> Unit,
    bypassPrivateNetwork: Boolean,
    onBypassPrivateNetworkChange: (Boolean) -> Unit,
    dnsHijacking: Boolean,
    onDnsHijackingChange: (Boolean) -> Unit,
    allowBypass: Boolean,
    onAllowBypassChange: (Boolean) -> Unit,
    allowIpv6: Boolean,
    onAllowIpv6Change: (Boolean) -> Unit,
    systemProxy: Boolean,
    onSystemProxyChange: (Boolean) -> Unit,
    accessControlMode: AccessControlMode,
    onAccessControlModeChange: (AccessControlMode) -> Unit,
    onAccessControlPackagesClick: () -> Unit,
) {
    val vpnDepsEnabled = enableVpn && !isRunning
    val snackbarHostState = remember { SnackbarHostState() }
    val unavailableMessage = stringResource(R.string.options_unavailable)

    if (isRunning) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar(unavailableMessage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.network)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_arrow_back),
                            contentDescription = null,
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            VpnSwitchItem(
                title = stringResource(R.string.route_system_traffic),
                summary = stringResource(R.string.routing_via_vpn_service),
                icon = R.drawable.ic_baseline_vpn_lock,
                checked = enableVpn,
                onCheckedChange = onEnableVpnChange,
                enabled = !isRunning,
            )

            CategoryHeader(stringResource(R.string.vpn_service_options))

            SwitchItem(
                title = stringResource(R.string.bypass_private_network),
                summary = stringResource(R.string.bypass_private_network_summary),
                checked = bypassPrivateNetwork,
                onCheckedChange = onBypassPrivateNetworkChange,
                enabled = vpnDepsEnabled,
            )
            SwitchItem(
                title = stringResource(R.string.dns_hijacking),
                summary = stringResource(R.string.dns_hijacking_summary),
                checked = dnsHijacking,
                onCheckedChange = onDnsHijackingChange,
                enabled = vpnDepsEnabled,
            )
            SwitchItem(
                title = stringResource(R.string.allow_bypass),
                summary = stringResource(R.string.allow_bypass_summary),
                checked = allowBypass,
                onCheckedChange = onAllowBypassChange,
                enabled = vpnDepsEnabled,
            )
            SwitchItem(
                title = stringResource(R.string.allow_ipv6),
                summary = stringResource(R.string.allow_ipv6_summary),
                checked = allowIpv6,
                onCheckedChange = onAllowIpv6Change,
                enabled = vpnDepsEnabled,
            )
            if (Build.VERSION.SDK_INT >= 29) {
                SwitchItem(
                    title = stringResource(R.string.system_proxy),
                    summary = stringResource(R.string.system_proxy_summary),
                    checked = systemProxy,
                    onCheckedChange = onSystemProxyChange,
                    enabled = vpnDepsEnabled,
                )
            }
            AccessControlModeItem(
                mode = accessControlMode,
                onModeChange = onAccessControlModeChange,
                enabled = vpnDepsEnabled,
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.access_control_packages)) },
                supportingContent = { Text(stringResource(R.string.access_control_packages_summary)) },
                modifier = Modifier.clickable(enabled = vpnDepsEnabled, onClick = onAccessControlPackagesClick),
            )
        }
    }
}

@Composable
private fun CategoryHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun VpnSwitchItem(
    title: String,
    summary: String,
    icon: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
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
        modifier = Modifier.clickable(enabled = enabled) { onCheckedChange(!checked) },
    )
}

@Composable
private fun SwitchItem(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(summary) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
            )
        },
        modifier = Modifier.clickable(enabled = enabled) { onCheckedChange(!checked) },
    )
}

@Composable
private fun AccessControlModeItem(
    mode: AccessControlMode,
    onModeChange: (AccessControlMode) -> Unit,
    enabled: Boolean,
) {
    var showDialog by remember { mutableStateOf(false) }
    val modes = AccessControlMode.entries
    val labels = listOf(
        stringResource(R.string.allow_all_apps),
        stringResource(R.string.allow_selected_apps),
        stringResource(R.string.deny_selected_apps),
    )
    val currentLabel = labels[modes.indexOf(mode)]

    if (showDialog) {
        AccessControlModeDialog(
            labels = labels,
            selectedIndex = modes.indexOf(mode),
            onSelect = { index ->
                showDialog = false
                onModeChange(modes[index])
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(stringResource(R.string.access_control_mode)) },
        supportingContent = { Text(currentLabel) },
        modifier = Modifier.clickable(enabled = enabled) { showDialog = true },
    )
}

@Composable
private fun AccessControlModeDialog(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.access_control_mode)) },
        text = {
            Column {
                labels.forEachIndexed { index, label ->
                    ListItem(
                        headlineContent = { Text(label) },
                        leadingContent = {
                            RadioButton(
                                selected = index == selectedIndex,
                                onClick = { onSelect(index) },
                            )
                        },
                        modifier = Modifier.clickable { onSelect(index) },
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
