/**
 * Applies the four dehiyyot (postponement rules) to calculate the final 
 * weekday of Rosh Hashanah for a given Hebrew year.
 * 
 * The logic follows Rambam, Hilchot Kiddush HaChodesh VII, 1–4.
 */
object DehiyyahCalculator {

    /**
     * Determine the final weekday of Rosh Hashanah after applying all dehiyyot.
     * 
     * @param year Hebrew year
     * @param molad The molad Tishrei for this year
     * @return DehiyyahResult containing the final day and triggered rules
     */
    fun calculateRoshHashanah(year: Int, molad: HebrewTime): DehiyyahResult {
        val triggers = mutableSetOf<Dehiyyah>()
        var currentDay = molad.dayOfWeek
        var postponements = 0

        // Step 1: Molad Zaken (hour >= 18)
        if (molad.isMoladZaken()) {
            triggers.add(Dehiyyah.MOLAD_ZAKEN)
            currentDay = nextDay(currentDay)
            postponements++
        }

        // Step 2: Lo Adu (not Sun/Wed/Fri)
        if (currentDay in listOf(1, 4, 6)) {  // Sunday, Wednesday, Friday
            triggers.add(Dehiyyah.LO_ADU)
            currentDay = nextDay(currentDay)
            postponements++
        }

        // Step 3: GaTRaD (simple year + Tuesday + 9:204+)
        if (!isLeapYear(year) && molad.dayOfWeek == 3) {  // Tuesday
            if (molad.hour > 9 || (molad.hour == 9 && molad.chalakim >= 204)) {
                triggers.add(Dehiyyah.GATRAD)
                // GaTRaD specifically postpones to Thursday
                currentDay = 5  // Thursday
                // Note: This bypasses other rules, so we exit early
                return DehiyyahResult(
                    moladDay = molad.dayOfWeek,
                    moladTime = molad,
                    triggers = triggers,
                    finalRoshHashanahDay = currentDay,
                    postponements = 2  // Tuesday to Thursday is 2 days
                )
            }
        }

        // Step 4: BeTU TaKPaT (year after leap + Monday + 15:589+)
        if (isYearAfterLeap(year) && molad.dayOfWeek == 2) {  // Monday
            if (molad.hour > 15 || (molad.hour == 15 && molad.chalakim >= 589)) {
                triggers.add(Dehiyyah.BETU_TAKPAT)
                currentDay = 3  // Tuesday
                return DehiyyahResult(
                    moladDay = molad.dayOfWeek,
                    moladTime = molad,
                    triggers = triggers,
                    finalRoshHashanahDay = currentDay,
                    postponements = 1  // Monday to Tuesday is 1 day
                )
            }
        }

        return DehiyyahResult(
            moladDay = molad.dayOfWeek,
            moladTime = molad,
            triggers = triggers,
            finalRoshHashanahDay = currentDay,
            postponements = postponements
        )
    }

    /**
     * Calculate next day in the week (1–7, where 1=Sunday, 7=Saturday).
     */
    private fun nextDay(day: Int): Int {
        return if (day == 7) 1 else day + 1
    }

    /**
     * Check if a Hebrew year is a leap year (has Adar I and II).
     * Leap years in 19-year cycle: 3, 6, 8, 11, 14, 17, 19.
     */
    private fun isLeapYear(year: Int): Boolean {
        val positionInCycle = (year - 1) % 19 + 1
        return positionInCycle in listOf(3, 6, 8, 11, 14, 17, 19)
    }

    /**
     * Check if this year immediately follows a leap year.
     */
    private fun isYearAfterLeap(year: Int): Boolean {
        val previousYear = year - 1
        return isLeapYear(previousYear)
    }
}
