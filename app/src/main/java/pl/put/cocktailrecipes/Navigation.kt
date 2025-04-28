package pl.put.cocktailrecipes

import androidx.compose.foundation.layout.*
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
import pl.put.cocktailrecipes.views.DetailScreen
import pl.put.cocktailrecipes.views.WelcomeScreen
import androidx.compose.ui.platform.LocalConfiguration
import pl.put.cocktailrecipes.views.CocktailListHorizontalPager




@Composable
fun ComposeNavigation(
    navController: NavHostController,
    padding: PaddingValues,
    setImgSrc: (String) -> Unit,
    onTitleChange: (String) -> Unit
) {
    val isTablet = LocalConfiguration.current.screenWidthDp > 600

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        composable("home") {
            LaunchedEffect(Unit) {
                onTitleChange("Cocktail Recipes")
            }
            WelcomeScreen()
        }

        composable(
            route = "category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            var cocktailForDetailPane by remember(categoryName) { mutableStateOf<Item?>(null) }


            LaunchedEffect(categoryName) {
                cocktailForDetailPane = null
                setImgSrc("")
            }



            if (isTablet) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)

                    ) {
                        CocktailListHorizontalPager(
                            categoryName = Item(categoryName),
                            onClick = { cocktail ->

                                cocktailForDetailPane = Item(cocktail.name)

                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(),
                            onTitleChange = onTitleChange
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)

                    ) {

                        val itemToShow = cocktailForDetailPane ?: Item(CocktailRecipes.mapCategoryToDrinkName(Item(categoryName)))
                        DetailScreen(
                            item = itemToShow,
                            modifier = Modifier.fillMaxSize(),
                            setImgSrc = setImgSrc,
                            isTablet = true
                        )
                    }
                }
            } else {
                CocktailListHorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    categoryName = Item(categoryName),
                    onClick = { cocktail ->
                        setImgSrc("")
                        navController.navigate("detail/${cocktail.name}")
                    },
                    onTitleChange = onTitleChange
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

                var selectedCocktailInList by remember(cocktailName) {
                    mutableStateOf(
                        Item(
                            cocktailName
                        )
                    )
                }

                LaunchedEffect(cocktailName) {
                    selectedCocktailInList = Item(cocktailName)
                    onTitleChange(CocktailRecipes.getCocktailDetails(cocktailName).category)
                }


                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        CocktailListHorizontalPager(
                            categoryName = categoryForList,

                            onClick = { cocktail ->

                                selectedCocktailInList = Item(cocktail.name)
                                navController.navigate("detail/${cocktail.name}") {
                                    launchSingleTop = true

                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            onTitleChange = onTitleChange
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)

                    ) {
                        DetailScreen(
                            item = Item(cocktailName),
                            modifier = Modifier.fillMaxSize(),
                            setImgSrc = setImgSrc,
                            isTablet = true
                        )
                    }
                }
            } else {

                LaunchedEffect(Unit) {
                    onTitleChange("")
                }

                DetailScreen(
                    item = Item(cocktailName),
                    modifier = Modifier.fillMaxSize(),
                    setImgSrc = setImgSrc,
                    isTablet = false
                )
            }
        }
    }
}