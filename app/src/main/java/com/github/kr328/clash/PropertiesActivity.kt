package com.github.kr328.clash

import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.setUUID
import com.github.kr328.clash.common.util.uuid
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.PropertiesScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.design.dialog.requestModelTextInput
import com.github.kr328.clash.design.dialog.withModelProgressBar
import com.github.kr328.clash.design.util.ValidatorAutoUpdateInterval
import com.github.kr328.clash.design.util.ValidatorHttpUrl
import com.github.kr328.clash.design.util.ValidatorNotBlank
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.withProfile
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PropertiesActivity : BaseComposeActivity() {
    private var canceled = false
    private lateinit var original: Profile

    private var profileName by mutableStateOf("")
    private var profileType by mutableStateOf(Profile.Type.File)
    private var profileSource by mutableStateOf("")
    private var profileInterval by mutableStateOf(0L)
    private var isProcessing by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)

        val uuid = intent.uuid ?: run { finish(); return }

        lifecycleScope.launch {
            original = withProfile { queryByUUID(uuid) } ?: run { finish(); return@launch }
            profileName = original.name
            profileType = original.type
            profileSource = original.source
            profileInterval = original.interval

            setContent {
                CoKlashTheme {
                    PropertiesScreen(
                        onBackClick = { handleBack() },
                        profileName = profileName,
                        onNameClick = { inputName() },
                        profileType = profileType,
                        profileSource = profileSource,
                        onUrlClick = { inputUrl() },
                        profileInterval = profileInterval,
                        onIntervalClick = { inputInterval() },
                        onBrowseFilesClick = {
                            startActivity(FilesActivity::class.intent.setUUID(uuid))
                        },
                        onCommitClick = { verifyAndCommit() },
                        isProcessing = isProcessing,
                    )
                }
            }
        }

        onBackPressedDispatcher.addCallback { handleBack() }
    }

    override fun onStop() {
        super.onStop()
        if (!canceled && ::original.isInitialized) {
            val current = buildCurrentProfile()
            if (current != original) {
                lifecycleScope.launch {
                    withProfile {
                        patch(current.uuid, current.name, current.source, current.interval)
                    }
                }
            }
        }
    }

    override fun onServiceRecreated() {
        super.onServiceRecreated()
        finish()
    }

    override fun onDestroy() {
        if (::original.isInitialized) {
            canceled = true
            lifecycleScope.launch {
                withProfile { release(original.uuid) }
            }
        }
        super.onDestroy()
    }

    private fun handleBack() {
        if (isProcessing) return
        val current = buildCurrentProfile()
        if (current == original) {
            finish()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.exit_without_save)
                .setMessage(R.string.exit_without_save_warning)
                .setPositiveButton(R.string.ok) { _, _ -> finish() }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun inputName() {
        lifecycleScope.launch {
            val name = requestModelTextInput(
                initial = profileName,
                title = getText(R.string.name),
                hint = getText(R.string.properties),
                error = getText(R.string.should_not_be_blank),
                validator = ValidatorNotBlank,
            )
            profileName = name
        }
    }

    private fun inputUrl() {
        if (profileType == Profile.Type.External) return
        lifecycleScope.launch {
            val url = requestModelTextInput(
                initial = profileSource,
                title = getText(R.string.url),
                hint = getText(R.string.profile_url),
                error = getText(R.string.accept_http_content),
                validator = ValidatorHttpUrl,
            )
            profileSource = url
        }
    }

    private fun inputInterval() {
        lifecycleScope.launch {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(profileInterval)
            val result = requestModelTextInput(
                initial = if (minutes == 0L) "" else minutes.toString(),
                title = getText(R.string.auto_update),
                hint = getText(R.string.auto_update_minutes),
                error = getText(R.string.at_least_15_minutes),
                validator = ValidatorAutoUpdateInterval,
            )
            profileInterval = TimeUnit.MINUTES.toMillis(result.toLongOrNull() ?: 0)
        }
    }

    private fun verifyAndCommit() {
        lifecycleScope.launch {
            when {
                profileName.isBlank() -> {
                    Toast.makeText(this@PropertiesActivity, R.string.empty_name, Toast.LENGTH_LONG).show()
                }
                profileType != Profile.Type.File && profileSource.isBlank() -> {
                    Toast.makeText(this@PropertiesActivity, R.string.invalid_url, Toast.LENGTH_LONG).show()
                }
                else -> {
                    try {
                        isProcessing = true
                        withModelProgressBar {
                            configure {
                                isIndeterminate = true
                                text = getString(R.string.initializing)
                            }
                            withProfile {
                                patch(original.uuid, profileName, profileSource, profileInterval)
                                coroutineScope {
                                    commit(original.uuid) { status ->
                                        launch {
                                            configure {
                                                applyFrom(status)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        canceled = true
                        setResult(RESULT_OK)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@PropertiesActivity, e.message ?: e.javaClass.simpleName, Toast.LENGTH_LONG).show()
                    } finally {
                        isProcessing = false
                    }
                }
            }
        }
    }

    private fun buildCurrentProfile(): Profile = original.copy(
        name = profileName,
        source = profileSource,
        interval = profileInterval,
    )

    private fun com.github.kr328.clash.design.dialog.ModelProgressBarConfigure.applyFrom(status: com.github.kr328.clash.core.model.FetchStatus) {
        when (status.action) {
            com.github.kr328.clash.core.model.FetchStatus.Action.FetchConfiguration -> {
                text = getString(R.string.format_fetching_configuration, status.args[0])
                isIndeterminate = true
            }
            com.github.kr328.clash.core.model.FetchStatus.Action.FetchProviders -> {
                text = getString(R.string.format_fetching_provider, status.args[0])
                isIndeterminate = false
                max = status.max
                progress = status.progress
            }
            com.github.kr328.clash.core.model.FetchStatus.Action.Verifying -> {
                text = getString(R.string.verifying)
                isIndeterminate = false
                max = status.max
                progress = status.progress
            }
        }
    }
}
