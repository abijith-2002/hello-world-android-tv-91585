# PROJECT_ROOT_MARKER: ANDROID_GRADLE_ROOT

Android Project Root Marker

This file exists to help automated analyzers determine the Gradle project root.

Use this directory (android_tv_frontend) as the Android project root:
- settings.gradle / settings.gradle.kts
- build.gradle / build.gradle.kts
- Modules: :app, :list, :utilities
- Wrapper: ./gradlew

Example commands:
./gradlew :app:assembleDebug
./gradlew clean

Note:
Other marker files are also present (ANDROID_PROJECT_ROOT.md, PROJECT_ROOT_POINTER.txt, README_PROJECT_ROOT.md). Some tooling requires a simple README marker named ANDROID_PROJECT_ROOT_README.md to resolve the root correctly.
