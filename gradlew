#!/usr/bin/env bash
# Proxy Gradle wrapper at repository root.
# Delegates to the actual Gradle wrapper under android_tv_frontend.
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SUB_DIR="${ROOT_DIR}/android_tv_frontend"
if [[ ! -x "${SUB_DIR}/gradlew" ]]; then
  echo "Error: Gradle wrapper not found at android_tv_frontend/gradlew" >&2
  exit 1
fi

# If caller already passed -p, do not override. Otherwise, add -p android_tv_frontend.
EXTRA_ARGS=()
if [[ " $* " != *" -p "* ]]; then
  EXTRA_ARGS=(-p android_tv_frontend)
fi

exec "${SUB_DIR}/gradlew" "${EXTRA_ARGS[@]}" "$@"
