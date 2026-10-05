# 📱 Android Integration Guide

**Complete guide to integrating the Hebrew Calendar Engine into your Android app**

---

## **Setup: Step by Step**

### **Step 1: Add Library to Your Project**

In your Android project's `settings.gradle.kts`:

```kotlin
include(":app")
include(":hebrew-calendar-kmm:shared")
```

In `app/build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":hebrew-calendar-kmm:shared"))
}
```

### **Step 2: Install Dependencies**

```gradle
// Core Android
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("androidx.core:core:1.12.0")

// Jetpack Compose
implementation("androidx.compose.ui:ui:1.7.0")
implementation("androidx.compose.material3:material3:1.2.0")
implementation("androidx.compose.runtime:runtime:1.7.0")
implementation("androidx.activity:activity-compose:1.8.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.0")

// ViewModel & Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.0.0")

// Testing
testImplementation("junit:junit:4.13.2")
androidTestImplementation("androidx.test.ext:junit:1.1.5")
```

### **Step 3: Create Theme**

**File:** `ui/theme/Theme.kt`

```kotlin
package wspdyb.calculator.zmanim.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Colors
val PrimaryDark = Color(0xFF1A3A52)      // Deep blue
val SecondaryDark = Color(0xFFC65D3B)    // Rust/bronze
val AccentGold = Color(0xFFD4AF37)       // Gold
val SurfaceLight = Color(0xFFF5F5F5)     // Light gray
val TextDark = Color(0xFF333333)         // Dark text
val TextLight = Color(0xFFFFFFFF)        // White text

val lightColorScheme = lightColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = AccentGold,
    surface = SurfaceLight,
    onSurface = TextDark,
    background = Color.White,
    onBackground = TextDark
)

val darkColorScheme = darkColorScheme(
    primary = Color(0xFF6BA3D4),         // Lighter blue for dark
    secondary = Color(0xFFE8A87C),       // Lighter rust for dark
    tertiary = AccentGold,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    background = Color(0xFF121212),
    onBackground = Color.White
)

// Typography
object ZmanimTypography {
    val DisplayLarge = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 36.sp
    )
    
    val HeadlineSmall = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 28.sp
    )
    
    val BodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp
    )
    
    val BodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    )
    
    val LabelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 16.sp
    )
}

@Composable
fun ZmanimTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
```

### **Step 4: Create Repository**

**File:** `repository/CalendarRepository.kt`

```kotlin
package wspdyb.calculator.zmanim.repository

import wspdyb.calculator.zmanim.TraceableHebrewCalendarEngine
import wspdyb.calculator.zmanim.ExplanationEngine
import wspdyb.calculator.zmanim.RuleValidator

class CalendarRepository {
    
    // Calculate year with tracing
    suspend fun calculateYear(year: Int) = 
        TraceableHebrewCalendarEngine.calculateYearWithTrace(year)
    
    // Get explanation
    suspend fun getExplanation(year: Int, language: String = "en") =
        TraceableHebrewCalendarEngine.explainYear(year, language)
    
    // Get summary
    suspend fun getSummary(year: Int, language: String = "en") =
        TraceableHebrewCalendarEngine.getSummary(year, language)
    
    // Validate rules
    suspend fun validateYear(year: Int): Boolean {
        val result = calculateYear(year).first
        val report = RuleValidator.validateCompleteYear(result)
        return report.allRestrictionsValid
    }
    
    // Get HTML explanation
    suspend fun getHTMLExplanation(year: Int) =
        TraceableHebrewCalendarEngine.getHTMLExplanation(year)
}
```

### **Step 5: Create ViewModel**

**File:** `viewmodel/CalculatorViewModel.kt`

