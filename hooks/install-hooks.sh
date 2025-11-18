#!/bin/bash
#
# Install Git hooks for sb-oauth-java
#
# Usage: ./hooks/install-hooks.sh
#

HOOKS_DIR=".git/hooks"
SOURCE_DIR="hooks"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "Installing Git hooks for sb-oauth-java..."

# Check if we're in a Git repository
if [ ! -d ".git" ]; then
    echo "Error: Not a Git repository. Run this script from the project root."
    exit 1
fi

# Create hooks directory if it doesn't exist
mkdir -p "$HOOKS_DIR"

# Install pre-commit hook
if [ -f "$SOURCE_DIR/pre-commit" ]; then
    cp "$SOURCE_DIR/pre-commit" "$HOOKS_DIR/pre-commit"
    chmod +x "$HOOKS_DIR/pre-commit"
    echo -e "${GREEN}✓${NC} Installed pre-commit hook"
else
    echo -e "${YELLOW}⚠${NC} pre-commit hook not found in $SOURCE_DIR"
fi

echo ""
echo "Git hooks installed successfully!"
echo ""
echo "The pre-commit hook will run:"
echo "  - Checkstyle"
echo "  - SpotBugs (for changed files)"
echo "  - PMD (for changed files)"
echo "  - Unit tests (for changed files)"
echo ""
echo "To bypass the hook (not recommended):"
echo "  git commit --no-verify"
echo ""
