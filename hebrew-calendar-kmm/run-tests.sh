#!/bin/bash

# Hebrew Calendar KMM — Build and Test Script

set -e

echo "🏗️  Hebrew Calendar Engine (Kotlin Multiplatform)"
echo "================================================="
echo ""

# Check if Gradle is installed
if ! command -v gradle &> /dev/null && [ ! -f "./gradlew" ]; then
    echo "❌ Gradle not found. Install Gradle or ensure gradlew is present."
    exit 1
fi

# Use gradlew if available, otherwise gradle
GRADLE_CMD="gradle"
if [ -f "./gradlew" ]; then
    GRADLE_CMD="./gradlew"
fi

echo "📦 Step 1: Build shared library..."
$GRADLE_CMD :shared:build --info || {
    echo "❌ Build failed"
    exit 1
}

echo ""
echo "✅ Build successful"
echo ""

echo "🧪 Step 2: Running unit tests..."
$GRADLE_CMD :shared:commonTest --info || {
    echo "❌ Tests failed"
    exit 1
}

echo ""
echo "✅ All tests passed!"
echo ""

echo "📊 Test Summary:"
echo "   - MoladCalculator tests"
echo "   - DehiyyahCalculator tests"
echo "   - HebrewTime arithmetic tests"
echo "   - Full year calculations (10+ years)"
echo "   - Leap year cycle validation"
echo "   - Rosh Hashanah restrictions (Lo Adu)"
echo "   - Year length constraints"
echo ""

echo "✨ Ready for next phase:"
echo "   - Phase 2: Rule Validator (14 keviyot, holiday restrictions)"
echo "   - Phase 3: Explanation Trace (source attribution)"
echo "   - Phase 4: UI Layers (Android, iOS, Desktop)"
