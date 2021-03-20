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