#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"
cd ..
./gradlew clean shadowJar

cd text-ui-test

# Run test and capture output
java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL_RAW.TXT

# Normalization function
normalize_text() {
    sed -e 's/\x1B\[[0-9;]*[mGK]//g' \
        -e 's/[[:space:]]*$//' \
        -e 's/\r$//' | \
    tr -d '\r' | \
    awk 'NF || !NF {printf "%s", $0; if (!NF) print ""}'
}

# Normalize both files
normalize_text < ACTUAL_RAW.TXT > ACTUAL_CLEAN.TXT
normalize_text < EXPECTED.TXT > EXPECTED_CLEAN.TXT

# Compare normalized output
if diff -wB --strip-trailing-cr EXPECTED_CLEAN.TXT ACTUAL_CLEAN.TXT > /dev/null
then
    echo "Test passed!"
    rm ACTUAL_CLEAN.TXT EXPECTED_CLEAN.TXT
    exit 0
else
    echo "Test failed!"
    echo
    echo "===== EXPECTED (original) ====="
    cat EXPECTED.TXT
    echo
    echo "===== ACTUAL (original) ====="
    cat ACTUAL_RAW.TXT
    echo
    echo "===== CLEAN DIFF ====="
    diff -wB -y --suppress-common-lines EXPECTED_CLEAN.TXT ACTUAL_CLEAN.TXT
    echo
    echo "Note: Raw output saved to ACTUAL_RAW.TXT"
    exit 1
fi