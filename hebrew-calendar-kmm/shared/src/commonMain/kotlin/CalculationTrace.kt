/**
 * Calculation Trace — records every step in the calendar computation.
 * 
 * Creates an auditable chain:
 * Molad → Dehiyyot → RH Weekday → Year Type → Month Lengths → Dates
 * 
 * Each step is tied to its source and reasoning.
 */

/**
 * Represents a single step in the calculation process.
 * 
 * Every step must have:
 * - Input and output values
 * - The source (Rambam, Talmud, etc.)
 * - The reasoning (why this step)
 * - The rule applied (if any)
 */
data class CalculationStep(
    /**
     * Unique step ID
     */
    val id: String,

    /**
     * Step number in sequence (1, 2, 3, ...)
     */
    val stepNumber: Int,

    /**
     * Category of step
     */
    val category: StepCategory,

    /**
     * Title of the step
     * E.g., "Calculate Molad of Tishrei", "Apply Molad Zaken"
     */
    val title: String,

    /**
     * Title in Hebrew
     */
    val titleHebrew: String,

    /**
     * Input values (what we started with)
     */
    val input: Map<String, String>,

    /**
     * Output values (what we calculated)
     */
    val output: Map<String, String>,

    /**
     * The calculation formula or method
     */
    val calculation: String,

    /**
     * Source reference for this step
     */
    val source: SourceReference,

    /**
     * Plain-language explanation
     */
    val explanation: String,

    /**
     * Hebrew explanation
     */
    val explanationHebrew: String = "",

    /**
     * Any notes or edge cases
     */
    val notes: String = "",

    /**
     * Was this step necessary? (e.g., Molad Zaken only if hour >= 18)
     */
    val wasApplied: Boolean = true,

    /**
     * Color code for UI (🟢 Talmud, 🔵 Algorithm, 🟠 Codification, 🟣 Derived, 🟡 Observance)
     */
    val colorCode: String = "",
)

/**
 * Categories of calculation steps
 */
enum class StepCategory {
    /**
     * Molad (conjugation) calculation
     */
    MOLAD_CALCULATION,

    /**
     * Dehiyyah (postponement) check
     */
    DEHIYYAH_CHECK,

    /**
     * Rosh Hashanah weekday determination
     */
    ROSH_HASHANAH_DETERMINATION,

    /**
     * Year type determination (leap/common, deficient/regular/complete)
     */
    YEAR_TYPE_DETERMINATION,

    /**
     * Month length calculation
     */
    MONTH_LENGTH_CALCULATION,

    /**
     * Weekday restriction validation
     */
    WEEKDAY_RESTRICTION,

    /**
     * Holiday date calculation
     */
    HOLIDAY_DATE_CALCULATION,

    /**
     * Other/meta steps
     */
    OTHER,
}

/**
 * Complete trace of a year's calculation.
 * 
 * Contains the full chain: from molad → final dates.
 * Can be converted to a narrative explanation.
 */
data class CalculationTrace(
    /**
     * The Hebrew year being calculated
     */
    val year: Int,

    /**
     * All steps in sequence
     */
    val steps: List<CalculationStep>,

    /**
     * The final result
     */
    val result: HebrewCalendarEngine.YearCalculation,

    /**
     * Whether all constraints were satisfied
     */
    val isValid: Boolean,

    /**
     * Any violations or warnings
     */
    val violations: List<String> = emptyList(),

    /**
     * Timestamp of calculation
     */
    val calculatedAt: Long = System.currentTimeMillis(),
) {
    /**
     * Get a specific step by ID
     */
    fun getStep(id: String): CalculationStep? {
        return steps.find { it.id == id }
    }

    /**
     * Get all steps in a category
     */
    fun getStepsInCategory(category: StepCategory): List<CalculationStep> {
        return steps.filter { it.category == category }
    }

    /**
     * Get the step by step number
     */
    fun getStepByNumber(number: Int): CalculationStep? {
        return steps.find { it.stepNumber == number }
    }

    /**
     * Total number of steps
     */
    fun stepCount(): Int = steps.size

    /**
     * Get all steps that were actually applied
     */
    fun getAppliedSteps(): List<CalculationStep> {
        return steps.filter { it.wasApplied }
    }
}

/**
 * Builder for constructing calculation traces.
 * 
 * Usage:
 * ```kotlin
 * val trace = CalculationTraceBuilder(5786)
 *     .addStep(moladStep)
 *     .addStep(moladZakenStep)
 *     .addStep(rhStep)
 *     .addStep(yearTypeStep)
 *     .build(result)
 * ```
 */
class CalculationTraceBuilder(val year: Int) {
    private val steps = mutableListOf<CalculationStep>()

    fun addStep(step: CalculationStep): CalculationTraceBuilder {
        steps.add(step.copy(stepNumber = steps.size + 1))
        return this
    }

