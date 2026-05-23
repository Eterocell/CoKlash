package com.github.kr328.clash

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.compat.startForegroundServiceCompat
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.common.util.fileName
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.core.model.LogMessage
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.LogcatScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.dialog.withModelProgressBar
import com.github.kr328.clash.design.model.LogFile
import com.github.kr328.clash.log.LogcatFilter
import com.github.kr328.clash.log.LogcatReader
import com.github.kr328.clash.util.logsDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter

class LogcatActivity : BaseComposeActivity() {
    private var conn: ServiceConnection? = null
    private var streamingJob: Job? = null

    private var streaming by mutableStateOf(false)
    private var messages by mutableStateOf<List<LogMessage>>(emptyList())
    private var logFile: LogFile? = null

    private val exportLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain"),
    ) { uri ->
        val file = logFile ?: return@registerForActivityResult
        if (uri != null) {
            lifecycleScope.launch {
                try {
                    writeLogTo(messages, file, uri)
                    Toast.makeText(this@LogcatActivity, R.string.file_exported, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@LogcatActivity, e.message ?: e.javaClass.simpleName, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileName = intent?.fileName

        if (fileName != null) {
            val file = LogFile.parseFromFileName(fileName)
            if (file == null) {
                Toast.makeText(this, R.string.invalid_log_file, Toast.LENGTH_LONG).show()
                finish()
                return
            }
            logFile = file
            streaming = false
            loadFile(file)
        } else {
            streaming = true
            startForegroundServiceCompat(LogcatService::class.intent)
            bindAndStreamLogs()
        }

        setContent {
            CoKlashTheme {
                LogcatScreen(
                    streaming = streaming,
                    messages = messages,
                    onBackClick = { finish() },
                    onClose = {
                        stopService(LogcatService::class.intent)
                        startActivity(LogsActivity::class.intent)
                        finish()
                    },
                    onDelete = {
                        val file = logFile ?: return@LogcatScreen
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                logsDir.resolve(file.fileName).delete()
                            }
                            finish()
                        }
                    },
                    onExport = {
                        val file = logFile ?: return@LogcatScreen
                        exportLauncher.launch(file.fileName)
                    },
                    onCopyMessage = { msg ->
                        val data = ClipData.newPlainText("log_message", msg.message)
                        getSystemService<ClipboardManager>()?.setPrimaryClip(data)
                        Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }

    private fun loadFile(file: LogFile) {
        lifecycleScope.launch {
            try {
                messages = withContext(Dispatchers.IO) {
                    LogcatReader(this@LogcatActivity, file).readAll()
                }
            } catch (e: Exception) {
                Log.e("Fail to read log file ${file.fileName}: ${e.message}")
                Toast.makeText(this@LogcatActivity, R.string.invalid_log_file, Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun bindAndStreamLogs() {
        streamingJob = lifecycleScope.launch {
            val service = bindLogcatService()
            var initial = true
            while (isActive) {
                val snapshot = service.snapshot(initial)
                if (snapshot != null) {
                    messages = snapshot.messages.toList()
                    initial = false
                }
                delay(500)
            }
        }
    }

    private suspend fun bindLogcatService(): LogcatService {
        return kotlin.coroutines.suspendCoroutine { ctx ->
            bindService(
                LogcatService::class.intent,
                object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        val srv = service!!.queryLocalInterface("") as LogcatService
                        ctx.resumeWith(Result.success(srv))
                        conn = this
                    }
                    override fun onServiceDisconnected(name: ComponentName?) {
                        conn = null
                    }
                },
                Context.BIND_AUTO_CREATE,
            )
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun writeLogTo(messages: List<LogMessage>, file: LogFile, uri: Uri) {
        LogcatFilter(OutputStreamWriter(contentResolver.openOutputStream(uri)), this).use {
            withContext(Dispatchers.Main) {
                withModelProgressBar {
                    configure {
                        isIndeterminate = true
                        max = messages.size
                    }
                    withContext(Dispatchers.IO) {
                        it.writeHeader(file.date)
                        messages.forEachIndexed { idx, msg ->
                            configure {
                                isIndeterminate = false
                                progress = idx
                            }
                            it.writeMessage(msg)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        streamingJob?.cancel()
        conn?.apply(this::unbindService)
        conn = null
        super.onDestroy()
    }
}
