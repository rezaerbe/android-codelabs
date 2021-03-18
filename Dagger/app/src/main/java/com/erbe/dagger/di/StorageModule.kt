package com.erbe.dagger.di

import com.erbe.dagger.storage.SharedPreferencesStorage
import com.erbe.dagger.storage.Storage
import dagger.Binds
import dagger.Module

// Tells Dagger this is a Dagger module
@Module
abstract class StorageModule {

    // Makes Dagger provide SharedPreferencesStorage when a Storage type is requested
    @Binds
    abstract fun provideStorage(storage: SharedPreferencesStorage): Storage
}