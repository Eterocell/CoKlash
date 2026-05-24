package com.github.kr328.clash

import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.design.compose.SettingsScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme

class SettingsActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoKlashTheme {
                SettingsScreen(
                    onBackClick = { finish() },
                    onAppClick = { startActivity(AppSettingsActivity::class.intent) },
                    onNetworkClick = { startActivity(NetworkSettingsActivity::class.intent) },
                    onOverrideClick = { startActivity(OverrideSettingsActivity::class.intent) },
                    onMetaFeatureClick = { startActivity(MetaFeatureSettingsActivity::class.intent) },
                )
            }
        }
    }
}
