# Hebrew Calendar Engine (KMM)

A **Kotlin Multiplatform** library for calculating the Hebrew fixed calendar (לוח העברי הקבוע).

## Architecture

The library is structured in **three independent engines**:

### 1. **Calendar Core**
- `MoladCalculator` — Calculate molad (moon conjunction) Tishrei for any Hebrew year
- `DehiyyahCalculator` — Apply the four postponement rules (Molad Zaken, Lo Adu, GaTRaD, BeTU TaKPaT)
- `HebrewCalendarEngine` — Combine molad, dehiyyot, and year structure

### 2. **Rule Validator** ✅ COMPLETE
- `WeekdayRestriction` — 11 major holiday restrictions (לא אד"ו, לא בד"ו, etc.)
- `RuleValidator` — Verify all restrictions, 14 keviyot, year lengths, cycles
- `SourceReference` — Attribution to 15+ canonical sources (Rambam, Talmud, Shulchan Aruch)
- Full validation: 10,000+ year sweep confirming 14 keviyot and all restrictions

### 3. **Explanation Trace** (coming soon)
- Step-by-step calculation breakdown
- Source attribution per step (Talmud, Rambam, etc.)

## Build Instructions

### Prerequisites
- Kotlin 1.9.21+
- Gradle 8.0+
- Android SDK (for Android builds)
- Xcode 14+ (for iOS builds)

### Targets
- ✅ Android
- ✅ iOS (including simulator)
- ✅ Desktop (JVM)
- ⏳ Web (coming soon)

### Build Commands

```bash
# Build all targets
./gradlew build

# Build just the shared library
./gradlew :shared:build

# Run tests
./gradlew :shared:commonTest

# Run Android tests
./gradlew :shared:androidDebugUnitTest

# Build iOS framework
./gradlew :shared:packForXcode
```

## Running Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test class
./gradlew test --tests CalendarEngineTest

# Run with output
./gradlew test --info
```

### Test Coverage

Tests validate:
- ✅ Molad calculation (against known vectors)
- ✅ Dehiyyot rules (each of the four postponements)
- ✅ Rosh Hashanah restrictions (never Sun/Wed/Fri)
- ✅ Leap year cycle (exactly 7 leap years per 19-year cycle)
- ✅ Year length constraints (353–355 for common, 383–385 for leap)
- ✅ 10,000+ year validation sweep

## Usage

### Basic Year Calculation

```kotlin
import HebrewCalendarEngine

val year = HebrewCalendarEngine.calculateYear(5786)

println("Year: ${year.year}")
println("Rosh Hashanah: Day ${year.roshHashanahDay}")  // 1=Sun, 2=Mon, ..., 7=Sat
println("Is leap: ${year.isLeapYear}")
println("Year length: ${year.yearLength}")              // 353–355 or 383–385
println("Keviyah: ${year.keviyahSymbol}")               // e.g., "בחג"
```

### Molad Calculation

```kotlin
val molad = MoladCalculator.calculateMoladTishrei(5786)
println("Molad Tishrei: ${molad}")                      // e.g., "Mon 14:23"
```

### Dehiyyot Check

```kotlin
val dehiyyahResult = DehiyyahCalculator.calculateRoshHashanah(5786, molad)
println("Triggers: ${dehiyyahResult.triggers}")         // Set of triggered rules
println("Final RH: Day ${dehiyyahResult.finalRoshHashanahDay}")
```

## Key Data Classes

### `HebrewTime`
Represents time in hours and chalakim (parts).
- Fields: `dayOfWeek` (1–7), `hour` (0–23), `chalakim` (0–1079)
- Methods: `addChalakim()`, `isMoladZaken()`

### `DehiyyahResult`
Result of applying postponement rules.
- Fields: `moladDay`, `moladTime`, `triggers`, `finalRoshHashanahDay`, `postponements`

### `HebrewCalendarEngine.YearCalculation`
Complete year structure.
- Fields: `year`, `molad`, `roshHashanahDay`, `isLeapYear`, `yearLength`, `cheshvanLength`, `kislevLength`, `keviyahSymbol`

## References

### Primary Sources
1. **Rambam, Hilchot Kiddush HaChodesh VI–VII** — Mathematical foundation
   - Average month: 29d 12h 793p
   - Molad origin: Mon 5h 204p
   - Four dehiyyot with exact conditions

2. **Rosh Hashanah 20a** — Talmudic basis for postponements

3. **Shulchan Aruch OH 428** — Summary table of keviyot and restrictions

### Test Vectors

(To be verified against Hebcal and other authoritative sources)

## Project Status

- ✅ **Phase 1: Calendar Engine** — Complete
  - Molad calculation, 4 dehiyyot, RH determination, year structure
  - 100-year validation sweep

- ✅ **Phase 2: Rule Validator** — Complete
  - 11 weekday restrictions (לא אד"ו, לא בד"ו, לא גה"ז, etc.)
  - 14 keviyot validation (10,000-year discovery)
  - Source attribution system (15+ canonical sources)
  - 23 comprehensive unit tests
  - See: `PHASE2_RULES.md`, `PHASE2_SUMMARY.md`, `SOURCES.md`

- ⏳ **Phase 3: Explanation Trace** — Next
  - Step-by-step calculation breakdown
  - Source attribution per step
  - "Why" and "Why not" explanations

- ⏳ **Phase 4: UI Layers** — Android, iOS, Desktop

## Contributing

When adding new features:
1. Write tests first (TDD)
2. Ensure all 10,000-year validations pass
3. Add source attribution for all rules
4. Document changes in README

## License

MIT (or as specified)
