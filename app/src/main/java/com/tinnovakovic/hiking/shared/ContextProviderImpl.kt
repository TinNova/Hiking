package com.tinnovakovic.hiking.shared

import android.app.Application
import javax.inject.Inject

class ContextProviderImpl @Inject constructor(
    private val appContext: Application
) : ContextProvider {

    override fun getContext() = appContext

}