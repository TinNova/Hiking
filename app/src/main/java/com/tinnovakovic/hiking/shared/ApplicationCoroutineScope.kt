package com.tinnovakovic.hiking.shared

import android.util.Log
import androidx.annotation.Keep
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

@Keep
@Singleton
class ApplicationCoroutineScope @Inject constructor() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("ApplicationScope", "Encountered an exception on the application scope: $throwable")
    }

    val coroutineScope =
        CoroutineScope(
            SupervisorJob() +
                    Dispatchers.Main +
                    CoroutineName(ApplicationCoroutineScope::class.java.simpleName) +
                    exceptionHandler
        )
}