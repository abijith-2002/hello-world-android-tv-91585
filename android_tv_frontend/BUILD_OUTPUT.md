# Android TV App Build Output

This document records the output path for the debug APK built via Gradle.

- Build command:
  - ./gradlew assembleDebug

- Debug APK output:
  - app/build/outputs/apk/debug/app-debug.apk

- Optional convenience script:
  - ./move_apk.sh
    - Moves the APK to: app-debug.apk at the project root of the android_tv_frontend container.

Notes:
- Build was successful using the existing Gradle wrapper and configuration.
- Gradle reported deprecations compatible up to Gradle 9; consider updating before Gradle 10.
