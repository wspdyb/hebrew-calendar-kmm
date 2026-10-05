# Phase 2: Rule Validator

## Overview

**Phase 2** implements the second pillar of the calendar engine: **verification** that all calculated dates conform to the established halachic and mathematical rules.

The architecture is:

```
Phase 1: Calendar Engine        Phase 2: Rule Validator
(calculates dates)              (verifies correctness)
         ↓                              ↓
    Molad + Dehiyyot        WeekdayRestrictions
    → RH weekday            + SourceReferences
    → Year structure        + RuleValidator
```

## Three Key Components

### 1. SourceReference — Attribution System

Every rule must trace back to an authoritative source.

**Types of sources:**

- `TALMUD` — Direct Talmudic discussion
- `RAMBAM` — Rambam (Maimonides), Hilchot Kiddush HaChodesh VI–VII
- `SHULCHAN_ARUCH` — Karo's codification, Orach Chaim
- `DERIVED` — Mathematically derived from Rambam's principles
- `ARITHMETIC` — Pure mathematical relationship (e.g., Plag)
- `VALIDATED` — Empirically verified across 10,000+ years

**Example: Molad Zaken**

```kotlin
val RAMBAM_VII_MOLAD_ZAKEN = SourceReference(
    id = "RAMBAM_VII_MOLAD_ZAKEN",
    type = SourceType.RAMBAM,
    title = "Rambam, Hilchot Kiddush HaChodesh VII, 1",
    titleHebrew = "הלכות קידוש החודש חלק ז הלכה א",
    citation = "Hilchot Kiddush HaChodesh VII, 1",
    quote = "If the molad falls at the eighteenth hour or later, we postpone Rosh Hashanah to the next day.",
    explanation = "Molad Zaken (aged molad): postpone if hour >= 18.",
)
```

### 2. WeekdayRestriction — Calendar Rules

Each significant Hebrew date has restrictions on which weekdays it may fall.

**Core Restrictions:**

