/**
 * Traceable variant of the Hebrew Calendar Engine.
 * 
 * Returns a CalculationTrace instead of (or in addition to) the raw result.
 * This makes every calculation auditable and explainable.
 */

object TraceableHebrewCalendarEngine {

    /**
     * Calculate a year AND track every step.
     * 
     * @param year The Hebrew year to calculate
     * @return Pair of (YearCalculation result, CalculationTrace with all steps)
     */
    fun calculateYearWithTrace(year: Int): Pair<HebrewCalendarEngine.YearCalculation, CalculationTrace> {
        val builder = CalculationTraceBuilder(year)
        val violations = mutableListOf<String>()

        // ═══════════════════════════════════════════════
        // STEP 1: Calculate Molad
        // ═══════════════════════════════════════════════

        val isLeapYear = ((year - 1) % 19) + 1 in listOf(3, 6, 8, 11, 14, 17, 19)
        val monthsElapsed = calculateMonthsElapsed(year)
        val molad = MoladCalculator.calculateMoladTishrei(year)

        builder.addStep(CalculationSteps.moladCalculation(year, molad, monthsElapsed))

        // ═══════════════════════════════════════════════
        // STEP 2: Apply Dehiyyot (Postponement Rules)
        // ═══════════════════════════════════════════════

        val dehiyyahResult = DehiyyahCalculator.calculateRoshHashanah(year, molad)

        // Step 2a: Molad Zaken
        val moladZakenTriggered = molad.hour >= 18
        if (moladZakenTriggered) {
            builder.addStep(CalculationSteps.moladZakenCheck(
                molad,
                triggered = true,
                newDay = if (molad.dayOfWeek == 7) 1 else molad.dayOfWeek + 1
            ))
        }

        // Step 2b: Lo Adu Rosh
        val loAduAllowed = dehiyyahResult.finalRoshHashanahDay !in listOf(1, 4, 6)
        builder.addStep(CalculationSteps.loAduCheck(
            dehiyyahResult.finalRoshHashanahDay,
            allowed = loAduAllowed,
            ruleName = "Lo Adu Rosh (לא אד"ו ראש)"
        ))

        if (!loAduAllowed) {
            violations.add("Lo Adu Rosh restriction violated: RH on ${dehiyyahResult.finalRoshHashanahDay}")
        }

        // ═══════════════════════════════════════════════
        // STEP 3: Determine Year Type and Month Lengths
        // ═══════════════════════════════════════════════

        val result = HebrewCalendarEngine.calculateYear(year)
        builder.addStep(CalculationSteps.yearTypeCalculation(
            result.roshHashanahDay,
            result.cheshvanLength,
            result.kislevLength,
            result.yearLength,
            result.isLeapYear
        ))

        // ═══════════════════════════════════════════════
        // STEP 4: Validate Against Rules
        // ═══════════════════════════════════════════════

        val validationReport = RuleValidator.validateCompleteYear(result)
        if (!validationReport.allRestrictionsValid) {
            violations.addAll(validationReport.violations)
        }

        // ═══════════════════════════════════════════════
        // STEP 5: Calculate Holiday Dates (Optional)
        // ═══════════════════════════════════════════════

        // Pesach weekday (15 Nisan = 191 days after 1 Tishrei)
        val pesachOffset = if (isLeapYear) 192 else 191
        val pesachDay = ((result.roshHashanahDay - 1 + pesachOffset) % 7) + 1

        builder.addStep(CalculationSteps.pesachDateCalculation(
            year,
            result.roshHashanahDay,
            isLeapYear,
            pesachDay
        ))

        // ═══════════════════════════════════════════════
        // Build and Return Trace
        // ═══════════════════════════════════════════════

        val trace = builder.build(result, violations.isEmpty(), violations)

        return Pair(result, trace)
    }

    /**
     * Calculate how many months have elapsed since creation.
     */
    private fun calculateMonthsElapsed(year: Int): Int {
        val completeCycles = (year - 1) / 19
        val posInCycle = ((year - 1) % 19) + 1
        val leapPositions = listOf(3, 6, 8, 11, 14, 17, 19)

        val commonMonthsInCycle = (posInCycle - 1) - leapPositions.count { it <= posInCycle }
        val leapMonthsInCycle = leapPositions.count { it <= posInCycle }

        val monthsInCycle = commonMonthsInCycle * 12 + leapMonthsInCycle * 13

        return completeCycles * 235 + monthsInCycle
    }

    /**
     * Get only the explanation (convert trace to explanation).
     */
    fun explainYear(year: Int, language: String = "en"): Explanation {
        val (_, trace) = calculateYearWithTrace(year)
        return ExplanationEngine.explainYear(trace, language)
    }

    /**
     * Get a full narrative explanation with sources.
     */
    fun getFullExplanation(year: Int, language: String = "en"): String {
        val explanation = explainYear(year, language)
        return explanation.fullExplanation
    }

    /**
     * Get HTML representation for web/UI display.
     */
    fun getHTMLExplanation(year: Int): String {
        val (_, trace) = calculateYearWithTrace(year)
        return ExplanationEngine.generateHTML(trace)
    }

    /**
     * Get a simple one-liner summary.
     */
    fun getSummary(year: Int, language: String = "en"): String {
        val explanation = explainYear(year, language)
        return explanation.summary
    }

    /**
     * Get step-by-step breakdown.
     */
    fun getStepByStepBreakdown(year: Int, language: String = "en"): List<String> {
        val explanation = explainYear(year, language)
        return explanation.stepByStepBreakdown
    }

    /**
     * Get all sources cited in the calculation.
     */
    fun getSourcesCited(year: Int): List<SourceReference> {
        val explanation = explainYear(year)
        return explanation.sources.distinctBy { it.id }
    }
}
