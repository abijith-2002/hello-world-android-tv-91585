#!/usr/bin/env bash
set -euo pipefail
ROOT="$(dirname "$0")/android_tv_frontend"
if [ -x "$ROOT/gradlew" ]; then
  echo "$ROOT"
  exit 0
else
  echo "Gradle wrapper not found at $ROOT" >&2
  exit 1
fi
