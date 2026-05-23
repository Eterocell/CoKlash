package com.github.kr328.clash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.setFileName
import com.github.kr328.clash.design.compose.LogsScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.model.LogFile
import com.github.kr328.clash.util.logsDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogsActivity : BaseComposeActivity() {
    private var logs by mutableStateOf<List<LogFile>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoKlashTheme {
                LogsScreen(
                    onBackClick = { finish() },
                    onStartLogcat = {
                        startActivity(LogcatActivity::class.intent)
                        finish()
                    },
                    onDeleteAll = { deleteAllLogs() },
                    logs = logs,
                    onLogFileClick = { file ->
                        startActivity(LogcatActivity::class.intent.setFileName(file.fileName))
                    },
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadFiles()
    }

    private fun loadFiles() {
        lifecycleScope.launch {
            logs = withContext(Dispatchers.IO) {
                val list = cacheDir.resolve("logs").listFiles()?.toList() ?: emptyList()
                list.mapNotNull { LogFile.parseFromFileName(it.name) }
            }
        }
    }

    private fun deleteAllLogs() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                logsDir.deleteRecursively()
            }
            loadFiles()
        }
    }
}
