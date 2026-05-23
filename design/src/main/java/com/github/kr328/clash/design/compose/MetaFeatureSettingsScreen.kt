package com.github.kr328.clash.design.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.github.kr328.clash.core.model.ConfigurationOverride
import com.github.kr328.clash.design.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaFeatureSettingsScreen(
    onBackClick: () -> Unit,
    onResetClick: () -> Unit,
    // Settings category
    unifiedDelay: Boolean?,
    onUnifiedDelayChange: (Boolean?) -> Unit,
    geodataMode: Boolean?,
    onGeodataModeChange: (Boolean?) -> Unit,
    tcpConcurrent: Boolean?,
    onTcpConcurrentChange: (Boolean?) -> Unit,
    findProcessMode: ConfigurationOverride.FindProcessMode?,
    onFindProcessModeChange: (ConfigurationOverride.FindProcessMode?) -> Unit,
    // Sniffer category
    snifferEnabled: Boolean?,
    onSnifferEnabledChange: (Boolean?) -> Unit,
    sniffHttpPorts: List<String>?,
    onSniffHttpPortsChange: (List<String>?) -> Unit,
    sniffHttpOverrideDest: Boolean?,
    onSniffHttpOverrideDestChange: (Boolean?) -> Unit,
    sniffTlsPorts: List<String>?,
    onSniffTlsPortsChange: (List<String>?) -> Unit,
    sniffTlsOverrideDest: Boolean?,
    onSniffTlsOverrideDestChange: (Boolean?) -> Unit,
    sniffQuicPorts: List<String>?,
    onSniffQuicPortsChange: (List<String>?) -> Unit,
    sniffQuicOverrideDest: Boolean?,
    onSniffQuicOverrideDestChange: (Boolean?) -> Unit,
    forceDnsMapping: Boolean?,
    onForceDnsMappingChange: (Boolean?) -> Unit,
    parsePureIp: Boolean?,
    onParsePureIpChange: (Boolean?) -> Unit,
    overrideDestination: Boolean?,
    onOverrideDestinationChange: (Boolean?) -> Unit,
    forceDomain: List<String>?,
    onForceDomainChange: (List<String>?) -> Unit,
    skipDomain: List<String>?,
    onSkipDomainChange: (List<String>?) -> Unit,
    skipSrcAddress: List<String>?,
    onSkipSrcAddressChange: (List<String>?) -> Unit,
    skipDstAddress: List<String>?,
    onSkipDstAddressChange: (List<String>?) -> Unit,
    // GeoX Files category
    onImportGeoIp: () -> Unit,
    onImportGeoSite: () -> Unit,
    onImportCountry: () -> Unit,
    onImportAsn: () -> Unit,
) {
    val snifferDepsEnabled = snifferEnabled != false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.meta_features)) },
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
            // Settings category
            SettingsCategoryHeader(stringResource(R.string.settings))
            TriStateListItem(
                title = stringResource(R.string.unified_delay),
                value = unifiedDelay,
                onValueChange = onUnifiedDelayChange,
            )
            TriStateListItem(
                title = stringResource(R.string.geodata_mode),
                value = geodataMode,
                onValueChange = onGeodataModeChange,
            )
            TriStateListItem(
                title = stringResource(R.string.tcp_concurrent),
                value = tcpConcurrent,
                onValueChange = onTcpConcurrentChange,
            )
            FindProcessModeListItem(
                value = findProcessMode,
                onValueChange = onFindProcessModeChange,
            )

            // Sniffer category
            SettingsCategoryHeader(stringResource(R.string.sniffer_setting))
            TriStateListItem(
                title = stringResource(R.string.strategy),
                value = snifferEnabled,
                onValueChange = onSnifferEnabledChange,
            )
            EditableTextListItem(
                title = stringResource(R.string.sniff_http_ports),
                value = sniffHttpPorts,
                onValueChange = onSniffHttpPortsChange,
                placeholder = stringResource(R.string.dont_modify),
                enabled = snifferDepsEnabled,
            )
            TriStateListItem(
                title = stringResource(R.string.sniff_http_override_destination),
                value = sniffHttpOverrideDest,
                onValueChange = onSniffHttpOverrideDestChange,
                enabled = snifferDepsEnabled,
            )
            EditableTextListItem(
                title = stringResource(R.string.sniff_tls_ports),
                value = sniffTlsPorts,
                onValueChange = onSniffTlsPortsChange,
                placeholder = stringResource(R.string.dont_modify),
                enabled = snifferDepsEnabled,
            )
            TriStateListItem(
                title = stringResource(R.string.sniff_tls_override_destination),
                value = sniffTlsOverrideDest,
                onValueChange = onSniffTlsOverrideDestChange,
                enabled = snifferDepsEnabled,
            )
            EditableTextListItem(
                title = stringResource(R.string.sniff_quic_ports),
                value = sniffQuicPorts,
                onValueChange = onSniffQuicPortsChange,
                placeholder = stringResource(R.string.dont_modify),
                enabled = snifferDepsEnabled,
            )
            TriStateListItem(
                title = stringResource(R.string.sniff_quic_override_destination),
                value = sniffQuicOverrideDest,
                onValueChange = onSniffQuicOverrideDestChange,
                enabled = snifferDepsEnabled,
            )
            TriStateListItem(
                title = stringResource(R.string.force_dns_mapping),
                value = forceDnsMapping,
                onValueChange = onForceDnsMappingChange,
                enabled = snifferDepsEnabled,
            )
            TriStateListItem(
                title = stringResource(R.string.parse_pure_ip),
                value = parsePureIp,
                onValueChange = onParsePureIpChange,
                enabled = snifferDepsEnabled,
            )
            TriStateListItem(
                title = stringResource(R.string.override_destination),
                value = overrideDestination,
                onValueChange = onOverrideDestinationChange,
                enabled = snifferDepsEnabled,
            )
            EditableTextListItem(
                title = stringResource(R.string.force_domain),
                value = forceDomain,
                onValueChange = onForceDomainChange,
                placeholder = stringResource(R.string.dont_modify),
                enabled = snifferDepsEnabled,
            )
            EditableTextListItem(
                title = stringResource(R.string.skip_domain),
                value = skipDomain,
                onValueChange = onSkipDomainChange,
                placeholder = stringResource(R.string.dont_modify),
                enabled = snifferDepsEnabled,
            )
            EditableTextListItem(
                title = stringResource(R.string.skip_src_address),
                value = skipSrcAddress,
                onValueChange = onSkipSrcAddressChange,
                placeholder = stringResource(R.string.dont_modify),
                enabled = snifferDepsEnabled,
            )
            EditableTextListItem(
                title = stringResource(R.string.skip_dst_address),
                value = skipDstAddress,
                onValueChange = onSkipDstAddressChange,
                placeholder = stringResource(R.string.dont_modify),
                enabled = snifferDepsEnabled,
            )

            SettingsCategoryHeader(stringResource(R.string.geox_files))
            ListItem(
                headlineContent = { Text(stringResource(R.string.import_geoip_file)) },
                supportingContent = { Text(stringResource(R.string.press_to_import)) },
                modifier = Modifier.clickable(onClick = onImportGeoIp),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.import_geosite_file)) },
                supportingContent = { Text(stringResource(R.string.press_to_import)) },
                modifier = Modifier.clickable(onClick = onImportGeoSite),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.import_country_file)) },
                supportingContent = { Text(stringResource(R.string.press_to_import)) },
                modifier = Modifier.clickable(onClick = onImportCountry),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.import_asn_file)) },
                supportingContent = { Text(stringResource(R.string.press_to_import)) },
                modifier = Modifier.clickable(onClick = onImportAsn),
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
private fun TriStateListItem(
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
private fun FindProcessModeListItem(
    value: ConfigurationOverride.FindProcessMode?,
    onValueChange: (ConfigurationOverride.FindProcessMode?) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val values = listOf(
        null,
        ConfigurationOverride.FindProcessMode.Off,
        ConfigurationOverride.FindProcessMode.Strict,
        ConfigurationOverride.FindProcessMode.Always,
    )
    val labels = listOf(
        stringResource(R.string.dont_modify),
        stringResource(R.string.off),
        stringResource(R.string.strict),
        stringResource(R.string.always),
    )
    val currentLabel = labels[values.indexOf(value).coerceAtLeast(0)]

    if (showDialog) {
        SelectionDialog(
            title = stringResource(R.string.find_process_mode),
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
        headlineContent = { Text(stringResource(R.string.find_process_mode)) },
        supportingContent = { Text(currentLabel) },
        modifier = Modifier.clickable { showDialog = true },
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
private fun EditableTextListItem(
    title: String,
    value: List<String>?,
    onValueChange: (List<String>?) -> Unit,
    placeholder: String,
    enabled: Boolean = true,
) {
    var showDialog by remember { mutableStateOf(false) }
    val displayText = if (value.isNullOrEmpty()) placeholder else value.joinToString(", ")

    if (showDialog) {
        EditableTextListDialog(
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
private fun EditableTextListDialog(
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
