#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
exec java -jar dist/fastpos-admin.jar --spring.profiles.active=demo