    fun addSteps(newSteps: List<CalculationStep>): CalculationTraceBuilder {
        for (step in newSteps) {
            addStep(step)
        }
        return this
    }

    fun build(
        result: HebrewCalendarEngine.YearCalculation,
        isValid: Boolean = true,
        violations: List<String> = emptyList()
    ): CalculationTrace {
        return CalculationTrace(
            year = year,
            steps = steps.toList(),
            result = result,
            isValid = isValid,
            violations = violations
        )
    }
}

/**
 * Step factory methods for common calculation steps.
 */
object CalculationSteps {

    fun moladCalculation(
        year: Int,
        molad: HebrewTime,
        monthsElapsed: Int
    ): CalculationStep {
        return CalculationStep(
            id = "molad_$year",
            stepNumber = 0,  // Will be set by builder
            category = StepCategory.MOLAD_CALCULATION,
            title = "Calculate Molad of Tishrei",
            titleHebrew = "חישוב מולד תשרי",
            input = mapOf(
                "year" to year.toString(),
                "months_elapsed" to monthsElapsed.toString()
            ),
            output = mapOf(
                "molad_day" to dayName(molad.dayOfWeek),
                "molad_time" to "${molad.hour}:${molad.chalakim}"
            ),
            calculation = """
                Months elapsed in cycle: $monthsElapsed
                Average lunar month: 29d 12h 793p (Rambam VI)
                Molad Breshit: Monday 5h 204p
                Total chalakim from Breshit: $monthsElapsed × (29d 12h 793p)
                Result: ${dayName(molad.dayOfWeek)} ${molad.hour}:${molad.chalakim}
            """.trimIndent(),
            source = Sources.RAMBAM_VI_MOLAD,
            explanation = """
                The molad (conjugation) is calculated by multiplying the number of months 
                elapsed since creation (Molad Breshit) by the average lunar month length.
                This is the mathematical foundation of the Hebrew calendar.
            """,
            explanationHebrew = """
                המולד מחושב על ידי הכפלת מספר החודשים שחלפו מתחילת הבריאה בממוצע 
                חודשי הלבנה. זה הבסיס המתמטי של הלוח העברי.
            """,
            colorCode = "🔵"  // Algorithm (fixed calculation)
        )
    }

    fun moladZakenCheck(
        molad: HebrewTime,
        triggered: Boolean,
        newDay: Int? = null
    ): CalculationStep {
        return CalculationStep(
            id = "molad_zaken_${molad.hour}",
            stepNumber = 0,
            category = StepCategory.DEHIYYAH_CHECK,
            title = "Check Molad Zaken Rule",
            titleHebrew = "בדיקת כלל מולד זקן",
            input = mapOf(
                "molad_hour" to molad.hour.toString(),
                "molad_day" to dayName(molad.dayOfWeek)
            ),
            output = mapOf(
                "triggered" to triggered.toString(),
                "new_day" to (newDay?.let { dayName(it) } ?: "—")
            ),
            calculation = """
                Molad hour: ${molad.hour}
                Rule: if hour >= 18 → postpone to next day
                ${if (triggered) "✓ Hour ${molad.hour} >= 18 → POSTPONE" else "✗ Hour ${molad.hour} < 18 → no postponement"}
            """.trimIndent(),
            source = Sources.RAMBAM_VII_MOLAD_ZAKEN,
            explanation = """
                If the molad occurs at the 18th hour or later, Rosh Hashanah is 
                postponed to the next day. This prevents setting the holy day too late 
                in the evening, affecting observance.
            """,
            explanationHebrew = """
                אם המולד מתחיל בשעה 18 ומעלה, ראש השנה נדחה ליום הבא. 
                זה למנוע קביעת יום קדוש מאוחר מדי בערב.
            """,
            wasApplied = triggered,
            colorCode = "🟢"  // Talmudic principle
        )
    }

    fun loAduCheck(
        rhDay: Int,
        allowed: Boolean,
        ruleName: String = "Lo Adu Rosh"
    ): CalculationStep {
        return CalculationStep(
            id = "lo_adu_$rhDay",
            stepNumber = 0,
            category = StepCategory.DEHIYYAH_CHECK,
            title = "Check $ruleName Restriction",
            titleHebrew = "בדיקת כלל $ruleName",
            input = mapOf(
                "rh_day" to dayName(rhDay)
            ),
            output = mapOf(
                "allowed" to allowed.toString(),
                "result" to if (allowed) "✓ Allowed" else "✗ Forbidden"
            ),
            calculation = """
                Rosh Hashanah day: ${dayName(rhDay)}
                Forbidden days: Sunday (1), Wednesday (4), Friday (6)
                ${if (allowed) "✓ ${dayName(rhDay)} is allowed" else "✗ ${dayName(rhDay)} is forbidden → POSTPONE"}
            """.trimIndent(),
            source = Sources.RAMBAM_VII_LO_ADU,
            explanation = """
                Rosh Hashanah can never fall on Sunday, Wednesday, or Friday. 
                This ancient rule ensures proper holiday observance and prevents 
                conflicts with other holy days.
            """,
            explanationHebrew = """
                ראש השנה לא יכול להיות ביום ראשון, רביעי או שישי. 
                כלל קדום זה מבטיח ציות נכון לחגים ומונע קונפליקטים עם ימים קדושים אחרים.
            """,
            wasApplied = !allowed,  // Applied only if forbidden
            colorCode = "🟢"  // Talmudic principle
        )
    }

