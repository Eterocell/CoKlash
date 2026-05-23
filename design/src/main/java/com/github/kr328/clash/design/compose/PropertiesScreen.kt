package com.github.kr328.clash.design.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.kr328.clash.design.R
import com.github.kr328.clash.service.model.Profile
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesScreen(
    onBackClick: () -> Unit,
    profileName: String,
    onNameClick: () -> Unit,
    profileType: Profile.Type,
    profileSource: String,
    onUrlClick: () -> Unit,
    profileInterval: Long,
    onIntervalClick: () -> Unit,
    onBrowseFilesClick: () -> Unit,
    onCommitClick: () -> Unit,
    isProcessing: Boolean,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.properties)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_arrow_back),
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onCommitClick,
                        enabled = !isProcessing,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_save),
                            contentDescription = "Commit",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            if (isProcessing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Text(
                text = stringResource(R.string.tips_properties),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.name)) },
                supportingContent = { Text(profileName) },
                modifier = Modifier.clickable(enabled = !isProcessing) { onNameClick() },
            )

            if (profileType != Profile.Type.File) {
                val urlText = profileSource.ifBlank { stringResource(R.string.not_selected) }
                val urlClickable = !isProcessing && profileType != Profile.Type.External
                ListItem(
                    headlineContent = { Text(stringResource(R.string.url)) },
                    supportingContent = { Text(urlText) },
                    modifier = if (urlClickable) {
                        Modifier.clickable { onUrlClick() }
                    } else {
                        Modifier
                    },
                )

                val intervalText = if (profileInterval == 0L) {
                    stringResource(R.string.disabled)
                } else {
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(profileInterval)
                    "$minutes ${stringResource(R.string.auto_update_minutes)}"
                }
                ListItem(
                    headlineContent = { Text(stringResource(R.string.auto_update)) },
                    supportingContent = { Text(intervalText) },
                    modifier = Modifier.clickable(enabled = !isProcessing) { onIntervalClick() },
                )
            }

            ListItem(
                headlineContent = { Text(stringResource(R.string.browse_files)) },
                modifier = Modifier.clickable(enabled = !isProcessing) { onBrowseFilesClick() },
            )
        }
    }
}
