package com.github.kr328.clash.navigation.destinations

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.github.kr328.clash.AppEvent
import com.github.kr328.clash.AppViewModel
import com.github.kr328.clash.core.model.Provider
import com.github.kr328.clash.design.compose.ProvidersScreen
import com.github.kr328.clash.util.withClash
import kotlinx.coroutines.launch
import com.github.kr328.clash.design.R as DesignR

@Composable
fun ProvidersDestination(navController: NavHostController, appViewModel: AppViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var providers by remember { mutableStateOf<List<Provider>>(emptyList()) }
    var updatingIndices by remember { mutableStateOf<Set<Int>>(emptySet()) }

    LaunchedEffect(Unit) {
        providers = withClash { queryProviders().sorted() }
    }

    LaunchedEffect(Unit) {
        appViewModel.events.collect { event ->
            when (event) {
                is AppEvent.ProfileLoaded -> {
                    val newList = withClash { queryProviders().sorted() }
                    providers = newList
                }
                else -> {}
            }
        }
    }

    fun updateProvider(index: Int, provider: Provider) {
        updatingIndices = updatingIndices + index
        scope.launch {
            try {
                withClash { updateProvider(provider.type, provider.name) }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(DesignR.string.format_update_provider_failure, provider.name, e.message),
                    Toast.LENGTH_LONG,
                ).show()
            } finally {
                updatingIndices = updatingIndices - index
            }
        }
    }

    ProvidersScreen(
        onBackClick = { navController.popBackStack() },
        providers = providers,
        updatingIndices = updatingIndices,
        onUpdateClick = { index, provider -> updateProvider(index, provider) },
        onUpdateAllClick = {
            providers.forEachIndexed { index, provider ->
                if (provider.vehicleType != Provider.VehicleType.Inline && index !in updatingIndices) {
                    updateProvider(index, provider)
                }
            }
        },
    )
}
