Android TV Project Build Instructions

Structure:
- Root Gradle project (this folder) includes mapped modules:
  - :app -> android_tv_frontend/app
  - :list -> android_tv_frontend/list
  - :utilities -> android_tv_frontend/utilities

Build from workspace root:
- Linux/macOS:
  ./gradlew :app:assembleDebug

- Windows:
  gradlew.bat :app:assembleDebug

Alternate (inside module folder):
- cd android_tv_frontend
- ./gradlew :app:assembleDebug

Config:
- API base URL can be set via environment or Gradle property API_BASE_URL.
- Default: https://kavia-alb-6bee460f-433381502.backend.kavia.app/

Environment example:
- See android_tv_frontend/.env.example
