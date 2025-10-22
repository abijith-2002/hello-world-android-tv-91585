# CI Build Instructions for Nested Android Project

The Android project lives in android_tv_frontend/.

Preferred build commands:
- From repo root:
  ./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug

Helper scripts:
- ./build-android.sh
- ./.ci-build-android

Project structure:
- Root: android_tv_frontend/
- App module: android_tv_frontend/app
- settings.gradle and gradlew are within android_tv_frontend/
