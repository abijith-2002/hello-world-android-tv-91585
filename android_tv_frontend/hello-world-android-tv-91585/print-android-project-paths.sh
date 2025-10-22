#!/usr/bin/env bash
set -euo pipefail
echo "ANDROID_PROJECT_DIR=android_tv_frontend"
echo "ANDROID_SETTINGS=android_tv_frontend/settings.gradle"
echo "ANDROID_WRAPPER=android_tv_frontend/gradlew"
echo "DEFAULT_BUILD_CMD=./android_tv_frontend/gradlew -p android_tv_frontend :app:assembleDebug"
