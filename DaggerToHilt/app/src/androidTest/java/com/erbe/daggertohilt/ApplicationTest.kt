package com.erbe.daggertohilt

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.erbe.daggertohilt.di.StorageModule
import com.erbe.daggertohilt.main.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test

@UninstallModules(StorageModule::class)
@HiltAndroidTest
class ApplicationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Test
    fun runApp() {
        ActivityScenario.launch(MainActivity::class.java)

        // Should be in Registration/EnterDetails because the user is not registered
        onView(withText("Register to Dagger World!")).check(matches(isDisplayed()))
        onView(withId(R.id.username)).perform(typeText("username"), closeSoftKeyboard())
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard())
        onView(withId(R.id.next)).perform(click())

        // Registration/T&Cs
        onView(withText("Terms and Conditions")).check(matches(isDisplayed()))
        onView(withText("REGISTER")).perform(click())

        // Main
        onView(withText("Hello username!")).check(matches(isDisplayed()))
        onView(withText("SETTINGS")).perform(click())

        // Settings
        onView(withText("REFRESH NOTIFICATIONS")).check(matches(isDisplayed()))
        onView(withText("LOGOUT")).perform(click())

        // Login
        onView(withText("Welcome to Dagger World!")).check(matches(isDisplayed()))
        onView(withId(R.id.password)).perform(typeText("password"), closeSoftKeyboard())
        onView(withText("LOGIN")).perform(click())

        // Main
        onView(withText("Hello username!")).check(matches(isDisplayed()))
        onView(withText("SETTINGS")).perform(click())

        // Settings
        onView(withText("REFRESH NOTIFICATIONS")).check(matches(isDisplayed()))
        onView(withText("LOGOUT")).perform(click())

        // Login
        onView(withText("Welcome to Dagger World!")).check(matches(isDisplayed()))
        onView(withId(R.id.unregister)).perform(click())

        // Registration
        onView(withText("Register to Dagger World!")).check(matches(isDisplayed()))
    }
}