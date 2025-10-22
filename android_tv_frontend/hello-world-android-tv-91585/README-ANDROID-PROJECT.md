# Android/Kotlin Project Location

This repository contains a nested Android TV project.

Android project root directory:
- android_tv_frontend/

Build (Debug):
- cd android_tv_frontend
- ./gradlew assembleDebug

APK Output:
- android_tv_frontend/app/build/outputs/apk/debug/app-debug.apk

Notes:
- The Gradle settings and wrapper live under android_tv_frontend/.
- If your CI or analyzer runs from repo root, point it to android_tv_frontend as the Gradle project root.
