# CI Runbook — Android TV Frontend

Android Gradle project root:
- android_tv_frontend/

Build (Debug):
- cd android_tv_frontend && ./gradlew assembleDebug

From repository root (forwarding):
- ./gradlew :app:assembleDebug
- or: ./build-android.sh

Artifacts:
- android_tv_frontend/app/build/outputs/apk/debug/app-debug.apk

Notes:
- Repository root uses settings.gradle includeBuild to forward to android_tv_frontend.
- Root gradlew and gradlew.bat delegate to nested wrappers.
