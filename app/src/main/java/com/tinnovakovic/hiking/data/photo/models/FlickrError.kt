package com.tinnovakovic.hiking.data.photo.models

import com.google.gson.annotations.SerializedName

data class FlickrError(
    val stat: String,
    val code: Int,
    @SerializedName("message")
    val errorMessage: String
) : Throwable()