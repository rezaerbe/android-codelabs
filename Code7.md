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