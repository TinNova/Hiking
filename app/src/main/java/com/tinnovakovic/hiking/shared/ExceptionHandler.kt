package com.tinnovakovic.hiking.shared

interface ExceptionHandler {

    fun execute(throwable: Throwable): ErrorToUser

}