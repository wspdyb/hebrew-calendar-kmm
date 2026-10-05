# Hebrew Calendar Engine — Technical Architecture

## Overview

```
┌─────────────────────────────────────────────────────────────┐
│                   Public API Layer                           │
│  HebrewCalendarEngine.calculateYear(year: Int)              │
└─────────────────────────────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ↓                    ↓                    ↓
┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│ MoladCalculator  │ │ DehiyyahCalc.    │ │ Year Structure   │
│ (Molad Tishrei)  │ │ (Postponements)  │ │ (Cheshvan/Kislev)│
└──────────────────┘ └──────────────────┘ └──────────────────┘
        │                    │                    │
        └────────────────────┼────────────────────┘
                             │
                    ┌────────▼────────┐
                    │  HebrewTime     │
                    │  (day, h, p)    │
                    └─────────────────┘
```

## Three Calculation Engines

### Engine 1: Molad Calculation

**Source:** Rambam, Hilchot Kiddush HaChodesh VI

**Input:** Hebrew year

**Output:** Molad Tishrei (day of week + hour + chalakim)

**Algorithm:**

```
months_elapsed = calculateMonthsElapsed(year)
chalakim_per_month = 29d + 12h + 793p
                   = 29 * 25920 + 12 * 1080 + 793 chalakim
                   = 765,432 chalakim

total_chalakim = months_elapsed * chalakim_per_month
molad = MOLAD_BRESHIT + total_chalakim
```

**Months Elapsed:**

```
complete_cycles = (year - 1) / 19
year_in_cycle = (year - 1) % 19 + 1

months_in_cycles = complete_cycles * 235  // 235 = 12*19 + 7

for y in 1..year_in_cycle-1:
    if isLeap(y):
        months_in_cycles += 13
    else:
        months_in_cycles += 12

return months_in_cycles
```

**Leap Year Positions:** In the 19-year cycle, leap years occur at positions 3, 6, 8, 11, 14, 17, 19.

### Engine 2: Dehiyyah (Postponements)

**Source:** Rambam, Hilchot Kiddush HaChodesh VII, 1–4

**Input:** Molad Tishrei, Hebrew year

**Output:** Final Rosh Hashanah weekday + set of triggered rules

**The Four Dehiyyot:**

#### 1. Molad Zaken (Aged Molad)
- **Condition:** If molad hour >= 18
- **Action:** Postpone RH to next day
- **Reference:** Rambam VII, 1

#### 2. Lo Adu (Not Sun/Wed/Fri)
- **Condition:** If RH day is 1 (Sunday), 4 (Wednesday), or 6 (Friday)
- **Action:** Postpone RH to next day
- **Reference:** Rambam VII, 2

#### 3. GaTRaD (Gat - Tuesday + Rad - 204 chalakim)
- **Condition:** 
  - Current year is simple (not leap)
  - AND Molad falls on Tuesday
  - AND hour > 9 OR (hour == 9 AND chalakim >= 204)
- **Action:** Postpone RH to Thursday (specific, bypasses other rules)
- **Reference:** Rambam VII, 3

#### 4. BeTU TaKPaT (Tuesday 589 chalakim after leap year)
- **Condition:**
  - Current year follows a leap year
  - AND Molad falls on Monday
  - AND hour > 15 OR (hour == 15 AND chalakim >= 589)
- **Action:** Postpone RH to Tuesday
- **Reference:** Rambam VII, 4

**Execution Order:**

```
1. Calculate molad
2. Apply Molad Zaken → if triggered, move to next day
3. Apply Lo Adu → if still on forbidden day, move to next day
4. Apply GaTRaD → if triggered, move to Thursday (exit early)
5. Apply BeTU TaKPaT → if triggered, move to Tuesday (exit early)
6. Return final day
```

