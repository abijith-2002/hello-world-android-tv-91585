Android project modules:
- android_tv_frontend/app (application)
- android_tv_frontend/list (library)
- android_tv_frontend/utilities (library)

Build using the repo root wrapper:
  ../../gradlew assembleDebug

Focus behavior:
- First-row DPAD_UP moves focus to the top menu's first button (menuHome), implemented in HomeActivity.
