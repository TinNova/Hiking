package com.tinnovakovic.hiking.domain

import android.content.Intent
import com.tinnovakovic.hiking.data.LocationService
import com.tinnovakovic.hiking.shared.ContextProvider
import javax.inject.Inject

class StartLocationServiceUseCase @Inject constructor(private val contextProvider: ContextProvider) {

    fun execute() {
        val context = contextProvider.getContext()

        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            context.startService(this)
        }
    }

    //TODO
    // -- This about injecting the LocationInMemoryCache here and returning a flow
    // -- of location, as right now that logic is handled in the ViewModel but doesn't
    // -- have to be.

}