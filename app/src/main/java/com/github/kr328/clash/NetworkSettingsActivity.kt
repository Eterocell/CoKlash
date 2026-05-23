package com.github.kr328.clash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.design.compose.NetworkSettingsScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.service.model.AccessControlMode
import com.github.kr328.clash.service.store.ServiceStore

class NetworkSettingsActivity : BaseComposeActivity() {
    private lateinit var srvStore: ServiceStore

    private var enableVpn by mutableStateOf(false)
    private var bypassPrivateNetwork by mutableStateOf(false)
    private var dnsHijacking by mutableStateOf(false)
    private var allowBypass by mutableStateOf(false)
    private var allowIpv6 by mutableStateOf(false)
    private var systemProxy by mutableStateOf(false)
    private var accessControlMode by mutableStateOf(AccessControlMode.AcceptAll)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        srvStore = ServiceStore(this)
        loadState()

        setContent {
            CoKlashTheme {
                NetworkSettingsScreen(
                    onBackClick = { finish() },
                    isRunning = clashRunning,
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
                    onAccessControlPackagesClick = {
                        startActivity(AccessControlActivity::class.intent)
                    },
                )
            }
        }
    }

    private fun loadState() {
        enableVpn = uiStore.enableVpn
        bypassPrivateNetwork = srvStore.bypassPrivateNetwork
        dnsHijacking = srvStore.dnsHijacking
        allowBypass = srvStore.allowBypass
        allowIpv6 = srvStore.allowIpv6
        systemProxy = srvStore.systemProxy
        accessControlMode = srvStore.accessControlMode
    }

    override fun onStarted() {
        super.onStarted()
        recreate()
    }

    override fun onStopped(cause: String?) {
        super.onStopped(cause)
        recreate()
    }

    override fun onServiceRecreated() {
        super.onServiceRecreated()
        recreate()
    }
}