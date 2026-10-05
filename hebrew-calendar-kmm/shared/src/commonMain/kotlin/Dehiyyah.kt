/**
 * Enum for the four dehiyyot (postponement rules) that may delay Rosh Hashanah.
 * 
 * Reference: Rambam, Hilchot Kiddush HaChodesh VII, 1–4.
 */
enum class Dehiyyah {
    /**
     * Molad Zaken: If molad Tishrei falls after 6 PM (hour 18), 
     * postpone Rosh Hashanah to the next day.
     * 
     * Rambam VII, 1: "If the molad fell on the eighteenth hour or later..."
     */
    MOLAD_ZAKEN,

    /**
     * Lo Adu: Do not set Rosh Hashanah on Sunday (1), Wednesday (4), or Friday (6).
     * 
     * If molad (or after Molad Zaken) lands on these days, postpone.
     * 
     * Rambam VII, 2: "If the day of the molad is Sunday, Wednesday, or Friday..."
     */
    LO_ADU,

    /**
     * GaTRaD: If the current year is simple (not a leap year), 
     * and molad Tishrei falls on Tuesday at 9:204 or later,
     * postpone Rosh Hashanah to Thursday.
     * 
     * Rambam VII, 3: "If it is a simple year and the molad fell on Tuesday 
     * at nine hours and 204 parts or more, we postpone..."
     */
    GATRAD,

    /**
     * BeTU TaKPaT: If the current year is the year after a leap year,
     * and molad Tishrei falls on Monday at 15:589 or later,
     * postpone Rosh Hashanah to Tuesday.
     * 
     * Rambam VII, 4: "If this year follows a leap year, and the molad 
     * fell on Monday at fifteen hours and 589 parts or more, we postpone..."
     */
    BETU_TAKPAT,
}

/**
 * Result of applying dehiyyot.
 * Tracks which rules were triggered and the final Rosh Hashanah weekday.
 */
data class DehiyyahResult(
    val moladDay: Int,                    // Original molad day (1–7)
    val moladTime: HebrewTime,            // Complete molad time
    val triggers: Set<Dehiyyah>,          // Which dehiyyot were triggered
    val finalRoshHashanahDay: Int,        // Final day of Rosh Hashanah (1–7)
    val postponements: Int                // How many days postponed (0–2)
)
