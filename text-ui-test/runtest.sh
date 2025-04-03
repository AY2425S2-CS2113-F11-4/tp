#!/usr/bin/env bash
set -euo pipefail  # 更严格的错误处理

# Change to script directory
cd "${0%/*}"
cd ..
./gradlew --no-daemon clean shadowJar  # 禁用守护进程避免缓存问题

cd text-ui-test

# 确保输出目录存在
mkdir -p test-output

# Run test and capture output (添加超时防止卡死)
timeout 30s java -jar $(find ../build/libs/ -name '*.jar' -print -quit) < input.txt > test-output/ACTUAL_RAW.TXT || {
    echo "Test execution timed out or failed"
    exit 1
}

# 增强的标准化函数 (处理Windows换行符和不可见字符)
normalize_text() {
    sed -e 's/\x1B\[[0-9;]*[mGK]//g' \  # 移除ANSI颜色代码
        -e 's/[[:space:]]\+$//' \        # 移除行尾空白
        -e 's/\r$//' \                   # 移除CR字符
        -e 's/[[:blank:]]/ /g' \         # 标准化空白字符
        -e '/^$/d' | \                   # 移除空行(根据需求可选)
    tr -d '\r' | \                       # 再次确保移除CR
    awk '{printf "%s\n", $0}'            # 确保每行以LF结束
}

# 标准化处理 (输出到特定目录)
normalize_text < test-output/ACTUAL_RAW.TXT > test-output/ACTUAL_CLEAN.TXT
normalize_text < EXPECTED.TXT > test-output/EXPECTED_CLEAN.TXT

# 比较标准化后的输出 (添加详细差异输出)
if diff -wB --strip-trailing-cr \
    --color=never \
    test-output/EXPECTED_CLEAN.TXT \
    test-output/ACTUAL_CLEAN.TXT > test-output/DIFF_OUTPUT.txt
then
    echo " Test passed!"
    # 可选: 保留测试输出供CI检查
    # rm -rf test-output
    exit 0
else
    echo " Test failed!"
    echo
    echo "===== EXPECTED (original) ====="
    cat EXPECTED.TXT
    echo
    echo "===== ACTUAL (original) ====="
    cat test-output/ACTUAL_RAW.TXT
    echo
    echo "===== CLEAN DIFF (side-by-side) ====="
    diff -wB -y --suppress-common-lines \
        test-output/EXPECTED_CLEAN.TXT \
        test-output/ACTUAL_CLEAN.TXT | tee test-output/DIFF_SIDEBYSIDE.txt
    echo
    echo "Note: Full test outputs saved to test-output/"
    echo "::group::View Diff Output"
    cat test-output/DIFF_OUTPUT.txt
    echo "::endgroup::"
    exit 1
fi