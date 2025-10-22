#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
NESTED="$ROOT_DIR/android_tv_frontend"
if [ -d "$NESTED" ] && [ -f "$NESTED/settings.gradle" ]; then
  echo "android_tv_frontend"
  exit 0
fi
if [ -d "$NESTED" ] && [ -f "$NESTED/settings.gradle.kts" ]; then
  echo "android_tv_frontend"
  exit 0
fi
echo "Unable to resolve nested Android Gradle root" >&2
exit 1
