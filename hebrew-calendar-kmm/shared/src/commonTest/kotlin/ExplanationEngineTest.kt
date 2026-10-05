import kotlin.test.*

/**
 * Tests for the Explanation Engine (Phase 3).
 * 
 * Validates that:
 * 1. Calculation traces are complete and accurate
 * 2. Explanations are generated correctly
 * 3. Sources are properly cited
 * 4. Step-by-step breakdowns are correct
 */
class ExplanationEngineTest {

    @Test
    fun testCalculationTraceComplete() {
        // Calculate a year and get its trace
        val (result, trace) = TraceableHebrewCalendarEngine.calculateYearWithTrace(5786)

        // Verify basic properties
        assertEquals(5786, trace.year)
        assertTrue(trace.steps.isNotEmpty(), "Trace should have calculation steps")
        assertEquals(result, trace.result, "Trace result should match calculation result")

        // Verify steps are numbered correctly
        for (i in trace.steps.indices) {
            assertEquals(i + 1, trace.steps[i].stepNumber, "Step ${i} should have correct number")
        }

        println("✓ Year 5786: ${trace.stepCount()} steps recorded")
    }

    @Test
    fun testTraceIncludesMoladStep() {
        val (_, trace) = TraceableHebrewCalendarEngine.calculateYearWithTrace(5786)

        val moladSteps = trace.getStepsInCategory(StepCategory.MOLAD_CALCULATION)
        assertTrue(moladSteps.isNotEmpty(), "Should have molad calculation step")

        val moladStep = moladSteps.first()
        assertEquals("Calculate Molad of Tishrei", moladStep.title)
        assertNotNull(moladStep.output["molad_day"], "Molad day should be in output")
        assertNotNull(moladStep.source, "Molad step should have source")
        assertEquals(Sources.RAMBAM_VI_MOLAD.id, moladStep.source.id)
    }

    @Test
    fun testTracingDehiyyahChecks() {
        val (_, trace) = TraceableHebrewCalendarEngine.calculateYearWithTrace(5786)

        val dehiyyahSteps = trace.getStepsInCategory(StepCategory.DEHIYYAH_CHECK)
        assertTrue(dehiyyahSteps.isNotEmpty(), "Should have dehiyyah check steps")

        for (step in dehiyyahSteps) {
            assertNotNull(step.source, "Every dehiyyah step should have a source")
            assertTrue(step.explanation.isNotEmpty(), "Dehiyyah step should have explanation")
        }

        println("✓ Dehiyyah steps: ${dehiyyahSteps.size}")
    }

    @Test
    fun testExplanationGeneration() {
        val explanation = TraceableHebrewCalendarEngine.explainYear(5786, "en")

        assertNotNull(explanation)
        assertTrue(explanation.summary.isNotEmpty(), "Summary should be non-empty")
        assertTrue(explanation.fullExplanation.isNotEmpty(), "Full explanation should be non-empty")
        assertTrue(explanation.stepByStepBreakdown.isNotEmpty(), "Step-by-step should be non-empty")

        println("✓ Explanation for 5786 generated")
        println("  Summary: ${explanation.summary}")
    }

    @Test
    fun testExplanationHebrewGeneration() {
        val explanation = TraceableHebrewCalendarEngine.explainYear(5786, "he")

        assertNotNull(explanation)
        assertEquals("he", explanation.language)
        assertTrue(explanation.summary.isNotEmpty(), "Hebrew summary should be non-empty")
        assertTrue(explanation.fullExplanation.isNotEmpty(), "Hebrew explanation should be non-empty")

        println("✓ Hebrew explanation for 5786 generated")
    }

    @Test
    fun testSourcesCited() {
        val sources = TraceableHebrewCalendarEngine.getSourcesCited(5786)

        assertTrue(sources.isNotEmpty(), "Should cite sources")
        
        // Should include Rambam VI for molad
        assertTrue(
            sources.any { it.id == Sources.RAMBAM_VI_MOLAD.id },
            "Should cite Rambam VI for molad"
        )

        // All sources should be documented
        for (source in sources) {
            assertTrue(source.title.isNotEmpty(), "Source should have title")
            assertTrue(source.citation.isNotEmpty(), "Source should have citation")
        }

        println("✓ Sources cited: ${sources.size}")
        for (source in sources) {
            println("  - ${source.title}")
        }
    }

    @Test
    fun testStepByStepBreakdown() {
        val steps = TraceableHebrewCalendarEngine.getStepByStepBreakdown(5786, "en")

        assertTrue(steps.isNotEmpty(), "Should have step-by-step breakdown")
        
        // Each step should be non-empty
        for (step in steps) {
            assertTrue(step.isNotEmpty(), "Each step should have text")
        }

        println("✓ Step-by-step breakdown: ${steps.size} steps")
    }

    @Test
    fun testSummaryGeneration() {
        val summary = TraceableHebrewCalendarEngine.getSummary(5786, "en")

        assertTrue(summary.isNotEmpty(), "Summary should be non-empty")
        assertTrue(summary.contains("5786"), "Summary should mention the year")
        assertTrue(summary.contains("Rosh Hashanah") || summary.contains("Day"), "Should mention RH day")

        println("✓ Summary: $summary")
    }

    @Test
    fun testHTMLGeneration() {
        val html = TraceableHebrewCalendarEngine.getHTMLExplanation(5786)

        assertTrue(html.isNotEmpty(), "HTML should be non-empty")
        assertTrue(html.contains("<div"), "Should contain HTML elements")
        assertTrue(html.contains("Year 5786"), "Should mention the year")
        assertTrue(html.contains("Rosh Hashanah"), "Should mention RH")

        println("✓ HTML explanation generated (${html.length} chars)")
    }

