# Android Project Root (Proxy)

This repository contains the Android TV project under:
- android_tv_frontend/ (actual Gradle project root)

Build from repo root (proxy):
- ./gradlew :app:assembleDebug

Build directly from project root:
- cd android_tv_frontend
- ./gradlew :app:assembleDebug

Modules:
- :app
- :list
- :utilities
