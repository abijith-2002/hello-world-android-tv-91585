# Android Gradle project root

The Android project root is located at:
- android_tv_frontend/

Build from repository root:
- ./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug

If your CI requires a root-level wrapper:
- Use ./gradlew (shim) which delegates to android_tv_frontend/gradlew

Primary module:
- android_tv_frontend/app

Other modules:
- android_tv_frontend/list
- android_tv_frontend/utilities
