package com.github.kr328.clash.design.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.github.kr328.clash.design.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onAppClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onOverrideClick: () -> Unit,
    onMetaFeatureClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    androidx.compose.material3.IconButton(onClick = onBackClick) {
                        Icon(painterResource(R.drawable.ic_baseline_arrow_back), null)
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
            ListItem(
                headlineContent = { Text(stringResource(R.string.app)) },
                leadingContent = { Icon(painterResource(R.drawable.ic_baseline_settings), null) },
                modifier = Modifier.clickable(onClick = onAppClick),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.network)) },
                leadingContent = { Icon(painterResource(R.drawable.ic_baseline_dns), null) },
                modifier = Modifier.clickable(onClick = onNetworkClick),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.override)) },
                leadingContent = { Icon(painterResource(R.drawable.ic_baseline_extension), null) },
                modifier = Modifier.clickable(onClick = onOverrideClick),
            )
            ListItem(
                headlineContent = { Text(stringResource(R.string.meta_features)) },
                leadingContent = { Icon(painterResource(R.drawable.ic_baseline_meta), null) },
                modifier = Modifier.clickable(onClick = onMetaFeatureClick),
            )
        }
    }
}
