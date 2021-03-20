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