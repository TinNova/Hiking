package com.tinnovakovic.hiking.shared

import android.util.Log
import com.tinnovakovic.hiking.R
import com.tinnovakovic.hiking.data.photo.models.FlickrError
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ExceptionHandlerImpl @Inject constructor(
    private val contextProvider: ContextProvider
) : ExceptionHandler {

    override fun execute(throwable: Throwable): String {

        val errorToLog = when (throwable) {
            is IOException -> "IOException: ${throwable.message}"
            is FlickrError -> "Flickr API Error, Code: ${throwable.code}, Error Message: ${throwable.errorMessage}"
            is HttpException -> "HttpException, Code: ${throwable.code()}, Message: ${throwable.message()}"
            else -> "Other Exception, Message: ${throwable.message}"
        }

        Log.e(javaClass.name, errorToLog)
        return contextProvider.getContext().getString(R.string.generic_network_error_message)
    }
}