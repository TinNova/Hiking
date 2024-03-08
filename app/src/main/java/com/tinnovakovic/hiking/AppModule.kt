package com.tinnovakovic.hiking

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tinnovakovic.hiking.data.location.LocationClientImpl
import com.tinnovakovic.hiking.data.location.LocationClient
import com.tinnovakovic.hiking.data.photo.HikingDatabase
import com.tinnovakovic.hiking.data.photo.HikingPhotoDao
import com.tinnovakovic.hiking.shared.ApplicationCoroutineScope
import com.tinnovakovic.hiking.shared.ContextProvider
import com.tinnovakovic.hiking.shared.ContextProviderImpl
import com.tinnovakovic.hiking.shared.network.ConnectivityObserver
import com.tinnovakovic.hiking.shared.network.ConnectivityObserverImpl
import com.tinnovakovic.hiking.shared.network.NetworkStateProvider
import com.tinnovakovic.hiking.shared.network.NetworkStateProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun bindsContextProvider(application: Application): ContextProvider =
        ContextProviderImpl(application)

    @Provides
    @Singleton
    fun bindsLocationClient(
        contextProvider: ContextProvider,
        client: FusedLocationProviderClient
    ): LocationClient = LocationClientImpl(contextProvider, client)

    @Singleton
    @Provides
    fun provideFuseClient(@ApplicationContext context: Context) =
        LocationServices.getFusedLocationProviderClient(context)

    @Singleton
    @Provides
    fun provideNetworkStateProvider(
        applicationCoroutineScope: ApplicationCoroutineScope,
        connectivityObserver: ConnectivityObserver
    ): NetworkStateProvider =
        NetworkStateProviderImpl(applicationCoroutineScope, connectivityObserver)

    @Provides
    @Singleton
    fun providesConnectivityObserver(contextProvider: ContextProvider): ConnectivityObserver =
        ConnectivityObserverImpl(contextProvider)

    @Provides
    @Singleton
    fun provideApplicationScope(
        applicationCoroutineScope: ApplicationCoroutineScope
    ): CoroutineScope {
        return applicationCoroutineScope.coroutineScope
    }

    @Singleton
    @Provides
    fun provideHikingDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, HikingDatabase::class.java, "hiking_database")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideSearchDAO(appDatabase: HikingDatabase): HikingPhotoDao {
        return appDatabase.hikingPhotoDao()
    }
}
