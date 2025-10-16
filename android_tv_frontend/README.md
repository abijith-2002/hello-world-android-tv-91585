# declarative-samples-android-app
A sample Android application written in the Declarative Gradle DSL, using the prototype Declarative Gradle `androidApplication` Software Type defined in the `org.gradle.experimental.android-ecosystem` ecosystem plugin.

## Building and Running

This sample shows the definition of a multiproject Android application implemented using Kotlin 2.0.21 source code.
The project is the result of reproducing the project produced by the `gradle init` command in Gradle 8.9 as an Android project.

To build the project without running, use:

```shell
  ./gradlew build
```

To run the application, first install it on a connected Android device using:

```shell
  :app:installDebug
```

Then search for "Sample Declarative Gradle Android App" and launch app to see a hello world message.

## Debugging visibility (temporary)
- The card content has a temporary debug background overlay (semi-transparent red/green) to verify visibility on TV devices/emulators.
- Once you confirm the text is visible with good contrast, you can remove the debug overlays:
  - In `activity_main.xml`, remove `cardContentDebug` background and container or revert to the previous version.
  - In `MainActivity.kt`, remove the block that sets the debug background color on `cardContentDebug`.