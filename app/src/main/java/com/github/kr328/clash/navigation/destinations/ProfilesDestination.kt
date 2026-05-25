package com.github.kr328.clash.navigation.destinations

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.AppEvent
import com.github.kr328.clash.AppViewModel
import com.github.kr328.clash.design.compose.ProfilesScreen
import com.github.kr328.clash.navigation.NewProfile
import com.github.kr328.clash.navigation.Properties
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.kr328.clash.design.R as DesignR

@Composable
fun ProfilesDestination(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var profiles by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var isUpdatingAll by remember { mutableStateOf(false) }
    var hasUpdatableProfiles by remember { mutableStateOf(false) }

    fun fetchProfiles() {
        scope.launch {
            val all = withProfile { queryAll() }
            profiles = all
            hasUpdatableProfiles = withContext(Dispatchers.Default) {
                all.any { it.imported && it.type != Profile.Type.File }
            }
        }
    }

    LaunchedEffect(Unit) { fetchProfiles() }

    LaunchedEffect(Unit) {
        appViewModel.events.collect { event ->
            when (event) {
                is AppEvent.ProfileChanged -> fetchProfiles()
                is AppEvent.ProfileUpdateCompleted -> {
                    val uuid = event.uuid ?: return@collect
                    scope.launch {
                        val name = withProfile { queryByUUID(uuid)?.name }
                        Toast.makeText(
                            context,
                            context.getString(DesignR.string.toast_profile_updated_complete, name),
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
                is AppEvent.ProfileUpdateFailed -> {
                    val uuid = event.uuid ?: return@collect
                    scope.launch {
                        val name = withProfile { queryByUUID(uuid)?.name }
                        Toast.makeText(
                            context,
                            context.getString(DesignR.string.toast_profile_updated_failed, name, event.reason),
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
                else -> {}
            }
        }
    }

    ProfilesScreen(
        onBackClick = { navController.popBackStack() },
        profiles = profiles,
        onProfileClick = { profile ->
            scope.launch {
                withProfile {
                    if (profile.imported) {
                        setActive(profile)
                    } else {
                        Toast.makeText(context, DesignR.string.active_unsaved_tips, Toast.LENGTH_LONG).show()
                    }
                }
            }
        },
        onProfileLongClick = { profile ->
            navController.navigate(Properties(profile.uuid.toString()))
        },
        onCreateClick = { navController.navigate(NewProfile) },
        onUpdateAllClick = {
            isUpdatingAll = true
            scope.launch {
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
        },
        isUpdatingAll = isUpdatingAll,
        hasUpdatableProfiles = hasUpdatableProfiles,
    )
}
