Android project root hint for CI/scanners:

- Android project root directory: android_tv_frontend
- Gradle settings file: android_tv_frontend/settings.gradle
- Primary app module: android_tv_frontend/app
- Build wrapper: ./android_tv_frontend/gradlew

Suggested build commands:
- ./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug
- ./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleRelease
