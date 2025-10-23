# Repository Root - Android Project Delegation

This repository contains the actual Android TV project under:
- android_tv_frontend/

For convenience and to support tooling that expects a Gradle project at the repository root:
- Root-level Gradle files delegate to android_tv_frontend.
- Use either:
  - ./gradlew assembleDebug   (delegates with -p android_tv_frontend)
  - ./android_tv_frontend/gradlew -p android_tv_frontend assembleDebug

APK output path:
- android_tv_frontend/app/build/outputs/apk/debug/app-debug.apk
