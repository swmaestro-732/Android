#!/usr/bin/env bash
# pre-commit ktlint 래퍼 — 로컬 ktlint 버전을 CI(ci.yml 의 KTLINT_VERSION)와
# 일치시켜 "로컬 통과 → CI 포맷 실패" 드리프트를 막는다. 불일치 시 커밋을 차단한다.
set -euo pipefail

# ⚠️ ci.yml 의 KTLINT_VERSION 과 동일하게 유지할 것.
REQUIRED="1.8.0"

command -v ktlint >/dev/null 2>&1 || {
  echo "❌ ktlint 미설치 → brew install ktlint (버전 ${REQUIRED})"
  exit 1
}

CURRENT="$(ktlint --version | grep -oE '[0-9]+\.[0-9]+\.[0-9]+' | head -1)"
if [ "$CURRENT" != "$REQUIRED" ]; then
  echo "❌ ktlint 버전 불일치: 로컬 ${CURRENT} ≠ CI ${REQUIRED}"
  echo "   CI 와 동일 포맷 규칙을 위해 ${REQUIRED} 설치 필요 (brew upgrade/install ktlint)."
  exit 1
fi

exec ktlint --format --relative "$@"
