package com.github.kr328.clash.navigation.destinations

import android.Manifest.permission.INTERNET
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.navigation.NavHostController
import com.github.kr328.clash.AppViewModel
import com.github.kr328.clash.design.compose.AccessControlScreen
import com.github.kr328.clash.design.model.AppInfo
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.util.toAppInfo
import com.github.kr328.clash.service.store.ServiceStore
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.kr328.clash.design.R as DesignR

@Composable
fun AccessControlDestination(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val serviceStore = remember { ServiceStore(context) }
    val uiStore = remember { UiStore(context) }
    val clashRunning by appViewModel.clashRunning.collectAsState()

    val selected = remember { mutableSetOf<String>() }
    var apps by remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    var selectedPackages by remember { mutableStateOf<Set<String>>(emptySet()) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var initialLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val initial = withContext(Dispatchers.IO) {
            serviceStore.accessControlPackages
        }
        selected.addAll(initial)
        selectedPackages = selected.toSet()

        apps = withContext(Dispatchers.IO) {
            val reverse = uiStore.accessControlReverse
            val sort = uiStore.accessControlSort
            val systemApp = uiStore.accessControlSystemApp

            val base = compareByDescending<AppInfo> { it.packageName in selected }
            val comparator = if (reverse) base.thenDescending(sort) else base.then(sort)

            val pm = context.packageManager
            val packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)

            packages
                .asSequence()
                .filter { it.packageName != context.packageName }
                .filter { it.applicationInfo != null }
                .filter {
                    it.requestedPermissions?.contains(INTERNET) == true ||
                        it.applicationInfo!!.uid < android.os.Process.FIRST_APPLICATION_UID
                }
                .filter { systemApp || it.applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) == 0 }
                .map { it.toAppInfo(pm) }
                .sortedWith(comparator)
                .toList()
        }
        initialLoaded = true
    }

    DisposableEffect(Unit) {
        onDispose {
            scope.launch {
                withContext(Dispatchers.IO) {
                    val changed = selected != serviceStore.accessControlPackages
                    serviceStore.accessControlPackages = selected
                    if (clashRunning && changed) {
                        context.stopClashService()
                        delay(500)
                        context.startClashService()
                    }
                }
            }
        }
    }

    if (initialLoaded) {
        AccessControlScreen(
            onBackClick = { navController.popBackStack() },
            apps = apps,
            selectedPackages = selectedPackages,
            onTogglePackage = { pkg ->
                if (pkg in selected) selected.remove(pkg) else selected.add(pkg)
                selectedPackages = selected.toSet()
            },
            onSelectAll = {
                selected.clear()
                selected.addAll(apps.map(AppInfo::packageName))
                selectedPackages = selected.toSet()
            },
            onSelectNone = {
                selected.clear()
                selectedPackages = selected.toSet()
            },
            onSelectInvert = {
                val all = apps.map(AppInfo::packageName).toSet() - selected
                selected.clear()
                selected.addAll(all)
                selectedPackages = selected.toSet()
            },
            onImport = {
                val clipboard = context.getSystemService<ClipboardManager>()
                val data = clipboard?.primaryClip
                if (data != null && data.itemCount > 0) {
                    val packages = data.getItemAt(0).text.split("\n").toSet()
                    val all = apps.map(AppInfo::packageName).intersect(packages)
                    selected.clear()
                    selected.addAll(all)
                    selectedPackages = selected.toSet()
                }
            },
            onExport = {
                val clipboard = context.getSystemService<ClipboardManager>()
                val data = ClipData.newPlainText("packages", selected.joinToString("\n"))
                clipboard?.setPrimaryClip(data)
                Toast.makeText(context, DesignR.string.copied, Toast.LENGTH_SHORT).show()
            },
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            isSearchActive = isSearchActive,
            onSearchActiveChange = { isSearchActive = it; if (!it) searchQuery = "" },
        )
    }
}
