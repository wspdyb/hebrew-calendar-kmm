import kotlin.test.*

class CalendarEngineTest {

    // Test vectors (known values)
    // These should be verified against Hebcal or other authoritative sources

    @Test
    fun testMoladCalculation() {
        // Test a few known molad times
        // Year 5786 (2025-2026)
        val year5786 = MoladCalculator.calculateMoladTishrei(5786)
        assertNotNull(year5786)
        assertTrue(year5786.dayOfWeek in 1..7)
        assertTrue(year5786.hour in 0..23)
        assertTrue(year5786.chalakim in 0..1079)
    }

    @Test
    fun testDehiyyahLoAdu() {
        // Create a molad that would fall on Wednesday (day 4)
        val molad = HebrewTime(
            dayOfWeek = 4,  // Wednesday (forbidden by Lo Adu)
            hour = 12,
            chalakim = 500
        )

        val result = DehiyyahCalculator.calculateRoshHashanah(5786, molad)
        
        // Should postpone due to Lo Adu
        assertTrue(Dehiyyah.LO_ADU in result.triggers)
        assertNotEquals(4, result.finalRoshHashanahDay)
    }

    @Test
    fun testDehiyyahMoladZaken() {
        // Create a molad after 6 PM (hour >= 18)
        val molad = HebrewTime(
            dayOfWeek = 2,  // Monday
            hour = 18,
            chalakim = 0
        )

        val result = DehiyyahCalculator.calculateRoshHashanah(5786, molad)
        
        // Should postpone due to Molad Zaken
        assertTrue(Dehiyyah.MOLAD_ZAKEN in result.triggers)
        assertNotEquals(2, result.finalRoshHashanahDay)
    }

    @Test
    fun testHebrewTimeAddition() {
        val time = HebrewTime(
            dayOfWeek = 2,  // Monday
            hour = 23,
            chalakim = 500
        )

        // Add 600 chalakim (should cross into next hour)
        val result = time.addChalakim(600)
        
        assertEquals(2, result.dayOfWeek)  // Still Monday
        assertEquals(0, result.hour)        // Next hour (midnight)
        assertEquals(100, result.chalakim)  // 500 + 600 = 1100 = 1h + 20
    }

    @Test
    fun testLeapYearDetection() {
        // Test leap year positions in 19-year cycle
        // Positions 3, 6, 8, 11, 14, 17, 19 are leap
        
        // Year 5783 = position 1 in cycle (not leap)
        val year5783 = 5783
        val pos5783 = (year5783 - 1) % 19 + 1
        assertEquals(1, pos5783)
        assertFalse(isLeapYearHelper(year5783))
        
        // Year 5786 = position 4 in cycle (not leap)
        val year5786 = 5786
        val pos5786 = (year5786 - 1) % 19 + 1
        assertEquals(4, pos5786)
        assertFalse(isLeapYearHelper(year5786))
    }

    @Test
    fun testFullYearCalculation() {
        val year = HebrewCalendarEngine.calculateYear(5786)
        
        // Basic sanity checks
        assertEquals(5786, year.year)
        assertTrue(year.roshHashanahDay in 1..7)
        
        if (year.isLeapYear) {
            assertTrue(year.yearLength >= 383)
        } else {
            assertTrue(year.yearLength <= 355)
        }
    }

    @Test
    fun testMultipleYears() {
        // Calculate 10 consecutive years
        for (year in 5780..5789) {
            val calc = HebrewCalendarEngine.calculateYear(year)
            
            // Verify basic constraints
            assertTrue(calc.roshHashanahDay in 1..7, "Year $year: RH day out of range")
            assertTrue(calc.cheshvanLength in listOf(29, 30), "Year $year: Cheshvan length invalid")
            assertTrue(calc.kislevLength in listOf(29, 30), "Year $year: Kislev length invalid")
            
            // Rosh Hashanah should never be on Sun/Wed/Fri
            assertFalse(
                calc.roshHashanahDay in listOf(1, 4, 6),
                "Year $year: RH on forbidden day ${calc.roshHashanahDay}"
            )
        }
    }

    @Test
    fun testLoAduRestriction_RH() {
        // Rosh Hashanah should never fall on Sun (1), Wed (4), or Fri (6)
        
        for (year in 5700..5900) {
            val calc = HebrewCalendarEngine.calculateYear(year)
            assertFalse(
                calc.roshHashanahDay in listOf(1, 4, 6),
                "Year $year violates Lo Adu: RH on day ${calc.roshHashanahDay}"
            )
        }
    }

    @Test
    fun testYearLengthRange() {
        // Common years: 353–355, Leap years: 383–385
        
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
    fun testLeapYearCycle19() {
        // In any 19-year cycle, exactly 7 leap years at positions 3, 6, 8, 11, 14, 17, 19
        
        val leapPositions = setOf(3, 6, 8, 11, 14, 17, 19)
        var startYear = 5700
        
        for (cycleNum in 0..5) {  // Test 6 complete 19-year cycles
            var leapsInCycle = 0
            
            for (posInCycle in 1..19) {
                val year = startYear + (posInCycle - 1)
                val calc = HebrewCalendarEngine.calculateYear(year)
                
                if (calc.isLeapYear) {
                    leapsInCycle++
                    assertTrue(
                        posInCycle in leapPositions,
                        "Year $year (pos $posInCycle): unexpected leap year"
                    )
                }
            }
            
            assertEquals(7, leapsInCycle, "Cycle starting $startYear: expected 7 leap years, got $leapsInCycle")
            startYear += 19
        }
    }

    // Helper function to check leap year
    private fun isLeapYearHelper(year: Int): Boolean {
        val pos = (year - 1) % 19 + 1
        return pos in listOf(3, 6, 8, 11, 14, 17, 19)
    }
}
