package com.github.kr328.clash.design.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.kr328.clash.design.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    clashRunning: Boolean,
    profileName: String?,
    mode: String,
    forwarded: String,
    hasProviders: Boolean,
    onToggleClick: () -> Unit,
    onProxyClick: () -> Unit,
    onProfilesClick: () -> Unit,
    onProvidersClick: () -> Unit,
    onLogsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    val cardColor by animateColorAsState(
        targetValue = if (clashRunning)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = "cardColor",
    )
    val fabColor by animateColorAsState(
        targetValue = if (clashRunning)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = "fabColor",
    )

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = profileName ?: stringResource(R.string.no_profile_selected),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = mode,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Text(
                            text = forwarded,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    FloatingActionButton(
                        onClick = onToggleClick,
                        modifier = Modifier.size(64.dp),
                        containerColor = fabColor,
                        contentColor = if (clashRunning)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    ) {
                        Icon(
                            painter = painterResource(
                                if (clashRunning) R.drawable.ic_baseline_stop
                                else R.drawable.ic_baseline_flash_on,
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
            }
            ListItem(
                headlineContent = { Text(stringResource(R.string.proxy)) },
                leadingContent = {
                    Icon(painterResource(R.drawable.ic_baseline_flash_on), null)
                },
                modifier = Modifier.clickable(onClick = onProxyClick),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.profile)) },
                leadingContent = {
                    Icon(painterResource(R.drawable.ic_baseline_attach_file), null)
                },
                modifier = Modifier.clickable(onClick = onProfilesClick),
            )
            if (hasProviders) {
                ListItem(
                    headlineContent = { Text(stringResource(R.string.providers)) },
                    leadingContent = {
                        Icon(painterResource(R.drawable.ic_baseline_cloud_download), null)
                    },
                    modifier = Modifier.clickable(onClick = onProvidersClick),
                )
            }
            ListItem(
                headlineContent = { Text(stringResource(R.string.logs)) },
                leadingContent = {
                    Icon(painterResource(R.drawable.ic_baseline_assignment), null)
                },
                modifier = Modifier.clickable(onClick = onLogsClick),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.settings)) },
                leadingContent = {
                    Icon(painterResource(R.drawable.ic_baseline_settings), null)
                },
                modifier = Modifier.clickable(onClick = onSettingsClick),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.help)) },
                leadingContent = {
                    Icon(painterResource(R.drawable.ic_baseline_help_center), null)
                },
                modifier = Modifier.clickable(onClick = onHelpClick),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.about)) },
                leadingContent = {
                    Icon(painterResource(R.drawable.ic_baseline_info), null)
                },
                modifier = Modifier.clickable(onClick = onAboutClick),
            )
        }
    }
}
