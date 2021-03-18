package com.erbe.coroutines

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.erbe.coroutines.fakes.MainNetworkFake
import com.erbe.coroutines.main.RefreshMainDataWork
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RefreshMainDataWorkTest {

    @Test
    fun testRefreshMainDataWork() {
        val fakeNetwork = MainNetworkFake("OK")

        val context = ApplicationProvider.getApplicationContext<Context>()
        val worker = TestListenableWorkerBuilder<RefreshMainDataWork>(context)
                .setWorkerFactory(RefreshMainDataWork.Factory(fakeNetwork))
                .build()

        // Start the work synchronously
        val result = worker.startWork().get()

        assertThat(result).isEqualTo(ListenableWorker.Result.success())
    }
}