/**
 * Weekday restrictions for Hebrew calendar holidays and significant dates.
 * 
 * Defines which weekdays are ALLOWED and which are FORBIDDEN for each date.
 * 
 * Based on:
 * - Rambam, Hilchot Kiddush HaChodesh VI–VII
 * - Talmudic sources (Rosh Hashanah, Sukkah, Megillah, Pesachim, Taanit)
 * - Shulchan Aruch, Orach Chaim 428
 */

/**
 * Representation of a single Hebrew date with its weekday restrictions.
 * 
 * Note: Some holidays may have different restrictions than their calendar date
 * (e.g., observance adjustments for Shabbat).
 */
data class WeekdayRestriction(
    /**
     * Unique identifier
     */
    val id: String,

    /**
     * Name in English
     */
    val name: String,

    /**
     * Hebrew name
     */
    val nameHebrew: String,

    /**
     * Calendar date (month, day)
     */
    val month: String,    // e.g., "Tishrei", "Nisan"
    val day: Int,         // 1–30

    /**
     * Allowed weekdays (1–7: Sun–Sat)
     */
    val allowedDays: Set<Int>,

    /**
     * Forbidden weekdays
     */
    val forbiddenDays: Set<Int>,

    /**
     * The rule(s) that govern this restriction
     */
    val ruleName: String, // e.g., "Lo Adu Rosh"

    /**
     * Source reference
     */
    val source: SourceReference,

    /**
     * Explanation
     */
    val explanation: String,

    /**
     * Is this a date-specific restriction (calendar) vs observance-specific?
     */
    val isCalendarRestriction: Boolean = true,
)

/**
 * All weekday restrictions for the fixed Hebrew calendar.
 */
object WeekdayRestrictions {

    // =====================================================
    // ROSH HASHANAH & TISHREI
    // =====================================================

