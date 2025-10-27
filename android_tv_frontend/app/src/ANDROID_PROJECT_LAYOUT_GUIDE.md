Android/Kotlin Project Layout Guide for Analyzers
================================================

This repository contains an Android TV project with the following Gradle root and app module locations.

- Gradle root directory (contains settings.gradle and settings.gradle.kts):
  ../../

- App module Gradle path: :app
- App module directory: ../../app

Key files:
- Settings: ../../settings.gradle.kts (or ../../settings.gradle)
- Wrapper: ../../gradle/wrapper/gradle-wrapper.properties
- App Manifest: ../../app/src/main/AndroidManifest.xml
- App sources: ../../app/src/main/java
- Resources: ../../app/src/main/res

This file is a purely informational hint for automated analyzers; it does not affect builds or runtime.
