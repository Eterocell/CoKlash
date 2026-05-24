package com.github.kr328.clash

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.ProxyScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.model.ProxyState
import com.github.kr328.clash.util.withClash
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class ProxyActivity : BaseComposeActivity() {
    private var groupNames = emptyList<String>()
    private var states = emptyList<ProxyState>()

    private var groupProxies by mutableStateOf<Map<Int, List<Proxy>>>(emptyMap())
    private var groupStates by mutableStateOf<Map<Int, ProxyState>>(emptyMap())
    private var selectableGroups by mutableStateOf<Set<Int>>(emptySet())
    private var urlTestingGroups by mutableStateOf<Set<Int>>(emptySet())
    private var initialPage by mutableStateOf(0)

    private val reloadLock = Semaphore(10)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            groupNames = withClash { queryProxyGroupNames(uiStore.proxyExcludeNotSelectable) }
            states = List(groupNames.size) { ProxyState("?") }
            initialPage = (groupNames.indexOf(uiStore.proxyLastGroup)).coerceAtLeast(0)

            reloadAll()
        }

        setContent {
            CoKlashTheme {
                ProxyScreen(
                    onBackClick = { finish() },
                    groupNames = groupNames,
                    groupProxies = groupProxies,
                    groupStates = groupStates,
                    selectableGroups = selectableGroups,
                    onProxySelect = { idx, name -> selectProxy(idx, name) },
                    onUrlTest = { idx -> urlTest(idx) },
                    urlTestingGroups = urlTestingGroups,
                    onMenuClick = { showProxyMenu() },
                    initialPage = initialPage,
                    onPageChanged = { page ->
                        if (page in groupNames.indices) {
                            uiStore.proxyLastGroup = groupNames[page]
                        }
                    },
                )
            }
        }
    }

    override fun onProfileLoaded() {
        super.onProfileLoaded()
        lifecycleScope.launch {
            val newNames = withClash { queryProxyGroupNames(uiStore.proxyExcludeNotSelectable) }
            if (newNames != groupNames) {
                startActivity(ProxyActivity::class.intent)
                finish()
            }
        }
    }

    private fun reloadAll() {
        groupNames.indices.forEach { idx -> reloadGroup(idx) }
    }

    private fun reloadGroup(index: Int) {
        lifecycleScope.launch {
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

    private fun selectProxy(groupIndex: Int, proxyName: String) {
        lifecycleScope.launch {
            withClash { patchSelector(groupNames[groupIndex], proxyName) }
            states[groupIndex].now = proxyName
            groupStates = groupStates + (groupIndex to states[groupIndex])
        }
    }

    private fun urlTest(groupIndex: Int) {
        urlTestingGroups = urlTestingGroups + groupIndex
        lifecycleScope.launch {
            withClash { healthCheck(groupNames[groupIndex]) }
            reloadGroup(groupIndex)
        }
    }

    private fun showProxyMenu() {
        val items = mutableListOf<Pair<String, () -> Unit>>()
        items.add(getString(R.string.mode_switch_tips) to {})

        val modes = listOf(
            TunnelState.Mode.Direct to getString(R.string.direct_mode),
            TunnelState.Mode.Global to getString(R.string.global_mode),
            TunnelState.Mode.Rule to getString(R.string.rule_mode),
        )
        modes.forEach { (mode, label) ->
            items.add(label to { patchMode(mode) })
        }

        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(R.string.mode)
            .setItems(modes.map { it.second }.toTypedArray()) { _, which ->
                patchMode(modes[which].first)
            }
            .show()
    }

    private fun patchMode(mode: TunnelState.Mode) {
        Toast.makeText(this, R.string.mode_switch_tips, Toast.LENGTH_LONG).show()
        lifecycleScope.launch {
            withClash {
                val o = queryOverride(Clash.OverrideSlot.Session)
                o.mode = mode
                patchOverride(Clash.OverrideSlot.Session, o)
            }
        }
    }
}