```kotlin
package wspdyb.calculator.zmanim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wspdyb.calculator.zmanim.HebrewCalendarEngine
import wspdyb.calculator.zmanim.Explanation
import wspdyb.calculator.zmanim.repository.CalendarRepository

class CalculatorViewModel : ViewModel() {
    
    private val repository = CalendarRepository()
    
    private val _uiState = MutableStateFlow<CalculatorUiState>(
        CalculatorUiState.Initial
    )
    val uiState: StateFlow<CalculatorUiState> = _uiState
    
    fun calculateYear(year: Int) {
        viewModelScope.launch {
            _uiState.value = CalculatorUiState.Loading
            try {
                val (result, trace) = repository.calculateYear(year)
                val explanation = repository.getExplanation(year, "en")
                
                _uiState.value = CalculatorUiState.Success(
                    year = year,
                    result = result,
                    explanation = explanation
                )
            } catch (e: Exception) {
                _uiState.value = CalculatorUiState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }
    
    fun calculateYearHebrew(year: Int) {
        viewModelScope.launch {
            _uiState.value = CalculatorUiState.Loading
            try {
                val (result, trace) = repository.calculateYear(year)
                val explanation = repository.getExplanation(year, "he")
                
                _uiState.value = CalculatorUiState.Success(
                    year = year,
                    result = result,
                    explanation = explanation
                )
            } catch (e: Exception) {
                _uiState.value = CalculatorUiState.Error(
                    e.message ?: "Unknown error"
                )
            }
        }
    }
}

sealed class CalculatorUiState {
    object Initial : CalculatorUiState()
    object Loading : CalculatorUiState()
    data class Success(
        val year: Int,
        val result: HebrewCalendarEngine.YearCalculation,
        val explanation: Explanation
    ) : CalculatorUiState()
    data class Error(val message: String) : CalculatorUiState()
}
```

### **Step 6: Create UI Screens**

**File:** `ui/screens/CalculatorScreen.kt`

```kotlin
package wspdyb.calculator.zmanim.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import wspdyb.calculator.zmanim.viewmodel.CalculatorViewModel
import wspdyb.calculator.zmanim.viewmodel.CalculatorUiState

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedYear by remember { mutableStateOf("5787") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "מחשבון זמנים עבריים",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Year Input
        ZmanimCard(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("בחר שנה עברית:", fontWeight = FontWeight.SemiBold)
                
                TextField(
                    value = selectedYear,
                    onValueChange = { selectedYear = it },
                    label = { Text("שנה") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
                
                Button(
                    onClick = { 
                        viewModel.calculateYearHebrew(selectedYear.toIntOrNull() ?: 5787)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("חשב")
                }
            }
        }
        
        // Results
        when (uiState) {
            is CalculatorUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.CenterHorizontally)
                        .padding(32.dp)
                )
            }
            
            is CalculatorUiState.Success -> {
                val state = uiState as CalculatorUiState.Success
                ResultCard(state)
            }
            
            is CalculatorUiState.Error -> {
                ZmanimCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Red)
                ) {
                    Text(
                        "שגיאה: ${(uiState as CalculatorUiState.Error).message}",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Red
                    )
                }
            }
            
            else -> {}
        }
    }
}

@Composable
fun ZmanimCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray),
        color = Color(0xFFF5F5F5)
    ) {
        content()
    }
}

@Composable
fun ResultCard(state: CalculatorUiState.Success) {
    val result = state.result
    val explanation = state.explanation
    
    ZmanimCard(modifier = Modifier.padding(bottom = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Text(
                "תוצאות",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Summary
            Text(
                explanation.summary,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Year details
            Text("פרטי השנה:", fontWeight = FontWeight.SemiBold)
            DetailRow("קביעה:", result.keviyahSymbol)
            DetailRow("סוג:", if (result.isLeapYear) "מעוברת" else "פשוטה")
            DetailRow("אורך:", "${result.yearLength} ימים")
            DetailRow("חשוון:", "${result.cheshvanLength} ימים")
            DetailRow("כסלו:", "${result.kislevLength} ימים")
            
            // Expandable explanation
            Spacer(modifier = Modifier.height(16.dp))
            Text("הסבר מלא:", fontWeight = FontWeight.SemiBold)
            Text(
                explanation.fullExplanation.take(300) + "...",
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            // Sources
            if (explanation.sources.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("מקורות:", fontWeight = FontWeight.SemiBold)
                explanation.sources.forEach { source ->
                    Text(
                        "• ${source.titleHebrew}",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value)
    }
}
```

### **Step 7: Main Activity**

**File:** `MainActivity.kt`

```kotlin
package wspdyb.calculator.zmanim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import wspdyb.calculator.zmanim.ui.screens.CalculatorScreen
import wspdyb.calculator.zmanim.ui.theme.ZmanimTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            ZmanimTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        CalculatorScreen()
                    }
                }
            }
        }
    }
}
```

---

## **Advanced: Multi-Tab Navigation**

