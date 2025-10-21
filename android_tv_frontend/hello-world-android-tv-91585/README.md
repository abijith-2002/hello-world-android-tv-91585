# Android TV Container

This directory is the Android project root used by tooling and CI.

How to build locally:
- Use the Gradle wrapper here:
  ./gradlew :app:assembleDebug

Project layout (modules are physically under android_tv_frontend/):
- :app        -> android_tv_frontend/app
- :list       -> android_tv_frontend/list
- :utilities  -> android_tv_frontend/utilities

Notes:
- settings.gradle.kts in this directory maps the modules above.
- The nested android_tv_frontend/settings.gradle is not required for the top-level build; the container root settings controls the build graph.
- Content Info screen uses a local theme override; Nord theme does not apply on that Activity.
