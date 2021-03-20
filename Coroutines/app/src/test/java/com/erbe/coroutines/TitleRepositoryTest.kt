package com.erbe.coroutines

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.erbe.coroutines.fakes.MainNetworkCompletableFake
import com.erbe.coroutines.fakes.MainNetworkFake
import com.erbe.coroutines.fakes.TitleDaoFake
import com.erbe.coroutines.main.TitleRefreshError
import com.erbe.coroutines.main.TitleRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TitleRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun whenRefreshTitleSuccess_insertRows() = runBlockingTest {
        val titleDao = TitleDaoFake("title")
        val subject = TitleRepository(
            MainNetworkFake("OK"),
            titleDao
        )

        subject.refreshTitle()
        assertThat(titleDao.nextInsertedOrNull()).isEqualTo("OK")
    }

    @Test(expected = TitleRefreshError::class)
    fun whenRefreshTitleTimeout_throws() = runBlockingTest {
        val network = MainNetworkCompletableFake()
        val subject = TitleRepository(
            network,
            TitleDaoFake("title")
        )

        launch {
            subject.refreshTitle()
        }

        advanceTimeBy(5_000)
    }
}