package com.github.kr328.clash.navigation.destinations

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.AppEvent
import com.github.kr328.clash.AppViewModel
import com.github.kr328.clash.LogcatService
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.core.util.trafficTotal
import com.github.kr328.clash.design.compose.MainScreen
import com.github.kr328.clash.navigation.Help
import com.github.kr328.clash.navigation.Logcat
import com.github.kr328.clash.navigation.Logs
import com.github.kr328.clash.navigation.Profiles
import com.github.kr328.clash.navigation.Providers
import com.github.kr328.clash.navigation.Proxy
import com.github.kr328.clash.navigation.Settings
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withClash
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.github.kr328.clash.design.R as DesignR

@Composable
fun MainDestination(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val clashRunning by appViewModel.clashRunning.collectAsState()

    var profileName by remember { mutableStateOf<String?>(null) }
    var mode by remember { mutableStateOf("") }
    var forwarded by remember { mutableStateOf("") }
    var hasProviders by remember { mutableStateOf(false) }

    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            scope.launch { context.startClashService() }
        }
    }

    fun fetchState() {
        scope.launch {
            val state = withClash { queryTunnelState() }
            val providers = withClash { queryProviders() }
            mode = when (state.mode) {
                TunnelState.Mode.Direct -> context.getString(DesignR.string.direct_mode)
                TunnelState.Mode.Global -> context.getString(DesignR.string.global_mode)
                TunnelState.Mode.Rule -> context.getString(DesignR.string.rule_mode)
                else -> context.getString(DesignR.string.rule_mode)
            }
            hasProviders = providers.isNotEmpty()
            withProfile { profileName = queryActive()?.name }
        }
    }

    LaunchedEffect(Unit) { fetchState() }

    LaunchedEffect(clashRunning) {
        if (clashRunning) {
            while (isActive) {
                withClash { forwarded = queryTrafficTotal().trafficTotal() }
                delay(1000)
            }
        } else {
            forwarded = ""
        }
    }

    LaunchedEffect(Unit) {
        appViewModel.events.collect { event ->
            when (event) {
                is AppEvent.ClashStarted,
                is AppEvent.ClashStopped,
                is AppEvent.ProfileLoaded,
                is AppEvent.ProfileChanged,
                is AppEvent.ServiceRecreated -> fetchState()
                else -> {}
            }
        }
    }

    MainScreen(
        clashRunning = clashRunning,
        profileName = profileName,
        mode = mode,
        forwarded = forwarded,
        hasProviders = hasProviders,
        onToggleClick = {
            scope.launch {
                if (clashRunning) {
                    context.stopClashService()
                } else {
                    val active = withProfile { queryActive() }
                    if (active == null || !active.imported) {
                        Toast.makeText(context, DesignR.string.no_profile_selected, Toast.LENGTH_LONG).show()
                        navController.navigate(Profiles)
                        return@launch
                    }
                    val vpnRequest = context.startClashService()
                    if (vpnRequest != null) {
                        vpnPermissionLauncher.launch(vpnRequest)
                    }
                }
            }
        },
        onProxyClick = { navController.navigate(Proxy) },
        onProfilesClick = { navController.navigate(Profiles) },
        onProvidersClick = { navController.navigate(Providers) },
        onLogsClick = {
            if (LogcatService.running) {
                navController.navigate(Logcat())
            } else {
                navController.navigate(Logs)
            }
        },
        onSettingsClick = { navController.navigate(Settings) },
        onHelpClick = { navController.navigate(Help) },
        onAboutClick = { /* About dialog handled in screen */ },
    )
}
