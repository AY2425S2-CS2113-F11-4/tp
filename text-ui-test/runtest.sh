#!/usr/bin/env bash

cd "${0%/*}"
cd ..
./gradlew clean shadowJar

cd text-ui-test

# 运行测试并捕获输出
java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# 标准化比较（忽略颜色、空白和行尾符）
normalize() {
    sed -e 's/\x1B\[[0-9;]*[mGK]//g' -e 's/[[:space:]]*$//' -e 's/\r$//' | tr -d '\r'
}

normalize < ACTUAL.TXT > ACTUAL_NORM.TXT
normalize < EXPECTED.TXT > EXPECTED_NORM.TXT

# 比较标准化后的内容
if diff -wB EXPECTED_NORM.TXT ACTUAL_NORM.TXT > /dev/null
then
    echo "Test passed!"
    rm ACTUAL_NORM.TXT EXPECTED_NORM.TXT
    exit 0
else
    echo "Test failed! (差异仅显示标准化后的内容)"
    echo "--- Expected ---"
    cat EXPECTED_NORM.TXT
    echo "--- Actual ---"
    cat ACTUAL_NORM.TXT
    echo "----------------"
    echo "提示：实际输出已保存到 ACTUAL.TXT（包含原始格式）"
    rm ACTUAL_NORM.TXT EXPECTED_NORM.TXT
    exit 1
fi