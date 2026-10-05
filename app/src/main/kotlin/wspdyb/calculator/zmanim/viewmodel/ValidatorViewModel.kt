package wspdyb.calculator.zmanim.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Holiday data
data class Holiday(
    val id: String,
    val name: String,
    val hebrewName: String,
    val allowedDays: List<Int>  // 1=Sunday, 7=Saturday
)

data class ValidationResult(
    val holiday: Holiday,
    val selectedDay: Int,
    val canOccurOn: Boolean,
    val reason: String,
    val rule: String  // e.g. "לא אד״ו"
)

data class ValidatorUiState(
    val holidays: List<Holiday> = emptyList(),
    val selectedHoliday: Holiday? = null,
    val selectedDay: Int = 2,  // Default: Monday
    val result: ValidationResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ValidatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ValidatorUiState())
    val uiState: StateFlow<ValidatorUiState> = _uiState

    init {
        loadHolidays()
    }

    private fun loadHolidays() {
        // TODO: Load from RuleValidator
        val holidays = listOf(
            Holiday("rh", "Rosh Hashanah", "ראש השנה", listOf(2, 3, 5, 7)),
            Holiday("yk", "Yom Kippur", "יום כיפור", listOf(2, 3, 4, 5, 7)),
            Holiday("pesach", "Pesach", "פסח", listOf(1, 3, 5, 7)),
            Holiday("shavuot", "Shavuot", "שבועות", listOf(1, 2, 4, 6)),
            Holiday("sukkot", "Sukkot", "סוכות", listOf(2, 3, 5, 7)),
            Holiday("chanukah", "Chanukah", "חנוכה", listOf(1, 2, 4, 5, 6, 7)),
            Holiday("tu_bishvat", "Tu B'Shvat", "ט״ו בשבט", listOf(1, 2, 3, 4, 5, 6, 7)),
            Holiday("purim", "Purim", "פורים", listOf(1, 3, 5, 6, 7)),
            Holiday("17_tammuz", "17 Tammuz", "י״ז בתמוז", listOf(1, 3, 5, 7)),
            Holiday("tisha_bav", "Tisha B'Av", "ט״ישה באב", listOf(1, 3, 5, 7))
        )
        _uiState.value = _uiState.value.copy(holidays = holidays)
    }

    fun selectHoliday(holiday: Holiday) {
        _uiState.value = _uiState.value.copy(selectedHoliday = holiday)
        validateHoliday(holiday, _uiState.value.selectedDay)
    }

    fun setSelectedDay(day: Int) {
        _uiState.value = _uiState.value.copy(selectedDay = day)
        _uiState.value.selectedHoliday?.let {
            validateHoliday(it, day)
        }
    }

    private fun validateHoliday(holiday: Holiday, day: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // TODO: Call RuleValidator
                val canOccur = holiday.allowedDays.contains(day)
                val dayName = getDayName(day)
                val rule = getRuleForHoliday(holiday.id)
                
                val result = ValidationResult(
                    holiday = holiday,
                    selectedDay = day,
                    canOccurOn = canOccur,
                    reason = if (canOccur) {
                        "$holiday.name can occur on $dayName"
                    } else {
                        "$holiday.name cannot occur on $dayName due to rule: $rule"
                    },
                    rule = rule
                )
                
                _uiState.value = _uiState.value.copy(
                    result = result,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Validation error"
                )
            }
        }
    }

    private fun getDayName(day: Int): String {
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

    private fun getRuleForHoliday(holidayId: String): String {
        return when (holidayId) {
            "rh" -> "לא אד״ו"
            "yk" -> "לא אג״ו"
            "pesach" -> "לא בד״ו"
            "shavuot" -> "לא גה״ז"
            "sukkot" -> "inherited from RH"
            "chanukah" -> "not Wednesday"
            "17_tammuz" -> "derived from Pesach"
            "tisha_bav" -> "derived from Pesach"
            "purim" -> "לא זב״ד"
            else -> "custom rule"
        }
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(
            result = null,
            error = null
        )
    }
}
