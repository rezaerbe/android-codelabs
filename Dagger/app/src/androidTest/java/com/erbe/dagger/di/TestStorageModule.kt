package com.erbe.dagger.di

import com.erbe.dagger.storage.FakeStorage
import com.erbe.dagger.storage.Storage
import dagger.Binds
import dagger.Module

// Overrides StorageModule in android tests
@Module
abstract class TestStorageModule {

    // Makes Dagger provide FakeStorage when a Storage type is requested
    @Binds
    abstract fun provideStorage(storage: FakeStorage): Storage
}