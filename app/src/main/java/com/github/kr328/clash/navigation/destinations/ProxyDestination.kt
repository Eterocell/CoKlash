package com.github.kr328.clash.navigation.destinations

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.AppEvent
import com.github.kr328.clash.AppViewModel
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.design.compose.ProxyScreen
import com.github.kr328.clash.design.model.ProxyState
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.util.withClash
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import com.github.kr328.clash.design.R as DesignR

@Composable
fun ProxyDestination(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiStore = remember { UiStore(context) }

    var groupNames by remember { mutableStateOf(emptyList<String>()) }
    var states by remember { mutableStateOf(emptyList<ProxyState>()) }
    var groupProxies by remember { mutableStateOf<Map<Int, List<Proxy>>>(emptyMap()) }
    var groupStates by remember { mutableStateOf<Map<Int, ProxyState>>(emptyMap()) }
    var selectableGroups by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var urlTestingGroups by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var initialPage by remember { mutableStateOf(0) }

    val reloadLock = remember { Semaphore(10) }

    fun reloadGroup(index: Int) {
        scope.launch {
            val group = reloadLock.withPermit {
                withClash { queryProxyGroup(groupNames[index], uiStore.proxySort) }
            }
            val state = states[index]
            state.now = group.now

            groupProxies = groupProxies + (index to group.proxies)
            groupStates = groupStates + (index to state)
            if (group.type == Proxy.Type.Selector) {
                selectableGroups = selectableGroups + index
            }
            urlTestingGroups = urlTestingGroups - index
        }
    }

    LaunchedEffect(Unit) {
        val names = withClash { queryProxyGroupNames(uiStore.proxyExcludeNotSelectable) }
        groupNames = names
        states = List(names.size) { ProxyState("?") }
        initialPage = (names.indexOf(uiStore.proxyLastGroup)).coerceAtLeast(0)
        names.indices.forEach { idx -> reloadGroup(idx) }
    }

    LaunchedEffect(Unit) {
        appViewModel.events.collect { event ->
            when (event) {
                is AppEvent.ProfileLoaded -> {
                    val newNames = withClash { queryProxyGroupNames(uiStore.proxyExcludeNotSelectable) }
                    if (newNames != groupNames) {
                        groupNames = newNames
                        states = List(newNames.size) { ProxyState("?") }
                        groupProxies = emptyMap()
                        groupStates = emptyMap()
                        selectableGroups = emptySet()
                        newNames.indices.forEach { idx -> reloadGroup(idx) }
                    }
                }
                else -> {}
            }
        }
    }

    ProxyScreen(
        onBackClick = { navController.popBackStack() },
        groupNames = groupNames,
        groupProxies = groupProxies,
        groupStates = groupStates,
        selectableGroups = selectableGroups,
        onProxySelect = { idx, name ->
            scope.launch {
                withClash { patchSelector(groupNames[idx], name) }
                states[idx].now = name
                groupStates = groupStates + (idx to states[idx])
            }
        },
        onUrlTest = { idx ->
            urlTestingGroups = urlTestingGroups + idx
            scope.launch {
                withClash { healthCheck(groupNames[idx]) }
                reloadGroup(idx)
            }
        },
        urlTestingGroups = urlTestingGroups,
        onMenuClick = {
            Toast.makeText(context, DesignR.string.mode_switch_tips, Toast.LENGTH_LONG).show()
        },
        initialPage = initialPage,
        onPageChanged = { page ->
            if (page in groupNames.indices) {
                uiStore.proxyLastGroup = groupNames[page]
            }
        },
    )
}
