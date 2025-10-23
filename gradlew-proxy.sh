#!/usr/bin/env bash
# Simple proxy to invoke the Android project's Gradle wrapper from the workspace root.
# Usage examples:
#   ./gradlew-proxy.sh assembleDebug
#   ./gradlew-proxy.sh -p android_tv_frontend :app:assembleDebug

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ANDROID_DIR="${ROOT_DIR}/android_tv_frontend"

if [[ ! -x "${ANDROID_DIR}/gradlew" ]]; then
  echo "Error: Gradle wrapper not found at ${ANDROID_DIR}/gradlew" >&2
  exit 1
fi

cd "${ANDROID_DIR}"
exec ./gradlew "$@"
