package com.github.kr328.clash.navigation.destinations

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.design.compose.NewProfileScreen
import com.github.kr328.clash.design.model.ProfileProvider
import com.github.kr328.clash.navigation.Properties
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.withProfile
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.kr328.clash.design.R as DesignR

@Composable
fun NewProfileDestination(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var providers by remember { mutableStateOf<List<ProfileProvider>>(emptyList()) }
    var pendingExternalProvider by remember { mutableStateOf<ProfileProvider.External?>(null) }

    val scanLauncher = rememberLauncherForActivityResult(ScanQRCode()) { result ->
        scope.launch {
            when (result) {
                is QRResult.QRSuccess -> {
                    val url = result.content.rawValue
                        ?: result.content.rawBytes?.let { String(it) }.orEmpty()
                    withProfile {
                        val uuid = create(Profile.Type.Url, context.getString(DesignR.string.new_profile), url)
                        navController.navigate(Properties(uuid.toString()))
                    }
                }
                QRResult.QRUserCanceled -> {}
                QRResult.QRMissingPermission -> {
                    Toast.makeText(context, DesignR.string.import_from_qr_no_permission, Toast.LENGTH_LONG).show()
                }
                is QRResult.QRError -> {
                    Toast.makeText(context, DesignR.string.import_from_qr_exception, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val externalProviderLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val provider = pendingExternalProvider ?: return@rememberLauncherForActivityResult
        pendingExternalProvider = null
        if (result.resultCode != android.app.Activity.RESULT_OK) return@rememberLauncherForActivityResult
        val uri = result.data?.data ?: return@rememberLauncherForActivityResult
        val name = result.data?.getStringExtra(Intents.EXTRA_NAME)
        scope.launch {
            withProfile {
                val profileName = name ?: context.getString(DesignR.string.new_profile)
                val uuid = create(Profile.Type.External, profileName, uri.toString())
                navController.navigate(Properties(uuid.toString()))
            }
        }
    }

    LaunchedEffect(Unit) {
        providers = withContext(Dispatchers.IO) {
            val pm = context.packageManager
            val external = pm.queryIntentActivities(Intent(Intents.ACTION_PROVIDE_URL), 0)
                .map {
                    val activity = it.activityInfo
                    val name = activity.applicationInfo.loadLabel(pm)
                    val summary = activity.loadLabel(pm)
                    val icon = activity.loadIcon(pm)
                    val intent = Intent(Intents.ACTION_PROVIDE_URL)
                        .setComponent(ComponentName(activity.packageName, activity.name))
                    ProfileProvider.External(name.toString(), summary.toString(), icon, intent)
                }
            listOf(
                ProfileProvider.File(context),
                ProfileProvider.Url(context),
                ProfileProvider.QR(context),
            ) + external
        }
    }

    NewProfileScreen(
        onBackClick = { navController.popBackStack() },
        providers = providers,
        onProviderClick = { provider ->
            when (provider) {
                is ProfileProvider.QR -> scanLauncher.launch(null)
                is ProfileProvider.External -> {
                    pendingExternalProvider = provider
                    externalProviderLauncher.launch(provider.intent)
                }
                is ProfileProvider.File, is ProfileProvider.Url -> {
                    scope.launch {
                        withProfile {
                            val name = context.getString(DesignR.string.new_profile)
                            val uuid = when (provider) {
                                is ProfileProvider.File -> create(Profile.Type.File, name)
                                is ProfileProvider.Url -> create(Profile.Type.Url, name)
                                else -> return@withProfile
                            }
                            navController.navigate(Properties(uuid.toString()))
                        }
                    }
                }
            }
        },
        onProviderLongClick = { provider ->
            if (provider is ProfileProvider.External) {
                val packageName = provider.intent.component?.packageName ?: return@NewProfileScreen
                val data = Uri.fromParts("package", packageName, null)
                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(data))
            }
        },
    )
}
