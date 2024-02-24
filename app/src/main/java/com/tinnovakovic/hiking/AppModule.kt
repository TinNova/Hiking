package com.tinnovakovic.hiking

import com.tinnovakovic.hiking.shared.ContextProvider
import com.tinnovakovic.hiking.shared.ContextProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindsContextProvider(contextProviderImpl: ContextProviderImpl): ContextProvider

}