    fun yearTypeCalculation(
        rhDay: Int,
        cheshvanLength: Int,
        kislevLength: Int,
        yearLength: Int,
        isLeapYear: Boolean
    ): CalculationStep {
        val cheshvanType = if (cheshvanLength == 29) "חסרה" else "מלאה"
        val kislevType = if (kislevLength == 29) "חסרה" else "מלאה"

        return CalculationStep(
            id = "year_type_${rhDay}_$cheshvanLength$kislevLength",
            stepNumber = 0,
            category = StepCategory.YEAR_TYPE_DETERMINATION,
            title = "Determine Year Type and Month Lengths",
            titleHebrew = "קביעת סוג השנה ואורך החודשים",
            input = mapOf(
                "rh_day" to dayName(rhDay),
                "is_leap" to isLeapYear.toString()
            ),
            output = mapOf(
                "cheshvan_length" to "$cheshvanLength ($cheshvanType)",
                "kislev_length" to "$kislevLength ($kislevType)",
                "year_total" to "$yearLength days"
            ),
            calculation = """
                Rosh Hashanah: ${dayName(rhDay)}
                Next year's RH: calculated
                Days between: $yearLength
                
                Year classification:
                - Deficient (חסרה): 353/383 days
                - Regular (כסדרה): 354/384 days  
                - Complete (שלמה): 355/385 days
                
                Cheshvan and Kislev adjusted to maintain year length.
            """.trimIndent(),
            source = Sources.RAMBAM_VI_NINETEEN_YEAR_CYCLE,
            explanation = """
                The lengths of Cheshvan (חשוון) and Kislev (כסלו) are determined 
                by calculating the exact number of days between this year's and next year's 
                Rosh Hashanah. This creates three year types: deficient, regular, or complete.
            """,
            explanationHebrew = """
                אורכי חשוון וכסלו נקבעים על ידי חישוב מדויק של ימים בין ראש השנה של השנה 
                להשנה הבאה. זה יוצר שלושה סוגי שנים: חסרה, כסדרה או שלמה.
            """,
            colorCode = "🔵"  // Algorithm
        )
    }

    fun pesachDateCalculation(
        year: Int,
        roshHashanahDay: Int,
        isLeapYear: Boolean,
        pesachDay: Int
    ): CalculationStep {
        return CalculationStep(
            id = "pesach_date_$year",
            stepNumber = 0,
            category = StepCategory.HOLIDAY_DATE_CALCULATION,
            title = "Calculate Pesach Weekday",
            titleHebrew = "חישוב יום השבוע של פסח",
            input = mapOf(
                "rh_day" to dayName(roshHashanahDay),
                "days_from_rh" to "191${if (isLeapYear) " (leap)" else ""}"
            ),
            output = mapOf(
                "pesach_day" to dayName(pesachDay),
                "allowed" to if (pesachDay in listOf(1, 3, 5, 7)) "✓" else "✗"
            ),
            calculation = """
                15 Nisan is 191 days after 1 Tishrei
                (192 in leap years, accounting for Adar I)
                
                Distance: 191 ${if (isLeapYear) "/ 192" else ""} days = 27 weeks + 1 ${if (isLeapYear) "+ 1" else ""} day
                Pesach weekday = (RH day + offset) mod 7
                Result: ${dayName(pesachDay)}
            """.trimIndent(),
            source = Sources.PESACHIM_58A_PESACH_SHABBAT,
            explanation = """
                Pesach (15 Nisan) is always 191 days after Rosh Hashanah (15 Tishrei).
                This fixed distance determines Pesach's weekday once RH is known.
                The result must comply with Lo Bdu: never Mon/Wed/Fri.
            """,
            explanationHebrew = """
                פסח (15 ניסן) הוא תמיד 191 ימים אחרי ראש השנה. 
                המרחק הקבוע הזה קובע את יום השבוע של פסח ברגע שידוע ראש השנה.
                התוצאה חייבת להיות בקביעות לא בד"ו.
            """,
            colorCode = "🟣"  // Derived from Rambam
        )
    }

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
