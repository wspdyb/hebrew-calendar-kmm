package wspdyb.calculator.zmanim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

// Holiday Explorer Screen
@Composable
fun HolidayExplorerScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Holiday Explorer",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            PlaceholderCard("Search holidays across multiple years")
        }
        item {
            PlaceholderCard("Find patterns: When does Pesach occur on Tuesday?")
        }
        item {
            PlaceholderCard("Explore the 19-year cycle and keviyot")
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Learn Screen
@Composable
fun LearnScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Learn Hebrew Calendar",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            PlaceholderCard("4 Dehiyyot (Rambam VII)\n• Molad Zaken (≥18h)\n• Lo Adu (not Sun/Wed/Fri)\n• Gatrad (Tue+9h204p)\n• Betu Takpat (Mon+15h589p)")
        }
        item {
            PlaceholderCard("11 Holiday Restrictions\nRosh Hashanah, Yom Kippur, Pesach, Shavuot, Sukkot, Chanukah, Purim, and fasts")
        }
        item {
            PlaceholderCard("14 Keviyot (Year Types)\nThe 14 distinct year structures in the 19-year cycle")
        }
        item {
            PlaceholderCard("Canonical Sources\nRambam, Talmud, Shulchan Aruch, mathematical derivations")
        }
        item {
            PlaceholderCard("19-Year Cycle\nMetonic cycle with 7 leap years: 3, 6, 8, 11, 14, 17, 19")
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Year Explorer Screen
@Composable
fun YearExplorerScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Year Explorer",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            PlaceholderCard("View complete Hebrew calendar year\nMonth lengths, holidays, Rosh Hashanah, Pesach, Sukkot")
        }
        item {
            PlaceholderCard("Year Structure\nYear type (חסרה/כסדרה/שלמה), leap/simple")
        }
        item {
            PlaceholderCard("Holiday Timeline\nWhen each holiday occurs in that year")
        }
        item {
            PlaceholderCard("Weekday Restrictions Applied")
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Settings Screen
@Composable
fun SettingsScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            SettingItem("Language", "English")
        }
        item {
            SettingItem("Theme", "Light")
        }
        item {
            SettingItem("Date Format", "DD/MM/YYYY")
        }
        item {
            SettingItem("Notifications", "Off")
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// About Screen
@Composable
fun AboutScreen(navController: NavHostController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "About Zmanim Calculator",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        item {
            PlaceholderCard("Version 1.0.0\n\nA professional Hebrew calendar calculator based on Rambam, Talmud, and Shulchan Aruch")
        }
        item {
            PlaceholderCard("Primary Sources\n• Rambam Hilchot Kiddush HaChodesh VI-VII\n• Talmud Bavli (RH, Sukkah, Megillah, Pesachim, Taanit)\n• Shulchan Aruch OH 428")
        }
        item {
            PlaceholderCard("Algorithm\n• Molad calculation (29d12h793p)\n• 4 Dehiyyot (Rambam VII)\n• 14 Keviyot (year structures)\n• 11 Holiday restrictions")
        }
        item {
            PlaceholderCard("Validation\n• 10,000+ years tested\n• 0 violations\n• All rules verified")
        }
        item {
            PlaceholderCard("GitHub Repository\nhttps://github.com/wspdyb/hebrew-calendar-kmm")
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PlaceholderCard(content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp)
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SettingItem(label: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
