/**
 * Calculates the molad (moon conjunction) for Tishrei of any Hebrew year.
 * 
 * Based on Rambam, Hilchot Kiddush HaChodesh VI.
 * 
 * The molad is calculated from:
 * 1. Molad Breshit (theoretical creation)
 * 2. Number of months elapsed since creation
 * 3. Average lunar month duration
 */
object MoladCalculator {

    /**
     * Average lunar month duration according to Rambam.
     * 29 days + 12 hours + 793 chalakim
     */
    private const val AVERAGE_MONTH_DAYS = 29
    private const val AVERAGE_MONTH_HOURS = 12
    private const val AVERAGE_MONTH_CHALAKIM = 793

    /**
     * Calculate molad Tishrei for a given Hebrew year.
     * 
     * @param year Hebrew year (e.g., 5786)
     * @return HebrewTime representing the molad
     */
    fun calculateMoladTishrei(year: Int): HebrewTime {
        // Number of months from creation to Tishrei of the given year
        val monthsElapsed = calculateMonthsElapsed(year)

        // Total chalakim from one average month
        val chalakim = 
            AVERAGE_MONTH_CHALAKIM.toLong() +
            AVERAGE_MONTH_HOURS.toLong() * HebrewTime.CHALAKIM_PER_HOUR +
            AVERAGE_MONTH_DAYS.toLong() * HebrewTime.CHALAKIM_PER_DAY

        // Add all months' chalakim
        val totalChalakim = chalakim * monthsElapsed

        // Start from Molad Breshit and add elapsed chalakim
        return HebrewTime.MOLAD_BRESHIT.addChalakim(totalChalakim)
    }

    /**
     * Calculate the number of months from creation (Molad Breshit) 
     * to Tishrei of the given year.
     */
    private fun calculateMonthsElapsed(year: Int): Long {
        // Number of complete 19-year cycles before this year
        val completeCycles = (year - 1) / 19
        val yearInCycle = (year - 1) % 19 + 1  // 1–19

        // Months in complete cycles
        // Each 19-year cycle has 12*19 + 7 = 235 months
        var totalMonths = completeCycles * 235L

        // Months in partial cycle (up to but not including current year)
        for (y in 1 until yearInCycle) {
            totalMonths += if (isLeapYear(1 + (completeCycles * 19) + y - 1)) 13 else 12
        }

        return totalMonths
    }

    /**
     * Check if a Hebrew year is a leap year (has Adar I and Adar II).
     * 
     * Leap years in the 19-year cycle occur at positions: 3, 6, 8, 11, 14, 17, 19.
     */
    private fun isLeapYear(year: Int): Boolean {
        val positionInCycle = (year - 1) % 19 + 1
        return positionInCycle in listOf(3, 6, 8, 11, 14, 17, 19)
    }
}
