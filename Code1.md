## [Use Kotlin Coroutines in your Android App](https://developer.android.com/codelabs/kotlin-coroutines)

In this codelab you'll learn how to use [Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines.html) in an Android app—the recommended way of managing background threads that can simplify code by reducing the need for callbacks. Coroutines are a Kotlin feature that converts async callbacks for long-running tasks, such as database or network access, into *sequential* code.

Here is a code snippet to give you an idea of what you'll be doing.

```
// Async callbacks
networkRequest { result ->
   // Successful network request
   databaseSave(result) { rows ->
     // Result saved
   }
}
```

The callback-based code will be converted to sequential code using coroutines.

```
// The same code with coroutines
val result = networkRequest()
// Successful network request
databaseSave(result)
// Result saved
```

You will start with an existing app, built using [Architecture Components](https://developer.android.com/topic/libraries/architecture/), that uses a callback style for long-running tasks.

By the end of this codelab you will have enough experience to use coroutines in your app to load data from the network, and you will be able to integrate coroutines into an app. You'll also be familiar with best practices for coroutines, and how to write a test against code that uses coroutines.

On Android, it's essential to avoid blocking the main thread. The main thread is a single thread that handles all updates to the UI. It's also the thread that calls all click handlers and other UI callbacks. As such, it has to run smoothly to guarantee a great user experience.

For your app to display to the user without any visible pauses, the main thread has to update the screen [every 16ms or more](https://medium.com/androiddevelopers/exceed-the-android-speed-limit-b73a0692abc1), which is about 60 frames per second. Many common tasks take longer than this, such as parsing large JSON datasets, writing data to a database, or fetching data from the network. Therefore, calling code like this from the main thread can cause the app to pause, stutter, or even freeze. And if you block the main thread for too long, the app may even crash and present an **Application Not Responding** dialog.

### **The callback pattern**

One pattern for performing long-running tasks without blocking the main thread is callbacks. By using callbacks, you can start long-running tasks on a background thread. When the task completes, the callback is called to inform you of the result on the main thread.

Take a look at an example of the callback pattern.

```
// Slow request with callbacks
@UiThread
fun makeNetworkRequest() {
    // The slow network request runs on another thread
    slowFetch { result ->
        // When the result is ready, this callback will get the result
        show(result)
    }
    // makeNetworkRequest() exits after calling slowFetch without waiting for the result
}
```

Because this code is annotated with [`@UiThread`](https://developer.android.com/reference/android/support/annotation/UiThread), it must run fast enough to execute on the main thread. That means, it needs to return very quickly, so that the next screen update is not delayed. However, since `slowFetch` will take seconds or even minutes to complete, the main thread can't wait for the result. The `show(result)` callback allows `slowFetch` to run on a background thread and return the result when it's ready.

#### **Using coroutines to remove callbacks**

Callbacks are a great pattern, however they have a few drawbacks. Code that heavily uses callbacks can become hard to read and harder to reason about. In addition, callbacks don't allow the use of some language features, such as exceptions.

Kotlin coroutines let you convert callback-based code to sequential code. Code written sequentially is typically easier to read, and can even use language features such as exceptions.

In the end, they do the exact same thing: wait until a result is available from a long-running task and continue execution. However, in code they look very different.

The keyword `suspend` is Kotlin's way of marking a function, or function type, available to coroutines. When a coroutine calls a function marked `suspend`, instead of blocking until that function returns like a normal function call, it **suspends** execution until the result is ready then it **resumes** where it left off with the result. While it's suspended waiting for a result, **it unblocks the thread that it's running on** so other functions or coroutines can run.

For example in the code below, `makeNetworkRequest()` and `slowFetch()` are both `suspend` functions.

```
// Slow request with coroutines
@UiThread
suspend fun makeNetworkRequest() {
    // slowFetch is another suspend function so instead of 
    // blocking the main thread  makeNetworkRequest will `suspend` until the result is 
    // ready
    val result = slowFetch()
    // continue to execute after the result is ready
    show(result)
}

// slowFetch is main-safe using coroutines
suspend fun slowFetch(): SlowResult { ... }
```

Just like with the callback version, `makeNetworkRequest` must return from the main thread right away because it's marked `@UiThread`. This means that usually it could not call blocking methods like `slowFetch`. Here's where the `suspend` keyword works its magic.

Compared to callback-based code, coroutine code accomplishes the same result of unblocking the current thread with less code. Due to its sequential style, it's easy to chain several long running tasks without creating multiple callbacks. For example, code that fetches a result from two network endpoints and saves it to the database can be written as a function in coroutines with no callbacks. Like so:

```
// Request data from network and save it to database with coroutines

// Because of the @WorkerThread, this function cannot be called on the
// main thread without causing an error.
@WorkerThread
suspend fun makeNetworkRequest() {
    // slowFetch and anotherFetch are suspend functions
    val slow = slowFetch()
    val another = anotherFetch()
    // save is a regular function and will block this thread
    database.save(slow, another)
}

// slowFetch is main-safe using coroutines
suspend fun slowFetch(): SlowResult { ... }
// anotherFetch is main-safe using coroutines
suspend fun anotherFetch(): AnotherResult { ... }
```

In this codelab we have covered the basics you'll need to start using coroutines in your app!

We covered:

- How to integrate coroutines to Android apps from both the UI and WorkManager jobs to simplify asynchronous programming,
- How to use coroutines inside a `ViewModel` to fetch data from the network and save it to a database without blocking the main thread.
- And how to cancel all coroutines when the `ViewModel` is finished.

For testing coroutine based code, we covered both by testing behavior as well as directly calling `suspend` functions from tests.