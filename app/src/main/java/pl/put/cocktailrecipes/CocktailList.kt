package pl.put.cocktailrecipes

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CocktailList(modifier: Modifier, navController: NavController) {
    val cocktailNames = remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        CocktailRecipes.init()
        cocktailNames.value =
            CocktailRecipes.getCocktailNames()
    }
    LazyColumn(
        modifier = modifier
            .background(Color.LightGray)
            .fillMaxSize()
    ) {
        itemsIndexed(cocktailNames.value.toList()) { index, cocktailName ->
            Box(
                modifier = modifier
                    .background(Color(0xffedebe4))
                    .clickable {
                        navController.navigate("details/$cocktailName")
                        Log.d("CocktailClicked", "Cocktail clicked: $cocktailName")
                    }
                    .padding(16.dp)
                    .fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(cocktailName, modifier = modifier)
            }

        }
    }
}