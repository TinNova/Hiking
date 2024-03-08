package com.tinnovakovic.hiking

import com.google.gson.Gson
import com.tinnovakovic.hiking.data.photo.FlickrApi
import com.tinnovakovic.hiking.shared.ResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient.Builder, gsonConverterFactory: GsonConverterFactory): FlickrApi {
        return Retrofit.Builder()
            .baseUrl("https://api.flickr.com/services/rest/")
            .client(
                okHttpClient
                    .build()
            )
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .build()
            .create(FlickrApi::class.java)
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
    }

    @Provides
    @Singleton
    fun providesGsonConverter(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}
