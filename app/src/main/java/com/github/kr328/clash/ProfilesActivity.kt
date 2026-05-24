package com.github.kr328.clash

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.setUUID
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.ProfilesScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class ProfilesActivity : BaseComposeActivity() {
    private var profiles by mutableStateOf<List<Profile>>(emptyList())
    private var isUpdatingAll by mutableStateOf(false)
    private var hasUpdatableProfiles by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoKlashTheme {
                ProfilesScreen(
                    onBackClick = { finish() },
                    profiles = profiles,
                    onProfileClick = { profile -> activateProfile(profile) },
                    onProfileLongClick = { profile -> showProfileMenu(profile) },
                    onCreateClick = { startActivity(NewProfileActivity::class.intent) },
                    onUpdateAllClick = { updateAll() },
                    isUpdatingAll = isUpdatingAll,
                    hasUpdatableProfiles = hasUpdatableProfiles,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        fetchProfiles()
    }

    override fun onProfileChanged() {
        super.onProfileChanged()
        fetchProfiles()
    }

    override fun onProfileUpdateCompleted(uuid: UUID?) {
        super.onProfileUpdateCompleted(uuid)
        if (uuid == null) return
        lifecycleScope.launch {
            val name = withProfile { queryByUUID(uuid)?.name }
            Toast.makeText(
                this@ProfilesActivity,
                getString(R.string.toast_profile_updated_complete, name),
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onProfileUpdateFailed(uuid: UUID?, reason: String?) {
        super.onProfileUpdateFailed(uuid, reason)
        if (uuid == null) return
        lifecycleScope.launch {
            val name = withProfile { queryByUUID(uuid)?.name }
            Toast.makeText(
                this@ProfilesActivity,
                getString(R.string.toast_profile_updated_failed, name, reason),
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    private fun fetchProfiles() {
        lifecycleScope.launch {
            val all = withProfile { queryAll() }
            profiles = all
            hasUpdatableProfiles = withContext(Dispatchers.Default) {
                all.any { it.imported && it.type != Profile.Type.File }
            }
        }
    }

    private fun activateProfile(profile: Profile) {
        lifecycleScope.launch {
            withProfile {
                if (profile.imported) {
                    setActive(profile)
                } else {
                    Toast.makeText(
                        this@ProfilesActivity,
                        R.string.active_unsaved_tips,
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
        }
    }

    private fun showProfileMenu(profile: Profile) {
        val items = mutableListOf<Pair<String, () -> Unit>>()
        if (profile.imported && profile.type != Profile.Type.File) {
            items.add(getString(R.string.update) to { updateProfile(profile) })
        }
        items.add(getString(R.string.edit) to {
            startActivity(PropertiesActivity::class.intent.setUUID(profile.uuid))
        })
        items.add(getString(R.string.duplicate) to { duplicateProfile(profile) })
        items.add(getString(R.string.delete) to { deleteProfile(profile) })

        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle(profile.name)
            .setItems(items.map { it.first }.toTypedArray()) { _, which ->
                items[which].second()
            }
            .show()
    }

    private fun updateAll() {
        isUpdatingAll = true
        lifecycleScope.launch {
            try {
                withProfile {
                    queryAll().forEach { p ->
                        if (p.imported && p.type != Profile.Type.File) {
                            update(p.uuid)
                        }
                    }
                }
            } finally {
                isUpdatingAll = false
            }
        }
    }

    private fun updateProfile(profile: Profile) {
        lifecycleScope.launch {
            withProfile { update(profile.uuid) }
        }
    }

    private fun duplicateProfile(profile: Profile) {
        lifecycleScope.launch {
            val uuid = withProfile { clone(profile.uuid) }
            startActivity(PropertiesActivity::class.intent.setUUID(uuid))
        }
    }

    private fun deleteProfile(profile: Profile) {
        lifecycleScope.launch {
            withProfile { delete(profile.uuid) }
        }
    }
}
