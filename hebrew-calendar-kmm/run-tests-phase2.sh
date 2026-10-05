#!/bin/bash

# Phase 2 Test Runner
# Validates Rule Validator, Weekday Restrictions, and 14 Keviyot

set -e

echo "=============================================="
echo "Phase 2: Rule Validator Tests"
echo "Hebrew Calendar Engine (מחשבון זמנים עבריים)"
echo "=============================================="
echo ""

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if we're in the right directory
if [ ! -f "build.gradle.kts" ]; then
    echo "Error: build.gradle.kts not found. Run from project root."
    exit 1
fi

echo -e "${BLUE}Building Phase 2 modules...${NC}"
./gradlew :shared:build -q

echo ""
echo -e "${BLUE}Running Phase 2 Tests:${NC}"
echo ""

# Run all RuleValidatorTest tests
echo -e "${YELLOW}[1/5] Testing Lo Adu Rosh (1000-year sweep)...${NC}"
./gradlew :shared:commonTest --tests RuleValidatorTest.testLoAduRosh_NeverSunWedFri -q 2>/dev/null && echo -e "${GREEN}✓ PASSED${NC}" || echo -e "${RED}✗ FAILED${NC}"

echo ""
echo -e "${YELLOW}[2/5] Testing Year Length Constraints...${NC}"
./gradlew :shared:commonTest --tests RuleValidatorTest.testYearLengthConstraints -q 2>/dev/null && echo -e "${GREEN}✓ PASSED${NC}" || echo -e "${RED}✗ FAILED${NC}"

echo ""
echo -e "${YELLOW}[3/5] Testing Leap Year Positions...${NC}"
./gradlew :shared:commonTest --tests RuleValidatorTest.testLeapYearPositions -q 2>/dev/null && echo -e "${GREEN}✓ PASSED${NC}" || echo -e "${RED}✗ FAILED${NC}"

echo ""
echo -e "${YELLOW}[4/5] Testing All Weekday Restrictions...${NC}"
./gradlew :shared:commonTest --tests RuleValidatorTest -q 2>/dev/null && echo -e "${GREEN}✓ ALL TESTS PASSED${NC}" || echo -e "${RED}✗ SOME TESTS FAILED${NC}"

echo ""
echo -e "${YELLOW}[5/5] Testing 14 Keviyot (10,000-year validation)...${NC}"
./gradlew :shared:commonTest --tests RuleValidatorTest.testValidate14Keviyot -q 2>/dev/null && echo -e "${GREEN}✓ PASSED${NC}" || echo -e "${RED}✗ FAILED${NC}"

echo ""
echo "=============================================="
echo -e "${GREEN}Phase 2 Test Suite Complete${NC}"
echo "=============================================="
echo ""
echo "Next steps:"
echo "  - Review PHASE2_RULES.md for detailed documentation"
echo "  - Review PHASE2_SUMMARY.md for implementation overview"
echo "  - Proceed to Phase 3: Explanation Trace"
echo ""
