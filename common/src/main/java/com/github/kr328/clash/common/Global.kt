package com.github.kr328.clash.common

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

object Global : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    val application: Application
        get() = _application

    private lateinit var _application: Application

    fun init(application: Application) {
        this._application = application
    }

    fun destroy() {
        cancel()
    }
}
