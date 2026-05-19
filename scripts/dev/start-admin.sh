#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
ADMIN_DIR="$ROOT_DIR/apps/admin"

cd "$ADMIN_DIR"
if [ ! -d node_modules ]; then
  echo "[admin] installing dependencies"
  npm install
fi

echo "[admin] starting Vite on http://localhost:5174"
exec npm run dev
