package com.tinnovakovic.hiking.shared.network

import com.tinnovakovic.hiking.shared.ApplicationCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.newCoroutineContext
import javax.inject.Inject
import javax.inject.Singleton

class NetworkStateProviderImpl @Inject constructor(
    @Singleton val applicationCoroutineScope: ApplicationCoroutineScope,
    private val connectivityObserver: ConnectivityObserver,
) : NetworkStateProvider {

    //    override fun observeNetwork(): Flow<Boolean> {
//        val isOnlineFlow = MutableStateFlow(false)
//        connectivityObserver
//            .observerIsOnline()
//            .catch { e -> e.printStackTrace() }
//            .onEach {
//                isOnlineFlow.value = it
//            }
//            .launchIn(applicationCoroutineScope.coroutineScope)
//
//        return isOnlineFlow.asStateFlow()
//    }

    override fun observeNetwork(): Flow<Boolean> {
        return connectivityObserver
            .observerIsOnline()
            .catch { e ->
                e.printStackTrace()
                // Emit a default value or handle the error as needed
            }
            .map { isOnline ->
                isOnline // Return the value emitted by onEach
            }
    }

    override suspend fun isNetworkStateActive() = connectivityObserver.isOnline()

}
