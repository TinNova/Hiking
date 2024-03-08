package com.tinnovakovic.hiking.shared.network

import com.tinnovakovic.hiking.shared.ApplicationCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

class NetworkStateProviderImpl @Inject constructor(
    @Singleton val applicationCoroutineScope: ApplicationCoroutineScope,
    private val connectivityObserver: ConnectivityObserver,
) : NetworkStateProvider {

    override fun observeNetwork(): Flow<Boolean> {
        val isOnlineFlow = MutableStateFlow(false)
        connectivityObserver
            .observerIsOnline()
            .catch { e -> e.printStackTrace() }
            .onEach {
                isOnlineFlow.value = it
            }
            .launchIn(applicationCoroutineScope.coroutineScope)

        return isOnlineFlow.asStateFlow()
    }
}
