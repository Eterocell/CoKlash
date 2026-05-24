package com.github.kr328.clash.design.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.kr328.clash.design.R
import com.github.kr328.clash.service.model.Profile

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfilesScreen(
    onBackClick: () -> Unit,
    profiles: List<Profile>,
    onProfileClick: (Profile) -> Unit,
    onProfileLongClick: (Profile) -> Unit,
    onCreateClick: () -> Unit,
    onUpdateAllClick: () -> Unit,
    isUpdatingAll: Boolean,
    hasUpdatableProfiles: Boolean,
) {
    Scaffold(
        topBar = {
            ProfilesTopBar(onBackClick, hasUpdatableProfiles, isUpdatingAll, onUpdateAllClick)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_add),
                    contentDescription = null,
                )
            }
        },
    ) { padding ->
        if (profiles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
            ) {
                items(profiles, key = { it.uuid.toString() }) { profile ->
                    ProfileItem(profile, onProfileClick, onProfileLongClick)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfilesTopBar(
    onBackClick: () -> Unit,
    hasUpdatableProfiles: Boolean,
    isUpdatingAll: Boolean,
    onUpdateAllClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.profile)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_arrow_back),
                    contentDescription = null,
                )
            }
        },
        actions = {
            if (hasUpdatableProfiles) {
                val rotation = if (isUpdatingAll) {
                    val transition = rememberInfiniteTransition(label = "sync")
                    val angle by transition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart,
                        ),
                        label = "syncRotation",
                    )
                    angle
                } else {
                    0f
                }
                IconButton(onClick = onUpdateAllClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_sync),
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation),
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProfileItem(
    profile: Profile,
    onProfileClick: (Profile) -> Unit,
    onProfileLongClick: (Profile) -> Unit,
) {
    ListItem(
        headlineContent = { Text(profile.name) },
        supportingContent = { Text(formatProfileSummary(profile)) },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (profile.active) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = CircleShape,
                    ),
            )
        },
        modifier = Modifier.combinedClickable(
            onClick = { onProfileClick(profile) },
            onLongClick = { onProfileLongClick(profile) },
        ),
    )
}

private fun formatProfileSummary(profile: Profile): String = when (profile.type) {
    Profile.Type.File -> "File"
    Profile.Type.Url -> "URL • ${profile.source}"
    Profile.Type.External -> "External • ${profile.source}"
}
