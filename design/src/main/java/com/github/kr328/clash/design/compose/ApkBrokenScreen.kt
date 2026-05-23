package com.github.kr328.clash.design.compose

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import com.github.kr328.clash.design.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApkBrokenScreen(onLinkClick: (Uri) -> Unit) {
    val githubUrl = stringResource(R.string.meta_github_url)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.application_broken)) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = AnnotatedString.fromHtml(
                    stringResource(R.string.application_broken_tips),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            Text(
                text = stringResource(R.string.reinstall),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
            )

            ListItem(
                headlineContent = { Text(stringResource(R.string.github_releases)) },
                supportingContent = {
                    Text(
                        text = githubUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                modifier = Modifier.clickable { onLinkClick(Uri.parse(githubUrl)) },
            )
        }
    }
}
