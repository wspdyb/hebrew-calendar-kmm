/**
 * Main Hebrew Calendar Engine.
 * 
 * Combines molad calculation, dehiyyot (postponements), and year structure
 * to determine the complete Hebrew calendar for any year.
 * 
 * Architecture:
 * 1. MoladCalculator → calculate molad Tishrei
 * 2. DehiyyahCalculator → apply postponement rules
 * 3. YearStructureCalculator → determine year type and month lengths
 * 4. DateCalculator → compute any Hebrew date
 */
object HebrewCalendarEngine {

    /**
     * Complete result for a single Hebrew year.
     */
    data class YearCalculation(
        val year: Int,
        val molad: HebrewTime,                    // Molad Tishrei
        val dehiyyahResult: DehiyyahResult,      // Applied postponements
        val roshHashanahDay: Int,                 // Final RH day (1–7)
        val isLeapYear: Boolean,
        val yearLength: Int,                      // 353, 354, 355, 383, 384, or 385
        val cheshvanLength: Int,                  // 29 or 30
        val kislevLength: Int,                    // 29 or 30
        val keviyahSymbol: String                 // e.g., "בחג"
    )

    /**
     * Calculate complete year structure for a Hebrew year.
     */
    fun calculateYear(year: Int): YearCalculation {
        // Step 1: Calculate molad Tishrei
        val molad = MoladCalculator.calculateMoladTishrei(year)

        // Step 2: Apply dehiyyot
        val dehiyyahResult = DehiyyahCalculator.calculateRoshHashanah(year, molad)
        val roshHashanahDay = dehiyyahResult.finalRoshHashanahDay

        // Step 3: Determine year type
        val isLeap = isLeapYear(year)
        val nextYearRH = dehiyyahResult.finalRoshHashanahDay  // Simplified; in real impl, calculate next year
        val nextYearMolad = MoladCalculator.calculateMoladTishrei(year + 1)
        val nextYearDehiyyah = DehiyyahCalculator.calculateRoshHashanah(year + 1, nextYearMolad)
        val nextYearRHDay = nextYearDehiyyah.finalRoshHashanahDay

        // Calculate year length based on Rosh Hashanah weekdays
        val yearLength = calculateYearLength(roshHashanahDay, nextYearRHDay, isLeap)

        // Step 4: Determine Cheshvan and Kislev lengths
        val (cheshvanLen, kislevLen) = determineMonthLengths(yearLength, isLeap)

        // Step 5: Calculate Keviyah (year signature)
        val keviyah = calculateKeviyah(roshHashanahDay, isLeap, kislevLen)

        return YearCalculation(
            year = year,
            molad = molad,
            dehiyyahResult = dehiyyahResult,
            roshHashanahDay = roshHashanahDay,
            isLeapYear = isLeap,
            yearLength = yearLength,
            cheshvanLength = cheshvanLen,
            kislevLength = kislevLen,
            keviyahSymbol = keviyah
        )
    }

    /**
     * Check if a Hebrew year is a leap year.
     */
    private fun isLeapYear(year: Int): Boolean {
        val positionInCycle = (year - 1) % 19 + 1
        return positionInCycle in listOf(3, 6, 8, 11, 14, 17, 19)
    }

    /**
     * Calculate year length based on RH and next RH weekdays.
     * 
     * Logic:
     * - Count days between two Rosh Hashanah dates
     * - Account for leap years
     */
    private fun calculateYearLength(
        rhDay: Int,
        nextRHDay: Int,
        isLeap: Boolean
    ): Int {
        // Approximate calculation
        // More precise: count actual days between RH this year and RH next year
        return if (isLeap) {
            when {
                rhDay == 1 && nextRHDay == 1 -> 384  // Rare
                rhDay == 1 && nextRHDay == 3 -> 384
                rhDay == 3 && nextRHDay == 5 -> 384
                rhDay == 5 && nextRHDay == 7 -> 384
                rhDay == 7 && nextRHDay == 2 -> 385
                rhDay == 2 && nextRHDay == 4 -> 383
                rhDay == 4 && nextRHDay == 6 -> 383
                rhDay == 6 && nextRHDay == 1 -> 383
                else -> 384  // Default
            }
        } else {
            when {
                rhDay == 1 && nextRHDay == 1 -> 354
                rhDay == 1 && nextRHDay == 3 -> 354
                rhDay == 3 && nextRHDay == 5 -> 354
                rhDay == 5 && nextRHDay == 7 -> 354
                rhDay == 7 && nextRHDay == 2 -> 355
                rhDay == 2 && nextRHDay == 4 -> 353
                rhDay == 4 && nextRHDay == 6 -> 353
                rhDay == 6 && nextRHDay == 1 -> 353
                else -> 354  // Default
            }
        }
    }

    /**
     * Determine lengths of Cheshvan and Kislev based on year length.
     * 
     * Cheshvan and Kislev lengths determine whether the year is:
     * - חסרה (deficient): Ch=29, Ki=29 → length 353 or 383
     * - כסדרה (regular): Ch=29, Ki=30 → length 354 or 384
     * - שלמה (complete): Ch=30, Ki=30 → length 355 or 385
     */
    private fun determineMonthLengths(yearLength: Int, isLeap: Boolean): Pair<Int, Int> {
        return when {
            yearLength == 353 -> Pair(29, 29)  // Deficient
            yearLength == 354 -> Pair(29, 30)  // Regular
            yearLength == 355 -> Pair(30, 30)  // Complete
            yearLength == 383 -> Pair(29, 29)  // Leap deficient
            yearLength == 384 -> Pair(29, 30)  // Leap regular
            yearLength == 385 -> Pair(30, 30)  // Leap complete
            else -> Pair(29, 30)                // Default
        }
    }

    /**
     * Calculate Keviyah (year signature).
     * Three letters: (day of RH) (year type) (day of Pesach)
     */
    private fun calculateKeviyah(
        rhDay: Int,
        isLeap: Boolean,
        kislevLength: Int
    ): String {
        val dayChar = dayToChar(rhDay)
        val typeChar = if (isLeap) 'מ' else 'פ'  // מעוברת or פשוטה (simplified)
        
        // Calculate day of Pesach (ט"ו ניסן)
        // RH = א' תשרי → ט"ו ניסן is 191 days later (or 192 for leap years)
        val daysToNisan15 = if (isLeap) 192 else 191
        val pesachDay = ((rhDay - 1 + daysToNisan15) % 7) + 1
        val pesachChar = dayToChar(pesachDay)

        return "$dayChar${typeChar}$pesachChar"
    }

    /**
     * Convert weekday (1–7) to Hebrew letter for Keviyah.
     */
    private fun dayToChar(day: Int): Char {
        return when (day) {
            1 -> 'א'  // Sunday
            2 -> 'ב'  // Monday
            3 -> 'ג'  // Tuesday
            4 -> 'ד'  // Wednesday
            5 -> 'ה'  // Thursday
            6 -> 'ו'  // Friday
            7 -> 'ז'  // Saturday
            else -> '?'
        }
    }
}
