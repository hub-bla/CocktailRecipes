package pl.put.cocktailrecipes.views

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.put.cocktailrecipes.api.CocktailRecipes

import pl.put.cocktailrecipes.models.Item
import pl.put.cocktailrecipes.utils.SuccessComponent
import pl.put.cocktailrecipes.utils.TimerComponent


@Composable
fun CocktailsHorizontalPager(
    item: Item,
    modifier: Modifier
) {
    val successMessage = remember { mutableStateOf("") }
    val cocktail =CocktailRecipes.getCocktailDetails(item.name)

    val items = CocktailRecipes.getCocktailNamesByCategory(Item(cocktail.category))
    val initialPageIdx = items.indexOfFirst { it == item.name }

    val pagerState = androidx.compose.foundation.pager.rememberPagerState(initialPage = initialPageIdx,pageCount = {items.size},)

    LaunchedEffect (item) {
        val newIdx = items.indexOfFirst { it == item.name }
        pagerState.animateScrollToPage(newIdx)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val currentItem = Item(items[page])
            DetailScreen(
                item = currentItem,
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimerComponent(
                    modifier = Modifier
                )

                FloatingActionButton(
                    onClick = { successMessage.value = "Message was sent!" }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Play",
                        tint = Color.White
                    )
                }
            }
        }
        if (successMessage.value != "") {
            SuccessComponent(
                successMessage.value,
                3000,
                {
                    Log.d("", "Clear sucess message")
                    successMessage.value = ""
                },
                modifier
                    .offset(y = 30.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}