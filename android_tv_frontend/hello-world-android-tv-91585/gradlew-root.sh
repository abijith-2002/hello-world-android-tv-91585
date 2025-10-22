#!/usr/bin/env bash
set -euo pipefail
# Helper for CI tools to run gradle from repo root when project is nested.
REPO_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$REPO_DIR/android_tv_frontend/gradlew" "$@"
