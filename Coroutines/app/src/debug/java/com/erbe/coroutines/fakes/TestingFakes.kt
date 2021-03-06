package com.erbe.coroutines.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.erbe.coroutines.main.MainNetwork
import com.erbe.coroutines.main.Title
import com.erbe.coroutines.main.TitleDao
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

/**
 * Fake [TitleDao] for use in tests.
 */
class TitleDaoFake(initialTitle: String) : TitleDao {
    /**
     * A channel is a Coroutines based implementation of a blocking queue.
     *
     * We're using it here as a buffer of inserted elements.
     *
     * This uses a channel instead of a list to allow multiple threads to call insertTitle and
     * synchronize the results with the test thread.
     */
    private val insertedForNext = Channel<Title>(capacity = Channel.BUFFERED)

    private val _titleLiveData = MutableLiveData<Title?>(Title(initialTitle))

    override val titleLiveData: LiveData<Title?>
        get() = _titleLiveData

    override suspend fun insertTitle(title: Title) {
        insertedForNext.send(title)
        _titleLiveData.value = title
    }

    /**
     * Assertion that the next element inserted has a title of expected
     *
     * If the element was previously inserted and is currently the most recent element
     * this assertion will also match. This allows tests to avoid synchronizing calls to insert
     * with calls to assertNextInsert.
     *
     * If multiple items were inserted, this will always match the first item that was not
     * previously matched.
     *
     * @param expected the value to match
     * @param timeout duration to wait (this is provided for instrumentation tests that may run on
     *                multiple threads)
     * @param unit timeunit
     * @return the next value that was inserted into this dao, or null if none found
     */
    fun nextInsertedOrNull(timeout: Long = 2_000): String? {
        var result: String? = null
        runBlocking {
            // wait for the next insertion to complete
            try {
                withTimeout(timeout) {
                    result = insertedForNext.receive().title
                }
            } catch (ex: TimeoutCancellationException) {
                // ignore
            }
        }
        return result
    }
}

/**
 * Testing Fake implementation of MainNetwork
 */
class MainNetworkFake(var result: String) : MainNetwork {
    override suspend fun fetchNextTitle() = result
}

/**
 * Testing Fake for MainNetwork that lets you complete or error all current requests
 */
class MainNetworkCompletableFake() : MainNetwork {
    private var completable = CompletableDeferred<String>()

    override suspend fun fetchNextTitle(): String = completable.await()

    fun sendCompletionToAllCurrentRequest(result: String) {
        completable.complete(result)
        completable = CompletableDeferred()
    }

    fun sendErrorToCurrentRequest(throwable: Throwable) {
        completable.completeExceptionally(throwable)
        completable = CompletableDeferred()
    }
}