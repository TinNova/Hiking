package com.tinnovakovic.hiking.shared

import android.util.Log
import com.tinnovakovic.hiking.R
import com.tinnovakovic.hiking.data.location.LocationClient
import com.tinnovakovic.hiking.data.photo.models.FlickrError
import com.tinnovakovic.hiking.shared.ErrorToUser.GenericError
import com.tinnovakovic.hiking.shared.ErrorToUser.LocationError
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ExceptionHandlerImpl @Inject constructor(
    private val contextProvider: ContextProvider
) : ExceptionHandler {

    override fun execute(throwable: Throwable): ErrorToUser {

        val context = contextProvider.getContext()

        val errorToLog = when (throwable) {
            is IOException -> "IOException: ${throwable.message}"
            is FlickrError -> "Flickr API Error, Code: ${throwable.code}, Error Message: ${throwable.errorMessage}"
            is HttpException -> "HttpException, Code: ${throwable.code()}, Message: ${throwable.message()}"
            is LocationClient.LocationException -> "Location Permission is missing: ${throwable.message}"
            else -> "Other Exception, Message: ${throwable.message}"
        }

        val errorToUser: ErrorToUser = when (throwable) {
            is LocationClient.LocationException -> LocationError(context.getString(R.string.location_permission_error_message))
            else -> GenericError(context.getString(R.string.generic_network_error_message))
        }

        Log.e(javaClass.name, errorToLog)
        return errorToUser
    }
}

sealed class ErrorToUser {
    data class LocationError(
        val message: String
    ): ErrorToUser()

    data class GenericError(
        val message: String
    ): ErrorToUser()
}
