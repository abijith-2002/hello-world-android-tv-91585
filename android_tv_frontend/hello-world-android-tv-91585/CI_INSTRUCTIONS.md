# CI Build Instructions (Nested Android Project)

- Android project root: android_tv_frontend/
- Build wrapper: ./android_tv_frontend/gradlew
- Default debug build:
  ./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug

Alternatively, from repo root using shim:
  ./gradlew -p android_tv_frontend :app:assembleDebug
