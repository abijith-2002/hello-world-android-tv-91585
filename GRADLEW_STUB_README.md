This repository uses a nested Android/Kotlin Gradle project under:
- android_tv_frontend

Some analyzers look for ./gradlew at the workspace root. To accommodate them, a stub has been added:
- gradlew (workspace root)

That stub forwards all commands to:
- android_tv_frontend/gradlew

Recommended commands:
- ./gradlew -p android_tv_frontend assembleDebug
- or: cd android_tv_frontend && ./gradlew assembleDebug

APK location:
- android_tv_frontend/app/build/outputs/apk/debug/app-debug.apk
