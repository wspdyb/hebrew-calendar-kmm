package wspdyb.calculator.zmanim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import wspdyb.calculator.zmanim.R
import wspdyb.calculator.zmanim.ui.navigation.Route
import wspdyb.calculator.zmanim.ui.navigation.navigateTo
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = listOf("3761", "5784", "Hebrew Calendar")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Today in Hebrew Calendar
        item {
            TodayCard()
        }

        // Quick Access Cards
        item {
            Text(
                text = "Quick Access",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAccessButton(
                    label = "Calculator",
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigateTo(Route.Calculator) }
                )
                QuickAccessButton(
                    label = "Validator",
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigateTo(Route.Validator) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAccessButton(
                    label = "Holidays",
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigateTo(Route.HolidayExplorer) }
                )
                QuickAccessButton(
                    label = "Learn",
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigateTo(Route.Learn) }
                )
            }
        }

        // Recent Searches
        item {
            Text(
                text = stringResource(R.string.home_recent),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(recentSearches) { search ->
            RecentSearchItem(
                text = search,
                onClick = {
                    // Handle recent search
                }
            )
        }

        // Space at bottom
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TodayCard() {
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.home_quick_today),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = today.format(formatter),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hebrew Date: [Calculated]",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun QuickAccessButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RecentSearchItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = "Open",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(20.dp)
        )
    }
}
