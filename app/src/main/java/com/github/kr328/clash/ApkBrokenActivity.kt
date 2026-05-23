package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.kr328.clash.design.compose.ApkBrokenScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme

class ApkBrokenActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
