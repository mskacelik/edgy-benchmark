#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
    echo "Usage: $0 <path-to-benchmark.yaml>"
    exit 1
fi

BENCHMARK_FILE="$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
if [[ ! -f "$BENCHMARK_FILE" ]]; then
    echo "Error: benchmark file not found: $BENCHMARK_FILE"
    exit 1
fi

BENCHMARKS_DIR="$(cd "$(dirname "$0")" && pwd)"

BENCHMARK_NAME=$(grep '^name:' "$BENCHMARK_FILE" | awk '{print $2}' | tr -d '\r')
if [[ -z "$BENCHMARK_NAME" ]]; then
    echo "Error: could not determine benchmark name from $BENCHMARK_FILE"
    exit 1
fi

CATEGORY=$(basename "$(dirname "$BENCHMARK_FILE")")
TIMESTAMP=$(date +%Y%m%d-%H%M%S)

REPORT_DIR="$BENCHMARKS_DIR/reports/$CATEGORY/${BENCHMARK_NAME}-${TIMESTAMP}"
mkdir -p "$REPORT_DIR"

REL_BENCHMARK="${BENCHMARK_FILE#$BENCHMARKS_DIR/}"
CONTAINER_BENCHMARK="/benchmarks/$REL_BENCHMARK"
CONTAINER_REPORT_DIR="/benchmarks/reports/$CATEGORY/${BENCHMARK_NAME}-${TIMESTAMP}"

echo "-----------------------------------------------------------"
echo "  Benchmark : $BENCHMARK_NAME"
echo "  Category  : $CATEGORY"
echo "  Report dir: $REPORT_DIR/"
echo "-----------------------------------------------------------"
echo ""
echo "  Once the run completes, execute:"
echo "    report --destination=$CONTAINER_REPORT_DIR && export --destination=$CONTAINER_REPORT_DIR/export.json && exit"
echo "-----------------------------------------------------------"
echo ""

{ printf 'start-local\nupload %s\nrun %s\n' "$CONTAINER_BENCHMARK" "$BENCHMARK_NAME"; cat; } \
    | podman run --rm -i --network=host \
        -v "$BENCHMARKS_DIR:/benchmarks" \
        quay.io/hyperfoil/hyperfoil cli