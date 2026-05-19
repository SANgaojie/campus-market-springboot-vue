#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
WEB_DIR="$ROOT_DIR/apps/web"

cd "$WEB_DIR"
if [ ! -d node_modules ]; then
  echo "[web] installing dependencies"
  npm install
fi

echo "[web] starting Vite on http://localhost:5173"
exec npm run dev
