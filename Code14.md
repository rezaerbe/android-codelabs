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