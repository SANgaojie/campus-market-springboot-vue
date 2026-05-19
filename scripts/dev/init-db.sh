#!/usr/bin/env bash
# init-db 模块
#
# @author 阿德
# @date 2026/05/11
set -euo pipefail

DB_NAME="${DB_NAME:-campus_market}"
DB_USERNAME="${DB_USERNAME:-campus_app}"
DB_PASSWORD="${DB_PASSWORD:-campus_dev_password}"
MYSQL_ROOT_USER="${MYSQL_ROOT_USER:-root}"

cat <<SQL | mysql -u"$MYSQL_ROOT_USER" -p
CREATE DATABASE IF NOT EXISTS ${DB_NAME} DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS '${DB_USERNAME}'@'localhost' IDENTIFIED BY '${DB_PASSWORD}';
GRANT ALL PRIVILEGES ON ${DB_NAME}.* TO '${DB_USERNAME}'@'localhost';
FLUSH PRIVILEGES;
SQL

echo "[db] database and app user are ready. Schema migrations run automatically via Flyway when backend starts."
