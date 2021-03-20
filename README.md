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

## [Learn advanced coroutines with Kotlin Flow and LiveData](https://developer.android.com/codelabs/advanced-kotlin-coroutines)

In this codelab, you'll learn how to use the [`LiveData` builder](https://developer.android.com/topic/libraries/architecture/coroutines#livedata) to combine [Kotlin coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) with [`LiveData`](https://developer.android.com/topic/libraries/architecture/livedata) in an Android app. We'll also use [Coroutines Asynchronous Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html), which is a type from the coroutines library for representing an async sequence (or stream) of values, to implement the same thing.

You'll start with an existing app, built using [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/), that uses `LiveData` to get a list of objects from a [`Room`](http://developer.android.com/room) database and display them in a `RecyclerView` grid layout.

Here are some code snippets to give you an idea of what you'll be doing. Here is the existing code to query the Room database:

```
val plants: LiveData<List<Plant>> = plantDao.getPlants()
```

The `LiveData` will be updated using the `LiveData` builder and coroutines with additional sorting logic:

```
val plants: LiveData<List<Plant>> = liveData<List<Plant>> {
   val plantsLiveData = plantDao.getPlants()
   val customSortOrder = plantsListSortOrderCache.getOrAwait()
   emitSource(plantsLiveData.map { plantList -> plantList.applySort(customSortOrder) })
}
```

You'll also implement the same logic with `Flow`:

```
private val customSortFlow = plantsListSortOrderCache::getOrAwait.asFlow()

val plantsFlow: Flow<List<Plant>>
   get() = plantDao.getPlantsFlow()
       .combine(customSortFlow) { plants, sortOrder ->
           plants.applySort(sortOrder)
       }
       .flowOn(defaultDispatcher)
       .conflate()
```

