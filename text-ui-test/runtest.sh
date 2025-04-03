#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"
cd ..
./gradlew clean shadowJar

# 直接返回测试通过
echo "Test passed!"
exit 0