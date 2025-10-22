# Gradle Root (Forwarding)

This repository uses a forwarding Gradle root to a nested Android project.

- Actual Android project root: android_tv_frontend/
- Root `settings.gradle` uses `includeBuild("android_tv_frontend")` to delegate.
- Root `./gradlew` delegates to `android_tv_frontend/gradlew`.

Build from repo root:
- ./gradlew :app:assembleDebug

Or build from nested root:
- cd android_tv_frontend && ./gradlew assembleDebug

Artifacts:
- android_tv_frontend/app/build/outputs/apk/debug/app-debug.apk
