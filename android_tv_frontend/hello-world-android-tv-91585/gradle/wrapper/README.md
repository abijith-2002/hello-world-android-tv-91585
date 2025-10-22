This directory exists to support CI systems that expect a Gradle wrapper at the repository root.

The authoritative Gradle wrapper lives at:
- android_tv_frontend/gradle/wrapper/

The root-level ./gradlew delegates to the nested wrapper via includeBuild in settings.gradle.
