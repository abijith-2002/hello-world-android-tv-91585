Android/Kotlin project root hints

Project root: this directory (android_tv_frontend)

Key indicators:
- gradlew present
- settings.gradle(.kts) present
- build.gradle(.kts) present
- modules: :app, :list, :utilities

Build debug APK:
- ./gradlew assembleDebug
- or: ./gradlew -p . :app:assembleDebug

Debug APK output:
- app/build/outputs/apk/debug/app-debug.apk

If a tool cannot detect the root, run it from this folder or pass:
- -p hello-world-android-tv-91585/android_tv_frontend
