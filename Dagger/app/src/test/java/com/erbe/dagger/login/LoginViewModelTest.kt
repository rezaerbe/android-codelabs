package com.erbe.dagger.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.erbe.dagger.LiveDataTesUtil
import com.erbe.dagger.user.UserManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as whenever

class LoginViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: LoginViewModel
    private lateinit var userManager: UserManager

    @Before
    fun setup() {
        userManager = mock(UserManager::class.java)
        viewModel = LoginViewModel(userManager)
    }

    @Test
    fun `Get username`() {
        whenever(userManager.username).thenReturn("Username")

        val username = viewModel.getUsername()

        assertEquals("Username", username)
    }

    @Test
    fun `Login emits success`() {
        whenever(userManager.loginUser(anyString(), anyString())).thenReturn(true)

        viewModel.login("username", "login")

        assertEquals(LiveDataTesUtil.getValue(viewModel.loginState), LoginSuccess)
    }

    @Test
    fun `Login emits error`() {
        whenever(userManager.loginUser(anyString(), anyString())).thenReturn(false)

        viewModel.login("username", "login")

        assertEquals(LiveDataTesUtil.getValue(viewModel.loginState), LoginError)
    }

    @Test
    fun `Login unregisters`() {
        viewModel.unregister()

        verify(userManager).unregister()
    }
}