#!/usr/bin/env bash
set -euo pipefail
cat <<'JSON'
{ "project_type": "android-gradle", "root": "android_tv_frontend", "wrapper": "android_tv_frontend/gradlew", "module": ":app" }
JSON
