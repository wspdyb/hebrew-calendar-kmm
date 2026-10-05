package wspdyb.calculator.zmanim.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import wspdyb.calculator.zmanim.R
import wspdyb.calculator.zmanim.ui.navigation.AppNavHost
import wspdyb.calculator.zmanim.ui.navigation.Route
import wspdyb.calculator.zmanim.ui.navigation.navigateTo

// Navigation Item data
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Route
)

@Composable
fun ZmanimApp() {
    val navController = rememberNavController()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showDrawer by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        BottomNavItem(
            label = stringResource(R.string.nav_home),
            icon = Icons.Filled.Home,
            route = Route.Home
        ),
        BottomNavItem(
            label = stringResource(R.string.nav_calculator),
            icon = Icons.Filled.Calculate,
            route = Route.Calculator
        ),
        BottomNavItem(
            label = stringResource(R.string.nav_validator),
            icon = Icons.Filled.Search,
            route = Route.Validator
        ),
        BottomNavItem(
            label = stringResource(R.string.nav_holidays),
            icon = Icons.Filled.DateRange,
            route = Route.HolidayExplorer
        ),
        BottomNavItem(
            label = stringResource(R.string.nav_learn),
            icon = Icons.Filled.School,
            route = Route.Learn
        )
    )

    ModalNavigationDrawer(
        drawerState = rememberDrawerState(DrawerValue.Closed),
        scrimColor = MaterialTheme.colorScheme.scrim,
        drawerContent = {
            DrawerMenu(
                navController = navController,
                onDrawerClose = { showDrawer = false }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline
                        ),
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    bottomNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                navController.navigateTo(item.route)
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            },
            topBar = {
                TopAppBar(
                    title = { Text("Zmanim Calculator") },
                    navigationIcon = {
                        IconButton(onClick = { showDrawer = true }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                AppNavHost(navController = navController)
            }
        }
    }
}

@Composable
private fun DrawerMenu(
    navController: NavHostController,
    onDrawerClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.7f)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Zmanim Calendar",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )
        
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        
        // Drawer Items
        DrawerMenuItem(
            label = stringResource(R.string.drawer_year_explorer),
            onClick = {
                navController.navigateTo(Route.YearExplorer)
                onDrawerClose()
            }
        )
        
        DrawerMenuItem(
            label = stringResource(R.string.drawer_cycles),
            onClick = {
                navController.navigateTo(Route.Learn)
                onDrawerClose()
            }
        )
        
        DrawerMenuItem(
            label = stringResource(R.string.drawer_validation_test),
            onClick = {
                // Validation test
                onDrawerClose()
            }
        )
        
        DrawerMenuItem(
            label = stringResource(R.string.drawer_settings),
            onClick = {
                navController.navigateTo(Route.Settings)
                onDrawerClose()
            }
        )
        
        DrawerMenuItem(
            label = stringResource(R.string.drawer_about),
            onClick = {
                navController.navigateTo(Route.About)
                onDrawerClose()
            }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Divider(modifier = Modifier.padding(horizontal = 16.dp))
        
        Text(
            text = "v1.0.0",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun DrawerMenuItem(
    label: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
