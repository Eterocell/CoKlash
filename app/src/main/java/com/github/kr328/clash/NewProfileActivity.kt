package com.github.kr328.clash

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.setUUID
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.NewProfileScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.model.ProfileProvider
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.withProfile
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class NewProfileActivity : BaseComposeActivity() {
    private var providers by mutableStateOf<List<ProfileProvider>>(emptyList())

    private var pendingExternalProvider: ProfileProvider.External? = null
    private var pendingPropertiesCallback: ((Boolean) -> Unit)? = null

    private val scanLauncher = registerForActivityResult(ScanQRCode(), ::scanResultHandler)

    private val externalProviderLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val provider = pendingExternalProvider ?: return@registerForActivityResult
        pendingExternalProvider = null
        if (result.resultCode != RESULT_OK) return@registerForActivityResult
        val uri = result.data?.data ?: return@registerForActivityResult
        val name = result.data?.getStringExtra(Intents.EXTRA_NAME)
        lifecycleScope.launch {
            createExternalProfile(uri, name)
        }
    }

    private val propertiesLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            providers = queryProfileProviders()
        }
        setContent {
            CoKlashTheme {
                NewProfileScreen(
                    onBackClick = { finish() },
                    providers = providers,
                    onProviderClick = { provider -> handleProviderClick(provider) },
                    onProviderLongClick = { provider -> handleProviderLongClick(provider) },
                )
            }
        }
    }

    private fun handleProviderClick(provider: ProfileProvider) {
        when (provider) {
            is ProfileProvider.QR -> scanLauncher.launch(null)
            is ProfileProvider.External -> {
                pendingExternalProvider = provider
                externalProviderLauncher.launch(provider.intent)
            }
            is ProfileProvider.File, is ProfileProvider.Url -> {
                lifecycleScope.launch {
                    withProfile {
                        val name = getString(R.string.new_profile)
                        val uuid = when (provider) {
                            is ProfileProvider.File -> create(Profile.Type.File, name)
                            is ProfileProvider.Url -> create(Profile.Type.Url, name)
                            else -> return@withProfile
                        }
                        launchProperties(uuid)
                    }
                }
            }
        }
    }

    private fun handleProviderLongClick(provider: ProfileProvider) {
        if (provider is ProfileProvider.External) {
            val packageName = provider.intent.component?.packageName ?: return
            val data = Uri.fromParts("package", packageName, null)
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(data))
        }
    }

    private fun launchProperties(uuid: UUID) {
        propertiesLauncher.launch(PropertiesActivity::class.intent.setUUID(uuid))
    }

    private suspend fun createExternalProfile(uri: Uri, name: String?) {
        withProfile {
            val profileName = name ?: getString(R.string.new_profile)
            val uuid = create(Profile.Type.External, profileName, uri.toString())
            launchProperties(uuid)
        }
    }

    private suspend fun queryProfileProviders(): List<ProfileProvider> = withContext(Dispatchers.IO) {
        val providers = packageManager
            .queryIntentActivities(Intent(Intents.ACTION_PROVIDE_URL), 0)
            .map {
                val activity = it.activityInfo
                val name = activity.applicationInfo.loadLabel(packageManager)
                val summary = activity.loadLabel(packageManager)
                val icon = activity.loadIcon(packageManager)
                val intent = Intent(Intents.ACTION_PROVIDE_URL)
                    .setComponent(ComponentName(activity.packageName, activity.name))
                ProfileProvider.External(name.toString(), summary.toString(), icon, intent)
            }
        listOf(
            ProfileProvider.File(this@NewProfileActivity),
            ProfileProvider.Url(this@NewProfileActivity),
            ProfileProvider.QR(this@NewProfileActivity),
        ) + providers
    }

    private fun scanResultHandler(result: QRResult) {
        lifecycleScope.launch {
            when (result) {
                is QRResult.QRSuccess -> {
                    val url = result.content.rawValue
                        ?: result.content.rawBytes?.let { String(it) }.orEmpty()
                    createProfileByQrCode(url)
                }
                QRResult.QRUserCanceled -> {}
                QRResult.QRMissingPermission -> {
                    Toast.makeText(this@NewProfileActivity, R.string.import_from_qr_no_permission, Toast.LENGTH_LONG).show()
                }
                is QRResult.QRError -> {
                    Toast.makeText(this@NewProfileActivity, R.string.import_from_qr_exception, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun createProfileByQrCode(url: String) {
        withProfile {
            val uuid = create(Profile.Type.Url, getString(R.string.new_profile), url)
            launchProperties(uuid)
        }
    }
}
