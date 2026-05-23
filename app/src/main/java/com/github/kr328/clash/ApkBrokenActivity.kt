package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.github.kr328.clash.design.compose.ApkBrokenScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme

class ApkBrokenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            CoKlashTheme {
                ApkBrokenScreen(
                    onLinkClick = { uri ->
                        startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
                    },
                )
            }
        }
    }
}
