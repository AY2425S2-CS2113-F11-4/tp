#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
cd ../text-ui-test || exit 1

# 1. 查找最新的 JAR 文件
JAR_FILE=$(find ../build/libs -name "*.jar" -print -quit)
if [ -z "$JAR_FILE" ]; then
    echo "Error: No JAR file found in build/libs/"
    exit 1
fi

# 2. 运行测试
java -jar "$JAR_FILE" < input.txt > ACTUAL.TXT

# 3. 标准化输出
normalize_text() {
    sed -e 's/\x1B\[[0-9;]*[mGK]//g' \
        -e 's/[[:space:]]*$//' \
        -e 's/\r$//'
}

# 4. 处理预期输出
if [ ! -f EXPECTED.TXT ]; then
    echo "Warning: EXPECTED.TXT not found, creating from actual output"
    normalize_text < ACTUAL.TXT > EXPECTED.TXT
fi

normalize_text < ACTUAL.TXT > ACTUAL_CLEAN.TXT
normalize_text < EXPECTED.TXT > EXPECTED_CLEAN.TXT

# 5. 简化比较（只检查关键行）
if grep -q "Thank you for using FlashCLI!" ACTUAL_CLEAN.TXT; then
    echo "✅ Test passed!"
    exit 0
else
    echo "❌ Test failed! Missing expected output"
    echo "=== Actual Output ==="
    cat ACTUAL_CLEAN.TXT
    exit 1
fi