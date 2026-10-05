package wspdyb.calculator.zmanim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import wspdyb.calculator.zmanim.R
import wspdyb.calculator.zmanim.viewmodel.ValidatorViewModel

val DAYS_OF_WEEK = listOf(
    Pair(1, "Sun"),
    Pair(2, "Mon"),
    Pair(3, "Tue"),
    Pair(4, "Wed"),
    Pair(5, "Thu"),
    Pair(6, "Fri"),
    Pair(7, "Sat")
)

@Composable
fun ValidatorScreen(navController: NavHostController) {
    val viewModel: ValidatorViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Text(
                text = stringResource(R.string.val_title),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        // Holiday Selection
        item {
            Text(
                text = stringResource(R.string.val_select_holiday),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(uiState.holidays) { holiday ->
            HolidayCard(
                holiday = holiday,
                isSelected = uiState.selectedHoliday?.id == holiday.id,
                onClick = { viewModel.selectHoliday(holiday) }
            )
        }

        // Day Selection
        if (uiState.selectedHoliday != null) {
            item {
                Text(
                    text = stringResource(R.string.val_can_be_on),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            item {
                DayGrid(
                    selectedDay = uiState.selectedDay,
                    onDaySelected = { viewModel.setSelectedDay(it) }
                )
            }

            // Validation Result
            if (uiState.result != null) {
                item {
                    ValidationResultCard(result = uiState.result!!)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HolidayCard(
    holiday: wspdyb.calculator.zmanim.viewmodel.Holiday,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
            )
            .border(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = holiday.name,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = holiday.hebrewName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Text(
                text = holiday.allowedDays.size.toString(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun DayGrid(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Select Day of Week",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Two rows of days
        repeat(2) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { col ->
                    val dayIndex = row * 4 + col
                    if (dayIndex < DAYS_OF_WEEK.size) {
                        val (dayNum, dayName) = DAYS_OF_WEEK[dayIndex]
                        DayButton(
                            dayName = dayName,
                            isSelected = dayNum == selectedDay,
                            onClick = { onDaySelected(dayNum) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayButton(
    dayName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Text(
            text = dayName,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ValidationResultCard(result: wspdyb.calculator.zmanim.viewmodel.ValidationResult) {
    val backgroundColor = if (result.canOccurOn) {
        MaterialTheme.colorScheme.surface
    } else {
        Color(0xFFFFE0E0)  // Light red tint
    }

    val borderColor = if (result.canOccurOn) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.error
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(1.dp, borderColor)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Result Header
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = result.holiday.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (result.canOccurOn) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Allowed",
                        tint = Color(0xFF2E7D32)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Not Allowed",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Result Text
            Text(
                text = result.reason,
                style = MaterialTheme.typography.bodyMedium
            )

            // Rule Box
            if (result.rule.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "Rule:",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = result.rule,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
