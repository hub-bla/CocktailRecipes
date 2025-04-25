package pl.put.cocktailrecipes.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(scope: CoroutineScope, drawerState: DrawerState) {
    TopAppBar(
        title = { Text("Cocktail Recipes") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}
