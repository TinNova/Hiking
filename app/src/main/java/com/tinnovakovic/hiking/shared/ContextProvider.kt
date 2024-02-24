package com.tinnovakovic.hiking.shared

import android.app.Application

interface ContextProvider {

    fun getContext(): Application
}