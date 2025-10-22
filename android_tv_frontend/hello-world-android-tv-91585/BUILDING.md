# Building in this monorepo

Android Gradle root:
- android_tv_frontend/

Recommended:
- ./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug

Alternative entrypoints (equivalent):
- ./build-android.sh
- ./build.sh
- ./android_tv_frontend/build.sh
- make android
- make -f Makefile.android android-build

Notes:
- Root-level gradlew is a shim that delegates to android_tv_frontend/gradlew
- Primary module: android_tv_frontend/app
- Other modules: android_tv_frontend/list, android_tv_frontend/utilities
