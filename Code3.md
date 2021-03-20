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