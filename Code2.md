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