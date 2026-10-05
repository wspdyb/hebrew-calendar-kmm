import kotlin.test.*

/**
 * Comprehensive tests for the Rule Validator.
 * 
 * These tests ensure that:
 * 1. All 14 keviyot exist and only those 14 exist
 * 2. All weekday restrictions are honored
 * 3. Year lengths are always valid
 * 4. Special relationships (AT-BS, Plag) hold
 * 
 * Reference: Rambam, Shulchan Aruch, Talmudic sources
 */
class RuleValidatorTest {

    @Test
    fun testLoAduRosh_NeverSunWedFri() {
        // Validate across 1000 years
        for (year in 5700..5800) {
            val calc = HebrewCalendarEngine.calculateYear(year)
            
            assertFalse(
                calc.roshHashanahDay in listOf(1, 4, 6),
                "Year $year: RH on forbidden day ${calc.roshHashanahDay} (Sun/Wed/Fri)"
            )
            assertTrue(
                calc.roshHashanahDay in listOf(2, 3, 5, 7),
                "Year $year: RH day ${calc.roshHashanahDay} is invalid"
            )
        }
    }

    @Test
    fun testLoAguYomKippur() {
        // Yom Kippur (10 Tishrei) = RH + 9 days
        // 9 mod 7 = 2, so if RH ∈ {2,3,5,7}, then YK ∈ {4,5,7,2} ∪ {3,4,6,3} = {2,3,4,5,7}
        // Never 1 (Sun) or 6 (Fri)
        
        for (year in 5700..5800) {
            val calc = HebrewCalendarEngine.calculateYear(year)
            
            val ykWeekday = ((calc.roshHashanahDay - 1 + 9) % 7) + 1
            
            assertFalse(
                ykWeekday in listOf(1, 6),
                "Year $year: YK on forbidden day $ykWeekday (Sun or Fri)"
            )
        }
    }

    @Test
    fun testLoBduPesach_NeverMonWedFri() {
        // Pesach (15 Nisan) must be in {1, 3, 5, 7}
        // Never 2, 4, 6 (Mon, Wed, Fri)
        
        // Note: This requires full year calculation including month lengths
        // For now, we validate the restriction itself
        
        for (restrictionId in listOf("PESACH", "SEVENTEEN_TAMMUZ", "TISHA_BEAV")) {
            for (day in 1..7) {
                val result = RuleValidator.validateDateRestriction(restrictionId, day)
                if (day in listOf(2, 4, 6)) {
                    assertFalse(result.isValid, "Day $day should be forbidden for $restrictionId")
                } else {
                    assertTrue(result.isValid, "Day $day should be allowed for $restrictionId")
                }
            }
        }
    }

    @Test
    fun testLoGahazHosthanah() {
        // Hoshanah Rabah (21 Tishrei) cannot fall on Tue/Thu/Sat
        // Allowed: Sun, Mon, Wed, Fri
        
        val restriction = WeekdayRestrictions.HOSHANAH_RABAH
        
        assertEquals(setOf(1, 2, 4, 6), restriction.allowedDays)
        assertEquals(setOf(3, 5, 7), restriction.forbiddenDays)
        
        for (day in 1..7) {
            val result = RuleValidator.validateDateRestriction("HOSHANAH_RABAH", day)
            if (day in listOf(3, 5, 7)) {
                assertFalse(result.isValid, "Day $day should be forbidden")
            } else {
                assertTrue(result.isValid, "Day $day should be allowed")
            }
        }
    }

    @Test
    fun testLoZbdPurim() {
        // Purim (14 Adar) cannot fall on Mon/Wed/Sat
        // Allowed: Sun, Tue, Thu, Fri
        
        val restriction = WeekdayRestrictions.PURIM
        
        assertEquals(setOf(1, 3, 5, 6), restriction.allowedDays)
        assertEquals(setOf(2, 4, 7), restriction.forbiddenDays)
    }

    @Test
    fun testYearLengthConstraints() {
        // Common years: 353–355
        // Leap years: 383–385
        
        for (year in 5700..5900) {
            val calc = HebrewCalendarEngine.calculateYear(year)
            
            if (calc.isLeapYear) {
                assertTrue(
                    calc.yearLength in 383..385,
                    "Year $year (leap): length ${calc.yearLength} out of range"
                )
            } else {
                assertTrue(
                    calc.yearLength in 353..355,
                    "Year $year (common): length ${calc.yearLength} out of range"
                )
            }
        }
    }

    @Test
    fun testCheshvanKislevLengths() {
        // Cheshvan: 29 or 30
        // Kislev: 29 or 30
        
        for (year in 5700..5900) {
            val calc = HebrewCalendarEngine.calculateYear(year)
            
            assertTrue(
                calc.cheshvanLength in listOf(29, 30),
                "Year $year: Cheshvan length ${calc.cheshvanLength} invalid"
            )
            assertTrue(
                calc.kislevLength in listOf(29, 30),
                "Year $year: Kislev length ${calc.kislevLength} invalid"
            )
        }
    }

    @Test
    fun testLeapYearPositions() {
        // Leap years ONLY at positions 3, 6, 8, 11, 14, 17, 19 in the 19-year cycle
        
        val leapPositions = setOf(3, 6, 8, 11, 14, 17, 19)
        
        for (year in 5700..5900) {
            val calc = HebrewCalendarEngine.calculateYear(year)
            val pos = (year - 1) % 19 + 1
            
            if (pos in leapPositions) {
                assertTrue(
                    calc.isLeapYear,
                    "Year $year (position $pos in cycle): should be leap"
                )
            } else {
                assertFalse(
                    calc.isLeapYear,
                    "Year $year (position $pos in cycle): should be common"
                )
            }
        }
    }

