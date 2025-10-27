Per-Section Loaders Status
==========================

- Implementation: COMPLETE.
  - Global/full-screen loader: Removed.
  - HomeViewModel: Exposes per-section StateFlow<Boolean> loading flags and data lists.
  - HomeActivity: Binds each rail (Trending, Continue Watching, Drama) to its respective loading and data flows.
  - Layouts (activity_home.xml, view_rail.xml): Updated to inline loaders inside each section; no overlay loader present.
  - Repository: Returns empty lists on failures to allow non-blocking empty-state rendering.

- Remaining CI issue (external):
  - Error: "Could not determine project root directory for Android/Kotlin project"
  - Cause: Analyzer/CI tool is not starting from the Android Gradle root.
  - Fix (CI configuration):
    - Working directory should be set to: hello-world-android-tv-91585/android_tv_frontend
    - This directory contains settings.gradle(.kts) and is the Gradle root.
    - App module is :app at hello-world-android-tv-91585/android_tv_frontend/app

This note is for CI/tools maintainers. No further app/src changes are required for the loader task.
