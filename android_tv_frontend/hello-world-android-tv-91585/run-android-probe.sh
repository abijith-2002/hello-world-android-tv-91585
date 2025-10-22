#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "$DIR/gradlew" -I "$DIR/gradle-init-probe.gradle.kts" androidProbe
