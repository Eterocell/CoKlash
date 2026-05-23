package com.github.kr328.clash.design.compose

import android.text.format.DateUtils
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.model.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FilesScreen(
    onBackClick: () -> Unit,
    files: List<File>,
    currentInBaseDir: Boolean,
    configurationEditable: Boolean,
    onFileClick: (File) -> Unit,
    onFileLongClick: (File) -> Unit,
    onImportNew: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.browse_files)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_arrow_back),
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (!currentInBaseDir && configurationEditable) {
                FloatingActionButton(onClick = onImportNew) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_add),
                        contentDescription = "New file",
                    )
                }
            }
        },
    ) { padding ->
        if (files.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                items(files, key = { it.id }) { file ->
                    ListItem(
                        headlineContent = { Text(file.name) },
                        supportingContent = {
                            Text(formatFileInfo(file))
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(
                                    if (file.isDirectory) R.drawable.ic_outline_folder
                                    else R.drawable.ic_baseline_attach_file,
                                ),
                                contentDescription = null,
                            )
                        },
                        modifier = Modifier.combinedClickable(
                            onClick = { onFileClick(file) },
                            onLongClick = { onFileLongClick(file) },
                        ),
                    )
                }
            }
        }
    }
}

private fun formatFileInfo(file: File): String {
    if (file.isDirectory) return "Directory"
    val size = formatFileSize(file.size)
    val time = DateUtils.getRelativeTimeSpanString(file.lastModified)
    return "$size • $time"
}

private fun formatFileSize(size: Long): String = when {
    size < 1024 -> "$size B"
    size < 1024 * 1024 -> "${size / 1024} KB"
    else -> "${size / (1024 * 1024)} MB"
}
