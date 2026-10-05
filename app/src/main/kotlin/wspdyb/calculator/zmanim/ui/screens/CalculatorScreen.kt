package wspdyb.calculator.zmanim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import wspdyb.calculator.zmanim.R
import wspdyb.calculator.zmanim.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(navController: NavHostController) {
    val viewModel: CalculatorViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Selection
        item {
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    text = { Text("H→G", style = MaterialTheme.typography.labelMedium) },
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) }
                )
                Tab(
                    text = { Text("G→H", style = MaterialTheme.typography.labelMedium) },
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) }
                )
                Tab(
                    text = { Text("Molad", style = MaterialTheme.typography.labelMedium) },
                    selected = uiState.selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) }
                )
                Tab(
                    text = { Text("Year", style = MaterialTheme.typography.labelMedium) },
                    selected = uiState.selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) }
                )
            }
        }

        // Tab Content
        when (uiState.selectedTab) {
            0 -> item { HebrewToGregorianTab(viewModel) }
            1 -> item { GregorianToHebrewTab(viewModel) }
            2 -> item { MoladTab(viewModel) }
            3 -> item { YearInfoTab(viewModel) }
        }

        // Result Display
        if (uiState.result.hebrewDate.isNotEmpty() || 
            uiState.result.gregorianDate.isNotEmpty()) {
            item {
                ResultCard(result = uiState.result)
            }
        }

        // Error Display
        if (uiState.error != null) {
            item {
                ErrorCard(error = uiState.error!!)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HebrewToGregorianTab(viewModel: CalculatorViewModel) {
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.calc_hebrew_to_gregorian),
            style = MaterialTheme.typography.titleMedium
        )

        InputRow(
            label = "Year",
            value = year,
            onValueChange = { year = it },
            keyboardType = KeyboardType.Number
        )

        InputRow(
            label = "Month",
            value = month,
            onValueChange = { month = it },
            keyboardType = KeyboardType.Number
        )

        InputRow(
            label = "Day",
            value = day,
            onValueChange = { day = it },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (year.isNotEmpty() && month.isNotEmpty() && day.isNotEmpty()) {
                    viewModel.calculateHebrewToGregorian(
                        year.toIntOrNull() ?: 0,
                        month.toIntOrNull() ?: 0,
                        day.toIntOrNull() ?: 0
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.calculate))
        }
    }
}

@Composable
private fun GregorianToHebrewTab(viewModel: CalculatorViewModel) {
    var year by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.calc_gregorian_to_hebrew),
            style = MaterialTheme.typography.titleMedium
        )

        InputRow(
            label = "Year",
            value = year,
            onValueChange = { year = it },
            keyboardType = KeyboardType.Number
        )

        InputRow(
            label = "Month",
            value = month,
            onValueChange = { month = it },
            keyboardType = KeyboardType.Number
        )

        InputRow(
            label = "Day",
            value = day,
            onValueChange = { day = it },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                // TODO: Call with Gregorian date
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.calculate))
        }
    }
}

@Composable
private fun MoladTab(viewModel: CalculatorViewModel) {
    var year by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.calc_molad),
            style = MaterialTheme.typography.titleMedium
        )

        InputRow(
            label = "Hebrew Year",
            value = year,
            onValueChange = { year = it },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (year.isNotEmpty()) {
                    viewModel.calculateMolad(year.toIntOrNull() ?: 0)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.calculate))
        }
    }
}

@Composable
private fun YearInfoTab(viewModel: CalculatorViewModel) {
    var year by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.calc_year_info),
            style = MaterialTheme.typography.titleMedium
        )

        InputRow(
            label = "Hebrew Year",
            value = year,
            onValueChange = { year = it },
            keyboardType = KeyboardType.Number
        )

        Button(
            onClick = {
                if (year.isNotEmpty()) {
                    viewModel.getYearInfo(year.toIntOrNull() ?: 0)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.calculate))
        }
    }
}

@Composable
private fun InputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.CenterVertically)
                .width(60.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .border(1.dp, MaterialTheme.colorScheme.outline),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true
        )
    }
}

@Composable
private fun ResultCard(result: wspdyb.calculator.zmanim.viewmodel.CalculationResult) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (result.hebrewDate.isNotEmpty()) {
                Text("Hebrew: ${result.hebrewDate}", style = MaterialTheme.typography.bodyMedium)
            }
            if (result.gregorianDate.isNotEmpty()) {
                Text("Gregorian: ${result.gregorianDate}", style = MaterialTheme.typography.bodyMedium)
            }
            if (result.explanation.isNotEmpty()) {
                Divider()
                Text(result.explanation, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun ErrorCard(error: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .border(1.dp, MaterialTheme.colorScheme.error)
            .padding(16.dp)
    ) {
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
    }
}
