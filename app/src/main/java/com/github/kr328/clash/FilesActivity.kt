@file:Suppress("BlockingMethodInNonBlockingContext")

package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.util.grantPermissions
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.common.util.uuid
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.FilesScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.dialog.requestModelTextInput
import com.github.kr328.clash.design.model.File
import com.github.kr328.clash.design.util.ValidatorFileName
import com.github.kr328.clash.remote.FilesClient
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.fileName
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Stack
import java.util.concurrent.TimeUnit

class FilesActivity : BaseComposeActivity() {
    private lateinit var client: FilesClient
    private val stack = Stack<String>()
    private var root = ""
    private var configEditable = false

    private var files by mutableStateOf<List<File>>(emptyList())
    private var currentInBaseDir by mutableStateOf(true)

    private var pendingImportFile: File? = null

    private val openFileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { }

    private val importFileLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                try {
                    val file = pendingImportFile
                    if (file == null) {
                        val name = requestFileName(uri.fileName ?: "File")
                        client.importDocument(stack.last(), uri, name)
                    } else {
                        client.copyDocument(file.id, uri)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@FilesActivity, e.message, Toast.LENGTH_LONG).show()
                }
                fetchFiles()
            }
        }
        pendingImportFile = null
    }

    private val exportFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain"),
    ) { uri ->
        val file = pendingExportFile ?: return@registerForActivityResult
        pendingExportFile = null
        if (uri != null) {
            lifecycleScope.launch {
                try {
                    client.copyDocument(uri, file.id)
                } catch (e: Exception) {
                    Toast.makeText(this@FilesActivity, e.message, Toast.LENGTH_LONG).show()
                }
                fetchFiles()
            }
        }
    }

    private var pendingExportFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uuid = intent.uuid ?: run { finish(); return }

        lifecycleScope.launch {
            val profile = withProfile { queryByUUID(uuid) } ?: run { finish(); return@launch }
            root = uuid.toString()
            configEditable = profile.type != Profile.Type.Url
            client = FilesClient(this@FilesActivity)
            fetchFiles()

            setContent {
                CoKlashTheme {
                    FilesScreen(
                        onBackClick = { handleBack() },
                        files = files,
                        currentInBaseDir = currentInBaseDir,
                        configurationEditable = configEditable,
                        onFileClick = { file -> handleFileClick(file) },
                        onFileLongClick = { file -> showFileMenu(file) },
                        onImportNew = { importNewFile() },
                    )
                }
            }
        }

        onBackPressedDispatcher.addCallback { handleBack() }
    }

    override fun onStart() {
        super.onStart()
        if (::client.isInitialized) {
            lifecycleScope.launch { fetchFiles() }
        }
    }

    private fun handleBack() {
        if (stack.empty()) {
            finish()
        } else {
            stack.pop()
            lifecycleScope.launch { fetchFiles() }
        }
    }

    private fun handleFileClick(file: File) {
        if (file.isDirectory) {
            stack.push(file.id)
            lifecycleScope.launch { fetchFiles() }
        } else {
            openFileLauncher.launch(
                Intent(Intent.ACTION_VIEW)
                    .setDataAndType(client.buildDocumentUri(file.id), "text/plain")
                    .grantPermissions(),
            )
        }
    }

    private fun showFileMenu(file: File) {
        val items = mutableListOf<Pair<String, () -> Unit>>()
        if (configEditable) {
            items.add(getString(R.string.rename) to { renameFile(file) })
            items.add(getString(R.string.import_) to { importOverFile(file) })
        }
        items.add(getString(R.string.export) to { exportFile(file) })
        if (configEditable) {
            items.add(getString(R.string.delete) to { deleteFile(file) })
        }

        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(file.name)
            .setItems(items.map { it.first }.toTypedArray()) { _, which ->
                items[which].second()
            }
            .show()
    }

    private fun renameFile(file: File) {
        lifecycleScope.launch {
            try {
                val newName = requestFileName(file.name)
                client.renameDocument(file.id, newName)
                fetchFiles()
            } catch (e: Exception) {
                Toast.makeText(this@FilesActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteFile(file: File) {
        lifecycleScope.launch {
            try {
                client.deleteDocument(file.id)
                fetchFiles()
            } catch (e: Exception) {
                Toast.makeText(this@FilesActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun importNewFile() {
        pendingImportFile = null
        importFileLauncher.launch("*/*")
    }

    private fun importOverFile(file: File) {
        pendingImportFile = file
        importFileLauncher.launch("*/*")
    }

    private fun exportFile(file: File) {
        pendingExportFile = file
        exportFileLauncher.launch(file.name)
    }

    private suspend fun fetchFiles() {
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

    private suspend fun requestFileName(name: String): String = requestModelTextInput(
        initial = name,
        title = getText(R.string.file_name),
        hint = getText(R.string.file_name),
        error = getText(R.string.invalid_file_name),
        validator = ValidatorFileName,
    )
}
