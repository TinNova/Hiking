package com.tinnovakovic.hiking.shared.network

import kotlinx.coroutines.flow.Flow

interface NetworkStateProvider {

    fun observeNetwork(): Flow<Boolean>

}
