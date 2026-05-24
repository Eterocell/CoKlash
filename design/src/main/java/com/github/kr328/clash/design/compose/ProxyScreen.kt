package com.github.kr328.clash.design.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.model.ProxyState
import kotlinx.coroutines.launch

private val DelayGood = Color(0xFF4CAF50)
private val DelayMedium = Color(0xFFFFC107)
private val DelayPoor = Color(0xFFF44336)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProxyScreen(
    onBackClick: () -> Unit,
    groupNames: List<String>,
    groupProxies: Map<Int, List<Proxy>>,
    groupStates: Map<Int, ProxyState>,
    selectableGroups: Set<Int>,
    onProxySelect: (groupIndex: Int, proxyName: String) -> Unit,
    onUrlTest: (groupIndex: Int) -> Unit,
    urlTestingGroups: Set<Int>,
    onMenuClick: () -> Unit,
    initialPage: Int,
    onPageChanged: (Int) -> Unit,
) {
    if (groupNames.isEmpty()) {
        Scaffold(
            topBar = { ProxyTopBar(onBackClick, onMenuClick) },
        ) { padding ->
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
        }
        return
    }

    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { groupNames.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onPageChanged(pagerState.currentPage)
    }

    Scaffold(
        topBar = { ProxyTopBar(onBackClick, onMenuClick) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onUrlTest(pagerState.currentPage) },
            ) {
                if (pagerState.currentPage in urlTestingGroups) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_flash_on),
                        contentDescription = null,
                    )
                }
            }
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            PrimaryScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
            ) {
                groupNames.forEachIndexed { index, name ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(name) },
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val proxies = groupProxies[page].orEmpty()
                val isSelectable = page in selectableGroups
                val currentNow = groupStates[page]?.now

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(proxies, key = { it.name }) { proxy ->
                        ProxyItem(
                            proxy = proxy,
                            isSelectable = isSelectable,
                            isSelected = proxy.name == currentNow,
                            onClick = if (isSelectable) {
                                { onProxySelect(page, proxy.name) }
                            } else {
                                null
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProxyTopBar(onBackClick: () -> Unit, onMenuClick: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.proxy)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_arrow_back),
                    contentDescription = null,
                )
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_more_vert),
                    contentDescription = null,
                )
            }
        },
    )
}

@Composable
private fun ProxyItem(
    proxy: Proxy,
    isSelectable: Boolean,
    isSelected: Boolean,
    onClick: (() -> Unit)?,
) {
    ListItem(
        headlineContent = { Text(proxy.name) },
        supportingContent = { Text(proxy.subtitle) },
        leadingContent = if (isSelectable) {
            { RadioButton(selected = isSelected, onClick = onClick) }
        } else {
            null
        },
        trailingContent = when {
            proxy.delay > 0 -> {
                {
                    Text(
                        text = "${proxy.delay}ms",
                        color = when {
                            proxy.delay <= 200 -> DelayGood
                            proxy.delay <= 500 -> DelayMedium
                            else -> DelayPoor
                        },
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            proxy.delay == -1 -> {
                {
                    Text(
                        text = "timeout",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            else -> null
        },
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier,
    )
}
