# Android Project Root (Monorepo)

Gradle root: android_tv_frontend/
Wrapper: android_tv_frontend/gradlew
Settings: android_tv_frontend/settings.gradle

Primary module:
- android_tv_frontend/app

Other modules:
- android_tv_frontend/list
- android_tv_frontend/utilities

Build (from repo root):
- ./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug
