/**
 * Explanation Engine — converts calculation traces to human-readable narratives.
 * 
 * Transforms a CalculationTrace into plain-language explanations with sources cited.
 * 
 * Output formats:
 * - Plain text (one-liner)
 * - Full explanation (detailed, with sources)
 * - Step-by-step (each calculation step broken down)
 * - HTML/Markdown (formatted for display)
 */

/**
 * A narrative explanation of a calculation.
 */
data class Explanation(
    /**
     * Brief one-line summary
     */
    val summary: String,

    /**
     * Full detailed explanation
     */
    val fullExplanation: String,

    /**
     * Step-by-step breakdown
     */
    val stepByStepBreakdown: List<String>,

    /**
     * All sources cited (in order)
     */
    val sources: List<SourceReference>,

    /**
     * Language ("en" or "he")
     */
    val language: String = "en",

    /**
     * Whether this explains a violation or success
     */
    val isViolation: Boolean = false,
)

/**
 * Generates narrative explanations from calculation traces.
 */
object ExplanationEngine {

    /**
     * Generate a complete explanation for a year's calculation.
     */
    fun explainYear(trace: CalculationTrace, language: String = "en"): Explanation {
        val sources = mutableSetOf<SourceReference>()
        val stepExplanations = mutableListOf<String>()

        // Collect all sources
        for (step in trace.steps) {
            sources.add(step.source)
            stepExplanations.add(formatStep(step, language))
        }

        val summary = generateSummary(trace, language)
        val fullExplanation = generateFullExplanation(trace, language, sources.toList())
        val isViolation = trace.violations.isNotEmpty()

        return Explanation(
            summary = summary,
            fullExplanation = fullExplanation,
            stepByStepBreakdown = stepExplanations,
            sources = sources.toList(),
            language = language,
            isViolation = isViolation
        )
    }

    /**
     * Generate a summary explanation for a single calculation step.
     */
    fun explainStep(step: CalculationStep, language: String = "en"): String {
        return when (language) {
            "he" -> step.explanationHebrew.ifEmpty { step.explanation }
            else -> step.explanation
        }
    }

    /**
     * Format one step for narrative display.
     */
    private fun formatStep(step: CalculationStep, language: String): String {
        return when (step.category) {
            StepCategory.MOLAD_CALCULATION -> {
                when (language) {
                    "he" -> """
                        שלב ${step.stepNumber}: ${step.titleHebrew}
                        
                        חישוב המולד:
                        - חודשים שחלפו מהבריאה: ${step.input["months_elapsed"]}
                        - חודש ממוצע לבנה: 29 ימים 12 שעות 793 חלקים (רמב"ם ו)
                        - מולד בראשית: יום שני 5 שעות 204 חלקים
                        
                        תוצאה: ${step.output["molad_day"]} בשעה ${step.output["molad_time"]}
                        מקור: ${step.source.title}
                    """.trimIndent()
                    else -> """
                        Step ${step.stepNumber}: ${step.title}
                        
                        Molad calculation:
                        - Months elapsed from creation: ${step.input["months_elapsed"]}
                        - Average lunar month: 29d 12h 793p (Rambam VI)
                        - Molad Breshit: Monday 5h 204p
                        
                        Result: ${step.output["molad_day"]} at ${step.output["molad_time"]}
                        Source: ${step.source.title}
                    """.trimIndent()
                }
            }

            StepCategory.DEHIYYAH_CHECK -> {
                when (language) {
                    "he" -> """
                        שלב ${step.stepNumber}: ${step.titleHebrew}
                        
                        בדיקת כלל דחיה:
                        - כלל: ${step.source.title}
                        - הסבר: ${step.explanationHebrew}
                        - נוקטים בו: ${if (step.wasApplied) "כן ✓" else "לא"}
                    """.trimIndent()
                    else -> """
                        Step ${step.stepNumber}: ${step.title}
                        
                        Dehiyyah (postponement) check:
                        - Rule: ${step.source.title}
                        - Explanation: ${step.explanation}
                        - Applied: ${if (step.wasApplied) "Yes ✓" else "No"}
                    """.trimIndent()
                }
            }

            StepCategory.ROSH_HASHANAH_DETERMINATION -> {
                when (language) {
                    "he" -> """
                        שלב ${step.stepNumber}: ${step.titleHebrew}
                        
                        קביעת יום ראש השנה:
                        - יום קביעה: ${step.output["rh_day"]}
                        - בדיקות שעברו: לא אד"ו, לא מולד זקן, וכו'
                        - תוצאה סופית: ✓
                    """.trimIndent()
                    else -> """
                        Step ${step.stepNumber}: ${step.title}
                        
                        Rosh Hashanah determination:
                        - Final day: ${step.output["rh_day"]}
                        - Restrictions passed: Lo Adu, Molad Zaken, etc.
                        - Final result: ✓
                    """.trimIndent()
                }
            }

            else -> """
                Step ${step.stepNumber}: ${step.title}
                
                ${step.explanation}
                Source: ${step.source.title}
            """.trimIndent()
        }
    }

    /**
     * Generate a one-line summary.
     */
    private fun generateSummary(trace: CalculationTrace, language: String): String {
        val rhDay = trace.result.roshHashanahDay
        val rhDayName = dayName(rhDay, language)
        val keviyah = trace.result.keviyahSymbol

        return when (language) {
            "he" -> """
                שנה ${trace.year}: ראש השנה ביום $rhDayName 
                (קביעה: $keviyah, שנה ${if (trace.result.isLeapYear) "מעוברת" else "פשוטה"})
            """.trimIndent()
            else -> """
                Year ${trace.year}: Rosh Hashanah on $rhDayName 
                (keviyah: $keviyah, ${if (trace.result.isLeapYear) "leap year" else "simple year"})
            """.trimIndent()
        }
    }

