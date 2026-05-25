package com.github.kr328.clash.navigation.destinations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.design.compose.LogsScreen
import com.github.kr328.clash.design.model.LogFile
import com.github.kr328.clash.navigation.Logcat
import com.github.kr328.clash.util.logsDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LogsDestination(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var logs by remember { mutableStateOf<List<LogFile>>(emptyList()) }

    fun loadFiles() {
        scope.launch {
            logs = withContext(Dispatchers.IO) {
                val list = context.logsDir.listFiles()?.toList() ?: emptyList()
                list.mapNotNull { LogFile.parseFromFileName(it.name) }
            }
        }
    }

    LaunchedEffect(Unit) { loadFiles() }

    LogsScreen(
        onBackClick = { navController.popBackStack() },
        onStartLogcat = {
            navController.popBackStack()
            navController.navigate(Logcat())
        },
        onDeleteAll = {
            scope.launch {
                withContext(Dispatchers.IO) {
                    context.logsDir.deleteRecursively()
                }
                loadFiles()
            }
        },
        logs = logs,
        onLogFileClick = { file ->
            navController.navigate(Logcat(file.fileName))
        },
    )
}
