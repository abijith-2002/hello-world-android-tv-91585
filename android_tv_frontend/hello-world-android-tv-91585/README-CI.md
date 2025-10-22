Android TV project discovery

Location:
  android_tv_frontend/

Root markers inside android_tv_frontend/:
  - settings.gradle
  - gradlew / gradlew.bat
  - gradle/wrapper/gradle-wrapper.properties

Build commands:
  cd android_tv_frontend
  ./gradlew assembleDebug
  ./gradlew build

If invoking from the workspace root:
  ./gradlew build   # uses the shim to forward into android_tv_frontend
