package pl.put.cocktailrecipes

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.models.Item
import pl.put.cocktailrecipes.views.CocktailList
import pl.put.cocktailrecipes.views.DetailScreen
import pl.put.cocktailrecipes.views.EmptyScreen
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun ComposeNavigation(navController: NavHostController, padding: PaddingValues) {
    val isTablet = LocalConfiguration.current.screenWidthDp > 600
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        composable("home") {
            EmptyScreen()
        }

        composable(
            "category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""

            if (isTablet) {

                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        CocktailList(
                            cocktailName = Item(CocktailRecipes.mapCategoryToDrinkName(Item(categoryName))),
                            onClick = { cocktail ->
                                navController.navigate("detail/${cocktail.name}")
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        DetailScreen(
                            item = Item(CocktailRecipes.mapCategoryToDrinkName(Item(categoryName))),
                            modifier = Modifier.fillMaxSize(),
                            navigateBack = { navController.popBackStack() }
                        )
                    }
                }
            } else {

                CocktailList(
                    modifier = Modifier.fillMaxSize(),
                    cocktailName = Item(CocktailRecipes.mapCategoryToDrinkName(Item(categoryName))),
                    onClick = { cocktail ->
                        navController.navigate("detail/${cocktail.name}")
                    }
                )
            }
        }

        composable(
            "detail/{cocktailName}",
            arguments = listOf(navArgument("cocktailName") { type = NavType.StringType })
        ) { backStackEntry ->
            val cocktailName = backStackEntry.arguments?.getString("cocktailName") ?: ""

            if (isTablet) {
                Row(modifier = Modifier.fillMaxSize()) {

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        CocktailList(
                            cocktailName = Item(cocktailName),
                            onClick = { cocktail ->
                                navController.navigate("detail/${cocktail.name}")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        DetailScreen(
                            item = Item(cocktailName),
                            modifier = Modifier.fillMaxSize(),
                            navigateBack = { navController.popBackStack() }
                        )
                    }
                }
            } else {

                DetailScreen(

                    item = Item(cocktailName),
                    modifier = Modifier.fillMaxSize(),
                    navigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

