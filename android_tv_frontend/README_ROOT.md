# Android TV Frontend - Project Root

This is the root of the Android/Kotlin multi-module project.

Key markers:
- Gradle wrapper: ./gradlew
- Settings: settings.gradle and settings.gradle.kts
- Modules included: :app, :list, :utilities

Build:
- ./gradlew assembleDebug

Notes for analyzers:
- Treat this directory as the project root for Android/Kotlin analysis.
- APK output: app/build/outputs/apk/debug/app-debug.apk
