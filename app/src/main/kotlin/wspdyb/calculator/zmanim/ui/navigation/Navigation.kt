package wspdyb.calculator.zmanim.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import wspdyb.calculator.zmanim.ui.screens.HomeScreen
import wspdyb.calculator.zmanim.ui.screens.CalculatorScreen
import wspdyb.calculator.zmanim.ui.screens.ValidatorScreen
import wspdyb.calculator.zmanim.ui.screens.HolidayExplorerScreen
import wspdyb.calculator.zmanim.ui.screens.LearnScreen
import wspdyb.calculator.zmanim.ui.screens.YearExplorerScreen
import wspdyb.calculator.zmanim.ui.screens.SettingsScreen
import wspdyb.calculator.zmanim.ui.screens.AboutScreen

// Route definitions
sealed class Route(val route: String) {
    object Home : Route("home")
    object Calculator : Route("calculator")
    object Validator : Route("validator")
    object HolidayExplorer : Route("holiday_explorer")
    object Learn : Route("learn")
    object YearExplorer : Route("year_explorer")
    object Settings : Route("settings")
    object About : Route("about")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = Route.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Route.Home.route) {
            HomeScreen(navController = navController)
        }
        
        composable(Route.Calculator.route) {
            CalculatorScreen(navController = navController)
        }
        
        composable(Route.Validator.route) {
            ValidatorScreen(navController = navController)
        }
        
        composable(Route.HolidayExplorer.route) {
            HolidayExplorerScreen(navController = navController)
        }
        
        composable(Route.Learn.route) {
            LearnScreen(navController = navController)
        }
        
        composable(Route.YearExplorer.route) {
            YearExplorerScreen(navController = navController)
        }
        
        composable(Route.Settings.route) {
            SettingsScreen(navController = navController)
        }
        
        composable(Route.About.route) {
            AboutScreen(navController = navController)
        }
    }
}

// Helper function for navigation
fun NavHostController.navigateTo(route: Route) {
    this.navigate(route.route) {
        launchSingleTop = true
        restoreState = true
    }
}
