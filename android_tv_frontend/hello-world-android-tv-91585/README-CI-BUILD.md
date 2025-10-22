# CI Build Instructions (Nested Android Gradle Project)

This repository contains an Android TV application under a nested Gradle project:

- Nested project root: android_tv_frontend/
- Modules: :app, :list, :utilities

Preferred build (from repo root):
- ./gradlew :app:assembleDebug
  (This proxies to android_tv_frontend/gradlew)

Alternate build (from nested root):
- cd android_tv_frontend
- ./gradlew :app:assembleDebug

Discovery markers present for CI:
- settings.gradle (root proxy includes includeBuild("android_tv_frontend"))
- settings.gradle.kts (root proxy, Kotlin DSL)
- build.gradle (root proxy helper)
- gradle.properties (root proxy)
- ANDROID_GRADLE_PROJECT_DIR, ANDROID_PROJECT_ROOT, ANDROID_NESTED_PROJECT_ROOT
- android-project.json, ci.android.json, .android-project
- Makefile (routes to nested Gradle wrapper)
- build.sh (routes to nested Gradle wrapper)

If a tool still cannot detect the root automatically, set working-directory to:
- hello-world-android-tv-91585/android_tv_frontend
and run:
- ./gradlew :app:assembleDebug