#### Rosh Hashanah (1 Tishrei)
- **Allowed:** Mon, Tue, Thu, Sat (2, 3, 5, 7)
- **Forbidden:** Sun, Wed, Fri (1, 4, 6)
- **Rule:** Lo Adu Rosh (לא אד"ו ראש)
- **Source:** Rambam VII, 2

#### Yom Kippur (10 Tishrei)
- **Allowed:** Mon, Tue, Wed, Thu, Sat (2, 3, 4, 5, 7)
- **Forbidden:** Sun, Fri (1, 6)
- **Rule:** Lo Agu Yom Kippur
- **Derivation:** YK = RH + 9 days; 9 mod 7 = 2
- **Example:** If RH ∈ {2,3,5,7}, then YK ∈ {4,5,7,2,3} = {2,3,4,5,7}

#### Pesach (15 Nisan)
- **Allowed:** Sun, Tue, Thu, Sat (1, 3, 5, 7)
- **Forbidden:** Mon, Wed, Fri (2, 4, 6)
- **Rule:** Lo Bdu Pesach (לא בד"ו פסח)
- **Source:** Pesachim 58a–58b

#### Shavuot (6 Sivan)
- **Allowed:** Sun, Mon, Wed, Fri (1, 2, 4, 6)
- **Forbidden:** Tue, Thu, Sat (3, 5, 7)
- **Rule:** Lo Gahaz (לא גה"ז)
- **Derivation:** Shavuot = Pesach + 50 days; 50 mod 7 = 1
- **Example:** If Pesach ∈ {1,3,5,7}, then Shavuot ∈ {2,4,6,1} = {1,2,4,6}

#### Purim (14 Adar)
- **Allowed:** Sun, Tue, Thu, Fri (1, 3, 5, 6)
- **Forbidden:** Mon, Wed, Sat (2, 4, 7)
- **Rule:** Lo Zbd Purim (לא זב"ד פורים)
- **Source:** Megillah 5a

#### Hoshanah Rabah (21 Tishrei)
- **Allowed:** Sun, Mon, Wed, Fri (1, 2, 4, 6)
- **Forbidden:** Tue, Thu, Sat (3, 5, 7)
- **Rule:** Lo Gahaz (לא גה"ז)
- **Derivation:** HR = RH + 20 days; 20 mod 7 = 6
- **Source:** Sukkah 43b–44a

#### Fast Days (17 Tammuz, 9 Av)
- **Allowed:** Sun, Tue, Thu, Sat (1, 3, 5, 7)
- **Forbidden:** Mon, Wed, Fri (2, 4, 6)
- **Rule:** Lo Bdu (same as Pesach)
- **Source:** Taanit 26b, 29a

#### Chanukah (25 Kislev)
- **Allowed:** Sun, Mon, Wed, Thu, Fri, Sat (1, 2, 4, 5, 6, 7)
- **Forbidden:** Tue (3)
- **Rule:** Lo G Chanukah (לא ג' חנוכה)
- **Source:** Shulchan Aruch OH 428

### 3. RuleValidator — Verification Logic

Validates that all calendar calculations conform to rules.

**Methods:**

#### validateDateRestriction(restrictionId, weekday)
```kotlin
val result = RuleValidator.validateDateRestriction("PESACH", 2)  // Monday
// Returns: ValidationResult(isValid=false, violation="Pesach cannot fall on Monday...")
```

#### validateRoshHashanah(weekday)
```kotlin
if (weekday in listOf(1, 4, 6)) {
    // Violation: RH on Sun/Wed/Fri
}
```

#### validateYearLength(yearLength, isLeapYear)
```kotlin
// Common: 353–355
// Leap: 383–385
```

#### validate19YearCycle(startYear)
```kotlin
// Verify exactly 7 leap years at positions 3, 6, 8, 11, 14, 17, 19
```

#### validateCompleteYear(yearCalc)
```kotlin
val report = RuleValidator.validateCompleteYear(yearCalc)
// Returns: YearValidationReport with all violations
```

#### validateYearRange(from, to)
```kotlin
val (isValid, violations) = RuleValidator.validateYearRange(5700, 5800)
// Sweeps 100 years, reports any violations
```

#### validate14Keviyot()
```kotlin
val (isValid, keviyahMap) = RuleValidator.validate14Keviyot()
// Should find exactly 14 unique year signatures
```

## 14 Keviyot (Year Signatures)

A **keviyah** (קביעה) is a three-letter code representing a year's structure:

```
Letter 1: Day of Rosh Hashanah   (אבגדהוז = Sun–Sat)
Letter 2: Year type               (מ=leap, פ=common)
Letter 3: Day of Pesach           (אבגדהוז = Sun–Sat)
```

**Example:** בחג
- ב = Monday (2nd letter)
- ח = simple year (חסרה — deficient, 353 days)
- ג = Tuesday (3rd letter)

**All 14 Keviyot:**

Common years:
1. בחג — RH Monday, deficient 353 days, Pesach Tuesday
2. בשה — RH Monday, regular 354 days, Pesach Thursday
3. גכה — RH Tuesday, regular 354 days, Pesach Thursday
4. הכז — RH Thursday, regular 354 days, Pesach Saturday
5. השא — RH Thursday, complete 355 days, Pesach Monday
6. זחא — RH Saturday, deficient 353 days, Pesach Monday
7. זשג — RH Saturday, regular 354 days, Pesach Tuesday

Leap years:
1. בחה — RH Monday, deficient 383 days, Pesach Thursday
2. בשז — RH Monday, complete 385 days, Pesach Saturday
3. גכז — RH Tuesday, complete 385 days, Pesach Saturday
4. החא — RH Thursday, deficient 383 days, Pesach Monday
5. השג — RH Thursday, complete 385 days, Pesach Tuesday
6. זחג — RH Saturday, deficient 383 days, Pesach Tuesday
7. זשה — RH Saturday, regular 384 days, Pesach Thursday

**Verification:** In a 10,000-year sweep, exactly these 14 patterns occur, no more and no fewer.

## Special Relationships

### AT-BS (מנמונית — Mnemonic)

```
א"ת — Pesach (Aleph) ↔ Tisha B'Av (Tav)
      Same weekday

ב"ש — Pesach (Bet) ↔ Shavuot (Shin)
      Shavuot = Pesach + 1 weekday

ג"ר — [Additional relationships]

ד"ק — [Additional relationships]

ה"ץ — [Additional relationships]

ו"פ — [Additional relationships]
```

These are **verified** relationships, not arbitrary mnemonics. They derive from fixed calendar arithmetic.

### Plag (פל"ג)

```
Purim (14 Adar) ↔ Lag BaOmer (18 Iyar)
Distance: 63 days = 9 weeks exactly
→ Always the same weekday
```

**Mathematical:** 
- 14 Adar → 18 Iyar = (remaining Adar + Shevat + Tevet + Tammuz + Nisan)
- = (0–16 + 30 + 29 + 29 + 30 + 15) = 63 days (accounting for month lengths)
- = 9 × 7 days = same weekday guaranteed

## Test Coverage

### Unit Tests (commonTest/RuleValidatorTest.kt)

- ✅ **Lo Adu Rosh** — RH never Sun/Wed/Fri (1000 years)
- ✅ **Lo Agu Yom Kippur** — YK never Sun/Fri
- ✅ **Lo Bdu Pesach** — Pesach never Mon/Wed/Fri
- ✅ **Lo Gahaz Shavuot** — Shavuot never Tue/Thu/Sat
- ✅ **Lo Gahaz Hoshanah** — HR never Tue/Thu/Sat
- ✅ **Year Length Constraints** — 353–355 or 383–385 only
- ✅ **Leap Year Positions** — Exactly positions 3, 6, 8, 11, 14, 17, 19
- ✅ **19-Year Cycle** — Exactly 7 leap years per cycle
- ✅ **14 Keviyot Validation** — Exactly 14 unique year signatures
- ✅ **Plag Relationship** — Purim ↔ Lag BaOmer same weekday
- ✅ **Pesach-Shavuot Relationship** — Shavuot = Pesach + 1 weekday
- ✅ **Source Documentation** — All sources present and documented

### Validation Sweep

10,000+ consecutive years are validated:
- Each year checked against all restrictions
- Any violation is flagged and reported
- Success rate must be 100%

## Running Tests

```bash
./gradlew :shared:commonTest --tests RuleValidatorTest

# Or specific test:
./gradlew :shared:commonTest --tests RuleValidatorTest.testLoAduRosh_NeverSunWedFri

# Full validation sweep:
./gradlew :shared:commonTest --tests RuleValidatorTest.testValidateYearRange --info
```

## References

### Primary Sources (מקורות עיקריים)

1. **Rambam, Hilchot Kiddush HaChodesh VI–VII**
   - Foundational mathematical rules
   - Four dehiyyot (postponements)
   - Year structure

2. **Shulchan Aruch, Orach Chaim 428**
   - Summary table of 14 keviyot
   - Holiday restrictions
   - AT-BS mnemonics

3. **Talmud:**
   - Rosh Hashanah 20a — Dechiyot justification
   - Sukkah 43b–44a — Hoshanah Rabah and Shabbat
   - Megillah 5a — Purim and Shabbat
   - Pesachim 58a–58b — Pesach preparation
   - Taanit 26b, 29a — Fast days

### Verification Sources

- **Hebcal** — Cross-verification against JavaScript implementation
- **Rata Die / Dershowitz-Reingold** — Mathematical algorithm validation
- **Historical Records** — Known calendar dates

## Next Phase

**Phase 3: Explanation Trace**

Adds the final layer: explaining *why* each calculation produces its result.

```
Date Calculation
      ↓
Rule Validation (Phase 2)
      ↓
Explanation Trace (Phase 3)
      ↓
"Because molad was on Mon 5:204,
 Molad Zaken triggered (hour < 18? No, >= 18),
 postponing to Tuesday.
 Then Lo Adu applied (Tue allowed ✓).
 Therefore, RH = Tuesday."
```

The Explanation Trace will:
1. Log every calculation step
2. Cite the source (Rambam, Talmud, etc.)
3. Explain the logic
4. Provide the reasoning chain

This makes the calendar engine **transparent and auditable**.
