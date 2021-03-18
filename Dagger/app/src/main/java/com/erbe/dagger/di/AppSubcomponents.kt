package com.erbe.dagger.di

import com.erbe.dagger.login.LoginComponent
import com.erbe.dagger.registration.RegistrationComponent
import com.erbe.dagger.user.UserComponent
import dagger.Module

// This module tells a Component which are its subcomponents
@Module(
    subcomponents = [
        RegistrationComponent::class,
        LoginComponent::class,
        UserComponent::class
    ]
)
class AppSubcomponents