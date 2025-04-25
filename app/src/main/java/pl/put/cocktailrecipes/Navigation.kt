package pl.put.cocktailrecipes


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
            route = "category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            var cocktailForDetailPane by remember(categoryName) { mutableStateOf<Item?>(null) }


            LaunchedEffect(categoryName) {
                cocktailForDetailPane = null
            }

            if (isTablet) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        CocktailList(
                            categoryName = Item(categoryName),
                            onClick = { cocktail ->

                                cocktailForDetailPane = Item(cocktail.name)

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

                        val itemToShow = cocktailForDetailPane ?: Item(CocktailRecipes.mapCategoryToDrinkName(Item(categoryName)))
                        DetailScreen(
                            item = itemToShow,
                            modifier = Modifier.fillMaxSize(),
                            navigateBack = { cocktailForDetailPane = null }

                        )
                    }
                }
            } else {
                CocktailList(
                    modifier = Modifier.fillMaxSize(),
                    categoryName = Item(categoryName),

                    onClick = { cocktail ->
                        navController.navigate("detail/${cocktail.name}")
                    }
                )
            }
        }

        composable(
            route = "detail/{cocktailName}",
            arguments = listOf(navArgument("cocktailName") { type = NavType.StringType })
        ) { backStackEntry ->

            val cocktailName = backStackEntry.arguments?.getString("cocktailName") ?: ""
            val categoryForList = remember(cocktailName) {
                Item(CocktailRecipes.getCocktailDetails(cocktailName).category)
            }


            if (isTablet) {

                var selectedCocktailInList by remember(cocktailName) { mutableStateOf(Item(cocktailName)) }

                LaunchedEffect(cocktailName) {
                    selectedCocktailInList = Item(cocktailName)
                }


                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        CocktailList(
                            categoryName = categoryForList,

                            onClick = { cocktail ->

                                selectedCocktailInList = Item(cocktail.name)
                                navController.navigate("detail/${cocktail.name}") {
                                    launchSingleTop = true

                                }
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