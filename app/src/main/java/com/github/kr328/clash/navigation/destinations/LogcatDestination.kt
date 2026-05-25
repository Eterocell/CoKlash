package com.github.kr328.clash.navigation.destinations

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.navigation.NavHostController
import com.github.kr328.clash.LogcatService
import com.github.kr328.clash.common.compat.startForegroundServiceCompat
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.core.model.LogMessage
import com.github.kr328.clash.design.compose.LogcatScreen
import com.github.kr328.clash.design.model.LogFile
import com.github.kr328.clash.log.LogcatReader
import com.github.kr328.clash.navigation.Logs
import com.github.kr328.clash.util.logsDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.github.kr328.clash.design.R as DesignR

@Composable
fun LogcatDestination(navController: NavHostController, fileName: String?) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val streaming = remember { fileName == null }
    var messages by remember { mutableStateOf<List<LogMessage>>(emptyList()) }
    val logFile = remember { fileName?.let { LogFile.parseFromFileName(it) } }

    var conn by remember { mutableStateOf<ServiceConnection?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain"),
    ) { uri ->
        val file = logFile ?: return@rememberLauncherForActivityResult
        if (uri != null) {
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            val writer = java.io.OutputStreamWriter(out)
                            messages.forEach { msg ->
                                writer.write("${msg.level} ${msg.message}\n")
                            }
                            writer.flush()
                        }
                    }
                    Toast.makeText(context, DesignR.string.file_exported, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(context, e.message ?: e.javaClass.simpleName, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (streaming) {
        LaunchedEffect(Unit) {
            context.startForegroundServiceCompat(LogcatService::class.intent)
            val service = suspendCoroutine { cont ->
                val connection = object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                        val srv = service!!.queryLocalInterface("") as LogcatService
                        cont.resume(srv)
                    }
                    override fun onServiceDisconnected(name: ComponentName?) {}
                }
                context.bindService(
                    LogcatService::class.intent,
                    connection,
                    Context.BIND_AUTO_CREATE,
                )
                conn = connection
            }
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
    } else {
        LaunchedEffect(logFile) {
            val file = logFile ?: run {
                Toast.makeText(context, DesignR.string.invalid_log_file, Toast.LENGTH_LONG).show()
                navController.popBackStack()
                return@LaunchedEffect
            }
            try {
                messages = withContext(Dispatchers.IO) {
                    LogcatReader(context, file).readAll()
                }
            } catch (e: Exception) {
                Toast.makeText(context, DesignR.string.invalid_log_file, Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            conn?.let { context.unbindService(it) }
            conn = null
        }
    }

    LogcatScreen(
        streaming = streaming,
        messages = messages,
        onBackClick = { navController.popBackStack() },
        onClose = {
            context.stopService(LogcatService::class.intent)
            navController.popBackStack()
            navController.navigate(Logs)
        },
        onDelete = {
            val file = logFile ?: return@LogcatScreen
            scope.launch {
                withContext(Dispatchers.IO) {
                    context.logsDir.resolve(file.fileName).delete()
                }
                navController.popBackStack()
            }
        },
        onExport = {
            val file = logFile ?: return@LogcatScreen
            exportLauncher.launch(file.fileName)
        },
        onCopyMessage = { msg ->
            val data = ClipData.newPlainText("log_message", msg.message)
            context.getSystemService<ClipboardManager>()?.setPrimaryClip(data)
            Toast.makeText(context, DesignR.string.copied, Toast.LENGTH_SHORT).show()
        },
    )
}
