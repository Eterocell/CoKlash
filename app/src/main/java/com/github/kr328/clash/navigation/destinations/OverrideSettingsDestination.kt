package com.github.kr328.clash.navigation.destinations

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.core.model.ConfigurationOverride
import com.github.kr328.clash.core.model.LogMessage
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.design.compose.OverrideSettingsScreen
import com.github.kr328.clash.util.withClash
import kotlinx.coroutines.launch
import com.github.kr328.clash.design.R as DesignR

@Composable
fun OverrideSettingsDestination(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var configuration by remember { mutableStateOf<ConfigurationOverride?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }

    var httpPort by remember { mutableStateOf<Int?>(null) }
    var socksPort by remember { mutableStateOf<Int?>(null) }
    var redirectPort by remember { mutableStateOf<Int?>(null) }
    var tproxyPort by remember { mutableStateOf<Int?>(null) }
    var mixedPort by remember { mutableStateOf<Int?>(null) }
    var authentication by remember { mutableStateOf<List<String>?>(null) }
    var allowLan by remember { mutableStateOf<Boolean?>(null) }
    var ipv6 by remember { mutableStateOf<Boolean?>(null) }
    var bindAddress by remember { mutableStateOf<String?>(null) }
    var externalController by remember { mutableStateOf<String?>(null) }
    var externalControllerTls by remember { mutableStateOf<String?>(null) }
    var allowOrigins by remember { mutableStateOf<List<String>?>(null) }
    var allowPrivateNetwork by remember { mutableStateOf<Boolean?>(null) }
    var secret by remember { mutableStateOf<String?>(null) }
    var mode by remember { mutableStateOf<TunnelState.Mode?>(null) }
    var logLevel by remember { mutableStateOf<LogMessage.Level?>(null) }
    var hosts by remember { mutableStateOf<Map<String, String>?>(null) }
    var dnsEnable by remember { mutableStateOf<Boolean?>(null) }
    var preferH3 by remember { mutableStateOf<Boolean?>(null) }
    var dnsListen by remember { mutableStateOf<String?>(null) }
    var appendSystemDns by remember { mutableStateOf<Boolean?>(null) }
    var dnsIpv6 by remember { mutableStateOf<Boolean?>(null) }
    var useHosts by remember { mutableStateOf<Boolean?>(null) }
    var enhancedMode by remember { mutableStateOf<ConfigurationOverride.DnsEnhancedMode?>(null) }
    var nameServer by remember { mutableStateOf<List<String>?>(null) }
    var fallback by remember { mutableStateOf<List<String>?>(null) }
    var defaultServer by remember { mutableStateOf<List<String>?>(null) }
    var fakeIpFilter by remember { mutableStateOf<List<String>?>(null) }
    var fakeIpFilterMode by remember { mutableStateOf<ConfigurationOverride.FilterMode?>(null) }
    var geoIpFallback by remember { mutableStateOf<Boolean?>(null) }
    var geoIpCode by remember { mutableStateOf<String?>(null) }
    var domainFallback by remember { mutableStateOf<List<String>?>(null) }
    var ipcidrFallback by remember { mutableStateOf<List<String>?>(null) }
    var nameserverPolicy by remember { mutableStateOf<Map<String, String>?>(null) }

    LaunchedEffect(Unit) {
        val config = withClash { queryOverride(Clash.OverrideSlot.Persist) }
        configuration = config
        httpPort = config.httpPort
        socksPort = config.socksPort
        redirectPort = config.redirectPort
        tproxyPort = config.tproxyPort
        mixedPort = config.mixedPort
        authentication = config.authentication
        allowLan = config.allowLan
        ipv6 = config.ipv6
        bindAddress = config.bindAddress
        externalController = config.externalController
        externalControllerTls = config.externalControllerTLS
        allowOrigins = config.externalControllerCors.allowOrigins
        allowPrivateNetwork = config.externalControllerCors.allowPrivateNetwork
        secret = config.secret
        mode = config.mode
        logLevel = config.logLevel
        hosts = config.hosts
        dnsEnable = config.dns.enable
        preferH3 = config.dns.preferH3
        dnsListen = config.dns.listen
        appendSystemDns = config.app.appendSystemDns
        dnsIpv6 = config.dns.ipv6
        useHosts = config.dns.useHosts
        enhancedMode = config.dns.enhancedMode
        nameServer = config.dns.nameServer
        fallback = config.dns.fallback
        defaultServer = config.dns.defaultServer
        fakeIpFilter = config.dns.fakeIpFilter
        fakeIpFilterMode = config.dns.fakeIPFilterMode
        geoIpFallback = config.dns.fallbackFilter.geoIp
        geoIpCode = config.dns.fallbackFilter.geoIpCode
        domainFallback = config.dns.fallbackFilter.domain
        ipcidrFallback = config.dns.fallbackFilter.ipcidr
        nameserverPolicy = config.dns.nameserverPolicy
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(DesignR.string.reset_override_settings)) },
            text = { Text(stringResource(DesignR.string.reset_override_settings_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    scope.launch {
                        withClash { clearOverride(Clash.OverrideSlot.Persist) }
                        navController.popBackStack()
                    }
                }) { Text(stringResource(DesignR.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(DesignR.string.cancel))
                }
            },
        )
    }

    if (configuration != null) {
        val config = configuration!!
        OverrideSettingsScreen(
            onBackClick = {
                scope.launch {
                    withClash { patchOverride(Clash.OverrideSlot.Persist, config) }
                    navController.popBackStack()
                }
            },
            onResetClick = { showResetDialog = true },
            httpPort = httpPort,
            onHttpPortChange = { httpPort = it; config.httpPort = it },
            socksPort = socksPort,
            onSocksPortChange = { socksPort = it; config.socksPort = it },
            redirectPort = redirectPort,
            onRedirectPortChange = { redirectPort = it; config.redirectPort = it },
            tproxyPort = tproxyPort,
            onTproxyPortChange = { tproxyPort = it; config.tproxyPort = it },
            mixedPort = mixedPort,
            onMixedPortChange = { mixedPort = it; config.mixedPort = it },
            authentication = authentication,
            onAuthenticationChange = { authentication = it; config.authentication = it },
            allowLan = allowLan,
            onAllowLanChange = { allowLan = it; config.allowLan = it },
            ipv6 = ipv6,
            onIpv6Change = { ipv6 = it; config.ipv6 = it },
            bindAddress = bindAddress,
            onBindAddressChange = { bindAddress = it; config.bindAddress = it },
            externalController = externalController,
            onExternalControllerChange = { externalController = it; config.externalController = it },
            externalControllerTls = externalControllerTls,
            onExternalControllerTlsChange = { externalControllerTls = it; config.externalControllerTLS = it },
            allowOrigins = allowOrigins,
            onAllowOriginsChange = { allowOrigins = it; config.externalControllerCors.allowOrigins = it },
            allowPrivateNetwork = allowPrivateNetwork,
            onAllowPrivateNetworkChange = { allowPrivateNetwork = it; config.externalControllerCors.allowPrivateNetwork = it },
            secret = secret,
            onSecretChange = { secret = it; config.secret = it },
            mode = mode,
            onModeChange = { mode = it; config.mode = it },
            logLevel = logLevel,
            onLogLevelChange = { logLevel = it; config.logLevel = it },
            hosts = hosts,
            onHostsChange = { hosts = it; config.hosts = it },
            dnsEnable = dnsEnable,
            onDnsEnableChange = { dnsEnable = it; config.dns.enable = it },
            preferH3 = preferH3,
            onPreferH3Change = { preferH3 = it; config.dns.preferH3 = it },
            dnsListen = dnsListen,
            onDnsListenChange = { dnsListen = it; config.dns.listen = it },
            appendSystemDns = appendSystemDns,
            onAppendSystemDnsChange = { appendSystemDns = it; config.app.appendSystemDns = it },
            dnsIpv6 = dnsIpv6,
            onDnsIpv6Change = { dnsIpv6 = it; config.dns.ipv6 = it },
            useHosts = useHosts,
            onUseHostsChange = { useHosts = it; config.dns.useHosts = it },
            enhancedMode = enhancedMode,
            onEnhancedModeChange = { enhancedMode = it; config.dns.enhancedMode = it },
            nameServer = nameServer,
            onNameServerChange = { nameServer = it; config.dns.nameServer = it },
            fallback = fallback,
            onFallbackChange = { fallback = it; config.dns.fallback = it },
            defaultServer = defaultServer,
            onDefaultServerChange = { defaultServer = it; config.dns.defaultServer = it },
            fakeIpFilter = fakeIpFilter,
            onFakeIpFilterChange = { fakeIpFilter = it; config.dns.fakeIpFilter = it },
            fakeIpFilterMode = fakeIpFilterMode,
            onFakeIpFilterModeChange = { fakeIpFilterMode = it; config.dns.fakeIPFilterMode = it },
            geoIpFallback = geoIpFallback,
            onGeoIpFallbackChange = { geoIpFallback = it; config.dns.fallbackFilter.geoIp = it },
            geoIpCode = geoIpCode,
            onGeoIpCodeChange = { geoIpCode = it; config.dns.fallbackFilter.geoIpCode = it },
            domainFallback = domainFallback,
            onDomainFallbackChange = { domainFallback = it; config.dns.fallbackFilter.domain = it },
            ipcidrFallback = ipcidrFallback,
            onIpcidrFallbackChange = { ipcidrFallback = it; config.dns.fallbackFilter.ipcidr = it },
            nameserverPolicy = nameserverPolicy,
            onNameserverPolicyChange = { nameserverPolicy = it; config.dns.nameserverPolicy = it },
        )
    }
}
