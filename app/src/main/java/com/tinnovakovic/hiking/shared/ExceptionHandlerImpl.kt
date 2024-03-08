package com.tinnovakovic.hiking.shared

import com.tinnovakovic.hiking.R
import com.tinnovakovic.hiking.data.photo.models.FlickrError
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ExceptionHandlerImpl @Inject constructor(
    private val contextProvider: ContextProvider
) : ExceptionHandler {

    private val context = contextProvider.getContext()

    override fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is IOException -> context.getString(R.string.io_error_message)
            is FlickrError -> context.getString(
                R.string.flick_error_message,
                throwable.code.toString(),
                throwable.errorMessage
            )

            is HttpException -> {
                when (throwable.code()) {
                    in 400..499 -> context.getString(R.string.generic_four_hundred_error_message)
                    in 500..599 -> context.getString(R.string.generic_five_hundred_error_message)
                    else -> context.getString(R.string.unknown_message)
                }
            }

            else -> context.getString(R.string.unknown_message)
        }
    }
}
