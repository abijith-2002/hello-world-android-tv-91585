This repository uses a nested Gradle project at android_tv_frontend/.
The actual Gradle wrapper JAR is located at:
  android_tv_frontend/gradle/wrapper/gradle-wrapper.jar

Root-level wrapper settings delegate to the nested project via includeBuild("android_tv_frontend").
CI tools that require a wrapper at the repository root can call:
  ./gradlew :app:assembleDebug
which proxies to android_tv_frontend/gradlew.
