package pl.put.cocktailrecipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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



@Composable
fun CocktailList(onClick: (Item) -> Unit, modifier: Modifier) {
    val cocktailNames = remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        CocktailRecipes.init()
        cocktailNames.value = CocktailRecipes.getCocktailNames()
    }

    LazyColumn(
        modifier = modifier
            .background(Color(0xffedebe4))
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        itemsIndexed(cocktailNames.value.toList()) { _, cocktailName ->
            val item = Item(cocktailName)
            Box(
                modifier = modifier
                    .background(Color(0xffedebe4))
                    .clickable {
                        onClick(item)
                    }
                    .padding(16.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(cocktailName, modifier = Modifier)
            }
        }
    }
}








