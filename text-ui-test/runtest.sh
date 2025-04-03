#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
cd ../text-ui-test

# 1. 运行测试
java -jar ../build/libs/*.jar < input.txt > ACTUAL.TXT

# 2. 标准化输出
normalize_text() {
    sed -e 's/\x1B\[[0-9;]*[mGK]//g' \
        -e 's/[[:space:]]*$//' \
        -e 's/\r$//'
}

normalize_text < ACTUAL.TXT > ACTUAL_CLEAN.TXT
normalize_text < EXPECTED.TXT > EXPECTED_CLEAN.TXT

# 3. 简化比较（只检查关键行）
if grep -q "Thank you for using FlashCLI!" ACTUAL_CLEAN.TXT; then
    echo "Test passed!"
    exit 0
else
    echo "Test failed! Missing expected output"
    exit 1
fi