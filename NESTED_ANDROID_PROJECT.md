Nested Android project layout

- Android Gradle project root: android_tv_frontend
- Gradle wrapper: android_tv_frontend/gradlew
- Settings file(s): android_tv_frontend/settings.gradle(.kts)
- App module: android_tv_frontend/app
- Debug APK: android_tv_frontend/app/build/outputs/apk/debug/app-debug.apk

Build tips:
- cd android_tv_frontend && ./gradlew assembleDebug
- or: ./gradlew -p android_tv_frontend :app:assembleDebug (from workspace root)

If an analyzer cannot determine the root:
- Look for android.projectRoot in local.properties at the workspace root.
- Or read ANDROID_PROJECT_METADATA.json / .project-root.json for explicit paths.