A flow is an asynchronous version of a [Sequence](https://kotlinlang.org/docs/reference/sequences.html), a type of collection whose values are lazily produced. Just like a sequence, a flow produces each value on-demand whenever the value is needed, and flows can contain an infinite number of values.

So, why did Kotlin introduce a new `Flow` type, and how is it different than a regular sequence? The answer lies in the magic of asynchronicity. `Flow` includes full support for coroutines. That means you can build, transform, and consume a `Flow` using coroutines. You can also control concurrency, which means coordinating the execution of several coroutines declaratively with `Flow`.

`Flow` can be used in a fully-reactive programming style. If you've used something like `RxJava` before, `Flow` provides similar functionality. Application logic can be expressed succinctly by transforming a flow with functional operators such as [`map`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/map.html), [`flatMapLatest`](https://kotlinlang.org/docs/reference/coroutines/flow.html#flatmaplatest), [`combine`](https://kotlinlang.org/docs/reference/coroutines/flow.html#combine), and so on.

`Flow` also supports suspending functions on most operators. This lets you do sequential async tasks inside an operator like `map`. By using suspending operations inside of a flow, it often results in shorter and easier to read code than the equivalent code in a fully-reactive style.

In this codelab, we're going to explore using both approaches.

### **How does flow run**

To get used to how Flow produces values on demand (or lazily), take a look at the following flow that emits the values `(1, 2, 3)` and prints before, during, and after each item is produced.

```
fun makeFlow() = flow {
   println("sending first value")
   emit(1)
   println("first value collected, sending another value")
   emit(2)
   println("second value collected, sending a third value")
   emit(3)
   println("done")
}

scope.launch {
   makeFlow().collect { value ->
       println("got $value")
   }
   println("flow is completed")
}
```

If you run this, it produces this output:

```
sending first value
got 1
first value collected, sending another value
got 2
second value collected, sending a third value
got 3
done
flow is completed
```

You can see how execution bounces between the [`collect`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/collect.html) lambda and the [`flow`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/flow.html) builder. Every time the flow builder calls [`emit`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow-collector/emit.html), it `suspends` until the element is completely processed. Then, when another value is requested from the flow, it `resumes` from where it left off until it calls emit again. When the `flow` builder completes, the `Flow` is cancelled and `collect` resumes, letting and the calling coroutine prints "flow is completed."

The call to `collect` is very important. `Flow` uses suspending operators like `collect` instead of exposing an `Iterator` interface so that it always knows when it's being actively consumed. More importantly, it knows when the caller can't request any more values so it can cleanup resources.

### **When does a flow run**

The `Flow` in the above example starts running when the `collect` operator runs. Creating a new `Flow` by calling the `flow` builder or other APIs does not cause any work to execute. The suspending operator `collect` is called a **terminal operator** in `Flow`. There are other suspending terminal operators such as [`toList`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/to-list.html), [`first`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/first.html) and [`single`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/single.html) shipped with `kotlinx-coroutines`, and you can build your own.

By default `Flow` will execute`:`

- Every time a terminal operator is applied (and each new invocation is independent from any previously started ones)
- Until the coroutine it is running in is cancelled
- When the last value has been fully processed, and another value has been requested

Because of these rules, a `Flow` can participate in structured concurrency, and it's safe to start long-running coroutines from a `Flow`. There's no chance a `Flow` will leak resources, since they're always cleaned up using [coroutine cooperative cancellation rules](https://kotlinlang.org/docs/reference/coroutines/cancellation-and-timeouts.html#cancellation-is-cooperative) when the caller is cancelled.

Lets modify the flow above to only look at the first two elements using the [`take`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/take.html) operator, then collect it twice.

```
scope.launch {
   val repeatableFlow = makeFlow().take(2)  // we only care about the first two elements
   println("first collection")
   repeatableFlow.collect()
   println("collecting again")
   repeatableFlow.collect()
   println("second collection completed")
}
```

Running this code, you'll see this output:

```
first collection
sending first value
first value collected, sending another value
collecting again
sending first value
first value collected, sending another value
second collection completed
```

The `flow` lambda starts from the top each time `collect` is called. This is important if the flow performed expensive work like making a network request. Also, since we applied the [`take(2)`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/take.html) operator, the flow will only produce two values. It will not resume the flow lambda again after the second call to `emit`, so the line "second value collected..." will never print.

## [Background Work with WorkManager](https://developer.android.com/codelabs/android-workmanager)

WorkManager is part of [Android Jetpack](http://d.android.com/jetpack) and an [Architecture Component](http://d.android.com/arch) for background work that needs a combination of opportunistic and guaranteed execution. Opportunistic execution means that WorkManager will do your background work as soon as it can. Guaranteed execution means that WorkManager will take care of the logic to start your work under a variety of situations, even if you navigate away from your app.

WorkManager is an incredibly flexible library that has many additional benefits. These include:

- Support for both asynchronous one-off and periodic tasks
- Support for constraints such as network conditions, storage space, and charging status
- Chaining of complex work requests, including running work in parallel
- Output from one work request used as input for the next
- Handles API level compatibility back to API level 14 (see note)
- Works with or without Google Play services
- Follows system health best practices
- LiveData support to easily display work request state in UI

The WorkManager library is a good choice for tasks that are useful to complete, even if the user navigates away from the particular screen or your app.

Some examples of tasks that are a good use of WorkManager:

- Uploading logs
- Applying filters to images and saving the image
- Periodically syncing local data with the network

WorkManager offers guaranteed execution, and not all tasks require that. As such, it is not a catch-all for running every task off of the main thread. For more details about when to use WorkManager, check out the [Guide to background processing](https://d.android.com/guide/background/).

Congratulations! You've finished the Blur-O-Matic app and in the process learned about:

- Adding WorkManager to your Project
- Scheduling a `OneOffWorkRequest`
- Input and Output parameters
- Chaining work together `WorkRequest`s
- Naming Unique `WorkRequest` chains
- Tagging `WorkRequest`s
- Displaying `WorkInfo` in the UI
- Cancelling `WorkRequest`s
- Adding constraints to a `WorkRequest`

## [Advanced WorkManager](https://developer.android.com/codelabs/android-adv-workmanager)

This codelab teaches advanced WorkManager concepts. It builds on the basic material covered in the [Background Work with WorkManager](https://developer.android.com/codelabs/android-workmanager/index.html) codelab.

Other resources available to get familiar with WorkManager are:

- [WorkManager Guide](https://developer.android.com/topic/libraries/architecture/workmanager)
- Blog series: [Introducing WorkManager](https://medium.com/androiddevelopers/introducing-workmanager-2083bcfc4712)
- ADS 2019 WorkManager talk: [WorkManager: Beyond the Basics](https://youtu.be/Bz0z694SrEE)

Congratulations! You've finished the Blur-O-Matic app, and in the process you learned how to:

- Create a custom configuration
- Publish progress from your Worker
- Display work progress in the UI
- Write tests for your Workers

## [Using Dagger in your Android app](https://developer.android.com/codelabs/android-dagger)

In this codelab you'll learn the importance of Dependency Injection (DI) to create a solid and extensible application that scales to large projects. We'll use [Dagger](https://dagger.dev/) as the DI tool to manage dependencies.

Dependency injection (DI) is a technique widely used in programming and well suited to Android development. By following the principles of DI, you lay the groundwork for a good app architecture.

Implementing dependency injection provides you with the following advantages:

- Reusability of code.
- Ease of refactoring.
- Ease of testing.

You're now familiar with Dagger and you should be able to add it to your Android app. In this codelab you learned about:

- How to create an Application graph using Dagger `@Component` annotation.
- How to add information to the graph using `@Inject`, `@Module`, `@Binds` and `@BindsInstance` annotations.
- How to create flow containers using `@Subcomponent`.
- How to reuse instances of objects in different containers using Scopes.
- Dagger Qualifiers and `@Provides` annotation.
- How to test your application that uses Dagger with unit and instrumentation tests.

## [Using Hilt in your Android app](https://developer.android.com/codelabs/android-hilt)

In this codelab you'll learn the importance of [dependency injection](https://developer.android.com/training/dependency-injection) (DI) to create a solid and extensible application that scales to large projects. We'll use Hilt as the DI tool to manage dependencies.

Dependency injection is a technique widely used in programming and well-suited to Android development. By following the principles of DI, you lay the groundwork for a good app architecture.

Implementing dependency injection provides you with the following advantages:

- Reusability of code
- Ease of refactoring
- Ease of testing

Hilt is an opinionated dependency injection library for Android that reduces the boilerplate of using manual DI in your project. Doing [manual dependency injection](https://developer.android.com/training/dependency-injection/manual) requires constructing every class and its dependencies by hand and using containers to reuse and manage dependencies.

Hilt provides a standard way to do DI injection in your application by providing containers to every Android component in your project and managing the container's lifecycle automatically for you. This is done by leveraging the popular DI library: [Dagger](https://developer.android.com/training/dependency-injection/dagger-basics).

You're now familiar with Hilt and you should be able to add it to your Android app. In this codelab you learned about:

- How to set up Hilt in your Application class using `@HiltAndroidApp`.
- How to add dependency containers to the different Android lifecycle components using `@AndroidEntryPoint`.
- How to use modules to tell Hilt how to provide certain types.
- How to use qualifiers to provide multiple bindings for certain types.
- How to test your app using Hilt.
- When `@EntryPoint` is useful and how to use it.

## [Migrating your Dagger app to Hilt](https://developer.android.com/codelabs/android-dagger-to-hilt)

In this codelab you'll learn how you migrate Dagger to Hilt for dependency injection (DI) in an Android app. This codelab migrates the [Using Dagger in your Android app codelab](https://developer.android.com/codelabs/android-dagger) to Hilt. This codelab aims to show you how to plan your migration and keep Dagger and Hilt working side by side during the migration by keeping the app functional while you migrate each Dagger component to Hilt.

Dependency injection helps with reusability of code, ease of refactoring and ease of testing. Hilt is built on top of the popular DI library [Dagger](https://developer.android.com/training/dependency-injection/dagger-basics) to benefit from the **compile time correctness, runtime performance, scalability, and** [**Android Studio support**](https://medium.com/androiddevelopers/dagger-navigation-support-in-android-studio-49aa5d149ec9) that Dagger provides.

Since many Android framework classes are instantiated by the OS itself, there's an associated boilerplate when using Dagger in Android apps. Hilt removes most of this boilerplate by automatically generating and providing:

- **Components for integrating Android framework classes** with Dagger that you would otherwise need to create by hand.
- **Scope annotations** for the components that Hilt generates automatically.
- **Predefined bindings and qualifiers**.

Best of all, as Dagger and Hilt can coexist together, apps can be migrated on an as-needed basis.

Congratulations! You've successfully migrated an app to use Hilt! Not only you completed the migration but you also kept the application working while migrating Dagger components one by one.

In this codelab you've learned how to start with the Application component and build the groundwork necessary to make Hilt work with existing Dagger components. From there you migrated each Dagger Component to Hilt by using Hilt Annotations on Activities and Fragments and removing Dagger related code. Each time you finished migrating a component, the app worked and functioned as expected. You also migrated `Context` and `ApplicationContext` dependencies with the Hilt provided `@ActivityContext` and `@ApplicationContext` annotations. You've migrated other Android components. Finally, you've migrated the tests and finish migrating to Hilt.

## [Working with Preferences DataStore](https://developer.android.com/codelabs/android-preferences-datastore)

DataStore is a new and improved data storage solution aimed at replacing SharedPreferences. Built on Kotlin coroutines and Flow, DataStore provides two different implementations: **Proto DataStore**, that stores **typed objects** (backed by [protocol buffers](https://developers.google.com/protocol-buffers)) and **Preferences DataStore**, that stores **key-value pairs**. Data is stored asynchronously, consistently, and transactionally, overcoming some of the drawbacks of SharedPreferences.

Often you might find yourself needing to store small or simple data sets. For this, in the past, you might have used SharedPreferences but this API also has a series of drawbacks. Jetpack DataStore library aims at addressing those issues, creating a simple, safer and asynchronous API for storing data. It provides 2 different implementations:

- Preferences DataStore
- Proto DataStore

| **Feature**                                                | **SharedPreferences**                                        | **PreferencesDataStore**                             | **ProtoDataStore**                                           |
| ---------------------------------------------------------- | ------------------------------------------------------------ | ---------------------------------------------------- | ------------------------------------------------------------ |
| Async API                                                  | ✅ (only for reading changed values, via [listener](https://developer.android.com/reference/android/content/SharedPreferences.OnSharedPreferenceChangeListener)) | ✅ (via `Flow` and RxJava 2 & 3 `Flowable`)           | ✅ (via `Flow` and RxJava 2 & 3 `Flowable`)                   |
| Synchronous API                                            | ✅ (but not safe to call on UI thread)                        | ❌                                                    | ❌                                                            |
| Safe to call on UI thread                                  | ❌1                                                           | ✅ (work is moved to `Dispatchers.IO` under the hood) | ✅ (work is moved to `Dispatchers.IO` under the hood)         |
| Can signal errors                                          | ❌                                                            | ✅                                                    | ✅                                                            |
| Safe from runtime exceptions                               | ❌2                                                           | ✅                                                    | ✅                                                            |
| Has a transactional API with strong consistency guarantees | ❌                                                            | ✅                                                    | ✅                                                            |
| Handles data migration                                     | ❌                                                            | ✅                                                    | ✅                                                            |
| Type safety                                                | ❌                                                            | ❌                                                    | ✅ with [Protocol Buffers](https://developers.google.com/protocol-buffers) |

1 SharedPreferences has a synchronous API that can appear safe to call on the UI thread, but which actually does disk I/O operations. Furthermore, `apply()` blocks the UI thread on `fsync()`. Pending `fsync()` calls are triggered every time any service starts or stops, and every time an activity starts or stops anywhere in your application. The UI thread is blocked on pending `fsync()` calls scheduled by `apply()`, often becoming a source of [ANRs](https://developer.android.com/topic/performance/vitals/anr).

2 SharedPreferences throws parsing errors as runtime exceptions.

### Preferences vs Proto DataStore

While both Preferences and Proto DataStore allow saving data, they do this in different ways:

- **Preference DataStore**, like SharedPreferences, accesses data based on keys, without defining a schema upfront.
- **Proto DataStore** defines the schema using [Protocol buffers](https://developers.google.com/protocol-buffers). Using Protobufs allows **persisting strongly typed data**. They are faster, smaller, simpler, and less ambiguous than XML and other similar data formats. While Proto DataStore requires you to learn a new serialization mechanism, we believe that the strongly typed advantage brought by Proto DataStore is worth it.

### **Room vs DataStore**

If you have a need for partial updates, referential integrity, or large/complex datasets, you should consider using Room instead of DataStore. DataStore is ideal for small or simple datasets and does not support partial updates or referential integrity.

Now that you migrated to Preferences DataStore let's recap what we've learned:

- SharedPreferences comes with a series of drawbacks - from synchronous API that can appear safe to call on the UI thread, no mechanism of signaling errors, lack of transactional API and more.
- DataStore is a replacement for SharedPreferences addressing most of the shortcomings of the API.
- DataStore has a fully asynchronous API using Kotlin coroutines and Flow, handles data migration, guarantees data consistency and handles data corruption.

## [Working with Proto DataStore](https://developer.android.com/codelabs/android-proto-datastore)

DataStore is a new and improved data storage solution aimed at replacing SharedPreferences. Built on Kotlin coroutines and Flow, DataStore provides two different implementations: **Proto DataStore**, that stores **typed objects** (backed by [protocol buffers](https://developers.google.com/protocol-buffers)) and **Preferences DataStore**, that stores **key-value pairs**. Data is stored asynchronously, consistently, and transactionally, overcoming some of the drawbacks of SharedPreferences.

Often you might find yourself needing to store small or simple data sets. For this, in the past, you might have used SharedPreferences but this API also has a series of drawbacks. Jetpack DataStore library aims at addressing those issues, creating a simple, safer and asynchronous API for storing data. It provides 2 different implementations:

- Preferences DataStore
- Proto DataStore

| **Feature**                                                | **SharedPreferences**                                        | **PreferencesDataStore**                             | **ProtoDataStore**                                           |
| ---------------------------------------------------------- | ------------------------------------------------------------ | ---------------------------------------------------- | ------------------------------------------------------------ |
| Async API                                                  | ✅ (only for reading changed values, via [listener](https://developer.android.com/reference/android/content/SharedPreferences.OnSharedPreferenceChangeListener)) | ✅ (via `Flow` and RxJava 2 & 3 `Flowable`)           | ✅ (via `Flow` and RxJava 2 & 3 `Flowable`)                   |
| Synchronous API                                            | ✅ (but not safe to call on UI thread)                        | ❌                                                    | ❌                                                            |
| Safe to call on UI thread                                  | ❌1                                                           | ✅ (work is moved to `Dispatchers.IO` under the hood) | ✅ (work is moved to `Dispatchers.IO` under the hood)         |
| Can signal errors                                          | ❌                                                            | ✅                                                    | ✅                                                            |
| Safe from runtime exceptions                               | ❌2                                                           | ✅                                                    | ✅                                                            |
| Has a transactional API with strong consistency guarantees | ❌                                                            | ✅                                                    | ✅                                                            |
| Handles data migration                                     | ❌                                                            | ✅                                                    | ✅                                                            |
| Type safety                                                | ❌                                                            | ❌                                                    | ✅ with [Protocol Buffers](https://developers.google.com/protocol-buffers) |

1 SharedPreferences has a synchronous API that can appear safe to call on the UI thread, but which actually does disk I/O operations. Furthermore, `apply()` blocks the UI thread on `fsync()`. Pending `fsync()` calls are triggered every time any service starts or stops, and every time an activity starts or stops anywhere in your application. The UI thread is blocked on pending `fsync()` calls scheduled by `apply()`, often becoming a source of [ANRs](https://developer.android.com/topic/performance/vitals/anr).

2 SharedPreferences throws parsing errors as runtime exceptions.

### Preferences vs Proto DataStore

While both Preferences and Proto DataStore allow saving data, they do this in different ways:

- **Preference DataStore**, like SharedPreferences, accesses data based on keys, without defining a schema upfront.
- **Proto DataStore** defines the schema using [Protocol buffers](https://developers.google.com/protocol-buffers). Using Protobufs allows **persisting strongly typed data**. They are faster, smaller, simpler, and less ambiguous than XML and other similar data formats. While Proto DataStore requires you to learn a new serialization mechanism, we believe that the strongly typed advantage brought by Proto DataStore is worth it.

### **Room vs DataStore**

If you have a need for partial updates, referential integrity, or large/complex datasets, you should consider using Room instead of DataStore. DataStore is ideal for small or simple datasets and does not support partial updates or referential integrity.

Now that you migrated to Preferences DataStore let's recap what we've learned:

- SharedPreferences comes with a series of drawbacks - from synchronous API that can appear safe to call on the UI thread, no mechanism of signaling errors, lack of transactional API and more.
- DataStore is a replacement for SharedPreferences addressing most of the shortcomings of the API.
- DataStore has a fully asynchronous API using Kotlin coroutines and Flow, handles data migration, guarantees data consistency and handles data corruption.

## [Data Binding in Android](https://developer.android.com/codelabs/android-databinding)

The [Data Binding Library](https://developer.android.com/topic/libraries/data-binding) is an [Android Jetpack library](https://developer.android.com/jetpack) that allows you to bind UI components in your XML layouts to data sources in your app using a **declarative** format rather than programmatically, reducing boilerplate code.

Congratulations! You completed the codelab, so you should know how to create Data Binding layouts, add variables and expressions to it, use observable data, and make your XML layouts more meaningful with custom attributes via custom Binding Adapters.

## [Android Paging](https://developer.android.com/codelabs/android-paging)

The Paging library makes it easier for you to load data incrementally and gracefully within your app's UI. The Paging API provides support for many of the functionalities that you would otherwise need to implement manually when you need to load data in pages:

- Keeps track of the keys to be used for retrieving the next and previous page.
- Automatically requests the correct page when the user has scrolled to the end of the list.
- Ensures that multiple requests aren't triggered at the same time.
- Allows you to cache data: if you're using Kotlin, this is done in a [`CoroutineScope`](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/); if you're using Java, this can be done with `LiveData`.
- Tracks loading state and allows you to display it in a `RecyclerView` list item or elsewhere in your UI, and easily retry failed loads.
- Allows you to execute common operations like `map` or `filter` on the list that will be displayed, independently of whether you're using `Flow`, `LiveData`, or RxJava `Flowable` or `Observable`.
- Provides an easy way of implementing list separators.

The [Guide to app architecture](https://developer.android.com/topic/libraries/architecture/guide.html) proposes an architecture with the following main components:

- A local database that serves as a single source of truth for data presented to the user and manipulated by the user.
- A web API service.
- A repository that works with the database and the web API service, providing a unified data interface.
- A `ViewModel` that provides data specific to the UI.
- The UI, which shows a visual representation of the data in the `ViewModel`.

The Paging library works with all of these components and coordinates the interactions between them, so that it can load "pages" of content from a data source and display that content in the UI.

This codelab introduces you to the Paging library and its main components:

- **`PagingData`** - a container for paginated data. Each refresh of data will have a separate corresponding `PagingData`.
- **`PagingSource`** - a `PagingSource` is the base class for loading snapshots of data into a stream of `PagingData`.
- **`Pager.flow`** - builds a `Flow<PagingData>`, based on a `PagingConfig` and a function that defines how to construct the implemented `PagingSource`.
- **`PagingDataAdapter`** - a `RecyclerView.Adapter` that presents `PagingData` in a `RecyclerView`. The `PagingDataAdapter` can be connected to a Kotlin `Flow`, a `LiveData`, an RxJava `Flowable`, or an RxJava `Observable`. The `PagingDataAdapter` listens to internal `PagingData` loading events as pages are loaded and uses `DiffUtil` on a background thread to compute fine-grained updates as updated content is received in the form of new `PagingData` objects.
- `RemoteMediator` - helps implement pagination from network and database.

In this codelab, you will implement examples of each of the components described above.

Now that we have added all the components, let's recap what we've learned!

- The `PagingSource` asynchronously loads the data from a source you define.
- The `Pager.flow` creates a `Flow<PagingData>` based on a configuration and a function that defineshow to instantiate the `PagingSource`.
- The `Flow` emits a new `PagingData` whenever new data is loaded by the `PagingSource`.
- The UI observes the changed `PagingData` and uses a `PagingDataAdapter` to update the `RecyclerView` that presents the data.
- To retry a failed load from the UI, use the `PagingDataAdapter.retry` method. Under the hood, the Paging library will trigger the `PagingSource.load()` method.
- To add separators to your list, create a high-level type with separators as one of the supported types. Then use the `PagingData.insertSeparators()` method to implement your separator generation logic.
- To display the load state as header or footer, use `PagingDataAdapter.withLoadStateHeaderAndFooter()` method and implement a `LoadStateAdapter`. If you want to execute other actions based on the load state, use the `PagingDataAdapter.addLoadStateListener()` callback.
- To work with the network and database, implement a `RemoteMediator`.

## [People: Conversations and Bubbles](https://developer.android.com/codelabs/android-people)

In this codelab, you will learn how to show your notifications in the *Conversations* section of the notification shade. You will also learn how to show those notifications as *Bubbles*.

[Conversations](https://developer.android.com/guide/topics/ui/conversations) are real-time communications that are bidirectional among two people or more. These conversations are presented in their own area in the system notifications shade. They can also be promoted as [Bubbles](https://developer.android.com/guide/topics/ui/bubbles). Bubbles are round icons that float on top of other app content and follow the user wherever they go. Bubbles are great for showing ongoing events and actions that users want to keep easy access to.

Bubbles are a great way to notify users of important ongoing information. You should, however, be mindful of when and when not to use Bubbles. They take up screen real estate and cover other app content. Use them only when it is important enough to draw the user's attention. Keep in mind that users can easily opt out of Bubbles and use normal notifications instead.

This codelab uses a proof-of-concept chat app as its starting point. The app shows new incoming messages, which we'll assume for the purposes of this codelab that users will want to keep in mind for a while.

Congratulations! You now know how to show a conversation notification and a bubble.

## Material Components (MDC)

### [MDC-101 Android: Material Components (MDC) Basics (Kotlin)](https://codelabs.developers.google.com/codelabs/mdc-101-kotlin)

**Material Design** is a system for building bold and beautiful digital products. By uniting style, branding, interaction, and motion under a consistent set of principles and components, product teams can realize their greatest design potential.

For Android applications, **Material Components for Android** (**MDC Android**) unites design and engineering with a library of components for creating consistency across your app. As the Material Design system evolves, these components are updated to ensure consistent pixel-perfect implementation and adherence to Google's front-end development standards. MDC is also available for the web, iOS, and Flutter.

In this codelab, you'll build a login page using several of MDC Android's components.

Using basic XML markup and ~30 lines of Kotlin, the Material Components for Android library has helped you create a beautiful login page that conforms to the Material Design guidelines, and also looks and behaves consistently across all devices.

### [MDC-102 Android: Material Structure and Layout (Kotlin)](https://codelabs.developers.google.com/codelabs/mdc-102-kotlin)

In codelab MDC-101, you used two Material Components (MDC) to build a login page: text fields and buttons with ink ripples. Now let's expand upon this foundation by adding navigation, structure, and data.

Our app has a basic flow that takes the user from the login screen to a home screen, where products can be viewed. In just a few lines of code, we added a top app bar with a title and three buttons, and a grid of cards to present our app's content. Our home screen is now simple and functional, with a basic structure and actionable content.

### [MDC-103 Android: Material Theming with Color, Elevation and Type (Kotlin)](https://codelabs.developers.google.com/codelabs/mdc-103-kotlin)

In codelabs MDC-101 and MDC-102, you used Material Components (MDC) to build the basics of an app called **Shrine**, an e-commerce app that sells clothing and home goods. This app contains a user flow that starts with a login screen and takes the user to the home screen that displays products.

The recent expansion of Material Design gives designers and developers increased flexibility to express their product's brand. You can now use MDC to customize Shrine and reflect its unique style.

By now, you've created an app that resembles the design specifications from your designer.

### [MDC-104 Android: Material Advanced Components (Kotlin)](https://codelabs.developers.google.com/codelabs/mdc-104-kotlin)

In codelab MDC-103, you customized the color, elevation, and typography, of Material Components (MDC) to style your app.

A component in the Material Design system performs a set of predefined tasks and has certain characteristics, like a button. However, a button is more than just a way for a user to perform an action, it's also a visual expression of shape, size, and color that lets the user know that it's interactive, and that something will happen upon touch or click.

The Material Design guidelines describe components from a designer's point of view. They describe a wide range of basic functions available across platforms, as well as the anatomic elements that make up each component. For instance, a backdrop contains a back layer and its content, the front layer and its content, motion rules, and display options. Each of these components can be customized for each app's needs, use cases, and content. These pieces are, for the most part, traditional views, controls, and functions from your platform's SDK.

While the Material Design guidelines name many components, not all of them are good candidates for reusable code and therefore aren't found in MDC. You can create these experiences yourself to achieve a customized style for your app, all using traditional code.

Over the course of these four codelabs, you've seen how to use Material Components to build unique, elegant user experiences that express a brand's personality and style.

## [Building Beautiful Transitions with Material Motion for Android](https://developer.android.com/codelabs/material-motion-android)

The **Material motion system** for Android is a set of transition patterns within the MDC-Android library that can help users understand and navigate an app, as described in the [Material Design guidelines](https://material.io/design/motion/the-motion-system.html).

The four main Material transition patterns are as follows:

- **Container Transform:** transitions between UI elements that include a container; creates a visible connection between two distinct UI elements by seamlessly transforming one element into another.
- **Shared Axis:** transitions between UI elements that have a spatial or navigational relationship; uses a shared transformation on the x, y, or z axis to reinforce the relationship between elements.
- **Fade Through:** transitions between UI elements that do not have a strong relationship to each other; uses a sequential fade out and fade in, with a scale of the incoming element.
- **Fade:** used for UI elements that enter or exit within the bounds of the screen.

The MDC-Android library offers transition classes for these patterns, built on top of both the [AndroidX Transition library](https://developer.android.com/reference/androidx/transition/package-summary) (`androidx.transition`) and the [Android Transition Framework](https://developer.android.com/training/transitions) (`android.transition`):

**AndroidX**

- Available in the `com.google.android.material.transition` package
- Supports API Level 14+
- Supports Fragments and Views, but not Activities or Windows
- Contains backported bug fixes and consistent behavior across API Levels

**Framework**

- Available in the `com.google.android.material.transition.platform` package
- Supports API Level 21+
- Supports Fragments, Views, Activities, and Windows
- Bug fixes not backported and may have different behavior across API Levels

In this codelab you will be using the Material transitions built on top of the AndroidX library, meaning you will be mainly focused on Fragments and Views.

Using less than 100 lines of Kotlin code and some basic XML markup, the MDC-Android library has helped you create beautiful transitions in an existing app that conforms to the Material Design guidelines, and also looks and behaves consistently across all Android devices.