# CI Android Build

Gradle project root (nested):
- android_tv_frontend/

Wrapper:
- android_tv_frontend/gradlew

From repository root, you can build via the forwarding wrapper:
- ./gradlew :app:assembleDebug

Or use the helper scripts:
- ./build.sh
- ./run-android-build.sh
- ./discover-android-root.sh
