#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "${ROOT}/android_tv_frontend/gradlew" -p "${ROOT}/android_tv_frontend" :app:assembleDebug
