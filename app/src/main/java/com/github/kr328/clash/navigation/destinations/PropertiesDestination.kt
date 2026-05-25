package com.github.kr328.clash.navigation.destinations

import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.github.kr328.clash.design.compose.PropertiesScreen
import com.github.kr328.clash.navigation.Files
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.launch
import java.util.UUID
import com.github.kr328.clash.design.R as DesignR

@Composable
fun PropertiesDestination(navController: NavHostController, uuid: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val profileUuid = remember { UUID.fromString(uuid) }

    var original by remember { mutableStateOf<Profile?>(null) }
    var profileName by remember { mutableStateOf("") }
    var profileType by remember { mutableStateOf(Profile.Type.File) }
    var profileSource by remember { mutableStateOf("") }
    var profileInterval by remember { mutableStateOf(0L) }
    var isProcessing by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(profileUuid) {
        val profile = withProfile { queryByUUID(profileUuid) }
        if (profile == null) {
            navController.popBackStack()
            return@LaunchedEffect
        }
        original = profile
        profileName = profile.name
        profileType = profile.type
        profileSource = profile.source
        profileInterval = profile.interval
    }

    DisposableEffect(Unit) {
        onDispose {
            val orig = original ?: return@onDispose
            scope.launch {
                withProfile { release(orig.uuid) }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(DesignR.string.exit_without_save)) },
            text = { Text(stringResource(DesignR.string.exit_without_save_warning)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    navController.popBackStack()
                }) { Text(stringResource(DesignR.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(DesignR.string.cancel))
                }
            },
        )
    }

    if (original != null) {
        PropertiesScreen(
            onBackClick = {
                if (isProcessing) return@PropertiesScreen
                val orig = original ?: return@PropertiesScreen
                val changed = profileName != orig.name ||
                    profileSource != orig.source ||
                    profileInterval != orig.interval
                if (changed) {
                    showExitDialog = true
                } else {
                    navController.popBackStack()
                }
            },
            profileName = profileName,
            onNameClick = { },
            profileType = profileType,
            profileSource = profileSource,
            onUrlClick = { },
            profileInterval = profileInterval,
            onIntervalClick = { },
            onBrowseFilesClick = { navController.navigate(Files(uuid)) },
            onCommitClick = {
                scope.launch {
                    val orig = original ?: return@launch
                    when {
                        profileName.isBlank() -> {
                            Toast.makeText(context, DesignR.string.empty_name, Toast.LENGTH_LONG).show()
                        }
                        profileType != Profile.Type.File && profileSource.isBlank() -> {
                            Toast.makeText(context, DesignR.string.invalid_url, Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            try {
                                isProcessing = true
                                withProfile {
                                    patch(orig.uuid, profileName, profileSource, profileInterval)
                                    commit(orig.uuid) { }
                                }
                                navController.popBackStack()
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message ?: e.javaClass.simpleName, Toast.LENGTH_LONG).show()
                            } finally {
                                isProcessing = false
                            }
                        }
                    }
                }
            },
            isProcessing = isProcessing,
        )
    }
}
