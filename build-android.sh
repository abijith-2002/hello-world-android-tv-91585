#!/usr/bin/env bash
# Simple root-level build helper so CI/tools can discover the Android project root.
# This delegates to the Gradle wrapper inside android_tv_frontend.
# Usage:
#   ./build-android.sh assembleDebug
#   ./build-android.sh clean
#   ./build-android.sh app:assembleDebug

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="${SCRIPT_DIR}/android_tv_frontend"

if [[ ! -x "${PROJECT_DIR}/gradlew" ]]; then
  echo "Gradle wrapper not found at ${PROJECT_DIR}/gradlew"
  exit 1
fi

cd "${PROJECT_DIR}"
# If no args provided, default to assembleDebug
if [[ $# -eq 0 ]]; then
  ./gradlew assembleDebug
else
  ./gradlew "$@"
fi

# On success, report the expected artifact paths
APK_DIR="${PROJECT_DIR}/app/build/outputs/apk/debug"
AAB_DIR="${PROJECT_DIR}/app/build/outputs/bundle/debug"

echo "Build finished. If assembleDebug was run, APK should be in: ${APK_DIR}"
if [[ -d "${APK_DIR}" ]]; then
  echo "Contents of APK output dir:"
  ls -lah "${APK_DIR}" || true
fi

if [[ -d "${AAB_DIR}" ]]; then
  echo "Contents of AAB output dir:"
  ls -lah "${AAB_DIR}" || true
fi
