package com.github.kr328.clash

import android.Manifest.permission.INTERNET
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.AccessControlScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.model.AppInfo
import com.github.kr328.clash.design.model.AppInfoSort
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.util.toAppInfo
import com.github.kr328.clash.service.store.ServiceStore
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccessControlActivity : BaseComposeActivity() {
    private lateinit var serviceStore: ServiceStore
    private val selected = mutableSetOf<String>()

    private var apps by mutableStateOf<List<AppInfo>>(emptyList())
    private var selectedPackages by mutableStateOf<Set<String>>(emptySet())
    private var searchQuery by mutableStateOf("")
    private var isSearchActive by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceStore = ServiceStore(this)

        lifecycleScope.launch {
            val initial = withContext(Dispatchers.IO) {
                serviceStore.accessControlPackages
            }
            selected.addAll(initial)
            selectedPackages = selected.toSet()
            apps = loadApps()
        }

        setContent {
            CoKlashTheme {
                AccessControlScreen(
                    onBackClick = { finish() },
                    apps = apps,
                    selectedPackages = selectedPackages,
                    onTogglePackage = { pkg -> togglePackage(pkg) },
                    onSelectAll = { selectAll() },
                    onSelectNone = { selectNone() },
                    onSelectInvert = { selectInvert() },
                    onImport = { importFromClipboard() },
                    onExport = { exportToClipboard() },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    isSearchActive = isSearchActive,
                    onSearchActiveChange = { isSearchActive = it; if (!it) searchQuery = "" },
                )
            }
        }
    }

    override fun onDestroy() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val changed = selected != serviceStore.accessControlPackages
                serviceStore.accessControlPackages = selected
                if (clashRunning && changed) {
                    stopClashService()
                    while (clashRunning) {
                        delay(200)
                    }
                    startClashService()
                }
            }
        }
        super.onDestroy()
    }

    private fun togglePackage(pkg: String) {
        if (pkg in selected) {
            selected.remove(pkg)
        } else {
            selected.add(pkg)
        }
        selectedPackages = selected.toSet()
    }

    private fun selectAll() {
        selected.clear()
        selected.addAll(apps.map(AppInfo::packageName))
        selectedPackages = selected.toSet()
    }

    private fun selectNone() {
        selected.clear()
        selectedPackages = selected.toSet()
    }

    private fun selectInvert() {
        val all = apps.map(AppInfo::packageName).toSet() - selected
        selected.clear()
        selected.addAll(all)
        selectedPackages = selected.toSet()
    }

    private fun importFromClipboard() {
        val clipboard = getSystemService<ClipboardManager>()
        val data = clipboard?.primaryClip
        if (data != null && data.itemCount > 0) {
            val packages = data.getItemAt(0).text.split("\n").toSet()
            val all = apps.map(AppInfo::packageName).intersect(packages)
            selected.clear()
            selected.addAll(all)
            selectedPackages = selected.toSet()
        }
    }

    private fun exportToClipboard() {
        val clipboard = getSystemService<ClipboardManager>()
        val data = ClipData.newPlainText("packages", selected.joinToString("\n"))
        clipboard?.setPrimaryClip(data)
        Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show()
    }

    private suspend fun loadApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val uiStore = UiStore(this@AccessControlActivity)
        val reverse = uiStore.accessControlReverse
        val sort = uiStore.accessControlSort
        val systemApp = uiStore.accessControlSystemApp

        val base = compareByDescending<AppInfo> { it.packageName in selected }
        val comparator = if (reverse) base.thenDescending(sort) else base.then(sort)

        val pm = packageManager
        val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)

        packages
            .asSequence()
            .filter { it.packageName != packageName }
            .filter { it.applicationInfo != null }
            .filter {
                it.requestedPermissions?.contains(INTERNET) == true ||
                    it.applicationInfo!!.uid < android.os.Process.FIRST_APPLICATION_UID
            }
            .filter { systemApp || !it.isSystemApp }
            .map { it.toAppInfo(pm) }
            .sortedWith(comparator)
            .toList()
    }

    private val PackageInfo.isSystemApp: Boolean
        get() = applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) != 0
}
