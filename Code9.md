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