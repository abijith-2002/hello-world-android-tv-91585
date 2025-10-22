# Android Project Root Hint

This repository contains a nested Android project. The Android/Kotlin Gradle project root for builds is:

- android_tv_frontend (this directory)

Use the following to build:
- ./gradlew assembleDebug
- or from workspace root: ./android_tv_frontend/gradlew assembleDebug

Key modules:
- :app
- :list
- :utilities

Gradle settings file:
- settings.gradle (in this directory)

If your CI or analyzer requires an explicit path to the project root, point it to:
hello-world-android-tv-91585/android_tv_frontend