**Important:** GaTRaD and BeTU TaKPaT exit the chain early (they don't combine with other rules).

### Engine 3: Year Structure

**Input:** Rosh Hashanah weekday this year, Rosh Hashanah weekday next year, leap flag

**Output:** Year length, Cheshvan length, Kislev length

**Year Length Calculation:**

Year length is determined by the number of days between this Rosh Hashanah and next Rosh Hashanah.

For a **common year** (not leap):
- Minimum: 353 days (deficient — חסרה)
- Default: 354 days (regular — כסדרה)
- Maximum: 355 days (complete — שלמה)

For a **leap year**:
- Minimum: 383 days
- Default: 384 days
- Maximum: 385 days

**Month Lengths:**

The year length determines Cheshvan and Kislev:

| Year Length | Cheshvan | Kislev | Type |
|-------------|----------|--------|------|
| 353 / 383   | 29       | 29     | חסרה (deficient) |
| 354 / 384   | 29       | 30     | כסדרה (regular) |
| 355 / 385   | 30       | 30     | שלמה (complete) |

**All Other Months (Fixed):**

| Month    | Days |
|----------|------|
| Tishrei  | 30   |
| Tevet    | 29   |
| Shevat   | 30   |
| Nisan    | 30   |
| Iyar     | 29   |
| Sivan    | 30   |
| Tammuz   | 29   |
| Av       | 30   |
| Elul     | 29   |
| Adar (simple) | 29 |
| Adar I (leap) | 30 |
| Adar II (leap) | 29 |

## HebrewTime Data Model

**Representation:**
```kotlin
data class HebrewTime(
    val dayOfWeek: Int,    // 1–7 (1=Sunday in Talmudic convention)
    val hour: Int,         // 0–23
    val chalakim: Int      // 0–1079 (1080 per hour)
)
```

**Unit Conversions:**
- 1 hour = 1,080 chalakim
- 1 day = 24 hours = 25,920 chalakim

**Time Arithmetic:**

Adding chalakim requires careful handling of overflow:

```
new_chalakim = old_chalakim + delta
if new_chalakim >= 1080:
    new_hours += floor(new_chalakim / 1080)
    new_chalakim %= 1080

if new_hours >= 24:
    new_days += floor(new_hours / 24)
    new_hours %= 24

// Weekday wraps 1–7
new_days = ((new_days - 1) % 7) + 1
```

## Testing Strategy

### Unit Tests (commonTest)

- **MoladCalculator:** Verify molad calculation for known years
- **DehiyyahCalculator:** Test each of the four postponement rules
- **HebrewTime:** Test time arithmetic and overflow
- **Engine Integration:** Calculate full years and verify constraints

### Validation Sweep (10,000+ years)

```kotlin
for (year in 1..10000) {
    val calc = HebrewCalendarEngine.calculateYear(year)
    
    // Rosh Hashanah constraints
    assert(calc.roshHashanahDay !in listOf(1, 4, 6))  // Lo Adu
    
    // Year length constraints
    if (calc.isLeapYear)
        assert(calc.yearLength in 383..385)
    else
        assert(calc.yearLength in 353..355)
    
    // Leap year cycle
    val pos = (year - 1) % 19 + 1
    assert(calc.isLeapYear == (pos in listOf(3, 6, 8, 11, 14, 17, 19)))
}
```

### Cross-Verification

(To be implemented)

Compare results against:
1. Hebcal (JavaScript)
2. Rata Die / Dershowitz-Reingold algorithm
3. Known historical references

## Constants and Formulas

### Molad Constants (per Rambam)

```kotlin
MOLAD_BRESHIT = HebrewTime(2, 5, 204)  // Monday, 5h, 204p

// Average month
MONTH_DAYS = 29
MONTH_HOURS = 12
MONTH_CHALAKIM = 793

// Average year
YEAR_COMMON_DAYS = 354
YEAR_COMMON_HOURS = 8
YEAR_COMMON_CHALAKIM = 876

YEAR_LEAP_DAYS = 383
YEAR_LEAP_HOURS = 21
YEAR_LEAP_CHALAKIM = 589  // Actually -589 for subtraction, so +20591
```

### 19-Year Cycle

```kotlin
LEAP_POSITIONS = setOf(3, 6, 8, 11, 14, 17, 19)
MONTHS_PER_CYCLE = 235  // = 7 * 13 + 12 * 12

// 235 months = 6939 days + 16 hours + 595 chalakim
CYCLE_DAYS = 6939
CYCLE_HOURS = 16
CYCLE_CHALAKIM = 595
```

## Implementation Notes

### Platform Compatibility

- **Android:** Direct Kotlin compilation
- **iOS:** Compiled via Kotlin Native
- **Desktop (JVM):** Compiled as JAR
- **Web (future):** Compile via KotlinJS

### No External Dependencies

The calendar engine has **zero external dependencies** (except Kotlin stdlib). This ensures:
- Fast compilation
- Small footprint
- High reliability
- Easy distribution

### Precision

All calculations use `Long` and `Int` to avoid floating-point precision issues.

### Timezone-Independent

The molad and calendar calculations are timezone-independent. Real-world observance (sunrise-based zmanim) is handled separately.

## Next Phases

### Phase 2: Rule Validator
- Verify all 14 keviyot exist and only those 14
- Verify holiday weekday restrictions (לא בד"ו פסח, etc.)
- Verify AT-BS relationships
- Verify Plag (Purim ≡ Lag BaOmer mod 7)

### Phase 3: Explanation Trace
- Log every calculation step
- Attach source (Talmud, Rambam, derived)
- Generate "why?" explanations for users

### Phase 4: UI Layers
- Android: Jetpack Compose
- iOS: SwiftUI
- Desktop: Compose Multiplatform
