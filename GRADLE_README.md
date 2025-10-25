Build entry points:
- Root wrapper: ./gradlew (delegates to android_tv_frontend/gradlew)
- Project settings: settings.gradle and settings.gradle.kts at repo root include android_tv_frontend modules.
- Android app module: android_tv_frontend/app

Notes:
- CI should set ANDROID_SDK_ROOT. local.properties is optional and included as a hint.
- The Gradle wrapper JAR is not committed; distributionUrl is set in gradle-wrapper.properties.
