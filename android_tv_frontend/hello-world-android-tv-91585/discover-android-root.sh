#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
NESTED="$ROOT_DIR/android_tv_frontend/gradlew"
if [ -x "$NESTED" ]; then
  echo "android_tv_frontend"
  exit 0
else
  echo "ERROR: android_tv_frontend/gradlew not found or not executable" >&2
  exit 3
fi
