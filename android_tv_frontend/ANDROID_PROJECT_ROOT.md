# Android Project Root Hint

This directory is the root of the Android TV project.

- Gradle settings file: settings.gradle
- App module: app/
- To build locally: 
  ./gradlew :app:assembleDebug

CI tools relying on root detection should start from this folder.
