package com.erbe.dagger.main

import com.erbe.dagger.user.UserDataRepository
import javax.inject.Inject

/**
 * MainViewModel is the ViewModel that [MainActivity] uses to
 * obtain information of what to show on the screen.
 *
 * @Inject tells Dagger how to provide instances of this type. Dagger also knows
 * that UserDataRepository is a dependency.
 */
class MainViewModel @Inject constructor(private val userDataRepository: UserDataRepository) {

    val welcomeText: String
        get() = "Hello ${userDataRepository.username}!"

    val notificationsText: String
        get() = "You have ${userDataRepository.unreadNotifications} unread notifications"
}