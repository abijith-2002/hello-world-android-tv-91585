# PROJECT_ROOT_MARKER: ANDROID_GRADLE_ROOT

Android Project Root

This directory (android_tv_frontend) is the Gradle root for the Android TV app.

Key files:
- settings.gradle / settings.gradle.kts
- build.gradle / build.gradle.kts
- gradlew (Gradle wrapper)
- Modules: :app, :list, :utilities

Tooling notes:
- Some analyzers require a simple text marker to resolve the project root. This file serves that purpose in addition to the existing PROJECT_ROOT_POINTER.txt and ANDROID_PROJECT_ROOT.md.
- Use this directory as the working directory when running Gradle tasks, e.g.:
  ./gradlew :app:assembleDebug
