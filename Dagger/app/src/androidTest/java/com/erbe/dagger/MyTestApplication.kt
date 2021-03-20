package com.erbe.dagger

import com.erbe.dagger.di.AppComponent
import com.erbe.dagger.di.DaggerTestAppComponent

/**
 * MyTestApplication will override MyApplication in android tests
 */
class MyTestApplication : MyApplication() {

    override fun initializeComponent(): AppComponent {
        // Creates a new TestAppComponent that injects fakes types
        return DaggerTestAppComponent.create()
    }
}