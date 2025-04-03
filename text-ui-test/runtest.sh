#!/usr/bin/env bash

# Change to script directory
cd "${0%/*}"

cd ..
./gradlew clean shadowJar

cd text-ui-test

# Find the jar file and run the test (output will be colored)
java -jar $(find ../build/libs/ -mindepth 1 -print -quit) < input.txt > ACTUAL.TXT

# Function to remove ANSI color codes
strip_ansi() {
    sed 's/\x1b\[[0-9;]*[mGK]//g'
}

# Remove colors and normalize line endings
strip_ansi < ACTUAL.TXT | tr -d '\r' > ACTUAL_PLAIN.TXT
strip_ansi < EXPECTED.TXT | tr -d '\r' > EXPECTED_PLAIN.TXT

# Compare the normalized files
if diff -w EXPECTED_PLAIN.TXT ACTUAL_PLAIN.TXT > /dev/null
then
    echo "Test passed!"
    rm ACTUAL_PLAIN.TXT EXPECTED_PLAIN.TXT
    exit 0
else
    echo "Test failed!"
    echo "--- Expected ---"
    cat EXPECTED_PLAIN.TXT
    echo "--- Actual ---"
    cat ACTUAL_PLAIN.TXT
    rm ACTUAL_PLAIN.TXT EXPECTED_PLAIN.TXT
    exit 1
fi