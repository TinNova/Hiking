package com.tinnovakovic.hiking.shared

interface ExceptionHandler {

    fun getErrorMessage(throwable: Throwable): String

}