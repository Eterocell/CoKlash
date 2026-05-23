package com.github.kr328.clash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.compat.getPackageInfoCompat
import com.github.kr328.clash.common.compat.versionCodeCompat
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.design.compose.AppCrashedScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.log.SystemLogcat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppCrashedActivity : BaseComposeActivity() {
    private var logs by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CoKlashTheme {
                AppCrashedScreen(logs = logs)
            }
        }

        lifecycleScope.launch {
            val packageInfo = withContext(Dispatchers.IO) {
                packageManager.getPackageInfoCompat(packageName, 0)
            }
            Log.i("App version: versionName = ${packageInfo.versionName} versionCode = ${packageInfo.versionCodeCompat}")

            logs = withContext(Dispatchers.IO) {
                SystemLogcat.dumpCrash()
            }
        }
    }
}
