package pl.put.cocktailrecipes

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.models.Item
import pl.put.cocktailrecipes.views.CocktailList
import pl.put.cocktailrecipes.views.DetailScreen
import pl.put.cocktailrecipes.views.EmptyScreen
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.compose.rememberNavController

@Composable
fun ComposeNavigation(navController: NavHostController, padding: PaddingValues) {
    val isTablet = LocalConfiguration.current.screenWidthDp > 600

    // State to track the current route's arguments
    // This state will automatically update when the navigation back stack changes.
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Derive the current category and cocktail names from the route arguments
    // This makes the state react directly to navigation changes.
    val currentCategoryName by remember(navBackStackEntry) {
        derivedStateOf {
            // Check if the route is for a category or a detail screen to extract category
            when (navBackStackEntry?.destination?.route) {
                "category/{categoryName}" -> navBackStackEntry?.arguments?.getString("categoryName")
                "detail/{cocktailName}" -> {
                    // If on detail screen, derive category from cocktail name
                    val cocktailName = navBackStackEntry?.arguments?.getString("cocktailName")
                    cocktailName?.let { CocktailRecipes.getCocktailDetails(it).category }
                }
                else -> null // No category relevant for 'home' or other routes
            }
        }
    }

    val currentCocktailName by remember(navBackStackEntry) {
        derivedStateOf {
            // Only relevant on the detail screen route
            if (navBackStackEntry?.destination?.route == "detail/{cocktailName}") {
                navBackStackEntry?.arguments?.getString("cocktailName")
            } else {
                null // No specific cocktail selected via navigation on other routes
            }
        }
    }

    // --- Optional: Keep mutable state if needed for UI elements *outside* NavHost ---
    // If you need to hoist the *selected* item state perhaps for a top bar or
    // persistent UI element that needs to know the selection *even when not*
    // on the specific route, you could update these. Otherwise, relying on
    // derivedStateOf above is often sufficient.
    var selectedCategoryForUI by remember { mutableStateOf<Item?>(null) }
    var selectedCocktailForUI by remember { mutableStateOf<Item?>(null) }

    // Update the optional UI states when the derived states change
    LaunchedEffect(currentCategoryName) {
        selectedCategoryForUI = currentCategoryName?.let { Item(it) }
        // When category changes, reset the selected cocktail *if* not on a detail page
        if (currentCocktailName == null) {
            selectedCocktailForUI = null
        }
    }
    LaunchedEffect(currentCocktailName) {
        selectedCocktailForUI = currentCocktailName?.let { Item(it) }
    }
    // --- End Optional ---


    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        composable("home") {
            // Reset selections when returning home if using the optional states
            LaunchedEffect(Unit) {
                selectedCategoryForUI = null
                selectedCocktailForUI = null
            }
            EmptyScreen()
        }

        composable(
            route = "category/{categoryName}",
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extract argument directly here - this is the source of truth for this screen
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            // This state is specific to the tablet view's detail pane within the category route
            var cocktailForDetailPane by remember(categoryName) { mutableStateOf<Item?>(null) }

            // When the category changes, reset the detail pane selection
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
                            // Highlight the selected cocktail if needed, comparing with cocktailForDetailPane
                            // selectedCocktail = cocktailForDetailPane, // Pass this if CocktailList needs it
                            onClick = { cocktail ->
                                // On tablet, update the detail pane state *instead* of full navigation
                                cocktailForDetailPane = Item(cocktail.name)
                                // Optionally update the global UI state if used
                                // selectedCocktailForUI = Item(cocktail.name)
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
                        // Show detail based on the local state `cocktailForDetailPane`
                        val itemToShow = cocktailForDetailPane ?: Item(CocktailRecipes.mapCategoryToDrinkName(Item(categoryName))) // Show default or selected
                        DetailScreen(
                            item = itemToShow,
                            modifier = Modifier.fillMaxSize(),
                            // Navigate back might mean clearing selection or popping stack depending on UX goals
                            // Option 1: Clear selection within the category view
                            navigateBack = { cocktailForDetailPane = null }
                            // Option 2: Standard back navigation
                            // navigateBack = { navController.popBackStack() }
                        )
                    }
                }
            } else { // Phone layout
                CocktailList(
                    modifier = Modifier.fillMaxSize(),
                    categoryName = Item(categoryName),
                    // selectedCocktail = null, // No selection highlighting needed here usually
                    onClick = { cocktail ->
                        // On phone, navigate to the dedicated detail screen
                        navController.navigate("detail/${cocktail.name}")
                    }
                )
            }
        }

        composable(
            route = "detail/{cocktailName}",
            arguments = listOf(navArgument("cocktailName") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extract argument - source of truth for this screen
            val cocktailName = backStackEntry.arguments?.getString("cocktailName") ?: ""
            // Derive category for the list view on tablets
            val categoryForList = remember(cocktailName) {
                Item(CocktailRecipes.getCocktailDetails(cocktailName).category)
            }


            if (isTablet) {
                // State to track selection *within this specific* detail route instance for the list
                var selectedCocktailInList by remember(cocktailName) { mutableStateOf(Item(cocktailName)) }

                // If the cocktailName argument changes (e.g., navigated from detail A to detail B)
                // update the selection state.
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
                            // Pass the current selection for potential highlighting
                            // selectedCocktail = selectedCocktailInList,
                            onClick = { cocktail ->
                                // Navigate to the new detail screen, replacing the current one if it's
                                // already a detail screen. Update the selection state.
                                selectedCocktailInList = Item(cocktail.name)
                                navController.navigate("detail/${cocktail.name}") {
                                    // Optional: Pop up to category if preferred UX
                                    // popUpTo("category/${categoryForList.name}") { saveState = true }
                                    // Or just ensure single top
                                    launchSingleTop = true
                                    // Or pop up to the *start* of the graph if deep linked?
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
                            // Detail screen always shows the cocktail from the route argument
                            item = Item(cocktailName),
                            modifier = Modifier.fillMaxSize(),
                            navigateBack = { navController.popBackStack() }
                        )
                    }
                }
            } else { // Phone layout
                DetailScreen(
                    item = Item(cocktailName),
                    modifier = Modifier.fillMaxSize(),
                    navigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}