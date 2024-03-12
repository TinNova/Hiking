package com.tinnovakovic.hiking.shared.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.tinnovakovic.hiking.shared.ApplicationCoroutineScope
import com.tinnovakovic.hiking.shared.ContextProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConnectivityObserverImpl @Inject constructor(
    private val contextProvider: ContextProvider,
    private val applicationCoroutineScope: ApplicationCoroutineScope
) : ConnectivityObserver {

    private val connectivityManager = contextProvider.getContext()
        .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun isOnline(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        Log.d(javaClass.name, "TINTIN isOnline()")
        return connectivityManager
            .getNetworkCapabilities(activeNetwork)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ?: false
    }

    override fun observeIsOnline(): StateFlow<Boolean> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch { send(true) }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch { send(true) }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch { send(false) }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch { send(false) }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.stateIn(
            scope = applicationCoroutineScope.coroutineScope,
            started = Lazily,
            initialValue = isOnline()
        )
    }

}
