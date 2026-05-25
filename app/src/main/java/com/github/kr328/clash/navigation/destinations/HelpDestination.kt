package com.github.kr328.clash.navigation.destinations

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.design.compose.HelpScreen

@Composable
fun HelpDestination(navController: NavHostController) {
    val context = LocalContext.current
    HelpScreen(
        onBackClick = { navController.popBackStack() },
        onLinkClick = { uri ->
            context.startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
        },
    )
}
