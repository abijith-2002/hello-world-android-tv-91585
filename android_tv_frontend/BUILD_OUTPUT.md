Build Output and Root Detection Notes

Project root (Android/Kotlin): this directory (android_tv_frontend)

Key files:
- gradlew (Gradle wrapper)
- settings.gradle / settings.gradle.kts
- build.gradle / build.gradle.kts
- app/build.gradle (Android app module)

Build debug APK:
- ./gradlew assembleDebug
- Or: ./gradlew -p . :app:assembleDebug

APK location:
- app/build/outputs/apk/debug/app-debug.apk

If an analyzer reports "Could not determine project root directory":
- Ensure the working directory is android_tv_frontend
- Or pass -p hello-world-android-tv-91585/android_tv_frontend to Gradle-based tools
- Verify presence of gradlew and settings.gradle in this folder
