# Android root location

The Android project is nested here:
- android_tv_frontend/ (Gradle root with settings.gradle)
- Main app module under android_tv_frontend/app

Build command from this folder:
  ./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug
