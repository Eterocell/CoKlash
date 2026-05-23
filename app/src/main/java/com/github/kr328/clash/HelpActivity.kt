package com.github.kr328.clash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.kr328.clash.design.compose.HelpScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme

class HelpActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoKlashTheme {
                HelpScreen(
                    onBackClick = { finish() },
                    onLinkClick = { uri ->
                        startActivity(Intent(Intent.ACTION_VIEW).setData(uri))
                    },
                )
            }
        }
    }
}
