package com.github.kr328.clash.design.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.kr328.clash.core.model.ConfigurationOverride
import com.github.kr328.clash.core.model.LogMessage
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.design.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverrideSettingsScreen(
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    httpPort: Int?, onHttpPortChange: (Int?) -> Unit,
    socksPort: Int?, onSocksPortChange: (Int?) -> Unit,
    redirectPort: Int?, onRedirectPortChange: (Int?) -> Unit,
    tproxyPort: Int?, onTproxyPortChange: (Int?) -> Unit,
    mixedPort: Int?, onMixedPortChange: (Int?) -> Unit,
    authentication: List<String>?, onAuthenticationChange: (List<String>?) -> Unit,
    allowLan: Boolean?, onAllowLanChange: (Boolean?) -> Unit,
    ipv6: Boolean?, onIpv6Change: (Boolean?) -> Unit,
    bindAddress: String?, onBindAddressChange: (String?) -> Unit,
    externalController: String?, onExternalControllerChange: (String?) -> Unit,
    externalControllerTls: String?, onExternalControllerTlsChange: (String?) -> Unit,
    allowOrigins: List<String>?, onAllowOriginsChange: (List<String>?) -> Unit,
    allowPrivateNetwork: Boolean?, onAllowPrivateNetworkChange: (Boolean?) -> Unit,
    secret: String?, onSecretChange: (String?) -> Unit,
    mode: TunnelState.Mode?, onModeChange: (TunnelState.Mode?) -> Unit,
    logLevel: LogMessage.Level?, onLogLevelChange: (LogMessage.Level?) -> Unit,
    hosts: Map<String, String>?, onHostsChange: (Map<String, String>?) -> Unit,
    dnsEnable: Boolean?, onDnsEnableChange: (Boolean?) -> Unit,
    preferH3: Boolean?, onPreferH3Change: (Boolean?) -> Unit,
    dnsListen: String?, onDnsListenChange: (String?) -> Unit,
    appendSystemDns: Boolean?, onAppendSystemDnsChange: (Boolean?) -> Unit,
    dnsIpv6: Boolean?, onDnsIpv6Change: (Boolean?) -> Unit,
    useHosts: Boolean?, onUseHostsChange: (Boolean?) -> Unit,
    enhancedMode: ConfigurationOverride.DnsEnhancedMode?, onEnhancedModeChange: (ConfigurationOverride.DnsEnhancedMode?) -> Unit,
    nameServer: List<String>?, onNameServerChange: (List<String>?) -> Unit,
    fallback: List<String>?, onFallbackChange: (List<String>?) -> Unit,
    defaultServer: List<String>?, onDefaultServerChange: (List<String>?) -> Unit,
    fakeIpFilter: List<String>?, onFakeIpFilterChange: (List<String>?) -> Unit,
    fakeIpFilterMode: ConfigurationOverride.FilterMode?, onFakeIpFilterModeChange: (ConfigurationOverride.FilterMode?) -> Unit,
    geoIpFallback: Boolean?, onGeoIpFallbackChange: (Boolean?) -> Unit,
    geoIpCode: String?, onGeoIpCodeChange: (String?) -> Unit,
    domainFallback: List<String>?, onDomainFallbackChange: (List<String>?) -> Unit,
    ipcidrFallback: List<String>?, onIpcidrFallbackChange: (List<String>?) -> Unit,
    nameserverPolicy: Map<String, String>?, onNameserverPolicyChange: (Map<String, String>?) -> Unit,
) {
    val dnsDepsEnabled = dnsEnable != false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.override)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_arrow_back),
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onResetClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_delete_sweep),
                            contentDescription = stringResource(R.string.reset_override_settings),
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
            CategoryHeader(stringResource(R.string.general))

            PortPreference(
                title = stringResource(R.string.http_port),
                value = httpPort,
                onValueChange = onHttpPortChange,
            )
            PortPreference(
                title = stringResource(R.string.socks_port),
                value = socksPort,
                onValueChange = onSocksPortChange,
            )
            PortPreference(
                title = stringResource(R.string.redirect_port),
                value = redirectPort,
                onValueChange = onRedirectPortChange,
            )
            PortPreference(
                title = stringResource(R.string.tproxy_port),
                value = tproxyPort,
                onValueChange = onTproxyPortChange,
            )
            PortPreference(
                title = stringResource(R.string.mixed_port),
                value = mixedPort,
                onValueChange = onMixedPortChange,
            )
            TextListPreference(
                title = stringResource(R.string.authentication),
                value = authentication,
                onValueChange = onAuthenticationChange,
            )
            TriStatePreference(
                title = stringResource(R.string.allow_lan),
                value = allowLan,
                onValueChange = onAllowLanChange,
            )
            TriStatePreference(
                title = stringResource(R.string.ipv6),
                value = ipv6,
                onValueChange = onIpv6Change,
            )
            NullableTextPreference(
                title = stringResource(R.string.bind_address),
                value = bindAddress,
                onValueChange = onBindAddressChange,
            )
            NullableTextPreference(
                title = stringResource(R.string.external_controller),
                value = externalController,
                onValueChange = onExternalControllerChange,
            )
            NullableTextPreference(
                title = stringResource(R.string.external_controller_tls),
                value = externalControllerTls,
                onValueChange = onExternalControllerTlsChange,
            )
            TextListPreference(
                title = stringResource(R.string.allow_origins),
                value = allowOrigins,
                onValueChange = onAllowOriginsChange,
            )
            TriStatePreference(
                title = stringResource(R.string.allow_private_network),
                value = allowPrivateNetwork,
                onValueChange = onAllowPrivateNetworkChange,
            )
            NullableTextPreference(
                title = stringResource(R.string.secret),
                value = secret,
                onValueChange = onSecretChange,
            )
            ModePreference(
                value = mode,
                onValueChange = onModeChange,
            )
            LogLevelPreference(
                value = logLevel,
                onValueChange = onLogLevelChange,
            )
            TextMapPreference(
                title = stringResource(R.string.hosts),
                value = hosts,
                onValueChange = onHostsChange,
            )

            CategoryHeader(stringResource(R.string.dns))

            DnsEnablePreference(
                value = dnsEnable,
                onValueChange = onDnsEnableChange,
            )
            TriStatePreference(
                title = stringResource(R.string.prefer_h3),
                value = preferH3,
                onValueChange = onPreferH3Change,
                enabled = dnsDepsEnabled,
            )
            NullableTextPreference(
                title = stringResource(R.string.listen),
                value = dnsListen,
                onValueChange = onDnsListenChange,
                enabled = dnsDepsEnabled,
            )
            TriStatePreference(
                title = stringResource(R.string.append_system_dns),
                value = appendSystemDns,
                onValueChange = onAppendSystemDnsChange,
                enabled = dnsDepsEnabled,
            )
            TriStatePreference(
                title = stringResource(R.string.ipv6),
                value = dnsIpv6,
                onValueChange = onDnsIpv6Change,
                enabled = dnsDepsEnabled,
            )
            TriStatePreference(
                title = stringResource(R.string.use_hosts),
                value = useHosts,
                onValueChange = onUseHostsChange,
                enabled = dnsDepsEnabled,
            )
            EnhancedModePreference(
                value = enhancedMode,
                onValueChange = onEnhancedModeChange,
                enabled = dnsDepsEnabled,
            )
            TextListPreference(
                title = stringResource(R.string.name_server),
                value = nameServer,
                onValueChange = onNameServerChange,
                enabled = dnsDepsEnabled,
            )
            TextListPreference(
                title = stringResource(R.string.fallback),
                value = fallback,
                onValueChange = onFallbackChange,
                enabled = dnsDepsEnabled,
            )
            TextListPreference(
                title = stringResource(R.string.default_name_server),
                value = defaultServer,
                onValueChange = onDefaultServerChange,
                enabled = dnsDepsEnabled,
            )
            TextListPreference(
                title = stringResource(R.string.fakeip_filter),
                value = fakeIpFilter,
                onValueChange = onFakeIpFilterChange,
                enabled = dnsDepsEnabled,
            )
            FakeIpFilterModePreference(
                value = fakeIpFilterMode,
                onValueChange = onFakeIpFilterModeChange,
                enabled = dnsDepsEnabled,
            )
            TriStatePreference(
                title = stringResource(R.string.geoip_fallback),
                value = geoIpFallback,
                onValueChange = onGeoIpFallbackChange,
                enabled = dnsDepsEnabled,
            )
            NullableTextPreference(
                title = stringResource(R.string.geoip_fallback_code),
                value = geoIpCode,
                onValueChange = onGeoIpCodeChange,
                enabled = dnsDepsEnabled,
            )
            TextListPreference(
                title = stringResource(R.string.domain_fallback),
                value = domainFallback,
                onValueChange = onDomainFallbackChange,
                enabled = dnsDepsEnabled,
            )
            TextListPreference(
                title = stringResource(R.string.ipcidr_fallback),
                value = ipcidrFallback,
                onValueChange = onIpcidrFallbackChange,
                enabled = dnsDepsEnabled,
            )
            TextMapPreference(
                title = stringResource(R.string.name_server_policy),
                value = nameserverPolicy,
                onValueChange = onNameserverPolicyChange,
                enabled = dnsDepsEnabled,
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
private fun PortPreference(
    title: String,
    value: Int?,
    onValueChange: (Int?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val dontModify = stringResource(R.string.dont_modify)
    val displayText = value?.toString() ?: dontModify

    if (showDialog) {
        PortInputDialog(
            title = title,
            currentValue = value,
            onConfirm = { port ->
                showDialog = false
                onValueChange(port)
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(displayText) },
        modifier = Modifier.clickable { showDialog = true },
    )
}

@Composable
private fun NullableTextPreference(
    title: String,
    value: String?,
    onValueChange: (String?) -> Unit,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    val dontModify = stringResource(R.string.dont_modify)
    val displayText = if (value.isNullOrEmpty()) dontModify else value

    if (showDialog) {
        TextInputDialog(
            title = title,
            currentValue = value.orEmpty(),
            onConfirm = { text ->
                showDialog = false
                onValueChange(text.ifEmpty { null })
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(displayText) },
        modifier = Modifier.clickable(enabled = enabled) { showDialog = true },
    )
}

@Composable
private fun TriStatePreference(
    title: String,
    value: Boolean?,
    onValueChange: (Boolean?) -> Unit,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    val labels = listOf(
        stringResource(R.string.dont_modify),
        stringResource(R.string.enabled),
        stringResource(R.string.disabled),
    )
    val values = listOf(null, true, false)
    val currentLabel = when (value) {
        null -> labels[0]
        true -> labels[1]
        false -> labels[2]
    }

    if (showDialog) {
        SelectionDialog(
            title = title,
            options = labels,
            selectedIndex = values.indexOf(value),
            onSelect = { index ->
                showDialog = false
                onValueChange(values[index])
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(currentLabel) },
        modifier = Modifier.clickable(enabled = enabled) { showDialog = true },
    )
}

@Composable
private fun DnsEnablePreference(
    value: Boolean?,
    onValueChange: (Boolean?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val labels = listOf(
        stringResource(R.string.dont_modify),
        stringResource(R.string.force_enable),
        stringResource(R.string.use_built_in),
    )
    val values = listOf(null, true, false)
    val currentLabel = when (value) {
        null -> labels[0]
        true -> labels[1]
        false -> labels[2]
    }
    val title = stringResource(R.string.strategy)

    if (showDialog) {
        SelectionDialog(
            title = title,
            options = labels,
            selectedIndex = values.indexOf(value),
            onSelect = { index ->
                showDialog = false
                onValueChange(values[index])
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(currentLabel) },
        modifier = Modifier.clickable { showDialog = true },
    )
}

@Composable
private fun ModePreference(
    value: TunnelState.Mode?,
    onValueChange: (TunnelState.Mode?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val values = listOf(null, TunnelState.Mode.Direct, TunnelState.Mode.Global, TunnelState.Mode.Rule)
    val labels = listOf(
        stringResource(R.string.dont_modify),
        stringResource(R.string.direct_mode),
        stringResource(R.string.global_mode),
        stringResource(R.string.rule_mode),
    )
    val currentLabel = labels[values.indexOf(value).coerceAtLeast(0)]
    val title = stringResource(R.string.mode)

    if (showDialog) {
        SelectionDialog(
            title = title,
            options = labels,
            selectedIndex = values.indexOf(value).coerceAtLeast(0),
            onSelect = { index ->
                showDialog = false
                onValueChange(values[index])
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(currentLabel) },
        modifier = Modifier.clickable { showDialog = true },
    )
}

@Composable
private fun LogLevelPreference(
    value: LogMessage.Level?,
    onValueChange: (LogMessage.Level?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val values = listOf(null, LogMessage.Level.Info, LogMessage.Level.Warning, LogMessage.Level.Error, LogMessage.Level.Debug, LogMessage.Level.Silent)
    val labels = listOf(
        stringResource(R.string.dont_modify),
        stringResource(R.string.info),
        stringResource(R.string.warning),
        stringResource(R.string.error),
        stringResource(R.string.debug),
        stringResource(R.string.silent),
    )
    val currentLabel = labels[values.indexOf(value).coerceAtLeast(0)]
    val title = stringResource(R.string.log_level)

    if (showDialog) {
        SelectionDialog(
            title = title,
            options = labels,
            selectedIndex = values.indexOf(value).coerceAtLeast(0),
            onSelect = { index ->
                showDialog = false
                onValueChange(values[index])
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(currentLabel) },
        modifier = Modifier.clickable { showDialog = true },
    )
}

@Composable
private fun EnhancedModePreference(
    value: ConfigurationOverride.DnsEnhancedMode?,
    onValueChange: (ConfigurationOverride.DnsEnhancedMode?) -> Unit,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    val values = listOf(
        null,
        ConfigurationOverride.DnsEnhancedMode.None,
        ConfigurationOverride.DnsEnhancedMode.FakeIp,
        ConfigurationOverride.DnsEnhancedMode.Mapping,
    )
    val labels = listOf(
        stringResource(R.string.dont_modify),
        stringResource(R.string.disabled),
        stringResource(R.string.fakeip),
        stringResource(R.string.mapping),
    )
    val currentLabel = labels[values.indexOf(value).coerceAtLeast(0)]
    val title = stringResource(R.string.enhanced_mode)

    if (showDialog) {
        SelectionDialog(
            title = title,
            options = labels,
            selectedIndex = values.indexOf(value).coerceAtLeast(0),
            onSelect = { index ->
                showDialog = false
                onValueChange(values[index])
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(currentLabel) },
        modifier = Modifier.clickable(enabled = enabled) { showDialog = true },
    )
}

@Composable
private fun FakeIpFilterModePreference(
    value: ConfigurationOverride.FilterMode?,
    onValueChange: (ConfigurationOverride.FilterMode?) -> Unit,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    val values = listOf(
        null,
        ConfigurationOverride.FilterMode.BlackList,
        ConfigurationOverride.FilterMode.WhiteList,
    )
    val labels = listOf(
        stringResource(R.string.dont_modify),
        stringResource(R.string.blacklist),
        stringResource(R.string.whitelist),
    )
    val currentLabel = labels[values.indexOf(value).coerceAtLeast(0)]
    val title = stringResource(R.string.fakeip_filter_mode)

    if (showDialog) {
        SelectionDialog(
            title = title,
            options = labels,
            selectedIndex = values.indexOf(value).coerceAtLeast(0),
            onSelect = { index ->
                showDialog = false
                onValueChange(values[index])
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(currentLabel) },
        modifier = Modifier.clickable(enabled = enabled) { showDialog = true },
    )
}

@Composable
private fun TextListPreference(
    title: String,
    value: List<String>?,
    onValueChange: (List<String>?) -> Unit,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    val dontModify = stringResource(R.string.dont_modify)
    val displayText = if (value.isNullOrEmpty()) dontModify else value.joinToString(", ")

    if (showDialog) {
        TextListDialog(
            title = title,
            currentItems = value.orEmpty(),
            onConfirm = { items ->
                showDialog = false
                onValueChange(items.ifEmpty { null })
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(displayText) },
        modifier = Modifier.clickable(enabled = enabled) { showDialog = true },
    )
}

@Composable
private fun TextMapPreference(
    title: String,
    value: Map<String, String>?,
    onValueChange: (Map<String, String>?) -> Unit,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    val dontModify = stringResource(R.string.dont_modify)
    val displayText = if (value.isNullOrEmpty()) dontModify else value.entries.joinToString(", ") { "${it.key}: ${it.value}" }

    if (showDialog) {
        TextMapDialog(
            title = title,
            currentEntries = value.orEmpty(),
            onConfirm = { entries ->
                showDialog = false
                onValueChange(entries.ifEmpty { null })
            },
            onDismiss = { showDialog = false },
        )
    }

    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(displayText) },
        modifier = Modifier.clickable(enabled = enabled) { showDialog = true },
    )
}

@Composable
private fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEachIndexed { index, label ->
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

@Composable
private fun PortInputDialog(
    title: String,
    currentValue: Int?,
    onConfirm: (Int?) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(currentValue?.toString().orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { newText -> text = newText.filter { it.isDigit() } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text.toIntOrNull()) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}

@Composable
private fun TextInputDialog(
    title: String,
    currentValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(currentValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}

@Composable
private fun TextListDialog(
    title: String,
    currentItems: List<String>,
    onConfirm: (List<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var items by remember { mutableStateOf(currentItems.toMutableList()) }
    var newItemText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = {
                            items = items.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_close),
                                contentDescription = null,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = newItemText,
                        onValueChange = { newItemText = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    IconButton(
                        onClick = {
                            if (newItemText.isNotBlank()) {
                                items = items.toMutableList().also { it.add(newItemText.trim()) }
                                newItemText = ""
                            }
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_add),
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(items) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}

@Composable
private fun TextMapDialog(
    title: String,
    currentEntries: Map<String, String>,
    onConfirm: (Map<String, String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var entries by remember { mutableStateOf(currentEntries.toList().toMutableList()) }
    var newKey by remember { mutableStateOf("") }
    var newValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                entries.forEachIndexed { index, (key, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "$key: $value",
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = {
                            entries = entries.toMutableList().also { it.removeAt(index) }
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_baseline_close),
                                contentDescription = null,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    OutlinedTextField(
                        value = newKey,
                        onValueChange = { newKey = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = newValue,
                        onValueChange = { newValue = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    IconButton(
                        onClick = {
                            if (newKey.isNotBlank() && newValue.isNotBlank()) {
                                entries = entries.toMutableList().also { it.add(newKey.trim() to newValue.trim()) }
                                newKey = ""
                                newValue = ""
                            }
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_add),
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(entries.toMap()) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
    )
}
