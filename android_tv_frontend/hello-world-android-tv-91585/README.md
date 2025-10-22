# Project Repository

This repository hosts a nested Android TV project.

Android project root:
- android_tv_frontend/

Build from repository root (forwarding):
- ./gradlew :app:assembleDebug

Build from Android project root:
- cd android_tv_frontend
- ./gradlew assembleDebug

Artifacts:
- android_tv_frontend/app/build/outputs/apk/debug/app-debug.apk

Notes:
- The repository root contains a forwarding settings.gradle that includes the nested android_tv_frontend build.
- The root ./gradlew delegates to android_tv_frontend/gradlew.
