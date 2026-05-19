#!/usr/bin/env bash
# start-web 模块
#
# @author 阿德
# @date 2026/05/15
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
