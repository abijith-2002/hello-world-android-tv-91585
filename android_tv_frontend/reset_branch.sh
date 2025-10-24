#!/bin/bash
# Reset kavia-main to the current branch's commit

# Exit immediately if a command fails
set -e

# Get current branch name
current_branch=$(git rev-parse --abbrev-ref HEAD)

echo "Current branch: $current_branch"

# Make sure we are not already on kavia-main
if [ "$current_branch" = "kavia-main" ]; then
  echo "You're already on kavia-main. No reset needed."
  exit 1
fi

# Fetch the latest refs
git fetch origin

# Ensure kavia-main exists locally
if ! git show-ref --verify --quiet refs/heads/kavia-main; then
  echo "Creating local branch kavia-main..."
  git branch kavia-main origin/kavia-main || git branch kavia-main
fi

# Reset kavia-main to the current branch's commit
echo "Resetting kavia-main to match $current_branch..."
git checkout kavia-main
git reset --hard "$current_branch"
# Push the reset to remote (force update)
echo "Pushing reset to remote..."
git push origin kavia-main --force

echo "✅ kavia-main successfully reset to $current_branch"