    /**
     * Generate a full detailed explanation.
     */
    private fun generateFullExplanation(
        trace: CalculationTrace,
        language: String,
        sources: List<SourceReference>
    ): String {
        val sb = StringBuilder()

        when (language) {
            "he" -> {
                sb.append("הסבר מלא לחישוב שנה ${trace.year}\n")
                sb.append("═══════════════════════════════════════════════\n\n")

                sb.append("ראש השנה: ${dayName(trace.result.roshHashanahDay, language)}\n")
                sb.append("קביעה (קביעיות): ${trace.result.keviyahSymbol}\n")
                sb.append("סוג שנה: ${if (trace.result.isLeapYear) "מעוברת" else "פשוטה"}\n")
                sb.append("אורך שנה: ${trace.result.yearLength} ימים\n\n")

                sb.append("שלבי חישוב:\n")
                sb.append("─────────────────────────────────────────────────\n\n")

                for ((i, step) in trace.steps.withIndex()) {
                    sb.append("${i + 1}. ${step.titleHebrew}\n")
                    sb.append("   ${step.explanationHebrew}\n")
                    sb.append("   מקור: ${step.source.titleHebrew}\n\n")
                }

                sb.append("\nמקורות:\n")
                sb.append("─────────────────────────────────────────────────\n")
                sources.forEachIndexed { i, source ->
                    sb.append("${i + 1}. ${source.titleHebrew}\n")
                    sb.append("   ${source.citation}\n")
                    if (source.explanation.isNotEmpty()) {
                        sb.append("   הסבר: ${source.explanation}\n")
                    }
                    sb.append("\n")
                }
            }

            else -> {
                sb.append("Full Explanation of Year ${trace.year} Calculation\n")
                sb.append("═══════════════════════════════════════════════\n\n")

                sb.append("Rosh Hashanah: ${dayName(trace.result.roshHashanahDay, language)}\n")
                sb.append("Keviyah: ${trace.result.keviyahSymbol}\n")
                sb.append("Year Type: ${if (trace.result.isLeapYear) "Leap" else "Simple"}\n")
                sb.append("Year Length: ${trace.result.yearLength} days\n\n")

                sb.append("Calculation Steps:\n")
                sb.append("─────────────────────────────────────────────────\n\n")

                for ((i, step) in trace.steps.withIndex()) {
                    sb.append("${i + 1}. ${step.title}\n")
                    sb.append("   ${step.explanation}\n")
                    sb.append("   Source: ${step.source.title}\n\n")
                }

                sb.append("\nSources:\n")
                sb.append("─────────────────────────────────────────────────\n")
                sources.forEachIndexed { i, source ->
                    sb.append("${i + 1}. ${source.title}\n")
                    sb.append("   ${source.citation}\n")
                    if (source.explanation.isNotEmpty()) {
                        sb.append("   Explanation: ${source.explanation}\n")
                    }
                    sb.append("\n")
                }
            }
        }

        return sb.toString()
    }

    /**
     * Generate an HTML explanation for web display.
     */
    fun generateHTML(trace: CalculationTrace): String {
        val sb = StringBuilder()

        sb.append("<div class=\"calculation-trace\">\n")
        sb.append("<h1>Year ${trace.year} Calculation</h1>\n\n")

        sb.append("<div class=\"summary\">\n")
        sb.append("<p><strong>Rosh Hashanah:</strong> ${dayName(trace.result.roshHashanahDay, "en")}</p>\n")
        sb.append("<p><strong>Keviyah:</strong> ${trace.result.keviyahSymbol}</p>\n")
        sb.append("<p><strong>Year Type:</strong> ${if (trace.result.isLeapYear) "Leap (מעוברת)" else "Simple (פשוטה)"}</p>\n")
        sb.append("<p><strong>Year Length:</strong> ${trace.result.yearLength} days</p>\n")
        sb.append("</div>\n\n")

        sb.append("<div class=\"steps\">\n")
        sb.append("<h2>Calculation Steps</h2>\n")
        for ((i, step) in trace.steps.withIndex()) {
            sb.append("<div class=\"step\" data-step=\"${i + 1}\">\n")
            sb.append("<h3>${step.title}</h3>\n")
            sb.append("<p>${step.explanation}</p>\n")
            sb.append("<p><em>Source: ${step.source.title}</em></p>\n")
            sb.append("</div>\n\n")
        }
        sb.append("</div>\n")

        sb.append("<div class=\"sources\">\n")
        sb.append("<h2>Sources Cited</h2>\n")
        sb.append("<ul>\n")
        val uniqueSources = trace.steps.map { it.source }.distinctBy { it.id }
        for (source in uniqueSources) {
            sb.append("<li><strong>${source.title}</strong><br/>\n")
            sb.append("<em>${source.citation}</em></li>\n")
        }
        sb.append("</ul>\n")
        sb.append("</div>\n\n")

        sb.append("</div>\n")

        return sb.toString()
    }

    /**
     * Day name for display
     */
    private fun dayName(day: Int, language: String): String {
        return when (language) {
            "he" -> when (day) {
                1 -> "ראשון"
                2 -> "שני"
                3 -> "שלישי"
                4 -> "רביעי"
                5 -> "חמישי"
                6 -> "שישי"
                7 -> "שבת"
                else -> "לא ידוע"
            }
            else -> when (day) {
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
}