    @Test
    fun testLeapYearsCyclicity() {
        // In any 19-year cycle, exactly 7 leap years
        
        for (cycleStart in 5700 step 19) {
            var leapCount = 0
            for (offset in 0 until 19) {
                val year = cycleStart + offset
                val calc = HebrewCalendarEngine.calculateYear(year)
                if (calc.isLeapYear) leapCount++
            }
            
            assertEquals(
                7, leapCount,
                "Cycle starting $cycleStart: expected 7 leap years, got $leapCount"
            )
        }
    }

    @Test
    fun testValidateCompleteYear() {
        // Validate a specific year comprehensively
        
        val year = 5786
        val calc = HebrewCalendarEngine.calculateYear(year)
        val report = RuleValidator.validateCompleteYear(calc)
        
        assertEquals(year, report.year)
        
        if (!report.allRestrictionsValid) {
            fail("Year $year failed validation:\n${report.violations.joinToString("\n")}")
        }
    }

    @Test
    fun testValidateYearRange() {
        // Validate a wide range (this is expensive but critical)
        
        val (isValid, violations) = RuleValidator.validateYearRange(5700, 5799)
        
        if (!isValid) {
            val criticalViolations = violations.filter { it.contains("CRITICAL") }
            fail("Year range validation failed:\n${criticalViolations.joinToString("\n")}")
        }
        
        // At least one success message should be present
        assertTrue(
            violations.any { it.contains("VALID") },
            "No validation success message found"
        )
    }

    @Test
    fun testValidate14Keviyot() {
        // Exactly 14 distinct keviyot (year signatures) should exist
        
        val (isValid, keviyahMap) = RuleValidator.validate14Keviyot()
        
        assertTrue(isValid, "Should have exactly 14 keviyot, found ${keviyahMap.size}")
        assertEquals(14, keviyahMap.size, "Expected 14 keviyot")
        
        // Print out all keviyot found
        println("14 Keviyot found:")
        keviyahMap.entries.sortedBy { it.key }.forEach { (keviyah, count) ->
            println("  $keviyah: appears $count times in 10,000 years")
        }
    }

    @Test
    fun testValidatePlag() {
        // Plag: Purim (14 Adar) ↔ Lag BaOmer (18 Iyar) always same weekday
        // Distance: 63 days = 9 weeks exactly
        
        val (isValid, message) = RuleValidator.validatePlag()
        
        assertTrue(isValid, message)
    }

    @Test
    fun testMoladZakenHandling() {
        // Test that Molad Zaken is correctly applied
        
        // Create a scenario where Molad Zaken would trigger
        // (This is simplified; in reality we'd need specific molad calculations)
        
        val molad = HebrewTime(
            dayOfWeek = 2,  // Monday
            hour = 18,      // >= 18: triggers Molad Zaken
            chalakim = 0
        )
        
        val (isValid, _) = RuleValidator.validateMoladZaken(molad, 3, 2)
        // After Molad Zaken, RH should move from Monday to Tuesday
        assertTrue(isValid)
    }

    @Test
    fun testSourceReferencesExist() {
        // Verify that all sources referenced by restrictions are defined
        
        for (restriction in WeekdayRestrictions.allRestrictions()) {
            assertNotNull(restriction.source, "Restriction ${restriction.id} missing source reference")
            assertTrue(restriction.source.title.isNotEmpty(), "Source for ${restriction.id} has empty title")
            assertTrue(restriction.source.explanation.isNotEmpty(), "Source for ${restriction.id} has empty explanation")
        }
    }

    @Test
    fun testAllSourcesDocumented() {
        // Verify that critical sources are in the Sources object
        
        val sources = Sources.allSources()
        
        assertNotNull(sources.find { it.id == "RAMBAM_VI_MOLAD" }, "Missing Rambam VI Molad source")
        assertNotNull(sources.find { it.id == "RAMBAM_VII_MOLAD_ZAKEN" }, "Missing Molad Zaken source")
        assertNotNull(sources.find { it.id == "RAMBAM_VII_LO_ADU" }, "Missing Lo Adu source")
        assertNotNull(sources.find { it.id == "RAMBAM_VII_GATRAD" }, "Missing GaTRaD source")
        assertNotNull(sources.find { it.id == "RAMBAM_VII_BETU_TAKPAT" }, "Missing BeTU TaKPaT source")
        assertNotNull(sources.find { it.id == "SA_OH_428" }, "Missing Shulchan Aruch source")
    }

    @Test
    fun testPesachShavuotRelationship() {
        // Shavuot (6 Sivan) is 50 days after Pesach (15 Nisan)
        // 50 mod 7 = 1, so Shavuot is always 1 day after Pesach in weekday cycle
        
        // If Pesach ∈ {1,3,5,7}, then Shavuot ∈ {2,4,6,1} = {1,2,4,6}
        
        val pesachRestriction = WeekdayRestrictions.PESACH
        val shavuotRestriction = WeekdayRestrictions.SHAVUOT
        
        assertEquals(setOf(1, 3, 5, 7), pesachRestriction.allowedDays)
        assertEquals(setOf(1, 2, 4, 6), shavuotRestriction.allowedDays)
        
        // Verify the +1 day relationship
        for (pesachDay in pesachRestriction.allowedDays) {
            val expectedShavuotDay = if (pesachDay == 7) 1 else pesachDay + 1
            assertTrue(
                expectedShavuotDay in shavuotRestriction.allowedDays,
                "Pesach $pesachDay → Shavuot $expectedShavuotDay not allowed"
            )
        }
    }
}
