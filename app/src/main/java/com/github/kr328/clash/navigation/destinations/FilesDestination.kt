package com.github.kr328.clash.navigation.destinations

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.common.util.grantPermissions
import com.github.kr328.clash.design.compose.FilesScreen
import com.github.kr328.clash.design.model.File
import com.github.kr328.clash.remote.FilesClient
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.launch
import java.util.Stack
import java.util.UUID

@Composable
fun FilesDestination(navController: NavHostController, uuid: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profileUuid = remember { UUID.fromString(uuid) }

    var files by remember { mutableStateOf<List<File>>(emptyList()) }
    var currentInBaseDir by remember { mutableStateOf(true) }
    var configEditable by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }

    val client = remember { FilesClient(context) }
    val stack = remember { Stack<String>() }
    val root = remember { uuid }

    var pendingImportFile by remember { mutableStateOf<File?>(null) }
    var pendingExportFile by remember { mutableStateOf<File?>(null) }

    val openFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { }

    val importFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val file = pendingImportFile
                    if (file == null) {
                        client.importDocument(stack.lastOrNull() ?: root, uri, "imported")
                    } else {
                        client.copyDocument(file.id, uri)
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
                val documentId = stack.lastOrNull() ?: root
                files = client.list(documentId)
                currentInBaseDir = stack.empty()
            }
        }
        pendingImportFile = null
    }

    val exportFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain"),
    ) { uri ->
        val file = pendingExportFile ?: return@rememberLauncherForActivityResult
        pendingExportFile = null
        if (uri != null) {
            scope.launch {
                try {
                    client.copyDocument(uri, file.id)
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    suspend fun fetchFiles() {
        val documentId = stack.lastOrNull() ?: root
        val fileList = if (stack.empty()) {
            val list = client.list(documentId)
            val config = list.firstOrNull { it.id.endsWith("config.yaml") }
            if (config == null || config.size > 0) list else listOf(config)
        } else {
            client.list(documentId)
        }
        files = fileList
        currentInBaseDir = stack.empty()
    }

    LaunchedEffect(profileUuid) {
        val profile = withProfile { queryByUUID(profileUuid) }
        if (profile == null) {
            navController.popBackStack()
            return@LaunchedEffect
        }
        configEditable = profile.type != Profile.Type.Url
        fetchFiles()
        initialized = true
    }

    if (initialized) {
        FilesScreen(
            onBackClick = {
                if (stack.empty()) {
                    navController.popBackStack()
                } else {
                    stack.pop()
                    scope.launch { fetchFiles() }
                }
            },
            files = files,
            currentInBaseDir = currentInBaseDir,
            configurationEditable = configEditable,
            onFileClick = { file ->
                if (file.isDirectory) {
                    stack.push(file.id)
                    scope.launch { fetchFiles() }
                } else {
                    openFileLauncher.launch(
                        Intent(Intent.ACTION_VIEW)
                            .setDataAndType(client.buildDocumentUri(file.id), "text/plain")
                            .grantPermissions(),
                    )
                }
            },
            onFileLongClick = { file ->
                pendingExportFile = file
                exportFileLauncher.launch(file.name)
            },
            onImportNew = {
                pendingImportFile = null
                importFileLauncher.launch("*/*")
            },
        )
    }
}
