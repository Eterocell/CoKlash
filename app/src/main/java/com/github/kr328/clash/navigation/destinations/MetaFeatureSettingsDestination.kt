package com.github.kr328.clash.navigation.destinations

import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.core.model.ConfigurationOverride
import com.github.kr328.clash.design.compose.MetaFeatureSettingsScreen
import com.github.kr328.clash.util.clashDir
import com.github.kr328.clash.util.withClash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import com.github.kr328.clash.design.R as DesignR

private enum class ImportType { GeoIp, GeoSite, Country, ASN }

@Composable
fun MetaFeatureSettingsDestination(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var configuration by remember { mutableStateOf<ConfigurationOverride?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var pendingImportType by remember { mutableStateOf<ImportType?>(null) }

    var unifiedDelay by remember { mutableStateOf<Boolean?>(null) }
    var geodataMode by remember { mutableStateOf<Boolean?>(null) }
    var tcpConcurrent by remember { mutableStateOf<Boolean?>(null) }
    var findProcessMode by remember { mutableStateOf<ConfigurationOverride.FindProcessMode?>(null) }
    var snifferEnabled by remember { mutableStateOf<Boolean?>(null) }
    var sniffHttpPorts by remember { mutableStateOf<List<String>?>(null) }
    var sniffHttpOverrideDest by remember { mutableStateOf<Boolean?>(null) }
    var sniffTlsPorts by remember { mutableStateOf<List<String>?>(null) }
    var sniffTlsOverrideDest by remember { mutableStateOf<Boolean?>(null) }
    var sniffQuicPorts by remember { mutableStateOf<List<String>?>(null) }
    var sniffQuicOverrideDest by remember { mutableStateOf<Boolean?>(null) }
    var forceDnsMapping by remember { mutableStateOf<Boolean?>(null) }
    var parsePureIp by remember { mutableStateOf<Boolean?>(null) }
    var overrideDestination by remember { mutableStateOf<Boolean?>(null) }
    var forceDomain by remember { mutableStateOf<List<String>?>(null) }
    var skipDomain by remember { mutableStateOf<List<String>?>(null) }
    var skipSrcAddress by remember { mutableStateOf<List<String>?>(null) }
    var skipDstAddress by remember { mutableStateOf<List<String>?>(null) }

    val validDatabaseExtensions = remember { listOf(".metadb", ".db", ".dat", ".mmdb") }

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        val type = pendingImportType ?: return@rememberLauncherForActivityResult
        pendingImportType = null
        if (uri == null) return@rememberLauncherForActivityResult

        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val displayName: String = if (columnIndex != -1) it.getString(columnIndex) else ""
                val ext = "." + displayName.substringAfterLast(".")

                if (!validDatabaseExtensions.contains(ext)) {
                    Toast.makeText(context, DesignR.string.geofile_unknown_db_format, Toast.LENGTH_LONG).show()
                    return@rememberLauncherForActivityResult
                }

                val outputFileName = when (type) {
                    ImportType.GeoIp -> "geoip$ext"
                    ImportType.GeoSite -> "geosite$ext"
                    ImportType.Country -> "country$ext"
                    ImportType.ASN -> "ASN$ext"
                }

                scope.launch {
                    withContext(Dispatchers.IO) {
                        val outputFile = File(context.clashDir, outputFileName)
                        context.contentResolver.openInputStream(uri).use { ins ->
                            FileOutputStream(outputFile).use { outs ->
                                ins?.copyTo(outs)
                            }
                        }
                    }
                    Toast.makeText(
                        context,
                        context.getString(DesignR.string.geofile_imported, displayName),
                        Toast.LENGTH_LONG,
                    ).show()
                }
                return@rememberLauncherForActivityResult
            }
        }
        Toast.makeText(context, DesignR.string.geofile_import_failed, Toast.LENGTH_LONG).show()
    }

    LaunchedEffect(Unit) {
        val config = withClash { queryOverride(Clash.OverrideSlot.Persist) }
        configuration = config
        unifiedDelay = config.unifiedDelay
        geodataMode = config.geodataMode
        tcpConcurrent = config.tcpConcurrent
        findProcessMode = config.findProcessMode
        snifferEnabled = config.sniffer.enable
        sniffHttpPorts = config.sniffer.sniff.http.ports
        sniffHttpOverrideDest = config.sniffer.sniff.http.overrideDestination
        sniffTlsPorts = config.sniffer.sniff.tls.ports
        sniffTlsOverrideDest = config.sniffer.sniff.tls.overrideDestination
        sniffQuicPorts = config.sniffer.sniff.quic.ports
        sniffQuicOverrideDest = config.sniffer.sniff.quic.overrideDestination
        forceDnsMapping = config.sniffer.forceDnsMapping
        parsePureIp = config.sniffer.parsePureIp
        overrideDestination = config.sniffer.overrideDestination
        forceDomain = config.sniffer.forceDomain
        skipDomain = config.sniffer.skipDomain
        skipSrcAddress = config.sniffer.skipSrcAddress
        skipDstAddress = config.sniffer.skipDstAddress
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
        MetaFeatureSettingsScreen(
            onBackClick = {
                scope.launch {
                    withClash { patchOverride(Clash.OverrideSlot.Persist, config) }
                    navController.popBackStack()
                }
            },
            onResetClick = { showResetDialog = true },
            unifiedDelay = unifiedDelay,
            onUnifiedDelayChange = { unifiedDelay = it; config.unifiedDelay = it },
            geodataMode = geodataMode,
            onGeodataModeChange = { geodataMode = it; config.geodataMode = it },
            tcpConcurrent = tcpConcurrent,
            onTcpConcurrentChange = { tcpConcurrent = it; config.tcpConcurrent = it },
            findProcessMode = findProcessMode,
            onFindProcessModeChange = { findProcessMode = it; config.findProcessMode = it },
            snifferEnabled = snifferEnabled,
            onSnifferEnabledChange = { snifferEnabled = it; config.sniffer.enable = it },
            sniffHttpPorts = sniffHttpPorts,
            onSniffHttpPortsChange = { sniffHttpPorts = it; config.sniffer.sniff.http.ports = it },
            sniffHttpOverrideDest = sniffHttpOverrideDest,
            onSniffHttpOverrideDestChange = { sniffHttpOverrideDest = it; config.sniffer.sniff.http.overrideDestination = it },
            sniffTlsPorts = sniffTlsPorts,
            onSniffTlsPortsChange = { sniffTlsPorts = it; config.sniffer.sniff.tls.ports = it },
            sniffTlsOverrideDest = sniffTlsOverrideDest,
            onSniffTlsOverrideDestChange = { sniffTlsOverrideDest = it; config.sniffer.sniff.tls.overrideDestination = it },
            sniffQuicPorts = sniffQuicPorts,
            onSniffQuicPortsChange = { sniffQuicPorts = it; config.sniffer.sniff.quic.ports = it },
            sniffQuicOverrideDest = sniffQuicOverrideDest,
            onSniffQuicOverrideDestChange = { sniffQuicOverrideDest = it; config.sniffer.sniff.quic.overrideDestination = it },
            forceDnsMapping = forceDnsMapping,
            onForceDnsMappingChange = { forceDnsMapping = it; config.sniffer.forceDnsMapping = it },
            parsePureIp = parsePureIp,
            onParsePureIpChange = { parsePureIp = it; config.sniffer.parsePureIp = it },
            overrideDestination = overrideDestination,
            onOverrideDestinationChange = { overrideDestination = it; config.sniffer.overrideDestination = it },
            forceDomain = forceDomain,
            onForceDomainChange = { forceDomain = it; config.sniffer.forceDomain = it },
            skipDomain = skipDomain,
            onSkipDomainChange = { skipDomain = it; config.sniffer.skipDomain = it },
            skipSrcAddress = skipSrcAddress,
            onSkipSrcAddressChange = { skipSrcAddress = it; config.sniffer.skipSrcAddress = it },
            skipDstAddress = skipDstAddress,
            onSkipDstAddressChange = { skipDstAddress = it; config.sniffer.skipDstAddress = it },
            onImportGeoIp = { pendingImportType = ImportType.GeoIp; filePickerLauncher.launch("*/*") },
            onImportGeoSite = { pendingImportType = ImportType.GeoSite; filePickerLauncher.launch("*/*") },
            onImportCountry = { pendingImportType = ImportType.Country; filePickerLauncher.launch("*/*") },
            onImportAsn = { pendingImportType = ImportType.ASN; filePickerLauncher.launch("*/*") },
        )
    }
}
