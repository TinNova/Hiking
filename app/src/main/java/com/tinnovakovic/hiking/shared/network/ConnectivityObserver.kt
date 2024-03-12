package com.tinnovakovic.hiking.shared.network

import kotlinx.coroutines.flow.StateFlow

interface ConnectivityObserver {

    fun observeIsOnline(): StateFlow<Boolean>

}
