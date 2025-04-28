package pl.put.cocktailrecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import pl.put.cocktailrecipes.api.CocktailRecipes

@Composable
fun DrawerLayout(scope: CoroutineScope, drawerState: DrawerState, navController: NavController) {
    val categories = remember { mutableStateOf(emptyList<String>()) }

    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        categories.value = CocktailRecipes.getCategories()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .verticalScroll(scrollState)

    ) {

        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            selected = false,
            onClick = {
                navController.navigate("home")
                scope.launch {
                    drawerState.close()
                }
            },
            modifier = Modifier
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )


        Text(
            text = "Categories",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp, top = 4.dp)
        )

        categories.value.forEach { category ->
            NavigationDrawerItem(
                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                label = {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = false,
                onClick = {
                    navController.navigate("category/${category}")
                    scope.launch {
                        drawerState.close()
                    }
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

    }
}