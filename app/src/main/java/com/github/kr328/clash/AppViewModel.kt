package com.github.kr328.clash

import androidx.lifecycle.ViewModel
import com.github.kr328.clash.remote.Broadcasts
import com.github.kr328.clash.remote.Remote
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AppViewModel : ViewModel(), Broadcasts.Observer {

    private val _clashRunning = MutableStateFlow(Remote.broadcasts.clashRunning)
    val clashRunning: StateFlow<Boolean> = _clashRunning.asStateFlow()

    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 16)
    val events: SharedFlow<AppEvent> = _events.asSharedFlow()

    init {
        Remote.broadcasts.addObserver(this)
    }

    override fun onCleared() {
        super.onCleared()
        Remote.broadcasts.removeObserver(this)
    }

    override fun onServiceRecreated() {
        _clashRunning.value = false
        _events.tryEmit(AppEvent.ServiceRecreated)
    }

    override fun onStarted() {
        _clashRunning.value = true
        _events.tryEmit(AppEvent.ClashStarted)
    }

    override fun onStopped(cause: String?) {
        _clashRunning.value = false
        _events.tryEmit(AppEvent.ClashStopped(cause))
    }

    override fun onProfileChanged() {
        _events.tryEmit(AppEvent.ProfileChanged)
    }

    override fun onProfileUpdateCompleted(uuid: UUID?) {
        _events.tryEmit(AppEvent.ProfileUpdateCompleted(uuid))
    }

    override fun onProfileUpdateFailed(uuid: UUID?, reason: String?) {
        _events.tryEmit(AppEvent.ProfileUpdateFailed(uuid, reason))
    }

    override fun onProfileLoaded() {
        _events.tryEmit(AppEvent.ProfileLoaded)
    }
}

sealed interface AppEvent {
    data object ServiceRecreated : AppEvent
    data object ClashStarted : AppEvent
    data class ClashStopped(val cause: String?) : AppEvent
    data object ProfileChanged : AppEvent
    data class ProfileUpdateCompleted(val uuid: UUID?) : AppEvent
    data class ProfileUpdateFailed(val uuid: UUID?, val reason: String?) : AppEvent
    data object ProfileLoaded : AppEvent
}
