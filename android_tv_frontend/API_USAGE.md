Android TV App - API Usage and Configuration

Base URL
- Default: https://kavia-alb-6bee460f-433381502.backend.kavia.app/
- Configurable via:
  - Environment variable: API_BASE_URL
  - Gradle property: -PAPI_BASE_URL=<url>
- Code enforces a trailing slash for Retrofit.

Retrofit Endpoints (ApiService)
- GET /api/trending -> suspend fun getTrending(): List<ShowDto>
- GET /api/continue_watching -> suspend fun getContinueWatching(): List<ShowDto>
- GET /api/action -> suspend fun getAction(): List<ShowDto>
- GET /api/family -> suspend fun getFamily(): List<ShowDto>
- GET /api/comedy -> suspend fun getComedy(): List<ShowDto>
- GET /api/horror -> suspend fun getHorror(): List<ShowDto>
- GET /api/drama -> suspend fun getDrama(): List<ShowDto>

Data Models
- ShowDto(name: String, poster: String)
- Domain model for UI: ContentItem(name: String, poster: String?)

Repository Behavior
- CategoryRepository fetches from the corresponding ApiService method.
- On any exception, logs the error and returns sample fallback items so the UI remains functional.

Permissions
- AndroidManifest includes android.permission.INTERNET.

Build
- Build from the container root (recommended):
  cd android_tv_frontend
  ./gradlew :app:assembleDebug

- Or build from the workspace root (alternate):
  ./gradlew :app:assembleDebug
  (Workspace root maps modules to the container project via settings.gradle)

Environment Example
- See .env.example for setting API_BASE_URL.
