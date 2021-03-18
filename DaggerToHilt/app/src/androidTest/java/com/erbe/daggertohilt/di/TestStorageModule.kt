package com.erbe.daggertohilt.di

import com.erbe.daggertohilt.storage.FakeStorage
import com.erbe.daggertohilt.storage.Storage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Overrides StorageModule in android tests
@InstallIn(SingletonComponent::class)
@Module
abstract class TestStorageModule {

    // Makes Dagger provide FakeStorage when a Storage type is requested
    @Binds
    abstract fun provideStorage(storage: FakeStorage): Storage
}