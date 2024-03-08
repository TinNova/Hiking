package com.tinnovakovic.hiking.shared


import com.tinnovakovic.hiking.data.photo.models.FlickrError
import com.tinnovakovic.hiking.data.photo.models.FlickrPhotos
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class ResultCall<T>(private val delegate: Call<T>) :
    Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>
                ) {

//                    to test bad response
//                    val responseBody = FlickrError(stat="fail", code= 100, errorMessage ="Invalid API Key")


                    if (response.isSuccessful) {
                        val responseBody = when (response.body()) {
                            is FlickrPhotos -> response.body()
                            is FlickrError -> response.body()
                            else -> null
                        }


                        if (responseBody != null) {
                            when (responseBody) {
                                is FlickrPhotos -> {
                                    callback.onResponse(
                                        this@ResultCall,
                                        Response.success(Result.success(responseBody))
                                    )
                                }

                                is FlickrError -> {
                                    //handle it as failure
                                    callback.onResponse(
                                        this@ResultCall,
                                        Response.success(Result.failure(responseBody as Throwable))
                                    )
                                }
                            }
                        }
                    } else {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(
                                Result.failure(
                                    HttpException(response)
                                )
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(Result.failure(java.lang.Exception(t.message, t)))
                    )
                }
            }
        )
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun execute(): Response<Result<T>> {
        return Response.success(Result.success(delegate.execute().body()!!))
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun clone(): Call<Result<T>> {
        return ResultCall(delegate.clone())
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}
