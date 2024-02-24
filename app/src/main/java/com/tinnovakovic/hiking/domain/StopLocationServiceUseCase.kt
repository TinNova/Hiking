package com.tinnovakovic.hiking.domain

import android.content.Intent
import com.tinnovakovic.hiking.data.LocationService
import com.tinnovakovic.hiking.shared.ContextProvider
import javax.inject.Inject

class StopLocationServiceUseCase @Inject constructor(private val contextProvider: ContextProvider) {

    fun execute() {
        val context = contextProvider.getContext()

        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            context.startService(this)
        }
    }
}