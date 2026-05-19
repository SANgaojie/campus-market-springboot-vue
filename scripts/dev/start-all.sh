#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

cleanup() {
  if [ -n "${BACKEND_PID:-}" ]; then kill "$BACKEND_PID" 2>/dev/null || true; fi
  if [ -n "${WEB_PID:-}" ]; then kill "$WEB_PID" 2>/dev/null || true; fi
  if [ -n "${ADMIN_PID:-}" ]; then kill "$ADMIN_PID" 2>/dev/null || true; fi
}
trap cleanup EXIT INT TERM

"$ROOT_DIR/scripts/dev/start-backend.sh" &
BACKEND_PID=$!

"$ROOT_DIR/scripts/dev/start-web.sh" &
WEB_PID=$!

"$ROOT_DIR/scripts/dev/start-admin.sh" &
ADMIN_PID=$!

wait -n "$BACKEND_PID" "$WEB_PID" "$ADMIN_PID"
