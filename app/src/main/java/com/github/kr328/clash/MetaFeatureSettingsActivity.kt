package com.github.kr328.clash

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.core.model.ConfigurationOverride
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.MetaFeatureSettingsScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.util.clashDir
import com.github.kr328.clash.util.withClash
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MetaFeatureSettingsActivity : BaseComposeActivity() {
    private lateinit var configuration: ConfigurationOverride
    private var pendingImportType: ImportType? = null

    private var unifiedDelay by mutableStateOf<Boolean?>(null)
    private var geodataMode by mutableStateOf<Boolean?>(null)
    private var tcpConcurrent by mutableStateOf<Boolean?>(null)
    private var findProcessMode by mutableStateOf<ConfigurationOverride.FindProcessMode?>(null)
    private var snifferEnabled by mutableStateOf<Boolean?>(null)
    private var sniffHttpPorts by mutableStateOf<List<String>?>(null)
    private var sniffHttpOverrideDest by mutableStateOf<Boolean?>(null)
    private var sniffTlsPorts by mutableStateOf<List<String>?>(null)
    private var sniffTlsOverrideDest by mutableStateOf<Boolean?>(null)
    private var sniffQuicPorts by mutableStateOf<List<String>?>(null)
    private var sniffQuicOverrideDest by mutableStateOf<Boolean?>(null)
    private var forceDnsMapping by mutableStateOf<Boolean?>(null)
    private var parsePureIp by mutableStateOf<Boolean?>(null)
    private var overrideDestination by mutableStateOf<Boolean?>(null)
    private var forceDomain by mutableStateOf<List<String>?>(null)
    private var skipDomain by mutableStateOf<List<String>?>(null)
    private var skipSrcAddress by mutableStateOf<List<String>?>(null)
    private var skipDstAddress by mutableStateOf<List<String>?>(null)

    private enum class ImportType { GeoIp, GeoSite, Country, ASN }

    private val validDatabaseExtensions = listOf(".metadb", ".db", ".dat", ".mmdb")

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val type = pendingImportType ?: return@registerForActivityResult
            pendingImportType = null
            if (uri != null) importGeoFile(uri, type)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            configuration = withClash { queryOverride(Clash.OverrideSlot.Persist) }
            loadState()
            setContent {
                CoKlashTheme {
                    MetaFeatureSettingsScreen(
                        onBackClick = { saveAndFinish() },
                        onResetClick = { showResetConfirmDialog() },
                        unifiedDelay = unifiedDelay,
                        onUnifiedDelayChange = { unifiedDelay = it; configuration.unifiedDelay = it },
                        geodataMode = geodataMode,
                        onGeodataModeChange = { geodataMode = it; configuration.geodataMode = it },
                        tcpConcurrent = tcpConcurrent,
                        onTcpConcurrentChange = { tcpConcurrent = it; configuration.tcpConcurrent = it },
                        findProcessMode = findProcessMode,
                        onFindProcessModeChange = { findProcessMode = it; configuration.findProcessMode = it },
                        snifferEnabled = snifferEnabled,
                        onSnifferEnabledChange = { snifferEnabled = it; configuration.sniffer.enable = it },
                        sniffHttpPorts = sniffHttpPorts,
                        onSniffHttpPortsChange = { sniffHttpPorts = it; configuration.sniffer.sniff.http.ports = it },
                        sniffHttpOverrideDest = sniffHttpOverrideDest,
                        onSniffHttpOverrideDestChange = { sniffHttpOverrideDest = it; configuration.sniffer.sniff.http.overrideDestination = it },
                        sniffTlsPorts = sniffTlsPorts,
                        onSniffTlsPortsChange = { sniffTlsPorts = it; configuration.sniffer.sniff.tls.ports = it },
                        sniffTlsOverrideDest = sniffTlsOverrideDest,
                        onSniffTlsOverrideDestChange = { sniffTlsOverrideDest = it; configuration.sniffer.sniff.tls.overrideDestination = it },
                        sniffQuicPorts = sniffQuicPorts,
                        onSniffQuicPortsChange = { sniffQuicPorts = it; configuration.sniffer.sniff.quic.ports = it },
                        sniffQuicOverrideDest = sniffQuicOverrideDest,
                        onSniffQuicOverrideDestChange = { sniffQuicOverrideDest = it; configuration.sniffer.sniff.quic.overrideDestination = it },
                        forceDnsMapping = forceDnsMapping,
                        onForceDnsMappingChange = { forceDnsMapping = it; configuration.sniffer.forceDnsMapping = it },
                        parsePureIp = parsePureIp,
                        onParsePureIpChange = { parsePureIp = it; configuration.sniffer.parsePureIp = it },
                        overrideDestination = overrideDestination,
                        onOverrideDestinationChange = { overrideDestination = it; configuration.sniffer.overrideDestination = it },
                        forceDomain = forceDomain,
                        onForceDomainChange = { forceDomain = it; configuration.sniffer.forceDomain = it },
                        skipDomain = skipDomain,
                        onSkipDomainChange = { skipDomain = it; configuration.sniffer.skipDomain = it },
                        skipSrcAddress = skipSrcAddress,
                        onSkipSrcAddressChange = { skipSrcAddress = it; configuration.sniffer.skipSrcAddress = it },
                        skipDstAddress = skipDstAddress,
                        onSkipDstAddressChange = { skipDstAddress = it; configuration.sniffer.skipDstAddress = it },
                        onImportGeoIp = { launchFilePicker(ImportType.GeoIp) },
                        onImportGeoSite = { launchFilePicker(ImportType.GeoSite) },
                        onImportCountry = { launchFilePicker(ImportType.Country) },
                        onImportAsn = { launchFilePicker(ImportType.ASN) },
                    )
                }
            }
        }
    }

    private fun loadState() {
        unifiedDelay = configuration.unifiedDelay
        geodataMode = configuration.geodataMode
        tcpConcurrent = configuration.tcpConcurrent
        findProcessMode = configuration.findProcessMode
        snifferEnabled = configuration.sniffer.enable
        sniffHttpPorts = configuration.sniffer.sniff.http.ports
        sniffHttpOverrideDest = configuration.sniffer.sniff.http.overrideDestination
        sniffTlsPorts = configuration.sniffer.sniff.tls.ports
        sniffTlsOverrideDest = configuration.sniffer.sniff.tls.overrideDestination
        sniffQuicPorts = configuration.sniffer.sniff.quic.ports
        sniffQuicOverrideDest = configuration.sniffer.sniff.quic.overrideDestination
        forceDnsMapping = configuration.sniffer.forceDnsMapping
        parsePureIp = configuration.sniffer.parsePureIp
        overrideDestination = configuration.sniffer.overrideDestination
        forceDomain = configuration.sniffer.forceDomain
        skipDomain = configuration.sniffer.skipDomain
        skipSrcAddress = configuration.sniffer.skipSrcAddress
        skipDstAddress = configuration.sniffer.skipDstAddress
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

    private fun launchFilePicker(type: ImportType) {
        pendingImportType = type
        filePickerLauncher.launch("*/*")
    }

    private fun importGeoFile(uri: Uri, importType: ImportType) {
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val displayName: String =
                    if (columnIndex != -1) it.getString(columnIndex) else ""
                val ext = "." + displayName.substringAfterLast(".")

                if (!validDatabaseExtensions.contains(ext)) {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.geofile_unknown_db_format)
                        .setMessage(
                            getString(
                                R.string.geofile_unknown_db_format_message,
                                validDatabaseExtensions.joinToString("/"),
                            ),
                        ).setPositiveButton("OK") { _, _ -> }
                        .show()
                    return
                }

                val outputFileName = when (importType) {
                    ImportType.GeoIp -> "geoip$ext"
                    ImportType.GeoSite -> "geosite$ext"
                    ImportType.Country -> "country$ext"
                    ImportType.ASN -> "ASN$ext"
                }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val outputFile = File(clashDir, outputFileName)
                        contentResolver.openInputStream(uri).use { ins ->
                            FileOutputStream(outputFile).use { outs ->
                                ins?.copyTo(outs)
                            }
                        }
                    }
                    Toast.makeText(
                        this@MetaFeatureSettingsActivity,
                        getString(R.string.geofile_imported, displayName),
                        Toast.LENGTH_LONG,
                    ).show()
                }
                return
            }
        }
        Toast.makeText(this, R.string.geofile_import_failed, Toast.LENGTH_LONG).show()
    }
}