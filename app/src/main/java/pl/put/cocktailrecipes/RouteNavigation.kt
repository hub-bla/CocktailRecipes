package pl.put.cocktailrecipes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument


@Composable
fun RouteNavigation() {
    val routes = object {
        val Home = object {
            val route = "home"
        }
        val DetailsScreen = object {
            val route = "details/{cocktailName}"
            val arg = "cocktailName"
        }
    }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = routes.Home.route
    ) {
        composable(routes.Home.route) {
            CocktailList(Modifier, navController)
        }
        composable(
            route = routes.DetailsScreen.route,
            arguments = listOf(navArgument(routes.DetailsScreen.arg) { type = NavType.StringType })
        ) {backStackEntry ->
            val cocktailName = backStackEntry.arguments?.getString(routes.DetailsScreen.arg)
            DetailScreen(cocktailName.toString(), Modifier, navController)
        }
    }

}