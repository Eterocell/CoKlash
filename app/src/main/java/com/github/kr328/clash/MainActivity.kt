package com.github.kr328.clash

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.core.bridge.Bridge
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.core.util.trafficTotal
import com.github.kr328.clash.design.compose.AboutDialogContent
import com.github.kr328.clash.design.compose.MainScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withClash
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.kr328.clash.design.R as DesignR

class MainActivity : BaseComposeActivity() {
    private var isClashRunning by mutableStateOf(false)
    private var profileName by mutableStateOf<String?>(null)
    private var mode by mutableStateOf("")
    private var forwarded by mutableStateOf("")
    private var hasProviders by mutableStateOf(false)

    private var trafficJob: Job? = null

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            lifecycleScope.launch { startClashService() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNecessary()
        setupShortcuts()

        setContent {
            CoKlashTheme {
                MainScreen(
                    clashRunning = isClashRunning,
                    profileName = profileName,
                    mode = mode,
                    forwarded = forwarded,
                    hasProviders = hasProviders,
                    onToggleClick = { toggleClash() },
                    onProxyClick = { startActivity(ProxyActivity::class.intent) },
                    onProfilesClick = { startActivity(ProfilesActivity::class.intent) },
                    onProvidersClick = { startActivity(ProvidersActivity::class.intent) },
                    onLogsClick = {
                        if (LogcatService.running) {
                            startActivity(LogcatActivity::class.intent)
                        } else {
                            startActivity(LogsActivity::class.intent)
                        }
                    },
                    onSettingsClick = { startActivity(SettingsActivity::class.intent) },
                    onHelpClick = { startActivity(HelpActivity::class.intent) },
                    onAboutClick = { showAbout() },
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        fetchState()
        startTrafficPolling()
    }

    override fun onStop() {
        super.onStop()
        trafficJob?.cancel()
        trafficJob = null
    }

    override fun onStarted() {
        super.onStarted()
        fetchState()
        startTrafficPolling()
    }

    override fun onStopped(cause: String?) {
        super.onStopped(cause)
        fetchState()
        trafficJob?.cancel()
        trafficJob = null
    }

    override fun onProfileLoaded() {
        super.onProfileLoaded()
        fetchState()
    }

    override fun onProfileChanged() {
        super.onProfileChanged()
        fetchState()
    }

    override fun onServiceRecreated() {
        super.onServiceRecreated()
        fetchState()
    }

    private fun fetchState() {
        lifecycleScope.launch {
            isClashRunning = clashRunning
            val state = withClash { queryTunnelState() }
            val providers = withClash { queryProviders() }
            mode = when (state.mode) {
                TunnelState.Mode.Direct -> getString(DesignR.string.direct_mode)
                TunnelState.Mode.Global -> getString(DesignR.string.global_mode)
                TunnelState.Mode.Rule -> getString(DesignR.string.rule_mode)
                else -> getString(DesignR.string.rule_mode)
            }
            hasProviders = providers.isNotEmpty()
            withProfile { profileName = queryActive()?.name }
        }
    }

    private fun startTrafficPolling() {
        if (trafficJob?.isActive == true) return
        trafficJob = lifecycleScope.launch {
            while (isActive && clashRunning) {
                withClash { forwarded = queryTrafficTotal().trafficTotal() }
                delay(1000)
            }
        }
    }

    private fun toggleClash() {
        lifecycleScope.launch {
            if (clashRunning) {
                stopClashService()
            } else {
                requestNotificationPermissionIfNecessary()
                val active = withProfile { queryActive() }
                if (active == null || !active.imported) {
                    Toast.makeText(this@MainActivity, DesignR.string.no_profile_selected, Toast.LENGTH_LONG).show()
                    startActivity(ProfilesActivity::class.intent)
                    return@launch
                }
                val vpnRequest = startClashService()
                if (vpnRequest != null) {
                    vpnPermissionLauncher.launch(vpnRequest)
                }
            }
        }
    }

    private fun showAbout() {
        lifecycleScope.launch {
            val versionName = withContext(Dispatchers.IO) {
                packageManager.getPackageInfo(packageName, 0).versionName +
                    "\n" + Bridge.nativeCoreVersion().replace("_", "-")
            }
            val composeView = androidx.compose.ui.platform.ComposeView(this@MainActivity).apply {
                setContent {
                    CoKlashTheme {
                        AboutDialogContent(versionName = versionName)
                    }
                }
            }
            androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
                .setView(composeView)
                .show()
        }
    }

    private fun requestNotificationPermissionIfNecessary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0,
                )
            }
        }
    }

    private fun setupShortcuts() {
        if (uiStore.hideAppIcon) return
        val flags = Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
            Intent.FLAG_ACTIVITY_NO_ANIMATION

        val toggle = ShortcutInfoCompat.Builder(this, "toggle_clash")
            .setShortLabel(getString(DesignR.string.shortcut_toggle_short))
            .setLongLabel(getString(DesignR.string.shortcut_toggle_long))
            .setIcon(IconCompat.createWithResource(this, R.drawable.ic_toggle_all))
            .setIntent(Intent(Intents.ACTION_TOGGLE_CLASH)
                .setClassName(this, ExternalControlActivity::class.java.name)
                .addFlags(flags))
            .setRank(0).build()

        val start = ShortcutInfoCompat.Builder(this, "start_clash")
            .setShortLabel(getString(DesignR.string.shortcut_start_short))
            .setLongLabel(getString(DesignR.string.shortcut_start_long))
            .setIcon(IconCompat.createWithResource(this, R.drawable.ic_toggle_on))
            .setIntent(Intent(Intents.ACTION_START_CLASH)
                .setClassName(this, ExternalControlActivity::class.java.name)
                .addFlags(flags))
            .setRank(1).build()

        val stop = ShortcutInfoCompat.Builder(this, "stop_clash")
            .setShortLabel(getString(DesignR.string.shortcut_stop_short))
            .setLongLabel(getString(DesignR.string.shortcut_stop_long))
            .setIcon(IconCompat.createWithResource(this, R.drawable.ic_toggle_off))
            .setIntent(Intent(Intents.ACTION_STOP_CLASH)
                .setClassName(this, ExternalControlActivity::class.java.name)
                .addFlags(flags))
            .setRank(2).build()

        ShortcutManagerCompat.setDynamicShortcuts(this, listOf(toggle, start, stop))
    }
}
