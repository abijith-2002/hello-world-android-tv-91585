#!/usr/bin/env bash
# Installer for development tooling extensions in Code Server.
# This script installs recommended VS Code extensions used for Kotlin and theme.
# Usage:
#   chmod +x ./install.sh
#   ./install.sh
set -euo pipefail

# Install Kotlin support and a GitHub theme in code-server if available
code-server --install-extension mathiasfrohlich.Kotlin || true
code-server --install-extension github.github-vscode-theme || true

echo "Development extensions installation completed."
