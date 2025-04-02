#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

# Find the jar file and run the test
java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Normalize line endings and compare files
tr -d '\r' < ACTUAL.TXT > ACTUAL-UNIX.TXT
tr -d '\r' < EXPECTED.TXT > EXPECTED-UNIX.TXT

# Compare the normalized files
if diff -w EXPECTED-UNIX.TXT ACTUAL-UNIX.TXT > /dev/null
then
    echo "Test passed!"
    rm ACTUAL-UNIX.TXT EXPECTED-UNIX.TXT  # Clean up temporary files
    exit 0
else
    echo "Test failed!"
    echo "--- Expected ---"
    cat EXPECTED-UNIX.TXT
    echo "--- Actual ---"
    cat ACTUAL-UNIX.TXT
    rm ACTUAL-UNIX.TXT EXPECTED-UNIX.TXT  # Clean up temporary files
    exit 1
fi