For the full app with 6 tabs:

**File:** `ui/navigation/Navigation.kt`

```kotlin
package wspdyb.calculator.zmanim.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import wspdyb.calculator.zmanim.ui.screens.*

enum class ZmanimRoute(val title: String, val icon: String) {
    HOME("🏠", "Home"),
    CALCULATOR("🧮", "Calculator"),
    VALIDATOR("🔎", "Validator"),
    HOLIDAYS("📅", "Holidays"),
    LEARN("📖", "Learn")
}

@Composable
fun ZmanimNavigation() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(ZmanimRoute.HOME) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                ZmanimRoute.values().forEach { route ->
                    NavigationBarItem(
                        icon = { Text(route.icon) },
                        label = { Text(route.title) },
                        selected = selectedTab == route,
                        onClick = {
                            selectedTab = route
                            navController.navigate(route.name)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ZmanimRoute.HOME.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(ZmanimRoute.HOME.name) { HomeScreen() }
            composable(ZmanimRoute.CALCULATOR.name) { CalculatorScreen() }
            composable(ZmanimRoute.VALIDATOR.name) { ValidatorScreen() }
            composable(ZmanimRoute.HOLIDAYS.name) { HolidayScreen() }
            composable(ZmanimRoute.LEARN.name) { LearnScreen() }
        }
    }
}
```

---

## **Testing in Android**

**File:** `CalculatorViewModelTest.kt`

```kotlin
package wspdyb.calculator.zmanim.viewmodel

import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import wspdyb.calculator.zmanim.repository.CalendarRepository

class CalculatorViewModelTest : TestCase() {
    
    private val repository = CalendarRepository()
    
    fun testCalculateYear() = runBlocking {
        val (result, trace) = repository.calculateYear(5787)
        assertNotNull(result)
        assertNotNull(trace)
        assertTrue(result.yearLength in 353..355 || result.yearLength in 383..385)
    }
    
    fun testExplanationGeneration() = runBlocking {
        val explanation = repository.getExplanation(5787, "en")
        assertNotNull(explanation)
        assertFalse(explanation.summary.isEmpty())
        assertFalse(explanation.sources.isEmpty())
    }
    
    fun testValidation() = runBlocking {
        val isValid = repository.validateYear(5787)
        assertTrue(isValid)
    }
}
```

---

## **Performance Tips**

### **1. Caching**

```kotlin
class CachedCalendarRepository : CalendarRepository() {
    private val cache = mutableMapOf<Int, YearCalculation>()
    
    override suspend fun calculateYear(year: Int) =
        cache.getOrPut(year) {
            super.calculateYear(year)
        }
}
```

### **2. Coroutines**

Use `viewModelScope.launch` for background calculations:

```kotlin
fun calculateYear(year: Int) {
    viewModelScope.launch {
        _uiState.value = CalculatorUiState.Loading
        try {
            val result = repository.calculateYear(year)
            _uiState.value = CalculatorUiState.Success(result)
        } catch (e: Exception) {
            _uiState.value = CalculatorUiState.Error(e.message)
        }
    }
}
```

### **3. Lazy Loading**

For large lists (e.g., 10,000 years):

```kotlin
val lazyYears = LazyColumn {
    items(10000) { year ->
        YearRow(year)
    }
}
```

---

## **Dark Mode Support**

Already included in Theme.kt. Test with:

```bash
adb shell "cmd uimode night yes"
```

---

## **Accessibility**

Add semantics for screen readers:

```kotlin
Text(
    "Pesach 5787",
    modifier = Modifier.semantics { 
        contentDescription = "Pesach in year 5787 is Saturday" 
    }
)
```

---

## **Deployment Checklist**

- ✅ Test all screens
- ✅ Test landscape mode
- ✅ Test dark mode
- ✅ Test accessibility
- ✅ Verify calculations (10 years)
- ✅ Check memory usage
- ✅ Test on Android 7.0+
- ✅ Create app icon
- ✅ Write privacy policy
- ✅ Prepare Play Store listing

---

## **Next Steps**

1. ✅ Clone library
2. ✅ Add to Android project
3. ✅ Create UI screens
4. ✅ Connect to ViewModels
5. ✅ Test all features
6. ✅ Deploy to Play Store

---

**Status: READY TO BUILD** 🚀

All components ready to integrate. Build your professional Hebrew calendar app!
