package com.tinnovakovic.hiking.shared.network

import kotlinx.coroutines.flow.Flow

interface NetworkStateProvider {

    suspend fun isNetworkStateActive(): Boolean

    fun observeNetwork(): Flow<Boolean>

}
