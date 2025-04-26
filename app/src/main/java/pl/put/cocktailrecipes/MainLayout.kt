package pl.put.cocktailrecipes

import android.util.Log
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.put.cocktailrecipes.api.CocktailRecipes
import pl.put.cocktailrecipes.utils.Loading
import pl.put.cocktailrecipes.utils.TopBar


@Composable
fun MainLayout() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val isDataLoading = remember { mutableStateOf(true) }
    var showAnimation = remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        Log.d("loading", "loadings")
        withContext(Dispatchers.IO) {
            CocktailRecipes.init()
        }
        isDataLoading.value = false
    }


    if (showAnimation.value) {
        Loading({ showAnimation.value = false }, isDataLoading.value)
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerLayout(
                        scope = scope,
                        drawerState = drawerState,
                        navController = navController
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopBar(scope = scope, drawerState = drawerState, modifier = Modifier, navigateBack = {navController.navigateUp()})
                }
            ) { innerPadding ->
                ComposeNavigation(navController = navController, padding = innerPadding)
            }
        }
    }
}
