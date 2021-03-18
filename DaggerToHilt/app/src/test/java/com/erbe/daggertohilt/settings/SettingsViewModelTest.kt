package com.erbe.daggertohilt.settings

import com.erbe.daggertohilt.user.UserDataRepository
import com.erbe.daggertohilt.user.UserManager
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SettingsViewModelTest {

    private lateinit var userManager: UserManager
    private lateinit var userDataRepository: UserDataRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        userManager = mock(UserManager::class.java)
        userDataRepository = mock(UserDataRepository::class.java)
        viewModel = SettingsViewModel(userDataRepository, userManager)
    }

    @Test
    fun `Refresh notifications works as expected`() {
        viewModel.refreshNotifications()

        verify(userDataRepository).refreshUnreadNotifications()
    }

    @Test
    fun `Logout works as expected`() {
        viewModel.logout()

        verify(userManager).logout()
    }
}