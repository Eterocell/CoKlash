package com.github.kr328.clash

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.core.model.Provider
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.compose.ProvidersScreen
import com.github.kr328.clash.design.compose.theme.CoKlashTheme
import com.github.kr328.clash.util.withClash
import kotlinx.coroutines.launch

class ProvidersActivity : BaseComposeActivity() {
    private var providers by mutableStateOf<List<Provider>>(emptyList())
    private var updatingIndices by mutableStateOf<Set<Int>>(emptySet())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            providers = withClash { queryProviders().sorted() }
        }

        setContent {
            CoKlashTheme {
                ProvidersScreen(
                    onBackClick = { finish() },
                    providers = providers,
                    updatingIndices = updatingIndices,
                    onUpdateClick = { index, provider -> updateProvider(index, provider) },
                    onUpdateAllClick = { updateAll() },
                )
            }
        }
    }

    override fun onProfileLoaded() {
        super.onProfileLoaded()
        lifecycleScope.launch {
            val newList = withClash { queryProviders().sorted() }
            if (newList != providers) {
                startActivity(ProvidersActivity::class.intent)
                finish()
            }
        }
    }

    private fun updateProvider(index: Int, provider: Provider) {
        updatingIndices = updatingIndices + index
        lifecycleScope.launch {
            try {
                withClash { updateProvider(provider.type, provider.name) }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ProvidersActivity,
                    getString(R.string.format_update_provider_failure, provider.name, e.message),
                    Toast.LENGTH_LONG,
                ).show()
            } finally {
                updatingIndices = updatingIndices - index
            }
        }
    }

    private fun updateAll() {
        providers.forEachIndexed { index, provider ->
            if (provider.vehicleType != Provider.VehicleType.Inline && index !in updatingIndices) {
                updateProvider(index, provider)
            }
        }
    }
}