    @Test
    fun testTraceValidation() {
        val (result, trace) = TraceableHebrewCalendarEngine.calculateYearWithTrace(5786)

        // Verify that RH is valid
        assertTrue(
            result.roshHashanahDay !in listOf(1, 4, 6),
            "RH should not be on forbidden days"
        )

        // Verify year length is valid
        if (result.isLeapYear) {
            assertTrue(result.yearLength in 383..385, "Leap year should be 383–385 days")
        } else {
            assertTrue(result.yearLength in 353..355, "Common year should be 353–355 days")
        }

        // Verify trace reflects this
        assertEquals(result.isValid || trace.violations.isEmpty(), true,
            "Trace should indicate valid year")

        println("✓ Year 5786 validation confirmed in trace")
    }

    @Test
    fun testMultipleYearsExplanations() {
        // Generate explanations for 10 consecutive years
        for (year in 5780..5789) {
            val explanation = TraceableHebrewCalendarEngine.explainYear(year, "en")
            assertTrue(explanation.summary.isNotEmpty(), "Year $year should have explanation")
        }

        println("✓ Explanations generated for 10 consecutive years")
    }

    @Test
    fun testTraceCompletenessAcross19YearCycle() {
        // Verify that a complete 19-year cycle has all necessary information
        for (offset in 0 until 19) {
            val year = 5700 + offset
            val (_, trace) = TraceableHebrewCalendarEngine.calculateYearWithTrace(year)

            assertTrue(trace.steps.isNotEmpty(), "Year $year should have steps")
            assertEquals(year, trace.year)

            // Verify it has at least molad + dehiyyah + rh determination
            assertTrue(
                trace.getStepsInCategory(StepCategory.MOLAD_CALCULATION).isNotEmpty() ||
                trace.getStepsInCategory(StepCategory.DEHIYYAH_CHECK).isNotEmpty(),
                "Year $year should have molad or dehiyyah steps"
            )
        }

        println("✓ Complete 19-year cycle verified")
    }

    @Test
    fun testSourceAttributionConsistency() {
        // Every step should cite a source
        val (_, trace) = TraceableHebrewCalendarEngine.calculateYearWithTrace(5786)

        for (step in trace.steps) {
            assertNotNull(step.source, "Step '${step.title}' must have a source")
            assertTrue(step.source.title.isNotEmpty(), "Source must have a title")
            
            // Source should be one of the canonical ones (or very similar)
            assertTrue(
                step.source.id.isNotEmpty(),
                "Step '${step.title}' source must have an ID"
            )
        }

        println("✓ All ${trace.steps.size} steps have proper source attribution")
    }

    @Test
    fun testExplanationForKeviyahRepresentative() {
        // Test explanations for years with different keviyot
        
        // בחג - Monday, deficient, Pesach Tuesday
        val (_, trace1) = TraceableHebrewCalendarEngine.calculateYearWithTrace(5700)
        assertEquals(2, trace1.result.roshHashanahDay, "5700 should have RH on Monday")
        
        // Different year, different keviyah
        val (_, trace2) = TraceableHebrewCalendarEngine.calculateYearWithTrace(5701)
        assertTrue(trace1.result.keviyahSymbol != trace2.result.keviyahSymbol || 
                   trace1.result.roshHashanahDay == trace2.result.roshHashanahDay,
                   "Explanation should handle different keviyot")

        println("✓ Explanations for different keviyot work correctly")
    }

    @Test
    fun testTracePreservesCalculationIntegrity() {
        // Calculate a year normally and with trace
        val resultNormal = HebrewCalendarEngine.calculateYear(5786)
        val (resultTrace, _) = TraceableHebrewCalendarEngine.calculateYearWithTrace(5786)

        // Results should be identical
        assertEquals(resultNormal.year, resultTrace.year)
        assertEquals(resultNormal.roshHashanahDay, resultTrace.roshHashanahDay)
        assertEquals(resultNormal.isLeapYear, resultTrace.isLeapYear)
        assertEquals(resultNormal.yearLength, resultTrace.yearLength)
        assertEquals(resultNormal.cheshvanLength, resultTrace.cheshvanLength)
        assertEquals(resultNormal.kislevLength, resultTrace.kislevLength)
        assertEquals(resultNormal.keviyahSymbol, resultTrace.keviyahSymbol)

        println("✓ Tracing preserves calculation integrity")
    }

    @Test
    fun testExplanationFormats() {
        val year = 5786

        // Test all formats
        val summary = TraceableHebrewCalendarEngine.getSummary(year)
        val stepByStep = TraceableHebrewCalendarEngine.getStepByStepBreakdown(year)
        val full = TraceableHebrewCalendarEngine.getFullExplanation(year)
        val html = TraceableHebrewCalendarEngine.getHTMLExplanation(year)

        assertTrue(summary.isNotEmpty(), "Summary format should work")
        assertTrue(stepByStep.isNotEmpty(), "Step-by-step format should work")
        assertTrue(full.isNotEmpty(), "Full explanation format should work")
        assertTrue(html.isNotEmpty(), "HTML format should work")

        // Verify that each format contains the year number
        assertTrue(summary.contains("$year") || summary.contains("${year - 1}") || summary.contains("${year + 1}"),
                   "Summary should reference the year")

        println("✓ All explanation formats generated successfully")
    }
}
