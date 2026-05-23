package com.github.kr328.clash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.core.model.ConfigurationOverride
import com.github.kr328.clash.core.model.LogMessage
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.OverrideSettingsScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.util.withClash
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class OverrideSettingsActivity : BaseComposeActivity() {
    private lateinit var configuration: ConfigurationOverride

    private var httpPort by mutableStateOf<Int?>(null)
    private var socksPort by mutableStateOf<Int?>(null)
    private var redirectPort by mutableStateOf<Int?>(null)
    private var tproxyPort by mutableStateOf<Int?>(null)
    private var mixedPort by mutableStateOf<Int?>(null)
    private var authentication by mutableStateOf<List<String>?>(null)
    private var allowLan by mutableStateOf<Boolean?>(null)
    private var ipv6 by mutableStateOf<Boolean?>(null)
    private var bindAddress by mutableStateOf<String?>(null)
    private var externalController by mutableStateOf<String?>(null)
    private var externalControllerTls by mutableStateOf<String?>(null)
    private var allowOrigins by mutableStateOf<List<String>?>(null)
    private var allowPrivateNetwork by mutableStateOf<Boolean?>(null)
    private var secret by mutableStateOf<String?>(null)
    private var mode by mutableStateOf<TunnelState.Mode?>(null)
    private var logLevel by mutableStateOf<LogMessage.Level?>(null)
    private var hosts by mutableStateOf<Map<String, String>?>(null)
    private var dnsEnable by mutableStateOf<Boolean?>(null)
    private var preferH3 by mutableStateOf<Boolean?>(null)
    private var dnsListen by mutableStateOf<String?>(null)
    private var appendSystemDns by mutableStateOf<Boolean?>(null)
    private var dnsIpv6 by mutableStateOf<Boolean?>(null)
    private var useHosts by mutableStateOf<Boolean?>(null)
    private var enhancedMode by mutableStateOf<ConfigurationOverride.DnsEnhancedMode?>(null)
    private var nameServer by mutableStateOf<List<String>?>(null)
    private var fallback by mutableStateOf<List<String>?>(null)
    private var defaultServer by mutableStateOf<List<String>?>(null)
    private var fakeIpFilter by mutableStateOf<List<String>?>(null)
    private var fakeIpFilterMode by mutableStateOf<ConfigurationOverride.FilterMode?>(null)
    private var geoIpFallback by mutableStateOf<Boolean?>(null)
    private var geoIpCode by mutableStateOf<String?>(null)
    private var domainFallback by mutableStateOf<List<String>?>(null)
    private var ipcidrFallback by mutableStateOf<List<String>?>(null)
    private var nameserverPolicy by mutableStateOf<Map<String, String>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            configuration = withClash { queryOverride(Clash.OverrideSlot.Persist) }
            loadState()
            setContent {
                CoKlashTheme {
                    OverrideSettingsScreen(
                        onBackClick = { saveAndFinish() },
                        onResetClick = { showResetConfirmDialog() },
                        httpPort = httpPort,
                        onHttpPortChange = { httpPort = it; configuration.httpPort = it },
                        socksPort = socksPort,
                        onSocksPortChange = { socksPort = it; configuration.socksPort = it },
                        redirectPort = redirectPort,
                        onRedirectPortChange = { redirectPort = it; configuration.redirectPort = it },
                        tproxyPort = tproxyPort,
                        onTproxyPortChange = { tproxyPort = it; configuration.tproxyPort = it },
                        mixedPort = mixedPort,
                        onMixedPortChange = { mixedPort = it; configuration.mixedPort = it },
                        authentication = authentication,
                        onAuthenticationChange = { authentication = it; configuration.authentication = it },
                        allowLan = allowLan,
                        onAllowLanChange = { allowLan = it; configuration.allowLan = it },
                        ipv6 = ipv6,
                        onIpv6Change = { ipv6 = it; configuration.ipv6 = it },
                        bindAddress = bindAddress,
                        onBindAddressChange = { bindAddress = it; configuration.bindAddress = it },
                        externalController = externalController,
                        onExternalControllerChange = { externalController = it; configuration.externalController = it },
                        externalControllerTls = externalControllerTls,
                        onExternalControllerTlsChange = { externalControllerTls = it; configuration.externalControllerTLS = it },
                        allowOrigins = allowOrigins,
                        onAllowOriginsChange = { allowOrigins = it; configuration.externalControllerCors.allowOrigins = it },
                        allowPrivateNetwork = allowPrivateNetwork,
                        onAllowPrivateNetworkChange = { allowPrivateNetwork = it; configuration.externalControllerCors.allowPrivateNetwork = it },
                        secret = secret,
                        onSecretChange = { secret = it; configuration.secret = it },
                        mode = mode,
                        onModeChange = { mode = it; configuration.mode = it },
                        logLevel = logLevel,
                        onLogLevelChange = { logLevel = it; configuration.logLevel = it },
                        hosts = hosts,
                        onHostsChange = { hosts = it; configuration.hosts = it },
                        dnsEnable = dnsEnable,
                        onDnsEnableChange = { dnsEnable = it; configuration.dns.enable = it },
                        preferH3 = preferH3,
                        onPreferH3Change = { preferH3 = it; configuration.dns.preferH3 = it },
                        dnsListen = dnsListen,
                        onDnsListenChange = { dnsListen = it; configuration.dns.listen = it },
                        appendSystemDns = appendSystemDns,
                        onAppendSystemDnsChange = { appendSystemDns = it; configuration.app.appendSystemDns = it },
                        dnsIpv6 = dnsIpv6,
                        onDnsIpv6Change = { dnsIpv6 = it; configuration.dns.ipv6 = it },
                        useHosts = useHosts,
                        onUseHostsChange = { useHosts = it; configuration.dns.useHosts = it },
                        enhancedMode = enhancedMode,
                        onEnhancedModeChange = { enhancedMode = it; configuration.dns.enhancedMode = it },
                        nameServer = nameServer,
                        onNameServerChange = { nameServer = it; configuration.dns.nameServer = it },
                        fallback = fallback,
                        onFallbackChange = { fallback = it; configuration.dns.fallback = it },
                        defaultServer = defaultServer,
                        onDefaultServerChange = { defaultServer = it; configuration.dns.defaultServer = it },
                        fakeIpFilter = fakeIpFilter,
                        onFakeIpFilterChange = { fakeIpFilter = it; configuration.dns.fakeIpFilter = it },
                        fakeIpFilterMode = fakeIpFilterMode,
                        onFakeIpFilterModeChange = { fakeIpFilterMode = it; configuration.dns.fakeIPFilterMode = it },
                        geoIpFallback = geoIpFallback,
                        onGeoIpFallbackChange = { geoIpFallback = it; configuration.dns.fallbackFilter.geoIp = it },
                        geoIpCode = geoIpCode,
                        onGeoIpCodeChange = { geoIpCode = it; configuration.dns.fallbackFilter.geoIpCode = it },
                        domainFallback = domainFallback,
                        onDomainFallbackChange = { domainFallback = it; configuration.dns.fallbackFilter.domain = it },
                        ipcidrFallback = ipcidrFallback,
                        onIpcidrFallbackChange = { ipcidrFallback = it; configuration.dns.fallbackFilter.ipcidr = it },
                        nameserverPolicy = nameserverPolicy,
                        onNameserverPolicyChange = { nameserverPolicy = it; configuration.dns.nameserverPolicy = it },
                    )
                }
            }
        }
    }

    private fun loadState() {
        httpPort = configuration.httpPort
        socksPort = configuration.socksPort
        redirectPort = configuration.redirectPort
        tproxyPort = configuration.tproxyPort
        mixedPort = configuration.mixedPort
        authentication = configuration.authentication
        allowLan = configuration.allowLan
        ipv6 = configuration.ipv6
        bindAddress = configuration.bindAddress
        externalController = configuration.externalController
        externalControllerTls = configuration.externalControllerTLS
        allowOrigins = configuration.externalControllerCors.allowOrigins
        allowPrivateNetwork = configuration.externalControllerCors.allowPrivateNetwork
        secret = configuration.secret
        mode = configuration.mode
        logLevel = configuration.logLevel
        hosts = configuration.hosts
        dnsEnable = configuration.dns.enable
        preferH3 = configuration.dns.preferH3
        dnsListen = configuration.dns.listen
        appendSystemDns = configuration.app.appendSystemDns
        dnsIpv6 = configuration.dns.ipv6
        useHosts = configuration.dns.useHosts
        enhancedMode = configuration.dns.enhancedMode
        nameServer = configuration.dns.nameServer
        fallback = configuration.dns.fallback
        defaultServer = configuration.dns.defaultServer
        fakeIpFilter = configuration.dns.fakeIpFilter
        fakeIpFilterMode = configuration.dns.fakeIPFilterMode
        geoIpFallback = configuration.dns.fallbackFilter.geoIp
        geoIpCode = configuration.dns.fallbackFilter.geoIpCode
        domainFallback = configuration.dns.fallbackFilter.domain
        ipcidrFallback = configuration.dns.fallbackFilter.ipcidr
        nameserverPolicy = configuration.dns.nameserverPolicy
    }

    private fun saveAndFinish() {
        lifecycleScope.launch {
            withClash { patchOverride(Clash.OverrideSlot.Persist, configuration) }
            finish()
        }
    }

    private fun showResetConfirmDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.reset_override_settings)
            .setMessage(R.string.reset_override_settings_message)
            .setPositiveButton(R.string.ok) { _, _ ->
                lifecycleScope.launch {
                    withClash { clearOverride(Clash.OverrideSlot.Persist) }
                    finish()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}