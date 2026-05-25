package com.github.kr328.clash.navigation.destinations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.AppViewModel
import com.github.kr328.clash.design.compose.NetworkSettingsScreen
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.navigation.AccessControl
import com.github.kr328.clash.service.model.AccessControlMode
import com.github.kr328.clash.service.store.ServiceStore

@Composable
fun NetworkSettingsDestination(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val uiStore = remember { UiStore(context) }
    val srvStore = remember { ServiceStore(context) }
    val isRunning by appViewModel.clashRunning.collectAsState()

    var enableVpn by remember { mutableStateOf(uiStore.enableVpn) }
    var bypassPrivateNetwork by remember { mutableStateOf(srvStore.bypassPrivateNetwork) }
    var dnsHijacking by remember { mutableStateOf(srvStore.dnsHijacking) }
    var allowBypass by remember { mutableStateOf(srvStore.allowBypass) }
    var allowIpv6 by remember { mutableStateOf(srvStore.allowIpv6) }
    var systemProxy by remember { mutableStateOf(srvStore.systemProxy) }
    var accessControlMode by remember { mutableStateOf(srvStore.accessControlMode) }

    NetworkSettingsScreen(
        onBackClick = { navController.popBackStack() },
        isRunning = isRunning,
        enableVpn = enableVpn,
        onEnableVpnChange = { enableVpn = it; uiStore.enableVpn = it },
        bypassPrivateNetwork = bypassPrivateNetwork,
        onBypassPrivateNetworkChange = { bypassPrivateNetwork = it; srvStore.bypassPrivateNetwork = it },
        dnsHijacking = dnsHijacking,
        onDnsHijackingChange = { dnsHijacking = it; srvStore.dnsHijacking = it },
        allowBypass = allowBypass,
        onAllowBypassChange = { allowBypass = it; srvStore.allowBypass = it },
        allowIpv6 = allowIpv6,
        onAllowIpv6Change = { allowIpv6 = it; srvStore.allowIpv6 = it },
        systemProxy = systemProxy,
        onSystemProxyChange = { systemProxy = it; srvStore.systemProxy = it },
        accessControlMode = accessControlMode,
        onAccessControlModeChange = { accessControlMode = it; srvStore.accessControlMode = it },
        onAccessControlPackagesClick = { navController.navigate(AccessControl) },
    )
}
