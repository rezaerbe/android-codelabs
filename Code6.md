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