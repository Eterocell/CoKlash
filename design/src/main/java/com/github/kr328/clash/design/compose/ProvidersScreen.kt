package com.github.kr328.clash.design.compose

import android.text.format.DateUtils
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.github.kr328.clash.core.model.Provider
import com.github.kr328.clash.design.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(
    onBackClick: () -> Unit,
    providers: List<Provider>,
    updatingIndices: Set<Int>,
    onUpdateClick: (Int, Provider) -> Unit,
    onUpdateAllClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            ProvidersTopBar(onBackClick, onUpdateAllClick, updatingIndices.isNotEmpty())
        },
    ) { padding ->
        if (providers.isEmpty()) {
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
                itemsIndexed(providers) { index, provider ->
                    ProviderItem(
                        provider = provider,
                        isUpdating = index in updatingIndices,
                        onUpdateClick = { onUpdateClick(index, provider) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProvidersTopBar(
    onBackClick: () -> Unit,
    onUpdateAllClick: () -> Unit,
    isUpdating: Boolean,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.providers)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_arrow_back),
                    contentDescription = null,
                )
            }
        },
        actions = {
            val rotation = if (isUpdating) {
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
        },
    )
}

@Composable
private fun ProviderItem(
    provider: Provider,
    isUpdating: Boolean,
    onUpdateClick: () -> Unit,
) {
    val summary = buildString {
        append(provider.type.name)
        append(" · ")
        append(provider.vehicleType.name)
        if (provider.updatedAt > 0) {
            append(" · ")
            append(
                DateUtils.getRelativeTimeSpanString(
                    provider.updatedAt,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS,
                ),
            )
        }
    }
    ListItem(
        headlineContent = { Text(provider.name) },
        supportingContent = { Text(summary) },
        trailingContent = {
            if (provider.vehicleType != Provider.VehicleType.Inline) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    IconButton(onClick = onUpdateClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_baseline_sync),
                            contentDescription = null,
                        )
                    }
                }
            }
        },
    )
}