    val ROSH_HASHANAH = WeekdayRestriction(
        id = "RH",
        name = "Rosh Hashanah",
        nameHebrew = "ראש השנה",
        month = "Tishrei",
        day = 1,
        allowedDays = setOf(2, 3, 5, 7),          // Mon, Tue, Thu, Sat
        forbiddenDays = setOf(1, 4, 6),           // Sun, Wed, Fri
        ruleName = "Lo Adu Rosh (לא אד"ו ראש)",
        source = Sources.RAMBAM_VII_LO_ADU,
        explanation = "Rosh Hashanah never falls on Sunday (1), Wednesday (4), or Friday (6). This rule is applied after Molad Zaken and shapes the structure of the entire calendar year.",
        isCalendarRestriction = true
    )

    val YOM_KIPPUR = WeekdayRestriction(
        id = "YK",
        name = "Yom Kippur",
        nameHebrew = "יום הכיפורים",
        month = "Tishrei",
        day = 10,
        allowedDays = setOf(2, 3, 4, 5, 7),       // Mon, Tue, Wed, Thu, Sat (never Fri)
        forbiddenDays = setOf(1, 6),              // Sun, Fri
        ruleName = "Lo Agu Yom Kippur (לא אג"ו יוה"כ)",
        source = Sources.RAMBAM_VII_LO_ADU,  // Derived from RH + 9 days
        explanation = "Yom Kippur (10 Tishrei) cannot fall on Sunday (1) or Friday (6). Since YK is always 9 days after RH, and 9 mod 7 = 2, if RH is {2,3,5,7}, then YK is {4,5,7,2,3} mod 7 = {2,3,4,5,7}. Never 1 or 6.",
        isCalendarRestriction = true
    )

    val SUKKOT_FIRST_DAY = WeekdayRestriction(
        id = "SUKKOT",
        name = "Sukkot (First Day)",
        nameHebrew = "סוכות (יום ראשון)",
        month = "Tishrei",
        day = 15,
        allowedDays = setOf(2, 3, 5, 7),          // Mon, Tue, Thu, Sat
        forbiddenDays = setOf(1, 4, 6),           // Sun, Wed, Fri
        ruleName = "Lo Adu Sukkot (inherited from RH structure)",
        source = Sources.RAMBAM_VII_LO_ADU,
        explanation = "Sukkot (15 Tishrei) always falls on the same day of the week as Rosh Hashanah (1 Tishrei), because 15 = 1 + 14 = 1 + 2×7. Therefore, Sukkot inherits the Lo Adu restriction.",
        isCalendarRestriction = true
    )

    val HOSHANAH_RABAH = WeekdayRestriction(
        id = "HOSHANAH_RABAH",
        name = "Hoshanah Rabah",
        nameHebrew = "הושענא רבה",
        month = "Tishrei",
        day = 21,
        allowedDays = setOf(1, 2, 4, 6),          // Sun, Mon, Wed, Fri
        forbiddenDays = setOf(3, 5, 7),           // Tue, Thu, Sat
        ruleName = "Lo Gahaz Hoshanah Rabah (לא גה"ז הושענא רבה)",
        source = Sources.SUKKAH_43B_HOSHANAH_RABAH,
        explanation = "Hoshanah Rabah (21 Tishrei) cannot fall on Tuesday (3), Thursday (5), or Saturday (7). Since HR is 20 days after RH, and 20 mod 7 = 6, if RH is {2,3,5,7}, then HR is {1,4,4,6} (accounting for cycling), yielding allowed days {1,2,4,6}.",
        isCalendarRestriction = true
    )

    // =====================================================
    // PESACH (NISAN)
    // =====================================================

    val PESACH = WeekdayRestriction(
        id = "PESACH",
        name = "Pesach",
        nameHebrew = "פסח",
        month = "Nisan",
        day = 15,
        allowedDays = setOf(1, 3, 5, 7),          // Sun, Tue, Thu, Sat
        forbiddenDays = setOf(2, 4, 6),           // Mon, Wed, Fri
        ruleName = "Lo Bdu Pesach (לא בד"ו פסח)",
        source = Sources.PESACHIM_58A_PESACH_SHABBAT,
        explanation = "Pesach (15 Nisan) never falls on Monday (2), Wednesday (4), or Friday (6). This is a fundamental rule governing the structure of Nisan and the preparation for Yom Tov. The restriction reflects the requirements of Korban Pesach preparation.",
        isCalendarRestriction = true
    )

    val SHAVUOT = WeekdayRestriction(
        id = "SHAVUOT",
        name = "Shavuot",
        nameHebrew = "שבועות",
        month = "Sivan",
        day = 6,
        allowedDays = setOf(1, 2, 4, 6),          // Sun, Mon, Wed, Fri
        forbiddenDays = setOf(3, 5, 7),           // Tue, Thu, Sat
        ruleName = "Lo Gahaz Shavuot (לא גה"ז עצרת)",
        source = Sources.PESACHIM_58A_PESACH_SHABBAT,  // Derived from Pesach + 50 days
        explanation = "Shavuot (6 Sivan) is 50 days after Pesach (15 Nisan). Since 50 mod 7 = 1, Shavuot is always 1 day after Pesach in the weekly cycle. If Pesach ∈ {1,3,5,7}, then Shavuot ∈ {2,4,6,1} = {1,2,4,6}.",
        isCalendarRestriction = true
    )

    // =====================================================
    // PURIM & TAANIT ESTHER (ADAR)
    // =====================================================

    val PURIM = WeekdayRestriction(
        id = "PURIM",
        name = "Purim",
        nameHebrew = "פורים",
        month = "Adar",  // Adar II in leap year
        day = 14,
        allowedDays = setOf(1, 3, 5, 6),          // Sun, Tue, Thu, Fri
        forbiddenDays = setOf(2, 4, 7),           // Mon, Wed, Sat
        ruleName = "Lo Zbd Purim (לא זב"ד פורים)",
        source = Sources.MEGILLAH_5A_PURIM_SHABBAT,
        explanation = "Purim (14 Adar) never falls on Monday (2), Wednesday (4), or Saturday (7). The restriction prevents certain conflicts with Shabbat observance of Megillah reading and Seudah.",
        isCalendarRestriction = true
    )

    val TAANIT_ESTHER = WeekdayRestriction(
        id = "TAANIT_ESTHER",
        name = "Taanit Esther (Fast of Esther)",
        nameHebrew = "תענית אסתר",
        month = "Adar",
        day = 13,
        allowedDays = setOf(2, 4, 5, 7),          // Mon, Wed, Thu, Sat (never on Sun or Fri)
        forbiddenDays = setOf(1, 3, 6),           // Sun, Tue, Fri
        ruleName = "Lo Agu Taanit Esther (לא אג"ו צום אסתר)",
        source = Sources.MEGILLAH_5A_PURIM_SHABBAT,  // Derived from Purim - 1 day
        explanation = "Taanit Esther (13 Adar) is 1 day before Purim. If Purim ∈ {1,3,5,6}, then Taanit ∈ {7,2,4,5} = {2,4,5,7}. Never on Friday (would conflict with Shabbat) or Sunday.",
        isCalendarRestriction = true
    )

    // =====================================================
    // FAST DAYS (TAMMUZ & AV)
    // =====================================================

    val SEVENTEEN_TAMMUZ = WeekdayRestriction(
        id = "SEVENTEEN_TAMMUZ",
        name = "Seventeenth of Tammuz",
        nameHebrew = "י"ז בתמוז",
        month = "Tammuz",
        day = 17,
        allowedDays = setOf(1, 3, 5, 7),          // Sun, Tue, Thu, Sat
        forbiddenDays = setOf(2, 4, 6),           // Mon, Wed, Fri
        ruleName = "Lo Bdu Fast (לא בד"ו צום)",
        source = Sources.TAANIT_26B_FASTS,
        explanation = "17 Tammuz (a minor fast) has the same weekday restrictions as Pesach (Lo Bdu): never Mon/Wed/Fri. This reflects the parallel structure: both are day 17 in their respective months, and both have parallel halachic concerns.",
        isCalendarRestriction = true
    )

    val TISHA_BEAV = WeekdayRestriction(
        id = "TISHA_BEAV",
        name = "Ninth of Av",
        nameHebrew = "ט' באב",
        month = "Av",
        day = 9,
        allowedDays = setOf(1, 3, 5, 7),          // Sun, Tue, Thu, Sat
        forbiddenDays = setOf(2, 4, 6),           // Mon, Wed, Fri
        ruleName = "Lo Bdu Fast (לא בד"ו צום)",
        source = Sources.TAANIT_26B_FASTS,
        explanation = "9 Av has the same restrictions as 17 Tammuz: never Mon/Wed/Fri. However, when 9 Av falls on Saturday, the fast is postponed to Sunday (observance adjustment, not calendar adjustment).",
        isCalendarRestriction = true
    )

    // =====================================================
    // CHANUKAH
    // =====================================================

    val CHANUKAH_FIRST_DAY = WeekdayRestriction(
        id = "CHANUKAH_1",
        name = "Chanukah - First Day",
        nameHebrew = "חנוכה - יום ראשון",
        month = "Kislev",
        day = 25,
        allowedDays = setOf(1, 2, 4, 5, 6, 7),    // Sun, Mon, Wed, Thu, Fri, Sat
        forbiddenDays = setOf(3),                 // Tue only
        ruleName = "Lo G Chanukah (לא ג' חנוכה)",
        source = Sources.SHULCHAN_ARUCH_OH_428,
        explanation = "25 Kislev (1st day of Chanukah) cannot fall on Tuesday (3). This is derived from the fact that Kislev's length (29 or 30) depends on the year structure, and the Tuesday restriction prevents a specific problematic calendar configuration. Unlike other restrictions, this involves the length of Kislev itself.",
        isCalendarRestriction = true
    )

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Get all restrictions
     */
    fun allRestrictions(): List<WeekdayRestriction> {
        return listOf(
            ROSH_HASHANAH,
            YOM_KIPPUR,
            SUKKOT_FIRST_DAY,
            HOSHANAH_RABAH,
            PESACH,
            SHAVUOT,
            PURIM,
            TAANIT_ESTHER,
            SEVENTEEN_TAMMUZ,
            TISHA_BEAV,
            CHANUKAH_FIRST_DAY,
        )
    }

    /**
     * Find restriction by ID
     */
    fun findById(id: String): WeekdayRestriction? {
        return allRestrictions().find { it.id == id }
    }

    /**
     * Check if a date is allowed on a given weekday
     */
    fun isAllowed(restrictionId: String, weekday: Int): Boolean {
        val restriction = findById(restrictionId) ?: return true  // Unknown restrictions are allowed
        return weekday in restriction.allowedDays
    }

    /**
     * Check if a date is forbidden on a given weekday
     */
    fun isForbidden(restrictionId: String, weekday: Int): Boolean {
        val restriction = findById(restrictionId) ?: return false
        return weekday in restriction.forbiddenDays
    }

    /**
     * Get the violation reason if a weekday violates a restriction
     */
    fun getViolationReason(restrictionId: String, weekday: Int): String? {
        val restriction = findById(restrictionId) ?: return null
        if (isForbidden(restrictionId, weekday)) {
            return "${restriction.name} cannot fall on ${dayName(weekday)}. ${restriction.ruleName}: ${restriction.explanation}"
        }
        return null
    }

    /**
     * Day names for display
     */
    private fun dayName(day: Int): String {
        return when (day) {
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> "Unknown"
        }
    }
}
