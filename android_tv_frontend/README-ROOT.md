# Android TV Project Root

This is the actual Gradle project root for the Android TV app.

Build:
- ./gradlew :app:assembleDebug

Modules:
- :app         -> app/
- :list        -> list/
- :utilities   -> utilities/

If running from the repository root, use the forwarding wrapper:
- ../gradlew :app:assembleDebug (from this folder)
- or at repo root: ./gradlew :app:assembleDebug
