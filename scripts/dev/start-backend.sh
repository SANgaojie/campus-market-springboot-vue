#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
BACKEND_DIR="$ROOT_DIR/apps/backend"

if [ -d "$ROOT_DIR/../.tools/jdk-17" ]; then
  export JAVA_HOME="$ROOT_DIR/../.tools/jdk-17"
  export PATH="$JAVA_HOME/bin:$PATH"
fi

if [ -d "$ROOT_DIR/../.tools/apache-maven/bin" ]; then
  export PATH="$ROOT_DIR/../.tools/apache-maven/bin:$PATH"
fi

echo "[backend] starting Spring Boot on http://localhost:8080"
cd "$BACKEND_DIR"
exec mvn spring-boot:run
